package egovframework.groupware.leave.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LeaveRequestVO extends BaseVO {
    private Long leaveId;
    private Long userId;
    private String userName;
    private String leaveTypeCd;
    private LocalDate startDt;
    private LocalDate endDt;
    private BigDecimal days;
    private String reason;
    /** DRAFT | IN_PROGRESS | APPROVED | REJECTED | CANCELED */
    private String statusCd;
    private Long approvalDocId;
}
