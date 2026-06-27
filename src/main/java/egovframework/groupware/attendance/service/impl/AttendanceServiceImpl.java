package egovframework.groupware.attendance.service.impl;

import egovframework.groupware.attendance.mapper.AttendanceMapper;
import egovframework.groupware.attendance.service.AttendanceService;
import egovframework.groupware.attendance.service.AttendanceVO;
import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.leave.service.LeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    /** 표준 근로 시간 (9~18, 1시간 휴게 → 8시간). */
    private static final LocalTime STANDARD_START = LocalTime.of(9, 0);
    private static final LocalTime STANDARD_END   = LocalTime.of(18, 0);
    private static final int STANDARD_MIN = 8 * 60;

    private final AttendanceMapper mapper;
    private final LeaveService leaveService;

    public AttendanceServiceImpl(AttendanceMapper mapper, LeaveService leaveService) {
        this.mapper = mapper;
        this.leaveService = leaveService;
    }

    @Override
    @Transactional
    public AttendanceVO checkIn(Long userId) {
        LocalDate today = LocalDate.now();
        AttendanceVO existing = mapper.findByUserAndDate(userId, today);
        if (existing != null) return existing;

        // 종일 휴가(연차/병가/경조사/기타)가 잡혀 있으면 출근 차단 — 반차/시간연차는 허용
        String onLeave = leaveService.findFullDayLeaveTypeOn(userId, today);
        if (onLeave != null) {
            throw new ApiException("ON_LEAVE",
                "오늘은 휴가(" + onLeave + ") 가 등록되어 있어 출근할 수 없습니다");
        }

        LocalDateTime now = LocalDateTime.now();
        AttendanceVO vo = new AttendanceVO();
        vo.setUserId(userId);
        vo.setWorkDt(today);
        vo.setCheckIn(now);
        vo.setStatusCd(now.toLocalTime().isAfter(STANDARD_START) ? "LATE" : "NORMAL");
        mapper.insert(vo);
        return mapper.findByUserAndDate(userId, today);
    }

    @Override
    @Transactional
    public AttendanceVO checkOut(Long userId) {
        LocalDate today = LocalDate.now();
        AttendanceVO att = mapper.findByUserAndDate(userId, today);
        if (att == null || att.getCheckIn() == null)
            throw new ApiException("NOT_CHECKED_IN", "출근 기록이 없습니다");
        if (att.getCheckOut() != null) return att;

        LocalDateTime now = LocalDateTime.now();
        long totalMin = ChronoUnit.MINUTES.between(att.getCheckIn(), now);
        long breakMin = totalMin > 240 ? 60 : 0;
        long netMin = Math.max(0, totalMin - breakMin);

        int workMin = (int) Math.min(STANDARD_MIN, netMin);
        int otMin = (int) Math.max(0, netMin - STANDARD_MIN);
        int nightMin = computeNightMin(att.getCheckIn(), now);

        String statusCd = att.getStatusCd();
        if ("NORMAL".equals(statusCd) && now.toLocalTime().isBefore(STANDARD_END)) {
            statusCd = "EARLY_LEAVE";
        }
        if (today.getDayOfWeek() == DayOfWeek.SATURDAY || today.getDayOfWeek() == DayOfWeek.SUNDAY) {
            statusCd = "HOLIDAY";
        }
        // 출근 후 종일 휴가가 등록·승인된 경우(checkIn 시점에는 없었던 휴가) 휴가일로 마킹.
        // 출근 기록 자체는 남기되 payroll 의 근무일 카운트에서는 제외된다.
        if (leaveService.findFullDayLeaveTypeOn(userId, today) != null) {
            statusCd = "LEAVE";
        }
        mapper.updateCheckOut(att.getAttId(), now, workMin, otMin, nightMin, statusCd);
        return mapper.findByUserAndDate(userId, today);
    }

    @Override
    public AttendanceVO findToday(Long userId) {
        return mapper.findByUserAndDate(userId, LocalDate.now());
    }

    @Override
    public List<AttendanceVO> findMyMonth(Long userId, int year, int month) {
        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());
        return mapper.listByUserAndMonth(userId, from, to);
    }

    @Override
    public List<AttendanceVO> findMonthReport(int year, int month, Long deptId) {
        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());
        return mapper.listByMonth(from, to, deptId);
    }

    /** 야간 근로(22:00 ~ 익일 06:00)에 해당하는 분(min). */
    private int computeNightMin(LocalDateTime in, LocalDateTime out) {
        if (in == null || out == null || !out.isAfter(in)) return 0;
        int totalNight = 0;
        LocalDateTime cursor = in;
        while (cursor.isBefore(out)) {
            LocalDateTime nightStart = cursor.toLocalDate().atTime(22, 0);
            LocalDateTime nightEnd = cursor.toLocalDate().plusDays(1).atTime(6, 0);
            LocalDateTime sliceStart = cursor.isAfter(nightStart) ? cursor : nightStart;
            LocalDateTime sliceEnd = out.isBefore(nightEnd) ? out : nightEnd;
            if (sliceEnd.isAfter(sliceStart)) {
                totalNight += (int) ChronoUnit.MINUTES.between(sliceStart, sliceEnd);
            }
            cursor = cursor.toLocalDate().plusDays(1).atStartOfDay();
        }
        return totalNight;
    }
}
