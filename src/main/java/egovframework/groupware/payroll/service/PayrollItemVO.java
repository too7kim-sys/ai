package egovframework.groupware.payroll.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PayrollItemVO {
    private Long itemId;
    private Long payId;
    /** PAYMENT | DEDUCTION */
    private String kindCd;
    private String codeVal;
    private String itemNm;
    private BigDecimal amount;
    /** Y(과세) / N(비과세) */
    private String taxableYn;
    /** Y(자동산출) / N(수동) */
    private String autoYn;
    private Integer sortNo;
    private String memo;
}
