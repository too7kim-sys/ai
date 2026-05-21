package egovframework.groupware.bizcontract.mapper;

import egovframework.groupware.bizcontract.service.BillingScheduleVO;
import egovframework.groupware.bizcontract.service.BizContractVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BizContractMapper {

    /* 계약 */
    int insert(BizContractVO vo);
    int update(BizContractVO vo);
    int softDelete(@Param("bizContractId") Long id, @Param("userId") Long userId);
    int updateStatus(@Param("bizContractId") Long id, @Param("statusCd") String statusCd);
    int markSigned(@Param("bizContractId") Long id);
    int markTerminated(@Param("bizContractId") Long id, @Param("reason") String reason);

    BizContractVO findById(@Param("bizContractId") Long id);

    List<BizContractVO> search(@Param("keyword") String keyword,
                               @Param("statusCd") String statusCd,
                               @Param("vendorId") Long vendorId,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    long count(@Param("keyword") String keyword,
               @Param("statusCd") String statusCd,
               @Param("vendorId") Long vendorId);

    /** 1년 내 만료 예정 + ACTIVE 상태. */
    List<BizContractVO> findExpiringSoon(@Param("days") int days);

    /* 청구 스케줄 */
    int insertSchedule(BillingScheduleVO vo);
    int deleteSchedulesByContract(@Param("bizContractId") Long id);
    int updateScheduleInvoice(@Param("scheduleId") Long scheduleId,
                              @Param("invoiceId") Long invoiceId,
                              @Param("statusCd") String statusCd);

    List<BillingScheduleVO> findSchedulesByContract(@Param("bizContractId") Long id);
    List<BillingScheduleVO> findUpcomingSchedules(@Param("days") int days);
}
