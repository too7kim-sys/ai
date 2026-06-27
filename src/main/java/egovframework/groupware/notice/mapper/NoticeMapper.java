package egovframework.groupware.notice.mapper;

import egovframework.groupware.notice.service.NoticeCommentVO;
import egovframework.groupware.notice.service.NoticeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {

    int insert(NoticeVO vo);
    int update(NoticeVO vo);
    int softDelete(@Param("noticeId") Long noticeId, @Param("userId") Long userId);
    int incrementViewCnt(@Param("noticeId") Long noticeId);

    NoticeVO findById(@Param("noticeId") Long noticeId);

    List<NoticeVO> list(@Param("keyword") String keyword,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

    long count(@Param("keyword") String keyword);

    List<NoticeVO> listLatest(@Param("limit") int limit);

    /* 댓글 */
    int insertComment(NoticeCommentVO vo);
    int deleteComment(@Param("cmtId") Long cmtId, @Param("authorId") Long authorId, @Param("roleCd") String roleCd);
    List<NoticeCommentVO> listComments(@Param("noticeId") Long noticeId);
}
