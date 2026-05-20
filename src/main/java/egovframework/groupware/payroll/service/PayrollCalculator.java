package egovframework.groupware.payroll.service;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 한국 급여 자동 계산 (4대보험 + 소득세 간이세액 + 연장/야간/휴일 가산).
 *
 * 산식 요약 (자세한 표는 docs/security.md 또는 README 참조):
 *   통상시급 = (BASE + 정기수당 과세분) / 209h
 *   연장수당  = 통상시급 × OT시간 × 1.5
 *   야간가산  = 통상시급 × 야간시간 × 0.5
 *   휴일수당  = 통상시급 × 휴일시간 × 1.5
 *   보수월액  = 과세 지급액 (식대/차량/보육/연구비 비과세 한도 차감 후)
 *   NP        = clamp(보수월액, 하한, 상한) × employee_rate
 *   HI        = 보수월액 × employee_rate
 *   LTC       = HI × employee_rate
 *   EI        = 보수총액(과세) × employee_rate
 *   소득세    = 간이세액 (구간별 누진)
 *   지방세    = 소득세 × 10%
 *
 * 본 계산기는 단일 책임으로, 입력만 받아 PayrollCalculationResult 반환.
 * 요율(InsuranceRateVO)과 비과세 한도(NonTaxableLimit)는 외부에서 주입.
 */
public class PayrollCalculator {

    public static final BigDecimal MONTH_WORK_HOURS = new BigDecimal("209");
    public static final BigDecimal OT_RATE = new BigDecimal("1.5");
    public static final BigDecimal NIGHT_RATE = new BigDecimal("0.5");
    public static final BigDecimal HOLIDAY_RATE = new BigDecimal("1.5");
    public static final BigDecimal LOCAL_TAX_RATE = new BigDecimal("0.10");

    /** 비과세 한도(월) - 항목별 */
    public static final Map<String, BigDecimal> NON_TAXABLE_LIMIT = new HashMap<>();
    static {
        NON_TAXABLE_LIMIT.put("MEAL",      new BigDecimal("200000"));
        NON_TAXABLE_LIMIT.put("VEHICLE",   new BigDecimal("200000"));
        NON_TAXABLE_LIMIT.put("CHILDCARE", new BigDecimal("200000"));
        NON_TAXABLE_LIMIT.put("RESEARCH",  new BigDecimal("200000"));
    }

    @Getter
    public static class Input {
        public BigDecimal monthlyBaseSal = BigDecimal.ZERO;
        /** 코드별 수동 입력 지급 (예: POSITION_ALLOW=500000, MEAL=200000) */
        public Map<String, BigDecimal> manualPayments = new HashMap<>();
        public int otMin = 0;
        public int nightMin = 0;
        public int holidayMin = 0;
        public int dependents = 1;     // 본인 포함
        public int childrenUnder20 = 0;
        public List<InsuranceRateVO> rates = new ArrayList<>();

        public Input baseSalary(BigDecimal v) { this.monthlyBaseSal = v; return this; }
        public Input manual(String code, BigDecimal v) { this.manualPayments.put(code, v); return this; }
        public Input overtime(int otMin, int nightMin, int holidayMin) {
            this.otMin = otMin; this.nightMin = nightMin; this.holidayMin = holidayMin; return this;
        }
        public Input family(int dependents, int childrenUnder20) {
            this.dependents = dependents; this.childrenUnder20 = childrenUnder20; return this;
        }
        public Input withRates(List<InsuranceRateVO> rates) { this.rates = rates; return this; }
    }

    @Getter
    public static class Result {
        public List<PayrollItemVO> payments = new ArrayList<>();
        public List<PayrollItemVO> deductions = new ArrayList<>();
        public List<PayrollEmployerCostVO> employerCosts = new ArrayList<>();
        public BigDecimal taxablePay = BigDecimal.ZERO;
        public BigDecimal nonTaxablePay = BigDecimal.ZERO;
        public BigDecimal grossPay = BigDecimal.ZERO;
        public BigDecimal deductionTotal = BigDecimal.ZERO;
        public BigDecimal netPay = BigDecimal.ZERO;
    }

    public Result calculate(Input in) {
        Result r = new Result();

        // 1) 기본급
        addPayment(r, "BASE", "기본급", in.monthlyBaseSal, true);

        // 2) 통상임금 = 기본급 (단순화). 통상시급 계산용.
        BigDecimal regularPay = nz(in.monthlyBaseSal);
        BigDecimal hourly = regularPay.divide(MONTH_WORK_HOURS, 0, RoundingMode.HALF_UP);

        // 3) 연장/야간/휴일 가산 (과세)
        if (in.otMin > 0) {
            BigDecimal v = hourly.multiply(BigDecimal.valueOf(in.otMin))
                                 .divide(BigDecimal.valueOf(60), 0, RoundingMode.HALF_UP)
                                 .multiply(OT_RATE).setScale(0, RoundingMode.HALF_UP);
            addPayment(r, "OVERTIME", "연장근로수당", v, true);
        }
        if (in.nightMin > 0) {
            BigDecimal v = hourly.multiply(BigDecimal.valueOf(in.nightMin))
                                 .divide(BigDecimal.valueOf(60), 0, RoundingMode.HALF_UP)
                                 .multiply(NIGHT_RATE).setScale(0, RoundingMode.HALF_UP);
            addPayment(r, "NIGHT_WORK", "야간근로수당", v, true);
        }
        if (in.holidayMin > 0) {
            BigDecimal v = hourly.multiply(BigDecimal.valueOf(in.holidayMin))
                                 .divide(BigDecimal.valueOf(60), 0, RoundingMode.HALF_UP)
                                 .multiply(HOLIDAY_RATE).setScale(0, RoundingMode.HALF_UP);
            addPayment(r, "HOLIDAY_WORK", "휴일근로수당", v, true);
        }

        // 4) 수동 입력 지급 항목 (직책수당/식대/자가운전/명절상여 등)
        for (Map.Entry<String, BigDecimal> e : in.manualPayments.entrySet()) {
            String code = e.getKey();
            BigDecimal amount = nz(e.getValue());
            if (amount.signum() <= 0) continue;
            String name = codeName(code);
            if (NON_TAXABLE_LIMIT.containsKey(code)) {
                BigDecimal limit = NON_TAXABLE_LIMIT.get(code);
                BigDecimal nontax = amount.min(limit);
                BigDecimal tax = amount.subtract(nontax);
                if (nontax.signum() > 0) addPayment(r, code, name, nontax, false);
                if (tax.signum() > 0)    addPayment(r, code + "_TAX", name + "(과세분)", tax, true);
            } else {
                addPayment(r, code, name, amount, true); // 기본 과세
            }
        }

        // 5) 합계 (지급)
        for (PayrollItemVO p : r.payments) {
            r.grossPay = r.grossPay.add(p.getAmount());
            if ("Y".equals(p.getTaxableYn())) r.taxablePay = r.taxablePay.add(p.getAmount());
            else r.nonTaxablePay = r.nonTaxablePay.add(p.getAmount());
        }

        // 6) 4대보험 공제
        BigDecimal baseForNpHi = r.taxablePay; // 보수월액 (단순화)
        BigDecimal np  = applyRate(in.rates, "NP",  baseForNpHi, true);
        BigDecimal hi  = applyRate(in.rates, "HI",  baseForNpHi, false);
        BigDecimal ltc = hi.multiply(rate(in.rates, "LTC")).setScale(0, RoundingMode.HALF_UP);
        BigDecimal ei  = baseForNpHi.multiply(rate(in.rates, "EI")).setScale(0, RoundingMode.HALF_UP);

        addDeduction(r, "NP",  "국민연금",   np);
        addDeduction(r, "HI",  "건강보험",   hi);
        addDeduction(r, "LTC", "장기요양",   ltc);
        addDeduction(r, "EI",  "고용보험",   ei);

        // 7) 회사 부담분
        BigDecimal npE  = applyRateEmployer(in.rates, "NP",  baseForNpHi, true);
        BigDecimal hiE  = applyRateEmployer(in.rates, "HI",  baseForNpHi, false);
        BigDecimal ltcE = hiE.multiply(rateEmployer(in.rates, "LTC")).setScale(0, RoundingMode.HALF_UP);
        BigDecimal eiE  = baseForNpHi.multiply(rateEmployer(in.rates, "EI")).setScale(0, RoundingMode.HALF_UP);
        BigDecimal wcE  = baseForNpHi.multiply(rateEmployer(in.rates, "WC")).setScale(0, RoundingMode.HALF_UP);
        addEmployer(r, "NP",  npE);
        addEmployer(r, "HI",  hiE);
        addEmployer(r, "LTC", ltcE);
        addEmployer(r, "EI",  eiE);
        addEmployer(r, "WC",  wcE);

        // 8) 소득세 간이세액 + 지방소득세
        BigDecimal incomeTax = simplifiedIncomeTax(r.taxablePay, in.dependents, in.childrenUnder20);
        BigDecimal localTax  = incomeTax.multiply(LOCAL_TAX_RATE).setScale(0, RoundingMode.HALF_UP);
        addDeduction(r, "INCOME_TAX",       "소득세",     incomeTax);
        addDeduction(r, "LOCAL_INCOME_TAX", "지방소득세", localTax);

        // 9) 합계 (공제)
        for (PayrollItemVO d : r.deductions) r.deductionTotal = r.deductionTotal.add(d.getAmount());
        r.netPay = r.grossPay.subtract(r.deductionTotal);
        return r;
    }

    /**
     * 간이세액표(단순화). 부양가족 1인당 8천원, 자녀 1인당 추가 1만원 공제.
     * 실제 국세청 표는 PDF로 별도 매핑 가능.
     */
    public BigDecimal simplifiedIncomeTax(BigDecimal taxable, int dependents, int children) {
        long v = taxable == null ? 0 : taxable.longValueExact();
        if (v <= 0) return BigDecimal.ZERO;
        long tax;
        if (v < 2_000_000)       tax = 0;
        else if (v < 3_000_000)  tax = (long) ((v - 2_000_000) * 0.06);
        else if (v < 5_000_000)  tax = 60_000  + (long) ((v - 3_000_000) * 0.15);
        else if (v < 8_000_000)  tax = 360_000 + (long) ((v - 5_000_000) * 0.24);
        else if (v < 15_000_000) tax = 1_080_000 + (long) ((v - 8_000_000) * 0.35);
        else                      tax = 3_530_000 + (long) ((v - 15_000_000) * 0.38);
        tax -= Math.max(0, dependents) * 8_000L;
        tax -= Math.max(0, children) * 10_000L;
        return BigDecimal.valueOf(Math.max(0, tax));
    }

    private void addPayment(Result r, String code, String name, BigDecimal amount, boolean taxable) {
        PayrollItemVO i = new PayrollItemVO();
        i.setKindCd("PAYMENT"); i.setCodeVal(code); i.setItemNm(name);
        i.setAmount(amount); i.setTaxableYn(taxable ? "Y" : "N"); i.setAutoYn("Y");
        i.setSortNo(r.payments.size() + 1);
        r.payments.add(i);
    }

    private void addDeduction(Result r, String code, String name, BigDecimal amount) {
        PayrollItemVO i = new PayrollItemVO();
        i.setKindCd("DEDUCTION"); i.setCodeVal(code); i.setItemNm(name);
        i.setAmount(amount); i.setTaxableYn("N"); i.setAutoYn("Y");
        i.setSortNo(r.deductions.size() + 1);
        r.deductions.add(i);
    }

    private void addEmployer(Result r, String code, BigDecimal amount) {
        PayrollEmployerCostVO e = new PayrollEmployerCostVO();
        e.setInsuranceCd(code); e.setAmount(amount);
        r.employerCosts.add(e);
    }

    private BigDecimal applyRate(List<InsuranceRateVO> rates, String cd, BigDecimal base, boolean useBounds) {
        InsuranceRateVO r = findRate(rates, cd);
        if (r == null) return BigDecimal.ZERO;
        BigDecimal b = base == null ? BigDecimal.ZERO : base;
        if (useBounds) {
            if (r.getBaseMin() != null && b.compareTo(r.getBaseMin()) < 0) b = r.getBaseMin();
            if (r.getBaseMax() != null && b.compareTo(r.getBaseMax()) > 0) b = r.getBaseMax();
        }
        return b.multiply(r.getEmployeeRate()).setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal applyRateEmployer(List<InsuranceRateVO> rates, String cd, BigDecimal base, boolean useBounds) {
        InsuranceRateVO r = findRate(rates, cd);
        if (r == null) return BigDecimal.ZERO;
        BigDecimal b = base == null ? BigDecimal.ZERO : base;
        if (useBounds) {
            if (r.getBaseMin() != null && b.compareTo(r.getBaseMin()) < 0) b = r.getBaseMin();
            if (r.getBaseMax() != null && b.compareTo(r.getBaseMax()) > 0) b = r.getBaseMax();
        }
        return b.multiply(r.getEmployerRate()).setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal rate(List<InsuranceRateVO> rates, String cd) {
        InsuranceRateVO r = findRate(rates, cd);
        return r == null ? BigDecimal.ZERO : r.getEmployeeRate();
    }
    private BigDecimal rateEmployer(List<InsuranceRateVO> rates, String cd) {
        InsuranceRateVO r = findRate(rates, cd);
        return r == null ? BigDecimal.ZERO : r.getEmployerRate();
    }

    private InsuranceRateVO findRate(List<InsuranceRateVO> rates, String cd) {
        if (rates == null) return null;
        return rates.stream().filter(r -> cd.equals(r.getInsuranceCd())).findFirst().orElse(null);
    }

    private BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }

    private String codeName(String code) {
        return switch (code) {
            case "POSITION_ALLOW" -> "직책수당";
            case "JOB_ALLOW" -> "직무수당";
            case "MEAL" -> "식대";
            case "VEHICLE" -> "자가운전보조금";
            case "CHILDCARE" -> "출산보육수당";
            case "RESEARCH" -> "연구활동비";
            case "FAMILY" -> "가족수당";
            case "EDUCATION" -> "자녀학자금";
            case "REGULAR_BONUS" -> "정기상여";
            case "HOLIDAY_BONUS" -> "명절상여";
            case "PERFORMANCE_BONUS" -> "성과급";
            case "LONG_SERVICE" -> "장기근속수당";
            case "ETC_ALLOW" -> "기타수당";
            default -> code;
        };
    }
}
