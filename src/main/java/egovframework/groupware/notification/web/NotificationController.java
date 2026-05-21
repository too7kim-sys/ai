package egovframework.groupware.notification.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.notification.service.NotificationService;
import egovframework.groupware.notification.service.NotificationVO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) { this.service = service; }

    @GetMapping("/notification/list.do")
    public String list(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam(required = false, defaultValue = "false") boolean onlyUnread,
                       Model model) {
        List<NotificationVO> all = service.findByUser(me.getUserId(), onlyUnread, 100);
        model.addAttribute("list", all);
        model.addAttribute("onlyUnread", onlyUnread);
        model.addAttribute("unread", service.countUnread(me.getUserId()));
        return "notification/list";
    }

    @GetMapping("/notification/unread-count.do")
    @ResponseBody
    public Map<String, Object> unreadCount(@AuthenticationPrincipal CustomUserDetails me) {
        Map<String, Object> out = new HashMap<>();
        out.put("unread", service.countUnread(me.getUserId()));
        return out;
    }

    @PostMapping("/notification/read.do")
    public String read(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam Long notiId,
                       @RequestParam(required = false) String redirect) {
        service.markRead(notiId, me.getUserId());
        return "redirect:" + (redirect == null || redirect.isBlank() ? "/notification/list.do" : redirect);
    }

    @PostMapping("/notification/read-all.do")
    public String readAll(@AuthenticationPrincipal CustomUserDetails me) {
        service.markAllRead(me.getUserId());
        return "redirect:/notification/list.do";
    }
}
