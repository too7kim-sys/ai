package egovframework.groupware.hr.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HrCareerVO extends BaseVO {
    private Long careerId;
    private Long userId;
    private String companyNm;
    private String positionNm;
    private LocalDate startDt;
    private LocalDate endDt;
    private String description;
}
