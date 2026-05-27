package egovframework.groupware.hr.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HrAwardVO extends BaseVO {
    private Long awardId;
    private Long userId;
    private String awardTypeCd;          // AWARD / PUNISHMENT
    private String awardTypeNm;
    private String title;
    private LocalDate occurredOn;
    private String organization;
    private String reason;
}
