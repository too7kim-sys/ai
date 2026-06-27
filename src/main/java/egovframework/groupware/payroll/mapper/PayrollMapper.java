package egovframework.groupware.payroll.mapper;

import egovframework.groupware.payroll.service.IncomeTaxBracketVO;
import egovframework.groupware.payroll.service.InsuranceRateVO;
import egovframework.groupware.payroll.service.PayrollEmployerCostVO;
import egovframework.groupware.payroll.service.PayrollItemVO;
import egovframework.groupware.payroll.service.PayrollVO;
import egovframework.groupware.payroll.service.SalaryContractVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface PayrollMapper {

    List<InsuranceRateVO> findActiveRates(@Param("on") LocalDate on);

    /** 해당 시점에 유효한 소득세 간이세액 구간(min_taxable 오름차순). */
    List<IncomeTaxBracketVO> findActiveTaxBrackets(@Param("on") LocalDate on);

    int insertRate(InsuranceRateVO vo);
    int updateRate(InsuranceRateVO vo);
    int deleteRate(@Param("rateId") Long rateId);
    InsuranceRateVO findRate(@Param("rateId") Long rateId);
    /** 요율 관리 화면용 — 전 기간(과거/현재/미래) 모두 노출. */
    List<InsuranceRateVO> listAllRates();

    /** 부양가족 자동 카운트 (본인 제외 — 본인 +1 은 서비스에서 더함). */
    int countDependents(@Param("userId") Long userId);

    /** 만 20세 이하 자녀(=cutoff 이후 출생) 수. */
    int countChildrenUnder20(@Param("userId") Long userId,
                             @Param("cutoff") LocalDate cutoff);

    /** 해당 기간 근태 집계 (연장/야간/휴일 분, 근무일/결근일). */
    Map<String, Object> sumAttendance(@Param("userId") Long userId,
                                      @Param("from") LocalDate from,
                                      @Param("to") LocalDate to);

    /** 부서별 인건비 통계 (급여대장 화면용). */
    List<Map<String, Object>> sumByDept(@Param("payMonth") String payMonth);

    SalaryContractVO findCurrentContract(@Param("userId") Long userId,
                                         @Param("on") LocalDate on);

    int insertContract(SalaryContractVO vo);

    List<SalaryContractVO> listContracts(@Param("userId") Long userId);

    /** 월 일괄 생성용: 활성 사용자 조회 */
    List<Long> findActiveUserIds();

    /**
     * 월 일괄 생성용 — 활성 + 미퇴사 사용자와 입/퇴사일을 함께 조회.
     * Map 키: userId (Long), hireDate (LocalDate), resignDate (LocalDate)
     */
    java.util.List<java.util.Map<String, Object>> findPayrollEligibleUsers(
            @org.apache.ibatis.annotations.Param("firstOfMonth") java.time.LocalDate firstOfMonth);

    /**
     * 단일 사용자 입/퇴사일 조회. recalculate 시 일할 base 재계산용.
     * Map 키: hireDate (LocalDate), resignDate (LocalDate)
     */
    java.util.Map<String, Object> findUserHireResign(
            @org.apache.ibatis.annotations.Param("userId") Long userId);

    PayrollVO findPayroll(@Param("payId") Long payId);

    PayrollVO findByUserAndMonth(@Param("userId") Long userId,
                                 @Param("payMonth") String payMonth);

    List<PayrollVO> listPayrolls(@Param("payMonth") String payMonth,
                                 @Param("status") String status,
                                 @Param("offset") int offset,
                                 @Param("limit") int limit);

    long countPayrolls(@Param("payMonth") String payMonth,
                       @Param("status") String status);

    List<PayrollVO> listMyPayrolls(@Param("userId") Long userId);

    int insertPayroll(PayrollVO vo);

    int updatePayroll(PayrollVO vo);

    int deleteItems(@Param("payId") Long payId);

    int insertItem(PayrollItemVO item);

    List<PayrollItemVO> findItems(@Param("payId") Long payId);

    int deleteEmployerCosts(@Param("payId") Long payId);

    int insertEmployerCost(PayrollEmployerCostVO cost);

    List<PayrollEmployerCostVO> findEmployerCosts(@Param("payId") Long payId);

    int updateStatus(@Param("payId") Long payId,
                     @Param("statusCd") String statusCd,
                     @Param("paidDt") LocalDate paidDt);
}
