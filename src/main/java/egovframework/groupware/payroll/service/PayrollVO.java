package egovframework.groupware.payroll.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PayrollVO extends BaseVO {
    private Long payId;
    private Long userId;
    private String userName;
    private String deptNm;
    private String email;
    private String bankCd;
    private String bankAccount;
    private String payMonth;
    private Long contractId;
    private BigDecimal workDays;
    private BigDecimal absentDays;
    private Integer otMin;
    private Integer nightMin;
    private Integer holidayMin;
    private BigDecimal taxablePay;
    private BigDecimal nonTaxablePay;
    private BigDecimal grossPay;
    private BigDecimal deductionTotal;
    private BigDecimal netPay;
    private String statusCd;
    private LocalDate paidDt;
    private String memo;

    private List<PayrollItemVO> items = new ArrayList<>();
    private List<PayrollEmployerCostVO> employerCosts = new ArrayList<>();
}
