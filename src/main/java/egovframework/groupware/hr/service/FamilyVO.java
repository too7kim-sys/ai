package egovframework.groupware.hr.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class FamilyVO {
    private Long famId;
    private Long userId;
    private String userName;
    /** SPOUSE | CHILD | PARENT | SIBLING | OTHER */
    private String relationCd;
    private String name;
    private LocalDate birthDt;
    /** Y: 부양가족 */
    private String dependentYn;
    /** Y: 경로(만 70세 이상) */
    private String elderlyYn;
    /** Y: 장애 */
    private String disabledYn;
    private LocalDateTime createdAt;
}
