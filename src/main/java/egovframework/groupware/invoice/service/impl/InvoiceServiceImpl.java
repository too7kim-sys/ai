package egovframework.groupware.invoice.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.invoice.mapper.InvoiceMapper;
import egovframework.groupware.invoice.service.InvoiceService;
import egovframework.groupware.invoice.service.InvoiceVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceMapper mapper;

    public InvoiceServiceImpl(InvoiceMapper mapper) { this.mapper = mapper; }

    @Override
    @Transactional
    public Long create(InvoiceVO vo, Long ownerId) {
        validate(vo);
        if (vo.getInvoiceNo() == null || vo.getInvoiceNo().isBlank()) {
            vo.setInvoiceNo(generateInvoiceNo(vo.getDirectionCd()));
        }
        if (vo.getOwnerUserId() == null) vo.setOwnerUserId(ownerId);
        if (vo.getIssueDt() == null) vo.setIssueDt(LocalDate.now());
        recomputeAmount(vo);
        mapper.insert(vo);
        return vo.getInvoiceId();
    }

    @Override
    @Transactional
    public void update(InvoiceVO vo) {
        InvoiceVO existing = mapper.findById(vo.getInvoiceId());
        if (existing == null) throw new ApiException("NOT_FOUND", "인보이스를 찾을 수 없습니다");
        if ("PAID".equals(existing.getStatusCd()))
            throw new ApiException("INVALID_STATE", "완납된 인보이스는 수정할 수 없습니다");
        validate(vo);
        recomputeAmount(vo);
        mapper.update(vo);
    }

    @Override
    @Transactional
    public void delete(Long id) { mapper.softDelete(id); }

    @Override
    @Transactional
    public void issue(Long id) { mapper.issue(id); }

    @Override
    @Transactional
    public void cancel(Long id) { mapper.updateStatus(id, "CANCELED"); }

    @Override
    @Transactional
    public void applyPayment(Long id, BigDecimal paidDelta) {
        if (paidDelta == null || paidDelta.signum() == 0) return;
        InvoiceVO inv = mapper.findById(id);
        if (inv == null) throw new ApiException("NOT_FOUND", "인보이스를 찾을 수 없습니다");
        if ("DRAFT".equals(inv.getStatusCd())) mapper.issue(id);
        mapper.applyPayment(id, paidDelta);
    }

    @Override public InvoiceVO findById(Long id) { return mapper.findById(id); }

    @Override public List<InvoiceVO> search(String direction, String keyword, String status, Long vendorId,
                                            int offset, int limit) {
        return mapper.search(direction, keyword, status, vendorId, offset, limit);
    }

    @Override public long count(String direction, String keyword, String status, Long vendorId) {
        return mapper.count(direction, keyword, status, vendorId);
    }

    @Override
    @Transactional
    public int markOverdue() { return mapper.markOverdue(); }

    @Override public Map<String, Object> sumByDirection(String d) { return mapper.sumByDirection(d); }
    @Override public Map<String, Object> sumOutstanding(String d) { return mapper.sumOutstanding(d); }
    @Override public List<Map<String, Object>> monthlyByDirection(String d, int m) {
        return mapper.monthlyByDirection(d, m);
    }
    @Override public List<Map<String, Object>> topVendorsByAmount(String d, int l) {
        return mapper.topVendorsByAmount(d, l);
    }

    private void validate(InvoiceVO vo) {
        if (vo.getDirectionCd() == null || vo.getDirectionCd().isBlank())
            throw new ApiException("INVALID", "방향(OUT/IN)을 선택하세요");
        if (vo.getVendorId() == null)
            throw new ApiException("INVALID", "거래처를 선택하세요");
        if (vo.getAmountNet() == null)
            throw new ApiException("INVALID", "공급가액을 입력하세요");
    }

    private void recomputeAmount(InvoiceVO vo) {
        BigDecimal net = vo.getAmountNet() == null ? BigDecimal.ZERO : vo.getAmountNet();
        BigDecimal vat = net.multiply(BigDecimal.valueOf(0.1)).setScale(0, RoundingMode.HALF_UP);
        vo.setVatAmount(vat);
        vo.setAmountTotal(net.add(vat));
    }

    private String generateInvoiceNo(String direction) {
        String prefix = "OUT".equals(direction) ? "INV-OUT" : "INV-IN";
        return prefix + "-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%05d", System.nanoTime() % 100000);
    }
}
