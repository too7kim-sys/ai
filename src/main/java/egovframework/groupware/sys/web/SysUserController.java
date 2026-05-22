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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;

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
                                HttpServletRequest req,
                                RedirectAttributes ra) {
        // 고정 비밀번호 대신 1회용 임시 비밀번호를 생성 — 알려진 자격증명 재사용 방지.
        String tempPassword = generateTempPassword();
        userService.resetPassword(userId, tempPassword);
        auditService.log(me.getUserId(), "USER_RESET_PWD", "USER", userId.toString(),
                null, null, ip(req));
        ra.addFlashAttribute("resetUserId", userId);
        ra.addFlashAttribute("tempPassword", tempPassword);
        return "redirect:/sys/user/list.do";
    }

    private static final SecureRandom RANDOM = new SecureRandom();

    /** 정책(대문자·소문자·숫자·특수문자 포함)을 만족하는 12자리 임시 비밀번호. */
    private String generateTempPassword() {
        String upper = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        String lower = "abcdefghijkmnpqrstuvwxyz";
        String digit = "23456789";
        String special = "!@#$%^&*";
        String all = upper + lower + digit + special;
        StringBuilder sb = new StringBuilder();
        sb.append(upper.charAt(RANDOM.nextInt(upper.length())));
        sb.append(lower.charAt(RANDOM.nextInt(lower.length())));
        sb.append(digit.charAt(RANDOM.nextInt(digit.length())));
        sb.append(special.charAt(RANDOM.nextInt(special.length())));
        for (int i = 0; i < 8; i++) sb.append(all.charAt(RANDOM.nextInt(all.length())));
        return sb.toString();
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
