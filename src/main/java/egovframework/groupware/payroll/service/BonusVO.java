package egovframework.groupware.payroll.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BonusVO {
    private Long bonusId;
    private String payMonth;
    /** REGULAR 정기 | HOLIDAY 명절 | PERFORMANCE 성과급 | SPECIAL 특별 */
    private String bonusTypeCd;
    private Long userId;
    private String userName;
    private String deptNm;
    private BigDecimal amount;
    private String taxableYn;
    private String memo;
    /** PLANNED | APPLIED | CANCELED */
    private String statusCd;
    private LocalDateTime createdAt;
    private Long createdBy;

    public String getBonusTypeNm() {
        if (bonusTypeCd == null) return "";
        return switch (bonusTypeCd) {
            case "REGULAR"     -> "정기상여";
            case "HOLIDAY"     -> "명절상여";
            case "PERFORMANCE" -> "성과급";
            case "SPECIAL"     -> "특별상여";
            default            -> bonusTypeCd;
        };
    }
}
