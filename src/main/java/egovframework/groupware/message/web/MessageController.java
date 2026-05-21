package egovframework.groupware.message.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.message.service.MessageService;
import egovframework.groupware.message.service.MessageVO;
import egovframework.groupware.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessageController {

    private final MessageService service;
    private final UserService userService;

    public MessageController(MessageService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping("/message/inbox.do")
    public String inbox(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.inbox(me.getUserId()));
        model.addAttribute("box", "inbox");
        return "message/box";
    }

    @GetMapping("/message/sent.do")
    public String sent(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.sent(me.getUserId()));
        model.addAttribute("box", "sent");
        return "message/box";
    }

    @GetMapping("/message/detail.do")
    public String detail(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long msgId, Model model) {
        MessageVO m = service.read(msgId, me.getUserId());
        if (m == null) return "redirect:/message/inbox.do";
        model.addAttribute("msg", m);
        return "message/detail";
    }

    @GetMapping("/message/write.do")
    public String writeForm(@RequestParam(required = false) Long receiverId, Model model) {
        model.addAttribute("users", userService.listAll());
        model.addAttribute("receiverId", receiverId);
        return "message/write";
    }

    @PostMapping("/message/write.do")
    public String write(@AuthenticationPrincipal CustomUserDetails me,
                        @RequestParam Long receiverId,
                        @RequestParam String content) {
        service.send(me.getUserId(), receiverId, content);
        return "redirect:/message/sent.do";
    }

    @PostMapping("/message/delete.do")
    public String delete(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long msgId,
                         @RequestParam(required = false, defaultValue = "inbox") String box) {
        service.delete(msgId, me.getUserId());
        return "redirect:/message/" + box + ".do";
    }
}
