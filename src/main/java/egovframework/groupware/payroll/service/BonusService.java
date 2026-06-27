package egovframework.groupware.payroll.service;

import java.util.List;

public interface BonusService {

    List<BonusVO> search(String payMonth, String bonusTypeCd);

    BonusVO findById(Long bonusId);

    Long create(BonusVO vo, Long actorId);

    /** 여러 직원에게 동일 상여 일괄 등록. 반환: 생성 건수. */
    int bulkCreate(String payMonth, String bonusTypeCd, List<Long> userIds,
                   java.math.BigDecimal amount, String taxableYn, String memo, Long actorId);

    void delete(Long bonusId);

    void cancel(Long bonusId);
}
