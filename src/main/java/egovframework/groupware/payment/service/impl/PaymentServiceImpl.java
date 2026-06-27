package egovframework.groupware.payment.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.invoice.service.InvoiceService;
import egovframework.groupware.invoice.service.InvoiceVO;
import egovframework.groupware.payment.mapper.PaymentMapper;
import egovframework.groupware.payment.service.PaymentService;
import egovframework.groupware.payment.service.PaymentVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper mapper;
    private final InvoiceService invoiceService;

    public PaymentServiceImpl(PaymentMapper mapper, InvoiceService invoiceService) {
        this.mapper = mapper;
        this.invoiceService = invoiceService;
    }

    @Override
    @Transactional
    public Long create(PaymentVO vo, Long userId) {
        if (vo.getAmount() == null || vo.getAmount().signum() <= 0)
            throw new ApiException("INVALID", "금액을 입력하세요");
        if (vo.getPayTypeCd() == null || vo.getPayTypeCd().isBlank())
            throw new ApiException("INVALID", "거래 유형(INCOMING/OUTGOING)을 선택하세요");
        if (vo.getPayDt() == null) vo.setPayDt(LocalDate.now());
        if (vo.getRegUserId() == null) vo.setRegUserId(userId);
        mapper.insert(vo);

        // 인보이스 연결되면 잔액 갱신
        if (vo.getInvoiceId() != null) {
            InvoiceVO inv = invoiceService.findById(vo.getInvoiceId());
            if (inv == null) throw new ApiException("NOT_FOUND", "연결된 인보이스가 없습니다");
            // 매출 인보이스(OUT) + 입금(INCOMING) 또는 매입(IN) + 출금(OUTGOING)만 잔액 차감
            boolean reducesRemaining =
                    ("OUT".equals(inv.getDirectionCd()) && "INCOMING".equals(vo.getPayTypeCd()))
                 || ("IN".equals(inv.getDirectionCd())  && "OUTGOING".equals(vo.getPayTypeCd()));
            if (reducesRemaining) {
                invoiceService.applyPayment(vo.getInvoiceId(), vo.getAmount());
            }
        }
        return vo.getPaymentId();
    }

    @Override
    @Transactional
    public void delete(Long paymentId) {
        PaymentVO p = mapper.findById(paymentId);
        if (p == null) return;
        if (p.getInvoiceId() != null) {
            // 인보이스 잔액 복구 (음수 적용)
            InvoiceVO inv = invoiceService.findById(p.getInvoiceId());
            if (inv != null) {
                boolean reduces =
                        ("OUT".equals(inv.getDirectionCd()) && "INCOMING".equals(p.getPayTypeCd()))
                     || ("IN".equals(inv.getDirectionCd())  && "OUTGOING".equals(p.getPayTypeCd()));
                if (reduces) {
                    invoiceService.applyPayment(p.getInvoiceId(), p.getAmount().negate());
                }
            }
        }
        mapper.delete(paymentId);
    }

    @Override public PaymentVO findById(Long id) { return mapper.findById(id); }

    @Override public List<PaymentVO> findByInvoice(Long invoiceId) {
        return mapper.findByInvoice(invoiceId);
    }

    @Override public List<PaymentVO> search(String type, String keyword, LocalDate from, LocalDate to,
                                            int offset, int limit) {
        return mapper.search(type, keyword, from, to, offset, limit);
    }

    @Override public long count(String type, String keyword, LocalDate from, LocalDate to) {
        return mapper.count(type, keyword, from, to);
    }

    @Override public Map<String, Object> sumByType(String type) { return mapper.sumByType(type); }
}
