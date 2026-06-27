package egovframework.groupware.payroll.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PayrollService {

    /** 대상 월의 재직자 일괄 DRAFT 생성. 이미 있는 사람은 skip. */
    int generateForMonth(String payMonth);

    /** 단일 명세 자동 재계산 + 저장 (수동 입력 manualPayments는 보존). */
    PayrollVO recalculate(Long payId, Map<String, Long> manualPayments);

    PayrollVO findPayroll(Long payId);

    PayrollVO findByUserAndMonth(Long userId, String payMonth);

    List<PayrollVO> listPayrolls(String payMonth, String status, int offset, int limit);

    long countPayrolls(String payMonth, String status);

    /** 부서별 인건비 통계 (급여대장). */
    List<Map<String, Object>> deptCostSummary(String payMonth);

    List<PayrollVO> listMyPayrolls(Long userId);

    void confirm(Long payId);

    void markPaid(Long payId, LocalDate paidDt);

    SalaryContractVO findCurrentContract(Long userId, LocalDate on);

    List<SalaryContractVO> listContracts(Long userId);

    Long createContract(SalaryContractVO vo);

    List<InsuranceRateVO> findActiveRates(LocalDate on);

    /* ===== 4대보험 요율 관리(HR/ADMIN) ===== */
    /** 전 기간(과거/현재/미래) 요율 행을 effective_from DESC 로 반환. */
    List<InsuranceRateVO> listAllRates();
    InsuranceRateVO findRate(Long rateId);
    Long createRate(InsuranceRateVO vo);
    void updateRate(InsuranceRateVO vo);
    void deleteRate(Long rateId);

    /** 회사 부담분도 반환 */
    PayrollVO findPayrollWithDetails(Long payId);

    /**
     * 명세서 PDF 바이트.
     */
    byte[] generatePayslipPdf(Long payId);

    /**
     * 명세서 메일 발송 (PDF 첨부, 비동기).
     * @return 발송 큐에 등록한 mailId 목록
     */
    List<Long> sendPayslipsByMail(List<Long> payIds);
}
