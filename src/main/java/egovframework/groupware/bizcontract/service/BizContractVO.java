package egovframework.groupware.bizcontract.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class BizContractVO extends BaseVO {
    private Long bizContractId;
    private String contractNo;
    private Long vendorId;
    private String vendorNm;
    private String contractTypeCd;
    private String title;
    private LocalDate startDt;
    private LocalDate endDt;
    private String autoRenewYn;
    private Integer renewNoticeDays;
    private BigDecimal amountNet;
    private BigDecimal vatAmount;
    private BigDecimal amountTotal;
    private String vatIncludedYn;
    private String currencyCd;
    private String paymentTermsCd;
    private Integer paymentDayOfMonth;
    private Long ownerUserId;
    private String ownerName;
    private Long deptId;
    private String deptNm;
    /** DRAFT | ACTIVE | TERMINATED | EXPIRED */
    private String statusCd;
    private Long approvalDocId;
    private Long attachGroupId;
    private String memo;
    private LocalDateTime signedAt;
    private LocalDateTime terminatedAt;
    private String terminationReason;
}
