package egovframework.groupware.performance.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.performance.mapper.PerfMapper;
import egovframework.groupware.performance.service.PerfVO;
import egovframework.groupware.performance.service.PerformanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    private final PerfMapper mapper;

    public PerformanceServiceImpl(PerfMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Long save(PerfVO vo, Long currentUserId, String currentRoleCd) {
        if (vo.getUserId() == null) vo.setUserId(currentUserId);
        if (!canEdit(vo.getUserId(), currentUserId, currentRoleCd)) {
            throw new ApiException("FORBIDDEN", "다른 직원의 KPI는 수정할 수 없습니다");
        }
        if (vo.getPeriodId() == null) throw new ApiException("INVALID", "평가 기간을 선택하세요");
        if (vo.getGoal() == null || vo.getGoal().isBlank()) throw new ApiException("INVALID", "목표를 입력하세요");
        if (vo.getKpi() == null || vo.getKpi().isBlank()) throw new ApiException("INVALID", "KPI 지표를 입력하세요");

        vo.setAchRate(calcAchRate(vo.getTargetVal(), vo.getActualVal()));

        if (vo.getPerfId() == null) {
            mapper.insert(vo);
        } else {
            PerfVO existing = mapper.findById(vo.getPerfId());
            if (existing == null) throw new ApiException("NOT_FOUND", "KPI를 찾을 수 없습니다");
            if (!canEdit(existing.getUserId(), currentUserId, currentRoleCd)) {
                throw new ApiException("FORBIDDEN", "수정 권한이 없습니다");
            }
            mapper.update(vo);
        }
        return vo.getPerfId();
    }

    @Override
    @Transactional
    public void delete(Long perfId, Long currentUserId, String currentRoleCd) {
        PerfVO existing = mapper.findById(perfId);
        if (existing == null) return;
        if (!canEdit(existing.getUserId(), currentUserId, currentRoleCd)) {
            throw new ApiException("FORBIDDEN", "삭제 권한이 없습니다");
        }
        mapper.delete(perfId);
    }

    @Override public PerfVO findById(Long perfId) { return mapper.findById(perfId); }

    @Override
    public List<PerfVO> findByUserAndPeriod(Long userId, Long periodId) {
        return mapper.listByUserAndPeriod(userId, periodId);
    }

    @Override
    public List<PerfVO> findByDeptAndPeriod(Long deptId, Long periodId) {
        return mapper.listByDeptAndPeriod(deptId, periodId);
    }

    @Override public List<PerfVO> findByPeriod(Long periodId) { return mapper.listByPeriod(periodId); }

    @Override
    public BigDecimal weightedAchievement(List<PerfVO> items) {
        if (items == null || items.isEmpty()) return BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (PerfVO p : items) {
            BigDecimal w = p.getWeight() == null ? BigDecimal.ZERO : p.getWeight();
            BigDecimal a = p.getAchRate() == null ? BigDecimal.ZERO : p.getAchRate();
            sum = sum.add(w.multiply(a));
            totalWeight = totalWeight.add(w);
        }
        if (totalWeight.signum() == 0) return BigDecimal.ZERO;
        return sum.divide(totalWeight, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcAchRate(BigDecimal target, BigDecimal actual) {
        if (target == null || actual == null || target.signum() == 0) return null;
        return actual.multiply(BigDecimal.valueOf(100))
                .divide(target, 2, RoundingMode.HALF_UP);
    }

    private boolean canEdit(Long ownerId, Long currentUserId, String currentRoleCd) {
        if (ownerId == null || currentUserId == null) return false;
        if (ownerId.equals(currentUserId)) return true;
        return "ADMIN".equals(currentRoleCd) || "HR_MANAGER".equals(currentRoleCd) || "MANAGER".equals(currentRoleCd);
    }
}
