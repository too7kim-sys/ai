package egovframework.groupware.approval.service.impl;

import egovframework.groupware.approval.mapper.ApprovalMapper;
import egovframework.groupware.approval.service.*;
import egovframework.groupware.cmm.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
        if (doc.getDocNo() == null) {
            doc.setDocNo("AD-" + java.time.LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                    + "-" + System.currentTimeMillis() % 100000);
        }
        if (doc.getStatusCd() == null) doc.setStatusCd("DRAFT");
        mapper.insertDoc(doc);

        int step = 1;
        if (approverIds != null) {
            for (Long aid : approverIds) {
                ApprovalLineVO l = new ApprovalLineVO();
                l.setDocId(doc.getDocId());
                l.setStepNo(step++);
                l.setApproverId(aid);
                l.setLineTypeCd("APPROVE");
                l.setStatusCd("PENDING");
                mapper.insertLine(l);
            }
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
        mapper.submit(docId);
    }

    @Override
    @Transactional
    public void cancel(Long docId, Long requesterId) {
        ApprovalDocVO d = mapper.findDoc(docId);
        if (d == null) throw new ApiException("DOC_NOT_FOUND", "문서를 찾을 수 없습니다");
        if (!d.getDrafterId().equals(requesterId)) {
            throw new ApiException("FORBIDDEN", "본인의 문서만 회수할 수 있습니다");
        }
        if (!"IN_PROGRESS".equals(d.getStatusCd()) && !"DRAFT".equals(d.getStatusCd())) {
            throw new ApiException("INVALID_STATE", "회수 불가 상태: " + d.getStatusCd());
        }
        List<ApprovalLineVO> lines = mapper.findLines(docId);
        for (ApprovalLineVO l : lines) {
            if ("APPROVED".equals(l.getStatusCd())) {
                throw new ApiException("INVALID_STATE", "이미 승인된 단계가 있어 회수할 수 없습니다");
            }
        }
        mapper.updateDocStatus(docId, "CANCELED");
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
        if (!current.getApproverId().equals(approverId)) {
            throw new ApiException("FORBIDDEN", "현재 결재자가 아닙니다");
        }
        mapper.updateLineApproval(current.getLineId(), approve ? "APPROVED" : "REJECTED", comment);

        if (!approve) {
            mapper.complete(docId, "REJECTED");
            invokeHook(d, false);
            return;
        }
        // 다음 PENDING이 없으면 전체 완료
        boolean morePending = lines.stream()
            .anyMatch(l -> "PENDING".equals(l.getStatusCd()) && !l.getLineId().equals(current.getLineId()));
        if (!morePending) {
            mapper.complete(docId, "APPROVED");
            invokeHook(d, true);
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
