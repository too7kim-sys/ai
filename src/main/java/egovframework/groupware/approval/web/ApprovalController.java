package egovframework.groupware.approval.web;

import egovframework.groupware.approval.service.ApprovalDocVO;
import egovframework.groupware.approval.service.ApprovalService;
import egovframework.groupware.auth.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ApprovalController {

    private final ApprovalService service;

    public ApprovalController(ApprovalService service) { this.service = service; }

    @GetMapping("/approval/write.do")
    public String writeForm(@RequestParam(required = false) String form, Model model) {
        model.addAttribute("forms", service.listForms());
        model.addAttribute("formCd", form);
        return "approval/write";
    }

    @GetMapping("/approval/pending.do")
    public String pending(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.listPending(me.getUserId()));
        model.addAttribute("box", "pending");
        return "approval/box";
    }

    @GetMapping("/approval/draft.do")
    public String draft(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.listByDrafter(me.getUserId(), null));
        model.addAttribute("box", "draft");
        return "approval/box";
    }

    @GetMapping("/approval/completed.do")
    public String completed(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.listCompleted(me.getUserId(), "APPROVED"));
        model.addAttribute("box", "completed");
        return "approval/box";
    }

    @GetMapping("/approval/rejected.do")
    public String rejected(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.listCompleted(me.getUserId(), "REJECTED"));
        model.addAttribute("box", "rejected");
        return "approval/box";
    }

    @GetMapping("/approval/refer.do")
    public String refer(Model model) {
        model.addAttribute("list", java.util.Collections.emptyList());
        model.addAttribute("box", "refer");
        return "approval/box";
    }

    @GetMapping("/approval/detail.do")
    public String detail(@RequestParam Long docId, Model model) {
        ApprovalDocVO d = service.findDoc(docId);
        model.addAttribute("d", d);
        return "approval/detail";
    }

    @PostMapping("/approval/act.do")
    public String act(@AuthenticationPrincipal CustomUserDetails me,
                      @RequestParam Long docId,
                      @RequestParam boolean approve,
                      @RequestParam(required = false) String comment) {
        service.act(docId, me.getUserId(), approve, comment);
        return "redirect:/approval/pending.do";
    }

    @PostMapping("/approval/cancel.do")
    public String cancel(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long docId) {
        service.cancel(docId, me.getUserId());
        return "redirect:/approval/draft.do";
    }
}
