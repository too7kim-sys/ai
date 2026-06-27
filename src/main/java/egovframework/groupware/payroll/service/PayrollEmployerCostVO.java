package egovframework.groupware.payroll.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PayrollEmployerCostVO {
    private Long payId;
    private String insuranceCd;
    private BigDecimal amount;
}
