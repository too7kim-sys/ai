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
import java.time.LocalDateTime;
import java.time.LocalTime;
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
                        @RequestParam(required = false) String startDt,
                        @RequestParam(required = false) String endDt,
                        @RequestParam(required = false) String startTime,
                        @RequestParam(required = false) String endTime,
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

        LocalDate start = (startDt != null && !startDt.isEmpty()) ? LocalDate.parse(startDt) : null;
        LocalDate end   = (endDt   != null && !endDt.isEmpty())   ? LocalDate.parse(endDt)   : null;
        LocalDateTime startAt = null, endAt = null;
        if ("HOURLY".equals(leaveTypeCd)) {
            if (start == null || startTime == null || endTime == null) {
                throw new egovframework.groupware.cmm.ApiException("INVALID_TIME",
                        "시간연차는 일자와 시작/종료 시각이 모두 필요합니다");
            }
            startAt = LocalDateTime.of(start, LocalTime.parse(startTime));
            endAt   = LocalDateTime.of(start, LocalTime.parse(endTime));
        }

        leaveService.apply(me.getUserId(), leaveTypeCd, start, end, startAt, endAt, reason, approverIds);
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
