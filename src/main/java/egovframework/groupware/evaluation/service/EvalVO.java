package egovframework.groupware.evaluation.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class EvalVO {
    private Long evalId;
    private Long periodId;
    private String periodNm;
    private Long evaluateeId;
    private String evaluateeName;
    private String evaluateeDept;
    private Long evaluatorId;
    private String evaluatorName;
    /** {"comm":4, "ownership":5, ...} */
    private String scoresJson;
    private BigDecimal finalScore;
    /** S | A | B | C | D */
    private String gradeCd;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
