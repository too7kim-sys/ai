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

@Mapper
public interface PayrollMapper {

    List<InsuranceRateVO> findActiveRates(@Param("on") LocalDate on);

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
