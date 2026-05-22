package egovframework.groupware.calendar.mapper;

import egovframework.groupware.calendar.service.CalEventVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CalendarMapper {

    int insert(CalEventVO vo);
    int update(CalEventVO vo);
    int softDelete(@Param("evtId") Long evtId, @Param("userId") Long userId);
    int softDeleteSeries(@Param("repeatGroupId") Long repeatGroupId, @Param("userId") Long userId);

    CalEventVO findById(@Param("evtId") Long evtId);

    /** 본인/부서/회사 범위 이벤트를 한 번에 가져온다. */
    List<CalEventVO> listByRange(@Param("from") LocalDateTime from,
                                 @Param("to") LocalDateTime to,
                                 @Param("userId") Long userId,
                                 @Param("deptId") Long deptId);

    /** 오늘 일정 카운트 (대시보드). */
    long countToday(@Param("userId") Long userId, @Param("deptId") Long deptId,
                    @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
