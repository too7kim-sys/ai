package egovframework.groupware.evaluation.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EvalPeriodVO {
    private Long periodId;
    private String periodNm;
    private LocalDate startDt;
    private LocalDate endDt;
    /** PLANNED | IN_PROGRESS | CLOSED */
    private String statusCd;
}
