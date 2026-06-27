package egovframework.groupware.board.mapper;

import egovframework.groupware.board.service.BoardVO;
import egovframework.groupware.board.service.PostCommentVO;
import egovframework.groupware.board.service.PostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {

    /* 게시판 마스터 */
    List<BoardVO> listBoards();
    BoardVO findBoardByCd(@Param("boardCd") String boardCd);
    BoardVO findBoardById(@Param("boardId") Long boardId);

    /* 게시글 */
    int insertPost(PostVO vo);
    int updatePost(PostVO vo);
    int softDeletePost(@Param("postId") Long postId, @Param("userId") Long userId);
    int incrementViewCnt(@Param("postId") Long postId);
    int markAnswered(@Param("postId") Long postId);

    PostVO findPostById(@Param("postId") Long postId);

    List<PostVO> listPosts(@Param("boardId") Long boardId,
                           @Param("keyword") String keyword,
                           @Param("offset") int offset,
                           @Param("limit") int limit);

    long countPosts(@Param("boardId") Long boardId,
                    @Param("keyword") String keyword);

    /* 댓글 */
    int insertComment(PostCommentVO vo);
    int softDeleteComment(@Param("cmtId") Long cmtId,
                          @Param("authorId") Long authorId,
                          @Param("roleCd") String roleCd);

    List<PostCommentVO> listComments(@Param("postId") Long postId);
}
