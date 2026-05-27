package egovframework.groupware.leave.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LeaveBalanceRow {
    private Long userId;
    private String name;
    private String email;
    private String deptName;
    private LocalDate hireDate;
    private Integer year;
    private BigDecimal annualGiven;
    private BigDecimal annualUsed;

    public BigDecimal remaining() {
        BigDecimal g = annualGiven == null ? BigDecimal.ZERO : annualGiven;
        BigDecimal u = annualUsed == null ? BigDecimal.ZERO : annualUsed;
        return g.subtract(u);
    }
}
