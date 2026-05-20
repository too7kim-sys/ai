package egovframework.groupware.leave.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.leave.service.LeaveService;
import egovframework.groupware.user.service.UserService;
import egovframework.groupware.user.service.UserVO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LeaveController {

    private final LeaveService leaveService;
    private final UserService userService;

    public LeaveController(LeaveService leaveService, UserService userService) {
        this.leaveService = leaveService;
        this.userService = userService;
    }

    @GetMapping("/leave/write.do")
    public String writeForm(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("balance", leaveService.findBalance(me.getUserId(), Year.now().getValue()));
        model.addAttribute("approvers", userService.search(null, null, 0, 200));
        return "leave/write";
    }

    @PostMapping("/leave/write.do")
    public String write(@AuthenticationPrincipal CustomUserDetails me,
                        @RequestParam String leaveTypeCd,
                        @RequestParam String startDt,
                        @RequestParam String endDt,
                        @RequestParam(required = false) String reason,
                        @RequestParam(name = "approverIds", required = false) List<Long> approverIds) {
        if (approverIds == null || approverIds.isEmpty()) {
            // 결재선 미지정 시: 본인 부서장(없으면 HR 임의)을 기본 결재자로
            approverIds = new ArrayList<>();
            for (UserVO u : userService.search("admin@", null, 0, 1)) approverIds.add(u.getUserId());
            if (approverIds.isEmpty()) {
                approverIds.add(1L);
            }
        }
        leaveService.apply(me.getUserId(), leaveTypeCd,
            LocalDate.parse(startDt), LocalDate.parse(endDt), reason, approverIds);
        return "redirect:/leave/my.do";
    }

    @GetMapping("/leave/my.do")
    public String my(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", leaveService.listMine(me.getUserId()));
        model.addAttribute("balance", leaveService.findBalance(me.getUserId(), Year.now().getValue()));
        return "leave/my";
    }

    @GetMapping("/leave/balance.do")
    public String balance(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("balance", leaveService.findBalance(me.getUserId(), Year.now().getValue()));
        return "leave/balance";
    }
}
