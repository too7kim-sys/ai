package egovframework.groupware.room.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomVO {
    private Long roomId;
    private String roomNm;
    private Integer capacity;
    private String location;
    private String equipments;
    private String useYn;
}
