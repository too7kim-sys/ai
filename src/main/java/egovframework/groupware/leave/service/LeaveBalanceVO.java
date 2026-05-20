package egovframework.groupware.leave.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LeaveBalanceVO {
    private Long userId;
    private Integer year;
    private BigDecimal annualGiven;
    private BigDecimal annualUsed;

    public BigDecimal remaining() {
        BigDecimal g = annualGiven == null ? BigDecimal.ZERO : annualGiven;
        BigDecimal u = annualUsed == null ? BigDecimal.ZERO : annualUsed;
        return g.subtract(u);
    }
}
