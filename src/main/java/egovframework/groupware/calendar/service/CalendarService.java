package egovframework.groupware.calendar.service;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarService {

    Long create(CalEventVO vo, Long ownerId);

    /**
     * 반복 일정 생성. repeatType 이 NONE 이거나 repeatUntil 이 null 이면 단건 생성.
     * @param repeatType NONE | DAILY | WEEKLY | MONTHLY
     * @return 생성된 일정 건수
     */
    int createRecurring(CalEventVO base, String repeatType,
                        java.time.LocalDate repeatUntil, Long ownerId);

    void update(CalEventVO vo, Long actorId, String roleCd);

    void delete(Long evtId, Long actorId);

    /** evtId 가 속한 반복 묶음 전체를 삭제 (단건이면 해당 일정만). */
    void deleteSeries(Long evtId, Long actorId);

    CalEventVO findById(Long evtId);

    List<CalEventVO> findByRange(LocalDateTime from, LocalDateTime to, Long userId, Long deptId);

    long countToday(Long userId, Long deptId);
}
