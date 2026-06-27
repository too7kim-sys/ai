package egovframework.groupware.payroll.service;

import java.time.LocalDate;
import java.util.List;

public interface SettlementService {

    /* ===== 연말정산 ===== */

    /** 급여 데이터로 연말정산을 계산한다 (미저장 미리보기). */
    YearEndTaxVO computeYearEnd(Long userId, int taxYear);

    /** 연말정산 결과 저장/갱신. */
    Long saveYearEnd(YearEndTaxVO vo);

    YearEndTaxVO findYearEnd(Long userId, int taxYear);

    List<YearEndTaxVO> listYearEnd(int taxYear);

    void confirmYearEnd(Long userId, int taxYear);

    /* ===== 퇴직정산 ===== */

    /** 퇴직금을 계산한다 (미저장 미리보기). */
    SeveranceVO computeSeverance(Long userId, LocalDate leaveDate);

    Long saveSeverance(SeveranceVO vo, Long actorId);

    SeveranceVO findSeverance(Long sevId);

    List<SeveranceVO> listSeverance();

    void markSeverancePaid(Long sevId, LocalDate paidDt);
}
