package egovframework.groupware.attendance.web;

import egovframework.groupware.attendance.service.AttendanceService;
import egovframework.groupware.auth.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;

@Controller
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) { this.service = service; }

    @GetMapping("/attendance/my.do")
    public String my(@AuthenticationPrincipal CustomUserDetails me,
                     @RequestParam(required = false) String month,
                     Model model) {
        YearMonth ym = (month == null || month.isBlank()) ? YearMonth.now() : YearMonth.parse(month);
        model.addAttribute("today", service.findToday(me.getUserId()));
        model.addAttribute("month", ym);
        model.addAttribute("list", service.findMyMonth(me.getUserId(), ym.getYear(), ym.getMonthValue()));
        return "attendance/my";
    }

    @PostMapping("/attendance/check-in.do")
    public String checkIn(@AuthenticationPrincipal CustomUserDetails me) {
        service.checkIn(me.getUserId());
        return "redirect:/attendance/my.do";
    }

    @PostMapping("/attendance/check-out.do")
    public String checkOut(@AuthenticationPrincipal CustomUserDetails me) {
        service.checkOut(me.getUserId());
        return "redirect:/attendance/my.do";
    }

    @GetMapping("/attendance/admin/report.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER','MANAGER')")
    public String report(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam(required = false) String month,
                         @RequestParam(required = false) Long deptId,
                         Model model) {
        YearMonth ym = (month == null || month.isBlank()) ? YearMonth.now() : YearMonth.parse(month);
        // MANAGER 는 본인 부서만 조회 가능
        if ("MANAGER".equals(me.getRoleCd())) deptId = me.getDeptId();
        model.addAttribute("month", ym);
        model.addAttribute("deptId", deptId);
        model.addAttribute("list", service.findMonthReport(ym.getYear(), ym.getMonthValue(), deptId));
        return "attendance/admin-report";
    }
}
