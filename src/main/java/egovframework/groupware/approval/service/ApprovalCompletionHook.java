package egovframework.groupware.approval.service;

/**
 * 결재 종결(APPROVED/REJECTED) 시점에 후속 액션을 처리하는 훅.
 * 각 도메인 모듈(휴가/지출결의/사업자계약/지급 등)이 구현해 Spring Bean 으로 등록한다.
 *
 * <h3>호출 시점과 트랜잭션</h3>
 * <ul>
 *   <li>{@code ApprovalServiceImpl.act()} 의 동일 트랜잭션 내에서 호출된다. 훅이 예외를 던지면
 *       결재 상태 전이까지 함께 롤백된다(원자성 보장).</li>
 *   <li>회수(CANCEL) 시에는 호출되지 않는다. 회수의 후속 처리는 기안 도메인 측 cancel
 *       서비스(예: {@code LeaveServiceImpl.cancel}) 가 직접 책임진다.</li>
 * </ul>
 *
 * <h3>자원 차감 정책 — 도메인별로 모델이 다름</h3>
 * <ul>
 *   <li><b>휴가(LEAVE)</b> — 신청 시점에 잔여를 즉시 차감(이중 신청 방지). 따라서 hook 은
 *       승인 시 상태만 APPROVED 로 전이하고, 반려 시 상태 REJECTED + 잔여 복구(음수 차감).</li>
 *   <li><b>지출(EXPENSE)</b> — 승인 시점에 부서 예산 차감(요청 단계에서는 미차감). hook 은
 *       승인 시 상태 APPROVED + 예산 USED 가산, 반려 시 상태 REJECTED.</li>
 * </ul>
 *
 * 새 도메인 hook 을 구현할 때는 이 두 모델 중 어느 것을 따를지 먼저 결정한 뒤
 * apply/cancel/onCompleted 의 책임 분배를 그에 맞춰 설계한다.
 */
public interface ApprovalCompletionHook {

    /** 처리 가능한 양식 코드 (LEAVE, EXPENSE, CONTRACT_PROPOSAL, PAYMENT_REQUEST 등) */
    String supportedFormCd();

    /**
     * 결재 종결 시 호출. 동일 트랜잭션 내에서 실행됨.
     * @param doc 종결된 결재 문서
     * @param approved true=APPROVED, false=REJECTED
     */
    void onCompleted(ApprovalDocVO doc, boolean approved);
}
