package egovframework.groupware.payroll.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class YearEndTaxVO {
    private Long yetId;
    private Long userId;
    private String userName;
    private String deptNm;
    private Integer taxYear;
    private BigDecimal grossPay;          // 연간 총급여
    private BigDecimal taxablePay;        // 연간 과세대상
    private BigDecimal paidTax;           // 기납부 소득세
    private BigDecimal incomeDeduction;   // 소득공제 합계
    private BigDecimal taxCredit;         // 세액공제 합계
    private BigDecimal determinedTax;     // 결정세액
    private BigDecimal settledTax;        // (+)추징 / (-)환급
    /** DRAFT | CONFIRMED */
    private String statusCd;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
