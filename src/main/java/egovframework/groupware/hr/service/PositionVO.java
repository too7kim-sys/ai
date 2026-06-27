package egovframework.groupware.hr.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PositionVO {
    private Long positionId;
    private String positionNm;
    private Integer levelNo;
    private String useYn;
}
