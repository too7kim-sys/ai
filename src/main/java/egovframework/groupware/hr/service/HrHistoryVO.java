package egovframework.groupware.hr.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class HrHistoryVO {
    private Long hisId;
    private Long userId;
    private String userName;
    /** HIRE | DEPT_CHANGE | POSITION_CHANGE | ROLE_CHANGE | SALARY_CHANGE | TERMINATION */
    private String changeTypeCd;
    private String changeTypeNm;
    private String beforeJson;
    private String afterJson;
    private LocalDate effectiveDt;
    private LocalDateTime createdAt;
    private Long createdBy;
}
