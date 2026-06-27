package egovframework.groupware.notice.service;

import java.util.List;

public interface NoticeService {

    Long create(NoticeVO vo, Long authorId, String roleCd);

    void update(NoticeVO vo, Long actorId, String roleCd);

    void delete(Long noticeId, Long actorId, String roleCd);

    NoticeVO findById(Long noticeId, boolean incrementView);

    List<NoticeVO> search(String keyword, int offset, int limit);

    long count(String keyword);

    List<NoticeVO> findLatest(int limit);

    /* 댓글 */
    Long addComment(Long noticeId, Long authorId, String content);
    void deleteComment(Long cmtId, Long authorId, String roleCd);
    List<NoticeCommentVO> findComments(Long noticeId);
}
