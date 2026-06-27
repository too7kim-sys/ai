package egovframework.groupware.invoice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface InvoiceService {

    Long create(InvoiceVO vo, Long ownerId);
    void update(InvoiceVO vo);
    void delete(Long invoiceId);
    void issue(Long invoiceId);
    void cancel(Long invoiceId);

    /** 결제 발생을 인보이스에 반영. */
    void applyPayment(Long invoiceId, BigDecimal paidDelta);

    InvoiceVO findById(Long invoiceId);

    List<InvoiceVO> search(String directionCd, String keyword, String statusCd, Long vendorId,
                           int offset, int limit);
    long count(String directionCd, String keyword, String statusCd, Long vendorId);

    /** 만기 지난 미수 인보이스 OVERDUE 표시. */
    int markOverdue();

    /* 통계 */
    Map<String, Object> sumByDirection(String directionCd);
    Map<String, Object> sumOutstanding(String directionCd);
    List<Map<String, Object>> monthlyByDirection(String directionCd, int months);
    List<Map<String, Object>> topVendorsByAmount(String directionCd, int limit);
}
