package egovframework.groupware.calendar.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CalEventVO extends BaseVO {
    private Long evtId;
    private String title;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    /** PERSONAL | DEPT | COMPANY */
    private String scopeCd;
    private Long ownerId;
    private String ownerName;
    private Long deptId;
    private String deptNm;
    private String color;
    private String memo;
    /** 반복 일정 묶음 식별자 (단건 일정은 null). */
    private Long repeatGroupId;
}
