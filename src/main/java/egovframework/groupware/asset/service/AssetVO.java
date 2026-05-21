package egovframework.groupware.asset.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class AssetVO extends BaseVO {
    private Long assetId;
    private String assetNo;
    private String assetNm;
    /** LAPTOP | DESKTOP | MONITOR | PHONE | PRINTER | HEADSET | FURNITURE | OTHER */
    private String categoryCd;
    private String brand;
    private String modelNm;
    private String serialNo;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate purchaseDt;
    private BigDecimal purchaseAmount;
    private Integer depreciationMonths;
    /** IN_STOCK | IN_USE | REPAIRING | DISPOSED */
    private String statusCd;
    private Long assignedUserId;
    private String assignedUserName;
    private String assignedUserDept;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate assignedDt;
    private String location;
    private String memo;
}
