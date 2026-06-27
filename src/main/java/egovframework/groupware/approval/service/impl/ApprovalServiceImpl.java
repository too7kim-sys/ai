package egovframework.groupware.approval.service.impl;

import egovframework.groupware.approval.mapper.ApprovalMapper;
import egovframework.groupware.approval.service.*;
import egovframework.groupware.cmm.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalServiceImpl.class);

    private final ApprovalMapper mapper;
    private final Map<String, ApprovalCompletionHook> hooks = new HashMap<>();

    public ApprovalServiceImpl(ApprovalMapper mapper, List<ApprovalCompletionHook> hookList) {
        this.mapper = mapper;
        for (ApprovalCompletionHook h : hookList) {
            hooks.put(h.supportedFormCd(), h);
        }
        log.info("Registered approval completion hooks for: {}", hooks.keySet());
    }

    @Override public List<ApprovalFormVO> listForms() { return mapper.listForms(); }
    @Override public ApprovalFormVO findForm(String formCd) { return mapper.findForm(formCd); }

    @Override
    @Transactional
    public Long createDoc(ApprovalDocVO doc, List<Long> approverIds) {
        if (doc.getFormId() == null && doc.getFormCd() != null) {
            ApprovalFormVO f = mapper.findForm(doc.getFormCd());
            if (f == null) throw new ApiException("FORM_NOT_FOUND", "결재 양식이 없습니다: " + doc.getFormCd());
            doc.setFormId(f.getFormId());
        }
        // 상신 가능한 문서가 되려면 결재선이 최소 1명은 있어야 한다 — 빈 결재선으로 만든 뒤
        // submit 단계에서 막더라도 호출 측 실수를 더 명확하게 조기 감지.
        if (approverIds == null || approverIds.isEmpty()) {
            throw new ApiException("NO_APPROVERS", "결재선이 비어 있습니다");
        }
        if (doc.getDocNo() == null) {
            doc.setDocNo("AD-" + java.time.LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                    + "-" + System.currentTimeMillis() % 100000);
        }
        if (doc.getStatusCd() == null) doc.setStatusCd("DRAFT");
        mapper.insertDoc(doc);

        int step = 1;
        for (Long aid : approverIds) {
            ApprovalLineVO l = new ApprovalLineVO();
            l.setDocId(doc.getDocId());
            l.setStepNo(step++);
            l.setApproverId(aid);
            l.setLineTypeCd("APPROVE");
            l.setStatusCd("PENDING");
            mapper.insertLine(l);
        }
        return doc.getDocId();
    }

    @Override
    @Transactional
    public void submit(Long docId) {
        ApprovalDocVO d = mapper.findDoc(docId);
        if (d == null) throw new ApiException("DOC_NOT_FOUND", "문서를 찾을 수 없습니다");
        if (!"DRAFT".equals(d.getStatusCd())) {
            throw new ApiException("INVALID_STATE", "DRAFT 상태에서만 상신 가능합니다: " + d.getStatusCd());
        }
        // 결재선이 한 줄도 없는 상태로 상신되면 어떤 결재자도 act 할 수 없는 dead-end 가 된다.
        if (mapper.countLines(docId) <= 0) {
            throw new ApiException("NO_APPROVERS", "결재선이 비어 있어 상신할 수 없습니다");
        }
        int updated = mapper.submit(docId);
        if (updated != 1) {
            // DRAFT 가드 (mapper SQL) 와 위 상태 검사 사이 race — 다른 트랜잭션이 먼저 상신/회수.
            throw new ApiException("CONCURRENT_UPDATE", "상신 처리 중 상태가 변경되었습니다");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cancel(Long docId, Long requesterId) {
        ApprovalDocVO d = mapper.findDoc(docId);
        if (d == null) throw new ApiException("DOC_NOT_FOUND", "문서를 찾을 수 없습니다");
        if (!d.getDrafterId().equals(requesterId)) {
            throw new ApiException("FORBIDDEN", "본인의 문서만 회수할 수 있습니다");
        }
        // 단일 SQL 로 race-free 회수: drafter 검증 + 상태(DRAFT/IN_PROGRESS) + 승인된 단계 없음.
        // 동시 act 가 끼어들어 APPROVED 라인이 생기면 NOT EXISTS 가 막아 0 rows.
        int updated = mapper.cancelDoc(docId, requesterId);
        if (updated != 1) {
            throw new ApiException("INVALID_STATE",
                "회수 불가 — 이미 처리 중이거나 승인된 단계가 있습니다");
        }
        // 잔여 PENDING 라인은 SKIPPED 로 마킹해 목록의 '대기' 표시를 정리.
        mapper.skipPendingLines(docId);
    }

    @Override
    @Transactional
    public void act(Long docId, Long approverId, boolean approve, String comment) {
        ApprovalDocVO d = mapper.findDoc(docId);
        if (d == null) throw new ApiException("DOC_NOT_FOUND", "문서를 찾을 수 없습니다");
        if (!"IN_PROGRESS".equals(d.getStatusCd())) {
            throw new ApiException("INVALID_STATE", "진행중인 문서만 결재할 수 있습니다");
        }
        List<ApprovalLineVO> lines = mapper.findLines(docId);
        ApprovalLineVO current = lines.stream()
            .filter(l -> "PENDING".equals(l.getStatusCd()))
            .findFirst().orElseThrow(() -> new ApiException("NO_PENDING", "대기중인 단계가 없습니다"));
        // 본인 결재 또는 위임받은 결재 — UI 노출과 일관되도록 service 에서도 둘 다 허용.
        boolean isApprover = approverId.equals(current.getApproverId());
        boolean isDelegate = current.getDelegatedToUserId() != null
                && approverId.equals(current.getDelegatedToUserId());
        if (!isApprover && !isDelegate) {
            throw new ApiException("FORBIDDEN", "현재 결재자가 아닙니다");
        }
        // 라인 결재 — PENDING 가드(SQL) 로 동시 act 차단. 0 rows 면 다른 트랜잭션이 먼저 처리한 것.
        int lineUpdated = mapper.updateLineApproval(
                current.getLineId(), approve ? "APPROVED" : "REJECTED", comment);
        if (lineUpdated != 1) {
            throw new ApiException("CONCURRENT_UPDATE", "이미 처리된 결재 단계입니다");
        }

        if (!approve) {
            int docUpdated = mapper.complete(docId, "REJECTED");
            if (docUpdated == 1) {
                // 잔여 PENDING 라인을 SKIPPED 로 마킹 — 반려 후 결재선 목록의 '대기' 잔존 표시 정리.
                mapper.skipPendingLines(docId);
                invokeHook(d, false);
            }
            return;
        }
        // 다음 PENDING 이 없으면 전체 완료. 직전 라인은 위에서 APPROVED 처리됨.
        boolean morePending = lines.stream()
            .anyMatch(l -> "PENDING".equals(l.getStatusCd()) && !l.getLineId().equals(current.getLineId()));
        if (!morePending) {
            int docUpdated = mapper.complete(docId, "APPROVED");
            if (docUpdated == 1) {
                invokeHook(d, true);
            }
        }
    }

    private void invokeHook(ApprovalDocVO d, boolean approved) {
        ApprovalCompletionHook hook = hooks.get(d.getFormCd());
        if (hook != null) {
            try {
                ApprovalDocVO full = mapper.findDoc(d.getDocId());
                hook.onCompleted(full, approved);
            } catch (Exception ex) {
                log.error("Approval completion hook failed (docId={}, formCd={})", d.getDocId(), d.getFormCd(), ex);
                throw ex;
            }
        }
    }

    @Override
    public ApprovalDocVO findDoc(Long docId) {
        ApprovalDocVO d = mapper.findDoc(docId);
        if (d != null) d.getLines().addAll(mapper.findLines(docId));
        return d;
    }

    @Override public List<ApprovalDocVO> listByDrafter(Long drafterId, String status) {
        return mapper.listByDrafter(drafterId, status);
    }
    @Override public List<ApprovalDocVO> listPending(Long approverId) { return mapper.listPending(approverId); }
    @Override public List<ApprovalDocVO> listCompleted(Long approverId, String status) {
        return mapper.listCompleted(approverId, status);
    }
}
