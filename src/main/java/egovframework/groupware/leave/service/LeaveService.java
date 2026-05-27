package egovframework.groupware.leave.service;

import java.time.LocalDate;
import java.util.List;

public interface LeaveService {

    /** 휴가 신청 → 결재 자동 상신 (LEAVE 양식). */
    Long apply(Long userId, String leaveTypeCd, LocalDate start, LocalDate end,
               String reason, List<Long> approverIds);

    List<LeaveRequestVO> listMine(Long userId);

    LeaveBalanceVO findBalance(Long userId, int year);

    void grantInitialBalance(Long userId, int year, java.math.BigDecimal days);

    /* ===== HR 관리자용 ===== */

    /** 연도별 전 직원 부여/사용/잔여 목록. */
    List<LeaveBalanceRow> listBalances(int year, String keyword);

    /** 활성 직원 전원에게 지정 일수를 일괄 부여(이미 부여된 경우 덮어씀). 처리 인원 수 반환. */
    int grantAll(int year, java.math.BigDecimal days);

    /** 입사일 기반 한국 노동법 표준 연차를 일괄 부여(1년 미만 11일 / 1년 이상 15일 / 3년차부터 2년마다 +1, 최대 25일). */
    int grantByTenure(int year);
}
