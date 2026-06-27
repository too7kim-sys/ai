package egovframework.groupware.hr.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class HrRecordVO {
    private Long recId;
    private Long userId;
    private String userName;
    /** HIRE | PROMOTION | TRANSFER | EDUCATION | AWARD | DISCIPLINE | CERT | LEAVE | TERMINATION | ETC */
    private String categoryCd;
    private String categoryNm;
    private String title;
    private String content;
    private LocalDate eventDt;
    private LocalDateTime createdAt;
}
