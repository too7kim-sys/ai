package egovframework.groupware.room.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.room.mapper.RoomMapper;
import egovframework.groupware.room.service.RoomReservationVO;
import egovframework.groupware.room.service.RoomService;
import egovframework.groupware.room.service.RoomVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomMapper mapper;

    public RoomServiceImpl(RoomMapper mapper) { this.mapper = mapper; }

    @Override public List<RoomVO> findAllRooms() { return mapper.listRooms(); }
    @Override public RoomVO findRoom(Long roomId) { return mapper.findRoom(roomId); }

    @Override
    @Transactional
    public Long reserve(RoomReservationVO vo, Long userId) {
        if (vo.getRoomId() == null) throw new ApiException("INVALID", "회의실을 선택하세요");
        if (vo.getStartDt() == null || vo.getEndDt() == null
                || !vo.getEndDt().isAfter(vo.getStartDt()))
            throw new ApiException("INVALID_DATE", "예약 시간이 올바르지 않습니다");
        long overlap = mapper.countOverlap(vo.getRoomId(), vo.getStartDt(), vo.getEndDt(), null);
        if (overlap > 0) {
            throw new ApiException("CONFLICT", "선택한 시간대에 이미 다른 예약이 있습니다");
        }
        vo.setUserId(userId);
        mapper.insertReservation(vo);
        return vo.getResId();
    }

    @Override
    @Transactional
    public void cancel(Long resId, Long userId, String roleCd) {
        int n = mapper.softDeleteReservation(resId, userId, roleCd);
        if (n == 0) throw new ApiException("FORBIDDEN", "취소 권한이 없거나 예약이 없습니다");
    }

    @Override public RoomReservationVO findReservation(Long resId) {
        return mapper.findReservation(resId);
    }

    @Override
    public List<RoomReservationVO> findByRoomAndDay(Long roomId, LocalDate day) {
        return mapper.listByDay(roomId, day.atStartOfDay(), day.atTime(LocalTime.MAX));
    }

    @Override public List<RoomReservationVO> findByUser(Long userId) {
        return mapper.listByUser(userId);
    }

    @Override
    public List<RoomReservationVO> findUpcoming(int limit) {
        return mapper.listAllUpcoming(LocalDateTime.now(), limit);
    }
}
