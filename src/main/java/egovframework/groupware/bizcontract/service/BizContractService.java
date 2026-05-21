package egovframework.groupware.bizcontract.service;

import java.util.List;

public interface BizContractService {

    Long create(BizContractVO vo, Long ownerId);
    void update(BizContractVO vo);
    void delete(Long bizContractId, Long userId);
    void sign(Long bizContractId);
    void terminate(Long bizContractId, String reason);

    BizContractVO findById(Long bizContractId);

    List<BizContractVO> search(String keyword, String statusCd, Long vendorId,
                               int offset, int limit);
    long count(String keyword, String statusCd, Long vendorId);

    List<BizContractVO> findExpiringSoon(int days);

    /** 청구 스케줄 자동 생성 (PAYMENT_TERMS_CD 기반). */
    List<BillingScheduleVO> regenerateSchedules(Long bizContractId);

    List<BillingScheduleVO> findSchedules(Long bizContractId);

    List<BillingScheduleVO> findUpcomingSchedules(int days);
}
