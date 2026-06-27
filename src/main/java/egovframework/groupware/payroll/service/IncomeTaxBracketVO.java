package egovframework.groupware.payroll.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 월 과세급여 구간별 간이세액 1행. gw_income_tax_bracket 에 매년 누적.
 * 동일 effective_from 의 모든 구간에 같은 dependent/child 공제값이 들어간다(denormalized).
 */
@Getter
@Setter
public class IncomeTaxBracketVO {
    private Long bracketId;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private BigDecimal minTaxable;
    /** null = 최상위 구간(상한 없음) */
    private BigDecimal maxTaxable;
    private BigDecimal baseTax;
    private BigDecimal progressiveRate;
    private BigDecimal dependentDeduction;
    private BigDecimal childDeduction;
    private String note;
}
