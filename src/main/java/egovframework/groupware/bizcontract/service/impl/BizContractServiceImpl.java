package egovframework.groupware.bizcontract.service.impl;

import egovframework.groupware.bizcontract.mapper.BizContractMapper;
import egovframework.groupware.bizcontract.service.BillingScheduleVO;
import egovframework.groupware.bizcontract.service.BizContractService;
import egovframework.groupware.bizcontract.service.BizContractVO;
import egovframework.groupware.cmm.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BizContractServiceImpl implements BizContractService {

    private final BizContractMapper mapper;

    public BizContractServiceImpl(BizContractMapper mapper) { this.mapper = mapper; }

    @Override
    @Transactional
    public Long create(BizContractVO vo, Long ownerId) {
        validate(vo);
        if (vo.getContractNo() == null || vo.getContractNo().isBlank()) {
            vo.setContractNo(generateContractNo());
        }
        if (vo.getOwnerUserId() == null) vo.setOwnerUserId(ownerId);
        recomputeAmount(vo);
        mapper.insert(vo);
        return vo.getBizContractId();
    }

    @Override
    @Transactional
    public void update(BizContractVO vo) {
        BizContractVO existing = mapper.findById(vo.getBizContractId());
        if (existing == null) throw new ApiException("NOT_FOUND", "계약을 찾을 수 없습니다");
        validate(vo);
        recomputeAmount(vo);
        mapper.update(vo);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) { mapper.softDelete(id, userId); }

    @Override
    @Transactional
    public void sign(Long id) {
        BizContractVO c = mapper.findById(id);
        if (c == null) throw new ApiException("NOT_FOUND", "계약을 찾을 수 없습니다");
        if (!"DRAFT".equals(c.getStatusCd()))
            throw new ApiException("INVALID_STATE", "DRAFT 상태에서만 체결 가능합니다");
        mapper.markSigned(id);
    }

    @Override
    @Transactional
    public void terminate(Long id, String reason) {
        mapper.markTerminated(id, reason);
    }

    @Override public BizContractVO findById(Long id) { return mapper.findById(id); }

    @Override public List<BizContractVO> search(String keyword, String statusCd, Long vendorId,
                                                int offset, int limit) {
        return mapper.search(keyword, statusCd, vendorId, offset, limit);
    }

    @Override public long count(String keyword, String statusCd, Long vendorId) {
        return mapper.count(keyword, statusCd, vendorId);
    }

    @Override public List<BizContractVO> findExpiringSoon(int days) {
        return mapper.findExpiringSoon(days);
    }

    @Override
    @Transactional
    public List<BillingScheduleVO> regenerateSchedules(Long id) {
        BizContractVO c = mapper.findById(id);
        if (c == null) throw new ApiException("NOT_FOUND", "계약을 찾을 수 없습니다");
        mapper.deleteSchedulesByContract(id);

        List<BillingScheduleVO> out = new ArrayList<>();
        String terms = c.getPaymentTermsCd() == null ? "LUMPSUM" : c.getPaymentTermsCd();
        switch (terms) {
            case "MONTHLY" -> generateMonthly(c, out);
            case "QUARTERLY" -> generatePeriodic(c, out, 3);
            case "YEARLY" -> generatePeriodic(c, out, 12);
            default -> generateLumpSum(c, out);
        }
        for (BillingScheduleVO s : out) mapper.insertSchedule(s);
        return out;
    }

    @Override public List<BillingScheduleVO> findSchedules(Long id) {
        return mapper.findSchedulesByContract(id);
    }

    @Override public List<BillingScheduleVO> findUpcomingSchedules(int days) {
        return mapper.findUpcomingSchedules(days);
    }

    /* ============== private helpers ============== */

    private void validate(BizContractVO vo) {
        if (vo.getTitle() == null || vo.getTitle().isBlank())
            throw new ApiException("INVALID", "계약명을 입력하세요");
        if (vo.getVendorId() == null)
            throw new ApiException("INVALID", "거래처를 선택하세요");
        if (vo.getStartDt() == null)
            throw new ApiException("INVALID", "계약 시작일을 입력하세요");
        if (vo.getEndDt() != null && vo.getEndDt().isBefore(vo.getStartDt()))
            throw new ApiException("INVALID_DATE", "종료일이 시작일보다 빠릅니다");
    }

    /** 총액/VAT 자동 계산. VAT 포함이면 amount_total = 입력값, 미포함이면 amount_net + 10%. */
    private void recomputeAmount(BizContractVO vo) {
        BigDecimal net = vo.getAmountNet() == null ? BigDecimal.ZERO : vo.getAmountNet();
        if ("Y".equals(vo.getVatIncludedYn())) {
            BigDecimal total = net;
            BigDecimal supply = total.divide(BigDecimal.valueOf(1.1), 0, RoundingMode.HALF_UP);
            vo.setAmountNet(supply);
            vo.setVatAmount(total.subtract(supply));
            vo.setAmountTotal(total);
        } else {
            BigDecimal vat = net.multiply(BigDecimal.valueOf(0.1)).setScale(0, RoundingMode.HALF_UP);
            vo.setVatAmount(vat);
            vo.setAmountTotal(net.add(vat));
        }
    }

    private String generateContractNo() {
        return "BIZ-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%05d", System.nanoTime() % 100000);
    }

    private void generateLumpSum(BizContractVO c, List<BillingScheduleVO> out) {
        BillingScheduleVO s = new BillingScheduleVO();
        s.setBizContractId(c.getBizContractId());
        s.setSeqNo(1);
        s.setDueDt(c.getStartDt());
        s.setAmountNet(c.getAmountNet());
        s.setVatAmount(c.getVatAmount());
        s.setAmountTotal(c.getAmountTotal());
        s.setMemo("일시불");
        s.setStatusCd("PENDING");
        out.add(s);
    }

    private void generateMonthly(BizContractVO c, List<BillingScheduleVO> out) {
        if (c.getEndDt() == null) { generateLumpSum(c, out); return; }
        // 시작월부터 종료월까지 매월 1회. 분할금액 = 총액 / 개월수.
        long months = monthsBetween(c.getStartDt(), c.getEndDt());
        if (months <= 0) months = 1;
        BigDecimal[] split = split(c.getAmountNet(), c.getVatAmount(), c.getAmountTotal(), months);
        int payDay = c.getPaymentDayOfMonth() == null ? c.getStartDt().getDayOfMonth() : c.getPaymentDayOfMonth();
        LocalDate cursor = c.getStartDt();
        for (int i = 1; i <= months; i++) {
            LocalDate due = atDayOfMonth(cursor, payDay);
            BillingScheduleVO s = new BillingScheduleVO();
            s.setBizContractId(c.getBizContractId());
            s.setSeqNo(i);
            s.setDueDt(due);
            s.setAmountNet(split[0]);
            s.setVatAmount(split[1]);
            s.setAmountTotal(split[2]);
            s.setMemo(i + "회차 (월간)");
            s.setStatusCd("PENDING");
            out.add(s);
            cursor = cursor.plusMonths(1);
        }
    }

    private void generatePeriodic(BizContractVO c, List<BillingScheduleVO> out, int monthsPer) {
        if (c.getEndDt() == null) { generateLumpSum(c, out); return; }
        long total = monthsBetween(c.getStartDt(), c.getEndDt());
        long count = Math.max(1, total / monthsPer);
        BigDecimal[] split = split(c.getAmountNet(), c.getVatAmount(), c.getAmountTotal(), count);
        LocalDate cursor = c.getStartDt();
        int payDay = c.getPaymentDayOfMonth() == null ? c.getStartDt().getDayOfMonth() : c.getPaymentDayOfMonth();
        for (int i = 1; i <= count; i++) {
            LocalDate due = atDayOfMonth(cursor, payDay);
            BillingScheduleVO s = new BillingScheduleVO();
            s.setBizContractId(c.getBizContractId());
            s.setSeqNo(i);
            s.setDueDt(due);
            s.setAmountNet(split[0]);
            s.setVatAmount(split[1]);
            s.setAmountTotal(split[2]);
            s.setMemo(i + "회차 (" + monthsPer + "개월 단위)");
            s.setStatusCd("PENDING");
            out.add(s);
            cursor = cursor.plusMonths(monthsPer);
        }
    }

    private long monthsBetween(LocalDate from, LocalDate to) {
        return java.time.temporal.ChronoUnit.MONTHS.between(from.withDayOfMonth(1), to.withDayOfMonth(1)) + 1;
    }

    private LocalDate atDayOfMonth(LocalDate base, int day) {
        int max = base.lengthOfMonth();
        return base.withDayOfMonth(Math.min(day, max));
    }

    private BigDecimal[] split(BigDecimal net, BigDecimal vat, BigDecimal total, long n) {
        BigDecimal divisor = BigDecimal.valueOf(n);
        return new BigDecimal[]{
                net.divide(divisor, 0, RoundingMode.HALF_UP),
                vat.divide(divisor, 0, RoundingMode.HALF_UP),
                total.divide(divisor, 0, RoundingMode.HALF_UP)
        };
    }
}
