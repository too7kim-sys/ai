package egovframework.groupware.approval.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApprovalLineVO {
    private Long lineId;
    private Long docId;
    private Integer stepNo;
    private Long approverId;
    private String approverName;
    /** APPROVE | REVIEW | REFER */
    private String lineTypeCd;
    /** PENDING | APPROVED | REJECTED | SKIPPED */
    private String statusCd;
    private String comment;
    private LocalDateTime actedAt;
    private Long delegatedToUserId;
}
