package egovframework.groupware.payroll.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.payroll.mapper.SettlementMapper;
import egovframework.groupware.payroll.service.SettlementService;
import egovframework.groupware.payroll.service.SeveranceVO;
import egovframework.groupware.payroll.service.YearEndTaxVO;
import egovframework.groupware.user.mapper.UserMapper;
import egovframework.groupware.user.service.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * 연말정산 / 퇴직정산. 세액 계산은 한국 세법을 단순화한 <b>간이 추정치</b>이며
 * 실제 신고는 국세청 정산 결과를 따른다.
 */
@Service
public class SettlementServiceImpl implements SettlementService {

    private final SettlementMapper mapper;
    private final UserMapper userMapper;

    public SettlementServiceImpl(SettlementMapper mapper, UserMapper userMapper) {
        this.mapper = mapper;
        this.userMapper = userMapper;
    }

    /* ============ 연말정산 ============ */

    @Override
    public YearEndTaxVO computeYearEnd(Long userId, int taxYear) {
        UserVO user = userMapper.findById(userId);
        if (user == null) throw new ApiException("NOT_FOUND", "직원을 찾을 수 없습니다");

        Map<String, Object> agg = mapper.sumYearlyPayroll(userId, taxYear);
        BigDecimal gross   = num(agg, "gross_pay");
        BigDecimal taxable = num(agg, "taxable_pay");
        BigDecimal paidTax = num(agg, "income_tax");

        // 근로소득공제
        BigDecimal earnedDeduction = earnedIncomeDeduction(gross);
        // 인적공제 (본인 150만 기본)
        BigDecimal personalDeduction = BigDecimal.valueOf(1_500_000);
        BigDecimal incomeDeduction = earnedDeduction.add(personalDeduction);

        // 과세표준 = 총급여 - 공제 (음수 방지)
        BigDecimal taxBase = gross.subtract(incomeDeduction).max(BigDecimal.ZERO);
        // 산출세액 (종합소득세율 누진)
        BigDecimal calculatedTax = progressiveTax(taxBase);
        // 근로소득 세액공제 (간이: 산출세액의 일부, 한도 74만)
        BigDecimal taxCredit = calculatedTax.multiply(BigDecimal.valueOf(0.55))
                .min(BigDecimal.valueOf(740_000)).setScale(0, RoundingMode.HALF_UP);
        // 결정세액
        BigDecimal determined = calculatedTax.subtract(taxCredit).max(BigDecimal.ZERO);
        // 정산세액 = 결정세액 - 기납부 ( (-) 환급 / (+) 추징 )
        BigDecimal settled = determined.subtract(paidTax);

        YearEndTaxVO vo = new YearEndTaxVO();
        vo.setUserId(userId);
        vo.setUserName(user.getName());
        vo.setDeptNm(user.getDeptNm());
        vo.setTaxYear(taxYear);
        vo.setGrossPay(gross);
        vo.setTaxablePay(taxable);
        vo.setPaidTax(paidTax);
        vo.setIncomeDeduction(incomeDeduction);
        vo.setTaxCredit(taxCredit);
        vo.setDeterminedTax(determined);
        vo.setSettledTax(settled);
        vo.setStatusCd("DRAFT");
        vo.setMemo("간이 추정치 — 근로소득공제·본인 인적공제만 반영. 실제는 국세청 정산 기준.");
        return vo;
    }

    @Override
    @Transactional
    public Long saveYearEnd(YearEndTaxVO vo) {
        YearEndTaxVO existing = mapper.findYearEnd(vo.getUserId(), vo.getTaxYear());
        if (existing == null) {
            mapper.insertYearEnd(vo);
            return vo.getYetId();
        }
        vo.setYetId(existing.getYetId());
        if (vo.getStatusCd() == null) vo.setStatusCd(existing.getStatusCd());
        mapper.updateYearEnd(vo);
        return existing.getYetId();
    }

    @Override
    public YearEndTaxVO findYearEnd(Long userId, int taxYear) {
        return mapper.findYearEnd(userId, taxYear);
    }

    @Override
    public List<YearEndTaxVO> listYearEnd(int taxYear) {
        return mapper.listYearEnd(taxYear);
    }

    @Override
    @Transactional
    public void confirmYearEnd(Long userId, int taxYear) {
        YearEndTaxVO vo = mapper.findYearEnd(userId, taxYear);
        if (vo == null) throw new ApiException("NOT_FOUND", "연말정산 내역이 없습니다. 먼저 계산·저장하세요");
        vo.setStatusCd("CONFIRMED");
        mapper.updateYearEnd(vo);
    }

    /* ============ 퇴직정산 ============ */

    @Override
    public SeveranceVO computeSeverance(Long userId, LocalDate leaveDate) {
        UserVO user = userMapper.findById(userId);
        if (user == null) throw new ApiException("NOT_FOUND", "직원을 찾을 수 없습니다");
        if (leaveDate == null) throw new ApiException("INVALID", "퇴직일을 입력하세요");
        LocalDate hire = user.getHireDate();
        if (hire == null) throw new ApiException("INVALID", "입사일 정보가 없습니다");
        if (leaveDate.isBefore(hire)) throw new ApiException("INVALID_DATE", "퇴직일이 입사일보다 빠릅니다");

        long serviceDays = ChronoUnit.DAYS.between(hire, leaveDate);

        // 평균임금 = 최근 3개월 급여 총액 / 3
        Map<String, Object> agg = mapper.sumRecentPayroll(userId, 3);
        BigDecimal grossSum = num(agg, "gross_sum");
        int months = ((Number) agg.getOrDefault("months", 0)).intValue();
        BigDecimal avgMonthly = months > 0
                ? grossSum.divide(BigDecimal.valueOf(months), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        // 1일 평균임금 ≈ 월평균 / 30
        BigDecimal avgDaily = avgMonthly.divide(BigDecimal.valueOf(30), 0, RoundingMode.HALF_UP);

        // 퇴직금 = 1일 평균임금 × 30 × (재직일수 / 365)
        BigDecimal severance = avgDaily
                .multiply(BigDecimal.valueOf(30))
                .multiply(BigDecimal.valueOf(serviceDays))
                .divide(BigDecimal.valueOf(365), 0, RoundingMode.HALF_UP);

        // 퇴직소득세 (간이: 근속연수가 길수록 유리 — 단순히 5% 추정)
        BigDecimal sevTax = severance.multiply(BigDecimal.valueOf(0.05))
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal net = severance.subtract(sevTax);

        SeveranceVO vo = new SeveranceVO();
        vo.setUserId(userId);
        vo.setUserName(user.getName());
        vo.setDeptNm(user.getDeptNm());
        vo.setHireDate(hire);
        vo.setLeaveDate(leaveDate);
        vo.setServiceDays((int) serviceDays);
        vo.setAvgMonthlyWage(avgMonthly);
        vo.setAvgDailyWage(avgDaily);
        vo.setSeverancePay(severance);
        vo.setSeveranceTax(sevTax);
        vo.setNetPay(net);
        vo.setStatusCd("DRAFT");
        vo.setMemo("간이 추정치 — 평균임금은 최근 3개월 급여총액 기준. 퇴직소득세는 5% 추정.");
        return vo;
    }

    @Override
    @Transactional
    public Long saveSeverance(SeveranceVO vo, Long actorId) {
        vo.setCreatedBy(actorId);
        if (vo.getStatusCd() == null) vo.setStatusCd("DRAFT");
        mapper.insertSeverance(vo);
        return vo.getSevId();
    }

    @Override
    public SeveranceVO findSeverance(Long sevId) { return mapper.findSeverance(sevId); }

    @Override
    public List<SeveranceVO> listSeverance() { return mapper.listSeverance(); }

    @Override
    @Transactional
    public void markSeverancePaid(Long sevId, LocalDate paidDt) {
        mapper.updateSeveranceStatus(sevId, "PAID", paidDt == null ? LocalDate.now() : paidDt);
    }

    /* ============ 세액 계산 헬퍼 ============ */

    /** 근로소득공제 (총급여 구간별). */
    private BigDecimal earnedIncomeDeduction(BigDecimal gross) {
        long g = gross.longValue();
        long d;
        if (g <= 5_000_000)        d = Math.round(g * 0.70);
        else if (g <= 15_000_000)  d = 3_500_000  + Math.round((g - 5_000_000) * 0.40);
        else if (g <= 45_000_000)  d = 7_500_000  + Math.round((g - 15_000_000) * 0.15);
        else if (g <= 100_000_000) d = 12_000_000 + Math.round((g - 45_000_000) * 0.05);
        else                       d = 14_750_000 + Math.round((g - 100_000_000) * 0.02);
        return BigDecimal.valueOf(d);
    }

    /** 종합소득세 누진세율 (과세표준 기준, 누진공제 방식). */
    private BigDecimal progressiveTax(BigDecimal base) {
        long b = base.longValue();
        long tax;
        if (b <= 14_000_000)       tax = Math.round(b * 0.06);
        else if (b <= 50_000_000)  tax = Math.round(b * 0.15) - 1_260_000;
        else if (b <= 88_000_000)  tax = Math.round(b * 0.24) - 5_760_000;
        else if (b <= 150_000_000) tax = Math.round(b * 0.35) - 15_440_000;
        else if (b <= 300_000_000) tax = Math.round(b * 0.38) - 19_940_000;
        else                       tax = Math.round(b * 0.40) - 25_940_000;
        return BigDecimal.valueOf(Math.max(0, tax));
    }

    private BigDecimal num(Map<String, Object> m, String key) {
        if (m == null) return BigDecimal.ZERO;
        Object v = m.get(key);
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof BigDecimal bd) return bd;
        if (v instanceof Number n) return BigDecimal.valueOf(n.longValue());
        try { return new BigDecimal(v.toString()); } catch (Exception e) { return BigDecimal.ZERO; }
    }
}
