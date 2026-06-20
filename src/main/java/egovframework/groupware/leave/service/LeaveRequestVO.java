package egovframework.groupware.leave.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class LeaveRequestVO extends BaseVO {
    private Long leaveId;
    private Long userId;
    private String userName;
    private String leaveTypeCd;
    private LocalDate startDt;
    private LocalDate endDt;
    /** 시간연차일 때만 사용 (예: 2026-05-27T13:00). 그 외에는 null. */
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private BigDecimal days;
    private String reason;
    /** 반차 구분 (HALF_TYPE): AM(오전) / PM(오후). HALF 가 아니면 null. */
    private String halfTypeCd;
    /** DRAFT | IN_PROGRESS | APPROVED | REJECTED | CANCELED */
    private String statusCd;
    private Long approvalDocId;
}
