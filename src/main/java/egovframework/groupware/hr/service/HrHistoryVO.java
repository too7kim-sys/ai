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
    /** 사람이 읽을 수 있게 변환된 변경 전/후 요약. */
    private String beforeText;
    private String afterText;
    private LocalDate effectiveDt;
    private LocalDateTime createdAt;
    private Long createdBy;
}
