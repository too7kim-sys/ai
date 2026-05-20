package egovframework.groupware.payroll.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InsuranceRateVO {
    private Long rateId;
    private String insuranceCd;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private BigDecimal employeeRate;
    private BigDecimal employerRate;
    private BigDecimal baseMin;
    private BigDecimal baseMax;
    private String note;
}
