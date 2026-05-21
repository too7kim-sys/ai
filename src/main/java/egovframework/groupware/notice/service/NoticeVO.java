package egovframework.groupware.notice.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeVO extends BaseVO {
    private Long noticeId;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private String pinnedYn;
    private Integer viewCnt;
    private Long attachGroupId;
    /** ALL | DEPT */
    private String deptScope;
}
