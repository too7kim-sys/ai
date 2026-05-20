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
}
