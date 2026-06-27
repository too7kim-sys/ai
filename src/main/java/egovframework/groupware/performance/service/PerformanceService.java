package egovframework.groupware.performance.service;

import java.math.BigDecimal;
import java.util.List;

public interface PerformanceService {

    /**
     * KPI 항목 저장 (perfId == null 이면 신규, 아니면 갱신).
     * actualVal/targetVal이 주어지면 달성률(achRate)을 자동 계산한다.
     */
    Long save(PerfVO vo, Long currentUserId, String currentRoleCd);

    void delete(Long perfId, Long currentUserId, String currentRoleCd);

    PerfVO findById(Long perfId);

    List<PerfVO> findByUserAndPeriod(Long userId, Long periodId);

    List<PerfVO> findByDeptAndPeriod(Long deptId, Long periodId);

    List<PerfVO> findByPeriod(Long periodId);

    /** 부서 합산 등 요약 계산 (가중 평균 달성률). */
    BigDecimal weightedAchievement(List<PerfVO> items);
}
