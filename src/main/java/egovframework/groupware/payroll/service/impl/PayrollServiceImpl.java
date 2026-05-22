package egovframework.groupware.payroll.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.mail.service.MailAttachment;
import egovframework.groupware.mail.service.MailRequest;
import egovframework.groupware.mail.service.MailService;
import egovframework.groupware.payroll.mapper.BonusMapper;
import egovframework.groupware.payroll.mapper.PayrollMapper;
import egovframework.groupware.payroll.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PayrollServiceImpl implements PayrollService {

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
        LocalDate on = ym.atEndOfMonth();
        int created = 0;
        for (Long userId : mapper.findActiveUserIds()) {
            if (mapper.findByUserAndMonth(userId, payMonth) != null) continue;
            SalaryContractVO sc = mapper.findCurrentContract(userId, on);
            BigDecimal base = sc == null ? BigDecimal.ZERO : sc.getMonthlyBaseSal();
            PayrollVO p = new PayrollVO();
            p.setUserId(userId);
            p.setPayMonth(payMonth);
            p.setContractId(sc == null ? null : sc.getContractId());
            p.setStatusCd("DRAFT");
            p.setWorkDays(BigDecimal.valueOf(22));
            mapper.insertPayroll(p);
            // 자동 계산
            recalculateInternal(p.getPayId(), Map.of(), base);
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
        SalaryContractVO sc = mapper.findCurrentContract(p.getUserId(), LocalDate.now());
        BigDecimal base = sc == null ? BigDecimal.ZERO : sc.getMonthlyBaseSal();
        return recalculateInternal(payId, manualPayments == null ? Map.of() : manualPayments, base);
    }

    private PayrollVO recalculateInternal(Long payId, Map<String, Long> manualPayments, BigDecimal base) {
        PayrollVO p = mapper.findPayroll(payId);
        List<InsuranceRateVO> rates = mapper.findActiveRates(LocalDate.now());

        // 1) 근태 자동 집계 — 해당 급여월의 연장/야간/휴일 분, 근무일/결근일
        applyAttendance(p);

        // 2) 수동 입력 + 해당 월 상여(PLANNED)를 지급 항목으로 병합
        Map<String, Long> payments = new HashMap<>(manualPayments == null ? Map.of() : manualPayments);
        mergeBonuses(p, payments);

        PayrollCalculator.Input in = new PayrollCalculator.Input()
                .baseSalary(base)
                .overtime(nzi(p.getOtMin()), nzi(p.getNightMin()), nzi(p.getHolidayMin()))
                .family(1, 0)
                .withRates(rates);
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

    /** 해당 급여월의 PLANNED 상여를 지급 코드별 금액으로 병합 (REGULAR_BONUS/HOLIDAY_BONUS/PERFORMANCE_BONUS). */
    private void mergeBonuses(PayrollVO p, Map<String, Long> payments) {
        if (p.getPayMonth() == null) return;
        for (BonusVO b : bonusMapper.findPlannedByUserMonth(p.getUserId(), p.getPayMonth())) {
            String code = switch (b.getBonusTypeCd()) {
                case "HOLIDAY"     -> "HOLIDAY_BONUS";
                case "PERFORMANCE" -> "PERFORMANCE_BONUS";
                case "SPECIAL"     -> "ETC_ALLOW";
                default            -> "REGULAR_BONUS";
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
