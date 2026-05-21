package egovframework.groupware.calendar.service.impl;

import egovframework.groupware.calendar.mapper.CalendarMapper;
import egovframework.groupware.calendar.service.CalEventVO;
import egovframework.groupware.calendar.service.CalendarService;
import egovframework.groupware.cmm.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CalendarServiceImpl implements CalendarService {

    private final CalendarMapper mapper;

    public CalendarServiceImpl(CalendarMapper mapper) { this.mapper = mapper; }

    @Override
    @Transactional
    public Long create(CalEventVO vo, Long ownerId) {
        if (vo.getTitle() == null || vo.getTitle().isBlank())
            throw new ApiException("INVALID", "제목을 입력하세요");
        if (vo.getStartDt() == null || vo.getEndDt() == null
                || vo.getEndDt().isBefore(vo.getStartDt()))
            throw new ApiException("INVALID_DATE", "일정 시간이 올바르지 않습니다");
        vo.setOwnerId(ownerId);
        if (vo.getScopeCd() == null) vo.setScopeCd("PERSONAL");
        mapper.insert(vo);
        return vo.getEvtId();
    }

    @Override
    @Transactional
    public void update(CalEventVO vo, Long actorId, String roleCd) {
        CalEventVO existing = mapper.findById(vo.getEvtId());
        if (existing == null) throw new ApiException("NOT_FOUND", "일정을 찾을 수 없습니다");
        if (!canEdit(existing, actorId, roleCd))
            throw new ApiException("FORBIDDEN", "수정 권한이 없습니다");
        mapper.update(vo);
    }

    @Override
    @Transactional
    public void delete(Long evtId, Long actorId) {
        mapper.softDelete(evtId, actorId);
    }

    @Override public CalEventVO findById(Long evtId) { return mapper.findById(evtId); }

    @Override
    public List<CalEventVO> findByRange(LocalDateTime from, LocalDateTime to,
                                        Long userId, Long deptId) {
        return mapper.listByRange(from, to, userId, deptId);
    }

    @Override
    public long countToday(Long userId, Long deptId) {
        LocalDate today = LocalDate.now();
        return mapper.countToday(userId, deptId,
                today.atStartOfDay(), today.atTime(LocalTime.MAX));
    }

    private boolean canEdit(CalEventVO event, Long actorId, String roleCd) {
        if ("ADMIN".equals(roleCd)) return true;
        return event.getOwnerId().equals(actorId);
    }
}
