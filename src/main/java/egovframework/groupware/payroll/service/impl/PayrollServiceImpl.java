package egovframework.groupware.payroll.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.mail.service.MailAttachment;
import egovframework.groupware.mail.service.MailRequest;
import egovframework.groupware.mail.service.MailService;
import egovframework.groupware.payroll.mapper.BonusMapper;
import egovframework.groupware.payroll.mapper.PayrollMapper;
import egovframework.groupware.payroll.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PayrollServiceImpl implements PayrollService {

    private static final Logger log = LoggerFactory.getLogger(PayrollServiceImpl.class);

    /** 월 일할 계산 비율의 소수 자리수 (백만분율). */
    private static final int    PRORATION_SCALE = 6;
    /** 화폐 금액의 반올림 자리수 (원 단위 정수). */
    private static final int    MONEY_SCALE = 0;
    /** 근태 집계가 없을 때 사용하는 기본 영업일수. */
    private static final BigDecimal DEFAULT_WORK_DAYS = BigDecimal.valueOf(22);

    private final PayrollMapper mapper;
    private final BonusMapper bonusMapper;
    private final PayslipPdfWriter pdfWriter;
    private final MailService mailService;
    private final PayrollCalculator calculator = new PayrollCalculator();
    private static final NumberFormat WON = NumberFormat.getNumberInstance(Locale.KOREA);

    public PayrollServiceImpl(PayrollMapper mapper, BonusMapper bonusMapper,
                              PayslipPdfWriter pdfWriter, MailService mailService) {
        this.mapper = mapper;
        this.bonusMapper = bonusMapper;
        this.pdfWriter = pdfWriter;
        this.mailService = mailService;
    }

    @Override
    @Transactional
    public int generateForMonth(String payMonth) {
        YearMonth ym = YearMonth.parse(payMonth);
        LocalDate first = ym.atDay(1);
        LocalDate last  = ym.atEndOfMonth();
        int created = 0;

        // resign_date < 급여월 첫날 인 사용자는 SQL 단계에서 제외.
        // 입사 월(hire_date) 과 퇴사 월(resign_date) 모두 재직일수/월일수로 일할 계산.
        for (Map<String, Object> row : mapper.findPayrollEligibleUsers(first)) {
            Long userId = ((Number) row.get("userId")).longValue();
            LocalDate hireDate   = toLocalDate(row.get("hireDate"));
            LocalDate resignDate = toLocalDate(row.get("resignDate"));

            if (mapper.findByUserAndMonth(userId, payMonth) != null) continue;
            SalaryContractVO sc = mapper.findCurrentContract(userId, last);
            BigDecimal contractBase = (sc == null) ? BigDecimal.ZERO : sc.getMonthlyBaseSal();

            Prorated pr = prorate(contractBase, hireDate, resignDate, first, last);
            if (pr.prorated) {
                log.info("Payroll proration uid={} {} hire={} resign={} days={} base {} → {}",
                        userId, payMonth, hireDate, resignDate, pr.workDays, contractBase, pr.base);
            }

            PayrollVO p = new PayrollVO();
            p.setUserId(userId);
            p.setPayMonth(payMonth);
            p.setContractId(sc == null ? null : sc.getContractId());
            p.setStatusCd("DRAFT");
            p.setWorkDays(pr.workDays);
            mapper.insertPayroll(p);
            recalculateInternal(p.getPayId(), Map.of(), pr.base);
            created++;
        }
        return created;
    }

    @Override
    @Transactional
    public PayrollVO recalculate(Long payId, Map<String, Long> manualPayments) {
        PayrollVO p = mapper.findPayroll(payId);
        if (p == null) throw new ApiException("PAYROLL_NOT_FOUND", "급여 명세를 찾을 수 없습니다");
        if ("PAID".equals(p.getStatusCd())) {
            throw new ApiException("PAYROLL_LOCKED", "지급 완료된 명세는 수정할 수 없습니다");
        }
        // generateForMonth 와 같은 prorate 규칙을 사용해 일할 계산이 재계산에도 일관 적용되도록 한다.
        YearMonth ym = YearMonth.parse(p.getPayMonth());
        LocalDate first = ym.atDay(1);
        LocalDate last  = ym.atEndOfMonth();
        SalaryContractVO sc = mapper.findCurrentContract(p.getUserId(), last);
        BigDecimal contractBase = (sc == null) ? BigDecimal.ZERO : sc.getMonthlyBaseSal();

        Map<String, Object> u = mapper.findUserHireResign(p.getUserId());
        LocalDate hireDate   = (u == null) ? null : toLocalDate(u.get("hireDate"));
        LocalDate resignDate = (u == null) ? null : toLocalDate(u.get("resignDate"));
        Prorated pr = prorate(contractBase, hireDate, resignDate, first, last);

        return recalculateInternal(payId, manualPayments == null ? Map.of() : manualPayments, pr.base);
    }

    /** 입사일·퇴사일을 모두 반영한 월급 일할 계산 결과. */
    private static final class Prorated {
        final BigDecimal base;
        final BigDecimal workDays;
        final boolean prorated;
        Prorated(BigDecimal base, BigDecimal workDays, boolean prorated) {
            this.base = base; this.workDays = workDays; this.prorated = prorated;
        }
    }

    /**
     * 입사일·퇴사일을 모두 고려한 일할 계산.
     * <ul>
     *   <li>입사일이 급여월 첫날보다 늦으면 그 입사일부터 카운트</li>
     *   <li>퇴사일이 급여월 마지막날보다 이르면 그 퇴사일까지 카운트</li>
     *   <li>재직일수가 월 일수와 같으면 일할 적용 안 함 (기본 영업일 22 일 사용)</li>
     * </ul>
     */
    private static Prorated prorate(BigDecimal base, LocalDate hireDate, LocalDate resignDate,
                                    LocalDate first, LocalDate last) {
        LocalDate start = (hireDate   != null && hireDate.isAfter(first))   ? hireDate   : first;
        LocalDate end   = (resignDate != null && resignDate.isBefore(last)) ? resignDate : last;
        if (end.isBefore(start)) {
            // 입사 후 즉시 퇴사 등 경계 케이스 — 0 일.
            return new Prorated(BigDecimal.ZERO, BigDecimal.ZERO, true);
        }
        int monthDays  = last.getDayOfMonth();
        int daysWorked = (int) ChronoUnit.DAYS.between(start, end) + 1;
        if (daysWorked >= monthDays) {
            return new Prorated(base, DEFAULT_WORK_DAYS, false);
        }
        BigDecimal proration = BigDecimal.valueOf(daysWorked)
                .divide(BigDecimal.valueOf(monthDays), PRORATION_SCALE, RoundingMode.HALF_UP);
        BigDecimal proratedBase = base.multiply(proration)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        return new Prorated(proratedBase, BigDecimal.valueOf(daysWorked), true);
    }

    private static LocalDate toLocalDate(Object o) {
        if (o == null) return null;
        if (o instanceof LocalDate) return (LocalDate) o;
        if (o instanceof java.sql.Date) return ((java.sql.Date) o).toLocalDate();
        if (o instanceof java.util.Date) {
            return ((java.util.Date) o).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        }
        return LocalDate.parse(o.toString());
    }

    private PayrollVO recalculateInternal(Long payId, Map<String, Long> manualPayments, BigDecimal base) {
        PayrollVO p = mapper.findPayroll(payId);

        // 1) 적용 시점 결정 — 급여월 1일 기준으로 그 시점에 유효한 요율/세액표를 조회한다.
        //    급여를 과거 시점으로 재계산할 때도 그 시점의 요율이 적용되도록(연도별 변경 대응).
        LocalDate effectiveOn = (p.getPayMonth() != null)
                ? YearMonth.parse(p.getPayMonth()).atDay(1)
                : LocalDate.now();
        List<InsuranceRateVO> rates = mapper.findActiveRates(effectiveOn);
        List<IncomeTaxBracketVO> brackets = mapper.findActiveTaxBrackets(effectiveOn);

        // 2) 근태 자동 집계 — 해당 급여월의 연장/야간/휴일 분, 근무일/결근일
        applyAttendance(p);

        // 3) 수동 입력 + 해당 월 상여(PLANNED)를 지급 항목으로 병합
        Map<String, Long> payments = new HashMap<>(manualPayments == null ? Map.of() : manualPayments);
        mergeBonuses(p, payments);

        // 4) 부양가족 자동 카운트 — gw_family 에서 dependent_yn='Y' 합 + 본인(+1).
        //    20세 이하 자녀는 급여월 1일 기준 만 나이로 cutoff 일자를 계산해 birth_dt 비교.
        int dependents = 1 + mapper.countDependents(p.getUserId());
        int childrenUnder20 = mapper.countChildrenUnder20(p.getUserId(), effectiveOn.minusYears(20));

        PayrollCalculator.Input in = new PayrollCalculator.Input()
                .baseSalary(base)
                .overtime(nzi(p.getOtMin()), nzi(p.getNightMin()), nzi(p.getHolidayMin()))
                .family(dependents, childrenUnder20)
                .withRates(rates)
                .withTaxBrackets(brackets);
        for (Map.Entry<String, Long> e : payments.entrySet()) {
            in.manual(e.getKey(), BigDecimal.valueOf(e.getValue()));
        }
        PayrollCalculator.Result res = calculator.calculate(in);

        // persist
        mapper.deleteItems(payId);
        mapper.deleteEmployerCosts(payId);
        for (PayrollItemVO it : res.payments) { it.setPayId(payId); mapper.insertItem(it); }
        for (PayrollItemVO it : res.deductions) { it.setPayId(payId); mapper.insertItem(it); }
        for (PayrollEmployerCostVO c : res.employerCosts) { c.setPayId(payId); mapper.insertEmployerCost(c); }

        p.setTaxablePay(res.taxablePay);
        p.setNonTaxablePay(res.nonTaxablePay);
        p.setGrossPay(res.grossPay);
        p.setDeductionTotal(res.deductionTotal);
        p.setNetPay(res.netPay);
        mapper.updatePayroll(p);

        return findPayrollWithDetails(payId);
    }

    /** 급여월의 근태 데이터를 집계해 PayrollVO 의 연장/야간/휴일·근무일·결근일에 반영. */
    private void applyAttendance(PayrollVO p) {
        if (p.getPayMonth() == null) return;
        YearMonth ym = YearMonth.parse(p.getPayMonth());
        Map<String, Object> agg = mapper.sumAttendance(
                p.getUserId(), ym.atDay(1), ym.atEndOfMonth());
        if (agg == null) return;
        p.setOtMin(toInt(agg.get("ot_min")));
        p.setNightMin(toInt(agg.get("night_min")));
        p.setHolidayMin(toInt(agg.get("holiday_min")));
        int workDays = toInt(agg.get("work_days"));
        if (workDays > 0) p.setWorkDays(BigDecimal.valueOf(workDays));
        p.setAbsentDays(BigDecimal.valueOf(toInt(agg.get("absent_days"))));
    }

    /**
     * 해당 급여월의 PLANNED 상여를 지급 코드별 금액으로 병합.
     * 매핑: REGULAR→REGULAR_BONUS, HOLIDAY→HOLIDAY_BONUS, PERFORMANCE→PERFORMANCE_BONUS,
     *       SPECIAL→ETC_ALLOW. 그 외(스키마 확장으로 새 코드가 들어온 경우)는 운영자가
     *       인지할 수 있도록 WARN 로깅 + 안전한 ETC_ALLOW 로 분류한다.
     */
    private void mergeBonuses(PayrollVO p, Map<String, Long> payments) {
        if (p.getPayMonth() == null) return;
        for (BonusVO b : bonusMapper.findPlannedByUserMonth(p.getUserId(), p.getPayMonth())) {
            String typeCd = b.getBonusTypeCd();
            String code = switch (typeCd == null ? "" : typeCd) {
                case "REGULAR"     -> "REGULAR_BONUS";
                case "HOLIDAY"     -> "HOLIDAY_BONUS";
                case "PERFORMANCE" -> "PERFORMANCE_BONUS";
                case "SPECIAL"     -> "ETC_ALLOW";
                default -> {
                    log.warn("Unknown bonus_type_cd='{}' (bonusId={}) — ETC_ALLOW 로 분류. "
                            + "신규 코드라면 mergeBonuses 의 매핑을 업데이트하세요.",
                            typeCd, b.getBonusId());
                    yield "ETC_ALLOW";
                }
            };
            long amt = b.getAmount() == null ? 0 : b.getAmount().longValueExact();
            payments.merge(code, amt, Long::sum);
        }
    }

    private int toInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(o.toString()); } catch (Exception e) { return 0; }
    }

    @Override
    public PayrollVO findPayroll(Long payId) { return mapper.findPayroll(payId); }

    @Override
    public PayrollVO findByUserAndMonth(Long userId, String payMonth) {
        return mapper.findByUserAndMonth(userId, payMonth);
    }

    @Override
    public List<PayrollVO> listPayrolls(String payMonth, String status, int offset, int limit) {
        return mapper.listPayrolls(payMonth, status, offset, limit);
    }

    @Override
    public long countPayrolls(String payMonth, String status) {
        return mapper.countPayrolls(payMonth, status);
    }

    @Override
    public List<Map<String, Object>> deptCostSummary(String payMonth) {
        return mapper.sumByDept(payMonth);
    }

    @Override
    public List<PayrollVO> listMyPayrolls(Long userId) { return mapper.listMyPayrolls(userId); }

    @Override
    @Transactional
    public void confirm(Long payId) {
        PayrollVO p = mapper.findPayroll(payId);
        mapper.updateStatus(payId, "CONFIRMED", null);
        // 확정 시 반영된 상여를 APPLIED 로 마킹
        if (p != null && p.getPayMonth() != null) {
            bonusMapper.markApplied(p.getUserId(), p.getPayMonth());
        }
    }

    @Override
    @Transactional
    public void markPaid(Long payId, LocalDate paidDt) {
        mapper.updateStatus(payId, "PAID", paidDt == null ? LocalDate.now() : paidDt);
    }

    @Override
    public SalaryContractVO findCurrentContract(Long userId, LocalDate on) {
        return mapper.findCurrentContract(userId, on);
    }

    @Override
    public List<SalaryContractVO> listContracts(Long userId) { return mapper.listContracts(userId); }

    @Override
    @Transactional
    public Long createContract(SalaryContractVO vo) {
        if (vo.getMonthlyBaseSal() == null && vo.getAnnualSalary() != null) {
            vo.setMonthlyBaseSal(vo.getAnnualSalary().divide(BigDecimal.valueOf(12), 0, BigDecimal.ROUND_HALF_UP));
        }
        mapper.insertContract(vo);
        return vo.getContractId();
    }

    @Override
    public List<InsuranceRateVO> findActiveRates(LocalDate on) {
        return mapper.findActiveRates(on);
    }

    @Override public List<InsuranceRateVO> listAllRates() { return mapper.listAllRates(); }
    @Override public InsuranceRateVO findRate(Long rateId) { return mapper.findRate(rateId); }

    @Override
    @Transactional
    public Long createRate(InsuranceRateVO vo) {
        validateRate(vo);
        mapper.insertRate(vo);
        return vo.getRateId();
    }

    @Override
    @Transactional
    public void updateRate(InsuranceRateVO vo) {
        if (vo.getRateId() == null) throw new ApiException("INVALID", "rateId 가 필요합니다");
        validateRate(vo);
        if (mapper.updateRate(vo) != 1) {
            throw new ApiException("NOT_FOUND", "요율을 찾을 수 없습니다");
        }
    }

    @Override
    @Transactional
    public void deleteRate(Long rateId) {
        if (mapper.deleteRate(rateId) != 1) {
            throw new ApiException("NOT_FOUND", "요율을 찾을 수 없습니다");
        }
    }

    private static void validateRate(InsuranceRateVO vo) {
        if (vo.getInsuranceCd() == null || vo.getInsuranceCd().isBlank())
            throw new ApiException("INVALID", "보험 코드(NP/HI/LTC/EI/WC)가 필요합니다");
        if (vo.getEffectiveFrom() == null)
            throw new ApiException("INVALID", "적용 시작일이 필요합니다");
        if (vo.getEmployeeRate() == null) vo.setEmployeeRate(BigDecimal.ZERO);
        if (vo.getEmployerRate() == null) vo.setEmployerRate(BigDecimal.ZERO);
        if (vo.getEffectiveTo() != null && vo.getEffectiveTo().isBefore(vo.getEffectiveFrom()))
            throw new ApiException("INVALID", "종료일이 시작일보다 빠를 수 없습니다");
    }

    @Override
    public PayrollVO findPayrollWithDetails(Long payId) {
        PayrollVO p = mapper.findPayroll(payId);
        if (p == null) return null;
        p.getItems().addAll(mapper.findItems(payId));
        p.getEmployerCosts().addAll(mapper.findEmployerCosts(payId));
        return p;
    }

    @Override
    public byte[] generatePayslipPdf(Long payId) {
        PayrollVO p = findPayrollWithDetails(payId);
        if (p == null) throw new ApiException("PAYROLL_NOT_FOUND", "급여 명세를 찾을 수 없습니다");
        return pdfWriter.write(p);
    }

    @Override
    @Transactional
    public List<Long> sendPayslipsByMail(List<Long> payIds) {
        List<Long> mailIds = new ArrayList<>();
        for (Long payId : payIds) {
            PayrollVO p = findPayrollWithDetails(payId);
            if (p == null) continue;
            if (!"CONFIRMED".equals(p.getStatusCd()) && !"PAID".equals(p.getStatusCd())) {
                continue; // 확정된 명세만 발송
            }
            if (p.getEmail() == null || p.getEmail().isBlank()) continue;

            byte[] pdf = pdfWriter.write(p);
            MailRequest req = MailRequest.builder()
                .templateCd("PAYSLIP_NOTICE")
                .to(List.of(p.getEmail()))
                .vars(Map.of(
                    "userName", p.getUserName(),
                    "payMonth", p.getPayMonth(),
                    "netPay",   WON.format(p.getNetPay())
                ))
                .attachments(List.of(new MailAttachment(
                    "payslip-" + p.getPayMonth() + "-" + p.getUserName() + ".pdf",
                    pdf, "application/pdf")))
                .relatedEntity("PAYROLL")
                .relatedId(String.valueOf(payId))
                .build();
            mailIds.add(mailService.enqueue(req));
        }
        return mailIds;
    }

    private int nzi(Integer i) { return i == null ? 0 : i; }
}
