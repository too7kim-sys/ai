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

    /** 반복 일정 1묶음의 최대 생성 건수 (무한 루프·과다 생성 방지). */
    private static final int MAX_OCCURRENCES = 366;

    @Override
    @Transactional
    public int createRecurring(CalEventVO base, String repeatType,
                               LocalDate repeatUntil, Long ownerId) {
        if (base.getTitle() == null || base.getTitle().isBlank())
            throw new ApiException("INVALID", "제목을 입력하세요");
        if (base.getStartDt() == null || base.getEndDt() == null
                || base.getEndDt().isBefore(base.getStartDt()))
            throw new ApiException("INVALID_DATE", "일정 시간이 올바르지 않습니다");

        // 반복 아님 → 단건 생성
        if (repeatType == null || "NONE".equals(repeatType) || repeatUntil == null) {
            create(base, ownerId);
            return 1;
        }
        if (repeatUntil.isBefore(base.getStartDt().toLocalDate()))
            throw new ApiException("INVALID_DATE", "반복 종료일이 시작일보다 빠릅니다");

        java.time.Duration span = java.time.Duration.between(base.getStartDt(), base.getEndDt());
        long groupId = System.currentTimeMillis();
        LocalDateTime cursor = base.getStartDt();
        int count = 0;
        while (!cursor.toLocalDate().isAfter(repeatUntil) && count < MAX_OCCURRENCES) {
            CalEventVO e = new CalEventVO();
            e.setTitle(base.getTitle());
            e.setStartDt(cursor);
            e.setEndDt(cursor.plus(span));
            e.setScopeCd(base.getScopeCd() == null ? "PERSONAL" : base.getScopeCd());
            e.setDeptId(base.getDeptId());
            e.setColor(base.getColor());
            e.setMemo(base.getMemo());
            e.setOwnerId(ownerId);
            e.setRepeatGroupId(groupId);
            mapper.insert(e);
            count++;
            switch (repeatType) {
                case "DAILY":   cursor = cursor.plusDays(1); break;
                case "WEEKLY":  cursor = cursor.plusWeeks(1); break;
                case "MONTHLY": cursor = cursor.plusMonths(1); break;
                default: return count; // 알 수 없는 유형 — 첫 건만
            }
        }
        return count;
    }

    @Override
    @Transactional
    public void deleteSeries(Long evtId, Long actorId) {
        CalEventVO event = mapper.findById(evtId);
        if (event == null) return;
        if (event.getRepeatGroupId() != null) {
            mapper.softDeleteSeries(event.getRepeatGroupId(), actorId);
        } else {
            mapper.softDelete(evtId, actorId);
        }
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
