package egovframework.groupware.board.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostCommentVO {
    private Long cmtId;
    private Long postId;
    private Long parentCmtId;
    private Long authorId;
    private String authorName;
    private String content;
    private String anonymousYn;
    private LocalDateTime createdAt;
}
