package egovframework.groupware.calendar.service;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarService {

    Long create(CalEventVO vo, Long ownerId);

    void update(CalEventVO vo, Long actorId, String roleCd);

    void delete(Long evtId, Long actorId);

    CalEventVO findById(Long evtId);

    List<CalEventVO> findByRange(LocalDateTime from, LocalDateTime to, Long userId, Long deptId);

    long countToday(Long userId, Long deptId);
}
