package egovframework.groupware.room.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RoomReservationVO {
    private Long resId;
    private Long roomId;
    private String roomNm;
    private Long userId;
    private String userName;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private String purpose;
    private LocalDateTime createdAt;
}
