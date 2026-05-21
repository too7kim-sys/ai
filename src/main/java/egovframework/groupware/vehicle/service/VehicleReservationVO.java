package egovframework.groupware.vehicle.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VehicleReservationVO {
    private Long reservationId;
    private Long vehicleId;
    private String plateNo;
    private String modelNm;
    private Long userId;
    private String userName;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private String purpose;
    private String destination;
    private Integer passengerCnt;
    private Integer mileageStart;
    private Integer mileageEnd;
    /** PENDING | APPROVED | IN_USE | COMPLETED | CANCELED */
    private String statusCd;
    private LocalDateTime createdAt;
}
