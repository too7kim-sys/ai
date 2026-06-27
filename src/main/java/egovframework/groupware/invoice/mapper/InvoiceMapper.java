package egovframework.groupware.invoice.mapper;

import egovframework.groupware.invoice.service.InvoiceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface InvoiceMapper {

    int insert(InvoiceVO vo);
    int update(InvoiceVO vo);
    int softDelete(@Param("invoiceId") Long id);
    int updateStatus(@Param("invoiceId") Long id, @Param("statusCd") String statusCd);
    int issue(@Param("invoiceId") Long id);

    /** 입금/지급 발생 시 잔액과 상태를 갱신. */
    int applyPayment(@Param("invoiceId") Long id,
                     @Param("paidDelta") BigDecimal paidDelta);

    InvoiceVO findById(@Param("invoiceId") Long id);

    List<InvoiceVO> search(@Param("directionCd") String directionCd,
                           @Param("keyword") String keyword,
                           @Param("statusCd") String statusCd,
                           @Param("vendorId") Long vendorId,
                           @Param("offset") int offset,
                           @Param("limit") int limit);

    long count(@Param("directionCd") String directionCd,
               @Param("keyword") String keyword,
               @Param("statusCd") String statusCd,
               @Param("vendorId") Long vendorId);

    /** 만기 지난 미수 인보이스를 OVERDUE 로 일괄 표시. 반환: 변경된 행 수. */
    int markOverdue();

    /* 통계 (재무 대시보드용) */
    Map<String, Object> sumByDirection(@Param("directionCd") String directionCd);
    Map<String, Object> sumOutstanding(@Param("directionCd") String directionCd);
    List<Map<String, Object>> monthlyByDirection(@Param("directionCd") String directionCd,
                                                 @Param("months") int months);
    List<Map<String, Object>> topVendorsByAmount(@Param("directionCd") String directionCd,
                                                 @Param("limit") int limit);
}
