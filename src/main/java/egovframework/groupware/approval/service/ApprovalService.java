package egovframework.groupware.approval.service;

import java.util.List;

public interface ApprovalService {

    List<ApprovalFormVO> listForms();
    ApprovalFormVO findForm(String formCd);

    /** 문서 생성 (DRAFT). 결재선 라인도 함께 저장. */
    Long createDoc(ApprovalDocVO doc, List<Long> approverIds);

    /** 상신: DRAFT → IN_PROGRESS. 첫 라인은 PENDING. */
    void submit(Long docId);

    /** 회수: IN_PROGRESS && (모든 라인 PENDING) → CANCELED */
    void cancel(Long docId, Long requesterId);

    /**
     * 현재 단계 결재자의 승인/반려.
     * 모든 APPROVE 라인이 승인되면 doc 상태 APPROVED + 후속 훅 호출.
     * 반려 시 doc 상태 REJECTED + 훅 호출.
     */
    void act(Long docId, Long approverId, boolean approve, String comment);

    ApprovalDocVO findDoc(Long docId);

    List<ApprovalDocVO> listByDrafter(Long drafterId, String status);
    List<ApprovalDocVO> listPending(Long approverId);
    List<ApprovalDocVO> listCompleted(Long approverId, String status);
}
