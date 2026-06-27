package egovframework.groupware.bizcontract.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BillingScheduleVO {
    private Long scheduleId;
    private Long bizContractId;
    private Integer seqNo;
    private LocalDate dueDt;
    private BigDecimal amountNet;
    private BigDecimal vatAmount;
    private BigDecimal amountTotal;
    private String memo;
    /** PENDING | INVOICED | PAID | CANCELED */
    private String statusCd;
    private Long invoiceId;
}
