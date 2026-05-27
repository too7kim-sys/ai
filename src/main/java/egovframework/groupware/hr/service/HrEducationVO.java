package egovframework.groupware.hr.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HrEducationVO extends BaseVO {
    private Long eduId;
    private Long userId;
    private String schoolNm;
    private String major;
    private String degreeCd;
    private String degreeNm;
    private String eduStatusCd;
    private String eduStatusNm;
    private LocalDate admissionDt;
    private LocalDate graduationDt;
}
