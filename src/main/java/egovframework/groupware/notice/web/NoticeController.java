package egovframework.groupware.notice.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.cmm.Paging;
import egovframework.groupware.notice.service.NoticeService;
import egovframework.groupware.notice.service.NoticeVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NoticeController {

    private final NoticeService service;

    public NoticeController(NoticeService service) { this.service = service; }

    @GetMapping("/notice/list.do")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false, defaultValue = "1") int page,
                       Model model) {
        Paging p = new Paging();
        p.setPage(Math.max(1, page));
        p.setSize(15);
        model.addAttribute("list", service.search(keyword, p.getOffset(), p.getSize()));
        p.setTotal(service.count(keyword));
        model.addAttribute("paging", p);
        model.addAttribute("keyword", keyword);
        return "notice/list";
    }

    @GetMapping("/notice/detail.do")
    public String detail(@RequestParam Long noticeId, Model model) {
        NoticeVO n = service.findById(noticeId, true);
        if (n == null) return "redirect:/notice/list.do";
        model.addAttribute("notice", n);
        model.addAttribute("comments", service.findComments(noticeId));
        return "notice/detail";
    }

    @GetMapping("/notice/write.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER','MANAGER')")
    public String writeForm(@RequestParam(required = false) Long noticeId, Model model) {
        if (noticeId != null) {
            NoticeVO n = service.findById(noticeId, false);
            model.addAttribute("notice", n);
        }
        return "notice/write";
    }

    @PostMapping("/notice/write.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER','MANAGER')")
    public String write(@AuthenticationPrincipal CustomUserDetails me,
                        @RequestParam(required = false) Long noticeId,
                        @RequestParam String title,
                        @RequestParam String content,
                        @RequestParam(required = false) String pinnedYn,
                        @RequestParam(required = false) String deptScope) {
        NoticeVO vo = new NoticeVO();
        vo.setNoticeId(noticeId);
        vo.setTitle(title);
        vo.setContent(content);
        vo.setPinnedYn("Y".equals(pinnedYn) ? "Y" : "N");
        vo.setDeptScope(deptScope == null || deptScope.isBlank() ? "ALL" : deptScope);
        if (noticeId == null) {
            Long newId = service.create(vo, me.getUserId(), me.getRoleCd());
            return "redirect:/notice/detail.do?noticeId=" + newId;
        }
        service.update(vo, me.getUserId(), me.getRoleCd());
        return "redirect:/notice/detail.do?noticeId=" + noticeId;
    }

    @PostMapping("/notice/delete.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER','MANAGER')")
    public String delete(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long noticeId) {
        service.delete(noticeId, me.getUserId(), me.getRoleCd());
        return "redirect:/notice/list.do";
    }

    /* 댓글 */
    @PostMapping("/notice/comment/add.do")
    public String addComment(@AuthenticationPrincipal CustomUserDetails me,
                             @RequestParam Long noticeId,
                             @RequestParam String content) {
        service.addComment(noticeId, me.getUserId(), content);
        return "redirect:/notice/detail.do?noticeId=" + noticeId;
    }

    @PostMapping("/notice/comment/delete.do")
    public String deleteComment(@AuthenticationPrincipal CustomUserDetails me,
                                @RequestParam Long noticeId,
                                @RequestParam Long cmtId) {
        service.deleteComment(cmtId, me.getUserId(), me.getRoleCd());
        return "redirect:/notice/detail.do?noticeId=" + noticeId;
    }
}
