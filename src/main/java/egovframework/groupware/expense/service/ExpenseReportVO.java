package egovframework.groupware.expense.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ExpenseReportVO extends BaseVO {
    private Long reportId;
    private String reportNo;
    private Long drafterId;
    private String drafterName;
    private Long deptId;
    private String deptNm;
    private String title;
    private String purpose;
    private BigDecimal totalNet;
    private BigDecimal totalVat;
    private BigDecimal totalAmount;
    /** DRAFT | IN_APPROVAL | APPROVED | REJECTED | REIMBURSED | CANCELED */
    private String statusCd;
    private Long approvalDocId;
    private String reimburseRequiredYn;
    private Long reimbursePaymentId;
    private LocalDateTime reimbursedAt;
    private String memo;
    private Long attachGroupId;

    private List<ExpenseItemVO> items = new ArrayList<>();
}
