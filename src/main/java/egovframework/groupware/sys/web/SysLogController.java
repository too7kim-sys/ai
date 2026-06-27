package egovframework.groupware.sys.web;

import egovframework.groupware.auth.mapper.LoginLogMapper;
import egovframework.groupware.cmm.Paging;
import egovframework.groupware.sys.service.AuditLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 시스템 관리 - 로그인 이력 / 감사 로그 조회 (ADMIN 전용).
 */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class SysLogController {

    private final LoginLogMapper loginLogMapper;
    private final AuditLogService auditService;

    public SysLogController(LoginLogMapper loginLogMapper, AuditLogService auditService) {
        this.loginLogMapper = loginLogMapper;
        this.auditService = auditService;
    }

    @GetMapping("/sys/login-log.do")
    public String loginLog(@RequestParam(required = false) String emailKeyword,
                           @RequestParam(required = false) String successYn,
                           @RequestParam(required = false) String fromDate,
                           @RequestParam(required = false) String toDate,
                           @RequestParam(required = false, defaultValue = "1") int page,
                           Model model) {
        Paging p = new Paging();
        p.setPage(Math.max(1, page));
        p.setSize(30);
        LocalDateTime from = parseDate(fromDate, LocalTime.MIN);
        LocalDateTime to = parseDate(toDate, LocalTime.MAX);

        model.addAttribute("list",
                loginLogMapper.search(emailKeyword, blank(successYn), from, to, p.getOffset(), p.getSize()));
        p.setTotal(loginLogMapper.count(emailKeyword, blank(successYn), from, to));
        model.addAttribute("paging", p);
        model.addAttribute("emailKeyword", emailKeyword);
        model.addAttribute("successYn", successYn);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        return "sys/login-log";
    }

    @GetMapping("/sys/audit-log.do")
    public String auditLog(@RequestParam(required = false) String actionType,
                           @RequestParam(required = false) String entityName,
                           @RequestParam(required = false) Long userId,
                           @RequestParam(required = false) String fromDate,
                           @RequestParam(required = false) String toDate,
                           @RequestParam(required = false, defaultValue = "1") int page,
                           Model model) {
        Paging p = new Paging();
        p.setPage(Math.max(1, page));
        p.setSize(30);
        LocalDateTime from = parseDate(fromDate, LocalTime.MIN);
        LocalDateTime to = parseDate(toDate, LocalTime.MAX);

        model.addAttribute("list",
                auditService.search(blank(actionType), blank(entityName), userId, from, to,
                        p.getOffset(), p.getSize()));
        p.setTotal(auditService.count(blank(actionType), blank(entityName), userId, from, to));
        model.addAttribute("paging", p);
        model.addAttribute("actionType", actionType);
        model.addAttribute("entityName", entityName);
        model.addAttribute("userId", userId);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("actionTypes", auditService.distinctActionTypes());
        model.addAttribute("entityNames", auditService.distinctEntityNames());
        return "sys/audit-log";
    }

    private LocalDateTime parseDate(String s, LocalTime t) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s).atTime(t);
    }

    private String blank(String s) { return (s == null || s.isBlank()) ? null : s; }
}
