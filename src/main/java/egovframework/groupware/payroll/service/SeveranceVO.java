package egovframework.groupware.payroll.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class SeveranceVO {
    private Long sevId;
    private Long userId;
    private String userName;
    private String deptNm;
    private LocalDate hireDate;
    private LocalDate leaveDate;
    private Integer serviceDays;          // 근속일수
    private BigDecimal avgMonthlyWage;    // 평균임금 (최근 3개월 월평균)
    private BigDecimal avgDailyWage;      // 1일 평균임금
    private BigDecimal severancePay;      // 퇴직금 (세전)
    private BigDecimal severanceTax;      // 퇴직소득세 (간이)
    private BigDecimal netPay;            // 실지급액
    /** DRAFT | CONFIRMED | PAID */
    private String statusCd;
    private LocalDate paidDt;
    private String memo;
    private LocalDateTime createdAt;
    private Long createdBy;

    /** 근속연수 (소수 1자리). */
    public BigDecimal getServiceYears() {
        if (serviceDays == null) return BigDecimal.ZERO;
        return BigDecimal.valueOf(serviceDays)
                .divide(BigDecimal.valueOf(365), 1, java.math.RoundingMode.HALF_UP);
    }
}
