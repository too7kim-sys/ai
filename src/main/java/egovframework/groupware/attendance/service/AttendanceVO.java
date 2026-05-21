package egovframework.groupware.attendance.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AttendanceVO {
    private Long attId;
    private Long userId;
    private String userName;
    private String deptNm;
    private LocalDate workDt;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    /** NORMAL | LATE | EARLY_LEAVE | ABSENT | LEAVE | HOLIDAY */
    private String statusCd;
    private Integer workMin;
    private Integer otMin;
    private Integer nightMin;
    private Integer holidayMin;
    private String remark;
}
