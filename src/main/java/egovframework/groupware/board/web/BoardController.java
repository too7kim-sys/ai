package egovframework.groupware.board.web;

import egovframework.groupware.attach.service.AttachService;
import egovframework.groupware.attach.service.AttachVO;
import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.board.service.BoardService;
import egovframework.groupware.board.service.BoardVO;
import egovframework.groupware.board.service.PostVO;
import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.cmm.Paging;
import egovframework.groupware.doc.web.DocController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class BoardController {

    private final BoardService service;
    private final AttachService attachService;

    public BoardController(BoardService service, AttachService attachService) {
        this.service = service;
        this.attachService = attachService;
    }

    @GetMapping("/board/list.do")
    public String list(@RequestParam(required = false, defaultValue = "FREE") String boardCd,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false, defaultValue = "1") int page,
                       Model model) {
        BoardVO board = service.findBoardByCd(boardCd);
        if (board == null) throw new ApiException("NOT_FOUND", "게시판이 없습니다");
        Paging p = new Paging();
        p.setPage(Math.max(1, page));
        p.setSize(15);

        model.addAttribute("boards", service.findAllBoards());
        model.addAttribute("board", board);
        model.addAttribute("keyword", keyword);
        model.addAttribute("list", service.searchPosts(board.getBoardId(), keyword, p.getOffset(), p.getSize()));
        p.setTotal(service.countPosts(board.getBoardId(), keyword));
        model.addAttribute("paging", p);
        return "board/list";
    }

    @GetMapping("/board/detail.do")
    public String detail(@RequestParam Long postId, Model model) {
        PostVO post = service.findPost(postId, true);
        if (post == null) return "redirect:/board/list.do";
        model.addAttribute("post", post);
        model.addAttribute("comments", service.findComments(postId));
        if (post.getAttachGroupId() != null) {
            model.addAttribute("attachments", attachService.findByGroup(post.getAttachGroupId()));
        }
        return "board/detail";
    }

    @GetMapping("/board/write.do")
    public String writeForm(@RequestParam String boardCd,
                            @RequestParam(required = false) Long postId, Model model) {
        BoardVO board = service.findBoardByCd(boardCd);
        if (board == null) throw new ApiException("NOT_FOUND", "게시판이 없습니다");
        model.addAttribute("board", board);
        if (postId != null) model.addAttribute("post", service.findPost(postId, false));
        return "board/write";
    }

    @PostMapping("/board/write.do")
    public String write(@AuthenticationPrincipal CustomUserDetails me,
                        @RequestParam(required = false) Long postId,
                        @RequestParam String boardCd,
                        @RequestParam String title,
                        @RequestParam String content,
                        @RequestParam(required = false) String pinnedYn,
                        @RequestParam(required = false) String anonymousYn,
                        @RequestParam(value = "files", required = false) MultipartFile[] files) {
        BoardVO board = service.findBoardByCd(boardCd);
        PostVO vo = new PostVO();
        vo.setPostId(postId);
        vo.setBoardId(board.getBoardId());
        vo.setTitle(title);
        vo.setContent(content);
        vo.setPinnedYn("Y".equals(pinnedYn) ? "Y" : "N");
        vo.setAnonymousYn("Y".equals(anonymousYn) ? "Y" : "N");
        if (postId == null) {
            Long id = service.createPost(vo, files, me.getUserId());
            return "redirect:/board/detail.do?postId=" + id;
        }
        service.updatePost(vo, me.getUserId(), me.getRoleCd());
        return "redirect:/board/detail.do?postId=" + postId;
    }

    @PostMapping("/board/delete.do")
    public String delete(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long postId,
                         @RequestParam String boardCd) {
        service.deletePost(postId, me.getUserId(), me.getRoleCd());
        return "redirect:/board/list.do?boardCd=" + boardCd;
    }

    /* 댓글 */
    @PostMapping("/board/comment/add.do")
    public String addComment(@AuthenticationPrincipal CustomUserDetails me,
                             @RequestParam Long postId,
                             @RequestParam String content,
                             @RequestParam(required = false) String anonymousYn,
                             @RequestParam(required = false) Long parentCmtId) {
        service.addComment(postId, me.getUserId(), content, anonymousYn, parentCmtId);
        return "redirect:/board/detail.do?postId=" + postId;
    }

    @PostMapping("/board/comment/delete.do")
    public String deleteComment(@AuthenticationPrincipal CustomUserDetails me,
                                @RequestParam Long postId,
                                @RequestParam Long cmtId) {
        service.deleteComment(cmtId, me.getUserId(), me.getRoleCd());
        return "redirect:/board/detail.do?postId=" + postId;
    }

    /* 첨부파일 다운로드 */
    @GetMapping("/board/attach/download.do")
    public void download(@RequestParam Long attachId, HttpServletResponse resp) throws IOException {
        AttachVO vo = attachService.findById(attachId);
        if (vo == null) { resp.sendError(404); return; }
        DocController.streamAttachment(resp, vo, attachService.resolvePath(vo));
    }
}
