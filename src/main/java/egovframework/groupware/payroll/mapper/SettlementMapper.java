package egovframework.groupware.payroll.mapper;

import egovframework.groupware.payroll.service.SeveranceVO;
import egovframework.groupware.payroll.service.YearEndTaxVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SettlementMapper {

    /* ===== 급여 집계 (정산 계산용) ===== */

    /** 연간 급여 합계 — grossPay/taxablePay/incomeTax. */
    Map<String, Object> sumYearlyPayroll(@Param("userId") Long userId,
                                         @Param("year") int year);

    /** 최근 N개월 급여 합계 (퇴직금 평균임금 계산용). */
    Map<String, Object> sumRecentPayroll(@Param("userId") Long userId,
                                         @Param("months") int months);

    /* ===== 연말정산 ===== */
    int insertYearEnd(YearEndTaxVO vo);
    int updateYearEnd(YearEndTaxVO vo);
    YearEndTaxVO findYearEnd(@Param("userId") Long userId, @Param("taxYear") int taxYear);
    List<YearEndTaxVO> listYearEnd(@Param("taxYear") int taxYear);

    /* ===== 퇴직정산 ===== */
    int insertSeverance(SeveranceVO vo);
    int updateSeveranceStatus(@Param("sevId") Long sevId,
                              @Param("statusCd") String statusCd,
                              @Param("paidDt") java.time.LocalDate paidDt);
    SeveranceVO findSeverance(@Param("sevId") Long sevId);
    List<SeveranceVO> listSeverance();
}
