package egovframework.groupware.board.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostVO extends BaseVO {
    private Long postId;
    private Long boardId;
    private String boardNm;
    private String boardType;
    private String boardCd;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private String authorDept;
    private String pinnedYn;
    private String anonymousYn;
    private Integer viewCnt;
    private String answeredYn;
    private Long attachGroupId;
}
