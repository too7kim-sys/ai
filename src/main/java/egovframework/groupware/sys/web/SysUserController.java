package egovframework.groupware.sys.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.cmm.Paging;
import egovframework.groupware.sys.service.AuditLogService;
import egovframework.groupware.user.service.UserService;
import egovframework.groupware.user.service.UserVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 시스템 관리 - 사용자 관리 (ADMIN 전용).
 */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class SysUserController {

    private final UserService userService;
    private final AuditLogService auditService;

    public SysUserController(UserService userService, AuditLogService auditService) {
        this.userService = userService;
        this.auditService = auditService;
    }

    @GetMapping("/sys/user/list.do")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long deptId,
                       @RequestParam(required = false, defaultValue = "1") int page,
                       Model model) {
        Paging p = new Paging();
        p.setPage(Math.max(1, page));
        p.setSize(20);
        model.addAttribute("list", userService.search(keyword, deptId, p.getOffset(), p.getSize()));
        p.setTotal(userService.count(keyword, deptId));
        model.addAttribute("paging", p);
        model.addAttribute("keyword", keyword);
        model.addAttribute("deptId", deptId);
        return "sys/user-list";
    }

    @PostMapping("/sys/user/unlock.do")
    public String unlock(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long userId,
                         HttpServletRequest req) {
        userService.unlock(userId);
        auditService.log(me.getUserId(), "USER_UNLOCK", "USER", userId.toString(),
                null, null, ip(req));
        return "redirect:/sys/user/list.do";
    }

    @PostMapping("/sys/user/reset-password.do")
    public String resetPassword(@AuthenticationPrincipal CustomUserDetails me,
                                @RequestParam Long userId,
                                @RequestParam(defaultValue = "Demo!2025") String newPassword,
                                HttpServletRequest req) {
        userService.resetPassword(userId, newPassword);
        auditService.log(me.getUserId(), "USER_RESET_PWD", "USER", userId.toString(),
                null, null, ip(req));
        return "redirect:/sys/user/list.do";
    }

    @PostMapping("/sys/user/toggle.do")
    public String toggle(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long userId,
                         @RequestParam String useYn,
                         HttpServletRequest req) {
        userService.setUseYn(userId, useYn);
        auditService.log(me.getUserId(), "USER_TOGGLE", "USER", userId.toString(),
                null, "{\"useYn\":\"" + useYn + "\"}", ip(req));
        return "redirect:/sys/user/list.do";
    }

    @PostMapping("/sys/user/role.do")
    public String role(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam Long userId,
                       @RequestParam String roleCd,
                       HttpServletRequest req) {
        UserVO before = userService.findById(userId);
        userService.setRole(userId, roleCd);
        auditService.log(me.getUserId(), "USER_ROLE", "USER", userId.toString(),
                "{\"role\":\"" + (before == null ? "" : before.getRoleCd()) + "\"}",
                "{\"role\":\"" + roleCd + "\"}", ip(req));
        return "redirect:/sys/user/list.do";
    }

    private String ip(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) return h.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}
