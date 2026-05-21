package egovframework.groupware.notice.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoticeCommentVO {
    private Long cmtId;
    private Long noticeId;
    private Long authorId;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
}
