package egovframework.groupware.vehicle.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class VehicleVO {
    private Long vehicleId;
    private String plateNo;
    private String modelNm;
    private Integer yearModel;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate purchaseDt;
    /** GASOLINE | DIESEL | LPG | HYBRID | EV */
    private String fuelTypeCd;
    private BigDecimal fuelEfficiency;
    private Integer seats;
    private Long ownerDeptId;
    private String ownerDeptNm;
    private Integer currentMileage;
    /** AVAILABLE | IN_USE | MAINTENANCE | RETIRED */
    private String statusCd;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate nextMaintenanceDt;
    private String memo;
}
