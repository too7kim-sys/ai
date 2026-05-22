package egovframework.groupware.payroll.mapper;

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
