package egovframework.groupware.vehicle.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.vehicle.mapper.VehicleMapper;
import egovframework.groupware.vehicle.service.VehicleReservationVO;
import egovframework.groupware.vehicle.service.VehicleService;
import egovframework.groupware.vehicle.service.VehicleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper mapper;

    public VehicleServiceImpl(VehicleMapper mapper) { this.mapper = mapper; }

    @Override
    @Transactional
    public Long create(VehicleVO vo) {
        if (vo.getPlateNo() == null || vo.getPlateNo().isBlank())
            throw new ApiException("INVALID", "차량번호를 입력하세요");
        if (vo.getModelNm() == null || vo.getModelNm().isBlank())
            throw new ApiException("INVALID", "모델명을 입력하세요");
        mapper.insert(vo);
        return vo.getVehicleId();
    }

    @Override
    @Transactional
    public void update(VehicleVO vo) { mapper.update(vo); }

    @Override
    @Transactional
    public void delete(Long id) { mapper.softDelete(id); }

    @Override
    @Transactional
    public void setStatus(Long id, String statusCd) { mapper.updateStatus(id, statusCd); }

    @Override public VehicleVO findById(Long id) { return mapper.findById(id); }
    @Override public List<VehicleVO> findAll() { return mapper.findAll(); }
    @Override public List<VehicleVO> findAvailable() { return mapper.findAvailable(); }

    @Override
    @Transactional
    public Long reserve(VehicleReservationVO vo, Long userId) {
        if (vo.getVehicleId() == null)
            throw new ApiException("INVALID", "차량을 선택하세요");
        if (vo.getStartDt() == null || vo.getEndDt() == null
                || !vo.getEndDt().isAfter(vo.getStartDt()))
            throw new ApiException("INVALID_DATE", "예약 시간이 올바르지 않습니다");
        long overlap = mapper.countOverlap(vo.getVehicleId(), vo.getStartDt(), vo.getEndDt(), null);
        if (overlap > 0)
            throw new ApiException("CONFLICT", "선택한 시간대에 이미 다른 예약이 있습니다");

        VehicleVO v = mapper.findById(vo.getVehicleId());
        if (v == null) throw new ApiException("NOT_FOUND", "차량을 찾을 수 없습니다");
        if ("RETIRED".equals(v.getStatusCd()) || "MAINTENANCE".equals(v.getStatusCd()))
            throw new ApiException("INVALID_STATE", "이용 가능한 차량이 아닙니다");

        vo.setUserId(userId);
        if (vo.getStatusCd() == null) vo.setStatusCd("APPROVED");
        if (vo.getMileageStart() == null) vo.setMileageStart(v.getCurrentMileage());
        mapper.insertReservation(vo);
        return vo.getReservationId();
    }

    @Override
    @Transactional
    public void cancel(Long reservationId, Long userId, String roleCd) {
        int n = mapper.softDeleteReservation(reservationId, userId, roleCd);
        if (n == 0) throw new ApiException("FORBIDDEN", "취소 권한이 없거나 예약이 없습니다");
    }

    @Override
    @Transactional
    public void startUse(Long reservationId) {
        mapper.updateReservationStatus(reservationId, "IN_USE");
        VehicleReservationVO r = mapper.findReservation(reservationId);
        if (r != null) mapper.updateStatus(r.getVehicleId(), "IN_USE");
    }

    @Override
    @Transactional
    public void complete(Long reservationId, Integer mileageEnd) {
        mapper.completeReservation(reservationId, mileageEnd);
        VehicleReservationVO r = mapper.findReservation(reservationId);
        if (r != null) {
            mapper.updateStatus(r.getVehicleId(), "AVAILABLE");
            if (mileageEnd != null) mapper.updateMileage(r.getVehicleId(), mileageEnd);
        }
    }

    @Override public VehicleReservationVO findReservation(Long id) { return mapper.findReservation(id); }

    @Override
    public List<VehicleReservationVO> findByVehicleAndDay(Long vehicleId, LocalDate day) {
        return mapper.listByDay(vehicleId, day.atStartOfDay(), day.atTime(LocalTime.MAX));
    }

    @Override public List<VehicleReservationVO> findByUser(Long userId) { return mapper.listByUser(userId); }
    @Override public List<VehicleReservationVO> findUpcoming(int limit) { return mapper.listAllUpcoming(limit); }
}
