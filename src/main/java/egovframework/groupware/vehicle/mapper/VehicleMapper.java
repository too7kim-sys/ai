package egovframework.groupware.vehicle.mapper;

import egovframework.groupware.vehicle.service.VehicleReservationVO;
import egovframework.groupware.vehicle.service.VehicleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VehicleMapper {

    /* 차량 */
    int insert(VehicleVO vo);
    int update(VehicleVO vo);
    int softDelete(@Param("vehicleId") Long id);
    int updateStatus(@Param("vehicleId") Long id, @Param("statusCd") String statusCd);
    int updateMileage(@Param("vehicleId") Long id, @Param("mileage") int mileage);

    VehicleVO findById(@Param("vehicleId") Long id);
    List<VehicleVO> findAll();
    List<VehicleVO> findAvailable();

    /* 예약 */
    int insertReservation(VehicleReservationVO vo);
    int softDeleteReservation(@Param("reservationId") Long id, @Param("userId") Long userId, @Param("roleCd") String roleCd);
    int updateReservationStatus(@Param("reservationId") Long id, @Param("statusCd") String statusCd);
    int completeReservation(@Param("reservationId") Long id,
                            @Param("mileageEnd") Integer mileageEnd);

    VehicleReservationVO findReservation(@Param("reservationId") Long id);

    /** 시간 겹치는 예약 카운트 (취소/완료 제외). */
    long countOverlap(@Param("vehicleId") Long vehicleId,
                      @Param("start") LocalDateTime start,
                      @Param("end") LocalDateTime end,
                      @Param("excludeId") Long excludeId);

    List<VehicleReservationVO> listByDay(@Param("vehicleId") Long vehicleId,
                                         @Param("from") LocalDateTime from,
                                         @Param("to") LocalDateTime to);

    List<VehicleReservationVO> listByUser(@Param("userId") Long userId);

    List<VehicleReservationVO> listAllUpcoming(@Param("limit") int limit);
}
