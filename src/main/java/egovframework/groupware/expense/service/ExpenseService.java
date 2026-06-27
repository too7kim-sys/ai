package egovframework.groupware.expense.service;

import java.util.List;

public interface ExpenseService {

    /** 결의서 신규 작성 + 라인 + EXPENSE 양식 자동 결재 상신 */
    Long create(ExpenseReportVO report, List<ExpenseItemVO> items, List<Long> approverIds);

    ExpenseReportVO findById(Long reportId);

    List<ExpenseReportVO> listMine(Long userId);
    List<ExpenseReportVO> listAdmin(String status, Long deptId);

    /**
     * 결재 완료(APPROVED) 결의서를 일괄 환급:
     *   - 개인카드/현금 라인 합계로 GW_PAYMENT 출금 행 생성
     *   - 상태 REIMBURSED, 메일 발송
     * @return 환급 처리된 reportId 목록
     */
    List<Long> reimburse(List<Long> reportIds);
}
