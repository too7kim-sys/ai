package egovframework.groupware.payroll.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.payroll.mapper.BonusMapper;
import egovframework.groupware.payroll.service.BonusService;
import egovframework.groupware.payroll.service.BonusVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BonusServiceImpl implements BonusService {

    private final BonusMapper mapper;

    public BonusServiceImpl(BonusMapper mapper) { this.mapper = mapper; }

    @Override
    public List<BonusVO> search(String payMonth, String bonusTypeCd) {
        return mapper.search(blank(payMonth), blank(bonusTypeCd));
    }

    @Override
    public BonusVO findById(Long bonusId) { return mapper.findById(bonusId); }

    @Override
    @Transactional
    public Long create(BonusVO vo, Long actorId) {
        validate(vo);
        vo.setCreatedBy(actorId);
        if (vo.getStatusCd() == null) vo.setStatusCd("PLANNED");
        mapper.insert(vo);
        return vo.getBonusId();
    }

    @Override
    @Transactional
    public int bulkCreate(String payMonth, String bonusTypeCd, List<Long> userIds,
                          BigDecimal amount, String taxableYn, String memo, Long actorId) {
        if (payMonth == null || payMonth.isBlank())
            throw new ApiException("INVALID", "급여월을 입력하세요");
        if (userIds == null || userIds.isEmpty())
            throw new ApiException("INVALID", "대상 직원을 선택하세요");
        if (amount == null || amount.signum() <= 0)
            throw new ApiException("INVALID", "금액을 입력하세요");
        int n = 0;
        for (Long uid : userIds) {
            if (uid == null) continue;
            BonusVO vo = new BonusVO();
            vo.setPayMonth(payMonth);
            vo.setBonusTypeCd(bonusTypeCd);
            vo.setUserId(uid);
            vo.setAmount(amount);
            vo.setTaxableYn("N".equals(taxableYn) ? "N" : "Y");
            vo.setMemo(memo);
            vo.setStatusCd("PLANNED");
            vo.setCreatedBy(actorId);
            mapper.insert(vo);
            n++;
        }
        return n;
    }

    @Override
    @Transactional
    public void delete(Long bonusId) {
        int n = mapper.delete(bonusId);
        if (n == 0) throw new ApiException("BONUS_LOCKED", "이미 급여에 반영된 상여는 삭제할 수 없습니다");
    }

    @Override
    @Transactional
    public void cancel(Long bonusId) {
        mapper.updateStatus(bonusId, "CANCELED");
    }

    private void validate(BonusVO vo) {
        if (vo.getPayMonth() == null || vo.getPayMonth().isBlank())
            throw new ApiException("INVALID", "급여월을 입력하세요");
        if (vo.getUserId() == null)
            throw new ApiException("INVALID", "대상 직원을 선택하세요");
        if (vo.getBonusTypeCd() == null || vo.getBonusTypeCd().isBlank())
            throw new ApiException("INVALID", "상여 유형을 선택하세요");
        if (vo.getAmount() == null || vo.getAmount().signum() <= 0)
            throw new ApiException("INVALID", "금액을 입력하세요");
    }

    private String blank(String s) { return s == null || s.isBlank() ? null : s; }
}
