package egovframework.groupware.performance.mapper;

import egovframework.groupware.performance.service.PerfVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PerfMapper {

    int insert(PerfVO vo);
    int update(PerfVO vo);
    int delete(@Param("perfId") Long perfId);

    PerfVO findById(@Param("perfId") Long perfId);

    List<PerfVO> listByUserAndPeriod(@Param("userId") Long userId,
                                     @Param("periodId") Long periodId);

    List<PerfVO> listByDeptAndPeriod(@Param("deptId") Long deptId,
                                     @Param("periodId") Long periodId);

    List<PerfVO> listByPeriod(@Param("periodId") Long periodId);
}
