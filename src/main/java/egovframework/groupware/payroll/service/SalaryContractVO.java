package egovframework.groupware.payroll.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SalaryContractVO {
    private Long contractId;
    private Long userId;
    private String userName;
    private LocalDate startDt;
    private LocalDate endDt;
    private BigDecimal annualSalary;
    private BigDecimal monthlyBaseSal;
    private String divisionTypeCd;
    private Integer paymentDay;
    private String note;
}
