package egovframework.groupware.expense.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ExpenseItemVO {
    private Long itemId;
    private Long reportId;
    private Integer seqNo;
    private LocalDate expenseDt;
    private String categoryCd;
    private String accountCd;
    private String vendorNm;
    private String vendorBizNo;
    private Long linkedVendorId;
    private BigDecimal netAmount;
    private BigDecimal vatAmount;
    private BigDecimal totalAmount;
    /** CORPORATE_CARD | PERSONAL_CARD | CASH | BANK_TRANSFER */
    private String paymentMethodCd;
    private String cardLast4;
    /** TAX_INVOICE | CASH_RECEIPT | CARD | SIMPLE_RECEIPT | OTHER */
    private String receiptTypeCd;
    private Long linkedInvoiceId;
    private String projectCd;
    private String memo;
    private Long attachGroupId;
}
