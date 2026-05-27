package egovframework.groupware.hr.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class HrTrainingVO extends BaseVO {
    private Long trnId;
    private Long userId;
    private String courseNm;
    private String providerNm;
    private LocalDate startDt;
    private LocalDate endDt;
    private BigDecimal hours;
    private String certNo;
}
