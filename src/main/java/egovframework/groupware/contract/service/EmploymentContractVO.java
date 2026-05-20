package egovframework.groupware.contract.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class EmploymentContractVO extends BaseVO {
    private Long contractId;
    private String contractNo;
    private Long userId;
    private String userName;
    private String email;
    private String deptNm;
    private Long templateId;
    private String templateNm;
    private String contractTypeCd;
    private LocalDate startDt;
    private LocalDate endDt;
    private String workplace;
    private String jobDescription;
    private BigDecimal workHoursPerWeek;
    private String workStartTime;
    private String workEndTime;
    private Integer breakMinutes;
    private String weeklyHoliday;
    private Integer annualPaidLeaveDays;
    private Integer probationMonths;
    private Long salaryContractId;
    private BigDecimal annualSalary;
    private String insuranceAppliedJson;
    private String specialTerms;
    private String statusCd;
    private Long attachGroupId;
    private LocalDateTime companySignedAt;
    private LocalDateTime employeeSignedAt;
    private String employeeSignature;
    private LocalDate terminationDt;
    private String terminationReason;
}
