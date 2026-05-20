package egovframework.groupware.approval.service;

/**
 * 결재 완료(APPROVED) 시점에 후속 액션을 처리하는 훅.
 * 각 도메인 모듈(휴가/지출결의/사업자계약/지급 등)이 구현해 등록한다.
 */
public interface ApprovalCompletionHook {

    /** 처리 가능한 양식 코드 (LEAVE, EXPENSE, CONTRACT_PROPOSAL, PAYMENT_REQUEST 등) */
    String supportedFormCd();

    /**
     * 결재 완료 시 호출. 동일 트랜잭션 내에서 실행됨.
     * @param doc 완료된 결재 문서
     * @param approved true=APPROVED, false=REJECTED
     */
    void onCompleted(ApprovalDocVO doc, boolean approved);
}
