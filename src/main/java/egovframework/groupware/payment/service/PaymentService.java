package egovframework.groupware.payment.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PaymentService {

    /** 입금/출금 등록. invoiceId 있으면 인보이스 잔액 자동 갱신. */
    Long create(PaymentVO vo, Long userId);

    void delete(Long paymentId);

    PaymentVO findById(Long paymentId);

    List<PaymentVO> findByInvoice(Long invoiceId);

    List<PaymentVO> search(String payTypeCd, String keyword, LocalDate from, LocalDate to,
                           int offset, int limit);

    long count(String payTypeCd, String keyword, LocalDate from, LocalDate to);

    Map<String, Object> sumByType(String payTypeCd);
}
