package egovframework.groupware.attendance.service;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    /** 출근 등록 (이미 출근했으면 기존 행 반환). */
    AttendanceVO checkIn(Long userId);

    /** 퇴근 등록 + 근로/연장/야간 시간 자동 계산. */
    AttendanceVO checkOut(Long userId);

    AttendanceVO findToday(Long userId);

    List<AttendanceVO> findMyMonth(Long userId, int year, int month);

    /** 부서/전체 월간 리포트. */
    List<AttendanceVO> findMonthReport(int year, int month, Long deptId);
}
