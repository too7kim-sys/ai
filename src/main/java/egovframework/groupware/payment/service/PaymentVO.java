package egovframework.groupware.payment.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PaymentVO {
    private Long paymentId;
    private Long invoiceId;
    private String payTypeCd;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate payDt;
    private BigDecimal amount;
    private String methodCd;
    private String bankCd;
    private String bankAccount;
    private String counterpartNm;
    private String memo;
    private Long approvalDocId;
    private Long regUserId;
}
