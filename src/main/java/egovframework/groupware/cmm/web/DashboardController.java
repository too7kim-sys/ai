package egovframework.groupware.cmm.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.calendar.service.CalendarService;
import egovframework.groupware.leave.service.LeaveService;
import egovframework.groupware.notice.service.NoticeService;
import egovframework.groupware.notification.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Year;

@Controller
public class DashboardController {

    private final NoticeService noticeService;
    private final CalendarService calendarService;
    private final LeaveService leaveService;
    private final NotificationService notificationService;

    public DashboardController(NoticeService noticeService,
                               CalendarService calendarService,
                               LeaveService leaveService,
                               NotificationService notificationService) {
        this.noticeService = noticeService;
        this.calendarService = calendarService;
        this.leaveService = leaveService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard.do")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        if (me == null) return "dashboard";
        model.addAttribute("me", me.getUser());
        model.addAttribute("notices", noticeService.findLatest(5));
        model.addAttribute("todayEventCount",
                calendarService.countToday(me.getUserId(), me.getDeptId()));
        model.addAttribute("leaveBalance",
                leaveService.findBalance(me.getUserId(), Year.now().getValue()));
        model.addAttribute("recentNotifications",
                notificationService.findByUser(me.getUserId(), true, 5));
        return "dashboard";
    }
}
