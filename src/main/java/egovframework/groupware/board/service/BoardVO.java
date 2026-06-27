package egovframework.groupware.board.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardVO {
    private Long boardId;
    private String boardCd;
    private String boardNm;
    /** NORMAL | QNA | ANONYMOUS */
    private String boardType;
    private String description;
    private String useYn;
    private Integer sortNo;
}
