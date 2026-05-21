package egovframework.groupware.room.service;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    List<RoomVO> findAllRooms();
    RoomVO findRoom(Long roomId);

    /** 예약 등록. 시간 겹침 시 IllegalState 예외 발생. */
    Long reserve(RoomReservationVO vo, Long userId);

    void cancel(Long resId, Long userId, String roleCd);

    RoomReservationVO findReservation(Long resId);

    List<RoomReservationVO> findByRoomAndDay(Long roomId, LocalDate day);

    List<RoomReservationVO> findByUser(Long userId);

    List<RoomReservationVO> findUpcoming(int limit);
}
