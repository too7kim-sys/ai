package egovframework.groupware.board.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {

    /* 게시판 */
    List<BoardVO> findAllBoards();
    BoardVO findBoardByCd(String boardCd);

    /* 게시글 */
    Long createPost(PostVO vo, MultipartFile[] files, Long authorId);
    void updatePost(PostVO vo, Long actorId, String roleCd);
    void deletePost(Long postId, Long actorId, String roleCd);
    PostVO findPost(Long postId, boolean incrementView);

    List<PostVO> searchPosts(Long boardId, String keyword, int offset, int limit);
    long countPosts(Long boardId, String keyword);

    /* 댓글 */
    Long addComment(Long postId, Long authorId, String content,
                    String anonymousYn, Long parentCmtId);
    void deleteComment(Long cmtId, Long authorId, String roleCd);
    List<PostCommentVO> findComments(Long postId);
}
