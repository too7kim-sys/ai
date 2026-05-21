package egovframework.groupware.vehicle.service;

import java.time.LocalDate;
import java.util.List;

public interface VehicleService {

    /* 차량 */
    Long create(VehicleVO vo);
    void update(VehicleVO vo);
    void delete(Long vehicleId);
    void setStatus(Long vehicleId, String statusCd);

    VehicleVO findById(Long vehicleId);
    List<VehicleVO> findAll();
    List<VehicleVO> findAvailable();

    /* 예약 */
    Long reserve(VehicleReservationVO vo, Long userId);
    void cancel(Long reservationId, Long userId, String roleCd);
    void startUse(Long reservationId);
    void complete(Long reservationId, Integer mileageEnd);

    VehicleReservationVO findReservation(Long id);

    List<VehicleReservationVO> findByVehicleAndDay(Long vehicleId, LocalDate day);
    List<VehicleReservationVO> findByUser(Long userId);
    List<VehicleReservationVO> findUpcoming(int limit);
}
