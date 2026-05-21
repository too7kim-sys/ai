package egovframework.groupware.invoice.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InvoiceVO extends BaseVO {
    private Long invoiceId;
    private String invoiceNo;
    /** OUT (매출/발행) | IN (매입/수령) */
    private String directionCd;
    private Long vendorId;
    private String vendorNm;
    private Long bizContractId;
    private String contractTitle;
    private Long scheduleId;
    private LocalDate issueDt;
    private LocalDate dueDt;
    private BigDecimal amountNet;
    private BigDecimal vatAmount;
    private BigDecimal amountTotal;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private String taxInvoiceYn;
    /** DRAFT | ISSUED | PARTIALLY_PAID | PAID | OVERDUE | CANCELED */
    private String statusCd;
    private Long ownerUserId;
    private String ownerName;
    private String memo;
    private Long attachGroupId;
}
