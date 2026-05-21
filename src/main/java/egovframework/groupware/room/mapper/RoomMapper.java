package egovframework.groupware.room.mapper;

import egovframework.groupware.room.service.RoomReservationVO;
import egovframework.groupware.room.service.RoomVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RoomMapper {

    /* 회의실 */
    List<RoomVO> listRooms();
    RoomVO findRoom(@Param("roomId") Long roomId);

    /* 예약 */
    int insertReservation(RoomReservationVO vo);
    int softDeleteReservation(@Param("resId") Long resId, @Param("userId") Long userId, @Param("roleCd") String roleCd);
    RoomReservationVO findReservation(@Param("resId") Long resId);

    /** 시간 겹치는 예약이 있는지 카운트. */
    long countOverlap(@Param("roomId") Long roomId,
                      @Param("start") LocalDateTime start,
                      @Param("end") LocalDateTime end,
                      @Param("excludeResId") Long excludeResId);

    List<RoomReservationVO> listByDay(@Param("roomId") Long roomId,
                                      @Param("from") LocalDateTime from,
                                      @Param("to") LocalDateTime to);

    List<RoomReservationVO> listByUser(@Param("userId") Long userId);

    List<RoomReservationVO> listAllUpcoming(@Param("from") LocalDateTime from,
                                            @Param("limit") int limit);
}
