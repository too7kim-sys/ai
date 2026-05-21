package egovframework.groupware.board.service.impl;

import egovframework.groupware.attach.service.AttachService;
import egovframework.groupware.board.mapper.BoardMapper;
import egovframework.groupware.board.service.BoardService;
import egovframework.groupware.board.service.BoardVO;
import egovframework.groupware.board.service.PostCommentVO;
import egovframework.groupware.board.service.PostVO;
import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    private static final Logger log = LoggerFactory.getLogger(BoardServiceImpl.class);

    private final BoardMapper mapper;
    private final AttachService attachService;
    private final NotificationService notificationService;

    public BoardServiceImpl(BoardMapper mapper,
                            AttachService attachService,
                            NotificationService notificationService) {
        this.mapper = mapper;
        this.attachService = attachService;
        this.notificationService = notificationService;
    }

    @Override public List<BoardVO> findAllBoards() { return mapper.listBoards(); }
    @Override public BoardVO findBoardByCd(String boardCd) { return mapper.findBoardByCd(boardCd); }

    @Override
    @Transactional
    public Long createPost(PostVO vo, MultipartFile[] files, Long authorId) {
        if (vo.getTitle() == null || vo.getTitle().isBlank())
            throw new ApiException("INVALID", "제목을 입력하세요");
        if (vo.getContent() == null || vo.getContent().isBlank())
            throw new ApiException("INVALID", "내용을 입력하세요");

        BoardVO board = mapper.findBoardById(vo.getBoardId());
        if (board == null) throw new ApiException("NOT_FOUND", "게시판이 없습니다");

        // 익명 게시판이 아니면 anonymousYn 강제 N
        if (!"ANONYMOUS".equals(board.getBoardType())) vo.setAnonymousYn("N");

        // 첨부 처리
        if (files != null) {
            boolean hasFile = false;
            for (MultipartFile f : files) if (f != null && !f.isEmpty()) { hasFile = true; break; }
            if (hasFile) {
                Long groupId = attachService.createGroup("BOARD_POST", null);
                for (MultipartFile f : files) {
                    if (f == null || f.isEmpty()) continue;
                    try { attachService.store(groupId, f, authorId); }
                    catch (IOException e) { throw new ApiException("UPLOAD_FAIL", "파일 업로드 실패: " + e.getMessage()); }
                }
                vo.setAttachGroupId(groupId);
            }
        }

        vo.setAuthorId(authorId);
        mapper.insertPost(vo);
        return vo.getPostId();
    }

    @Override
    @Transactional
    public void updatePost(PostVO vo, Long actorId, String roleCd) {
        PostVO existing = mapper.findPostById(vo.getPostId());
        if (existing == null) throw new ApiException("NOT_FOUND", "게시글이 없습니다");
        if (!canEdit(existing, actorId, roleCd))
            throw new ApiException("FORBIDDEN", "수정 권한이 없습니다");
        vo.setAuthorId(actorId);
        // 익명 여부는 변경 불가
        vo.setAnonymousYn(existing.getAnonymousYn());
        mapper.updatePost(vo);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long actorId, String roleCd) {
        PostVO existing = mapper.findPostById(postId);
        if (existing == null) return;
        if (!canEdit(existing, actorId, roleCd))
            throw new ApiException("FORBIDDEN", "삭제 권한이 없습니다");
        mapper.softDeletePost(postId, actorId);
    }

    @Override
    @Transactional
    public PostVO findPost(Long postId, boolean incrementView) {
        if (incrementView) mapper.incrementViewCnt(postId);
        return mapper.findPostById(postId);
    }

    @Override public List<PostVO> searchPosts(Long boardId, String keyword, int offset, int limit) {
        return mapper.listPosts(boardId, keyword, offset, limit);
    }

    @Override public long countPosts(Long boardId, String keyword) {
        return mapper.countPosts(boardId, keyword);
    }

    @Override
    @Transactional
    public Long addComment(Long postId, Long authorId, String content,
                           String anonymousYn, Long parentCmtId) {
        if (content == null || content.isBlank())
            throw new ApiException("INVALID", "내용을 입력하세요");
        PostVO post = mapper.findPostById(postId);
        if (post == null) throw new ApiException("NOT_FOUND", "게시글이 없습니다");

        PostCommentVO c = new PostCommentVO();
        c.setPostId(postId);
        c.setParentCmtId(parentCmtId);
        c.setAuthorId(authorId);
        c.setContent(content);
        // 익명게시판인 경우만 익명 허용
        c.setAnonymousYn("ANONYMOUS".equals(post.getBoardType()) && "Y".equals(anonymousYn) ? "Y" : "N");
        mapper.insertComment(c);

        // QNA 답변 작성자가 본인이 아니면 게시글에 답변 알림 + answered
        if ("QNA".equals(post.getBoardType()) && !authorId.equals(post.getAuthorId())) {
            mapper.markAnswered(postId);
            notificationService.notify(post.getAuthorId(), "BOARD",
                    "Q&A '" + post.getTitle() + "'에 답변이 등록되었습니다",
                    content.length() > 80 ? content.substring(0, 80) + "..." : content,
                    "/board/detail.do?postId=" + postId);
        }
        // 일반 게시판도 작성자에 댓글 알림
        else if (!authorId.equals(post.getAuthorId())) {
            notificationService.notify(post.getAuthorId(), "BOARD",
                    "[" + post.getBoardNm() + "] 새 댓글이 달렸습니다",
                    content.length() > 80 ? content.substring(0, 80) + "..." : content,
                    "/board/detail.do?postId=" + postId);
        }
        return c.getCmtId();
    }

    @Override
    @Transactional
    public void deleteComment(Long cmtId, Long authorId, String roleCd) {
        mapper.softDeleteComment(cmtId, authorId, roleCd);
    }

    @Override public List<PostCommentVO> findComments(Long postId) {
        return mapper.listComments(postId);
    }

    private boolean canEdit(PostVO post, Long actorId, String roleCd) {
        if ("ADMIN".equals(roleCd) || "HR_MANAGER".equals(roleCd)) return true;
        return post.getAuthorId().equals(actorId);
    }
}
