package egovframework.groupware.calendar.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.calendar.service.CalEventVO;
import egovframework.groupware.calendar.service.CalendarService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CalendarController {

    private final CalendarService service;

    public CalendarController(CalendarService service) { this.service = service; }

    @GetMapping("/calendar/main.do")
    public String main(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("me", me.getUser());
        return "calendar/main";
    }

    /** FullCalendar 이벤트 JSON 엔드포인트. */
    @GetMapping("/calendar/events.json")
    @ResponseBody
    public List<Map<String, Object>> events(@AuthenticationPrincipal CustomUserDetails me,
                                            @RequestParam String start,
                                            @RequestParam String end) {
        LocalDateTime from = parse(start);
        LocalDateTime to = parse(end);
        List<CalEventVO> list = service.findByRange(from, to, me.getUserId(), me.getDeptId());
        List<Map<String, Object>> out = new ArrayList<>();
        DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        for (CalEventVO e : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", e.getEvtId());
            m.put("title", e.getTitle());
            m.put("start", e.getStartDt().format(iso));
            m.put("end", e.getEndDt().format(iso));
            m.put("color", colorFor(e));
            m.put("extendedProps", Map.of(
                    "scope", e.getScopeCd(),
                    "owner", e.getOwnerName() == null ? "" : e.getOwnerName(),
                    "dept", e.getDeptNm() == null ? "" : e.getDeptNm(),
                    "memo", e.getMemo() == null ? "" : e.getMemo()
            ));
            out.add(m);
        }
        return out;
    }

    @PostMapping("/calendar/save.do")
    public String save(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam(required = false) Long evtId,
                       @RequestParam String title,
                       @RequestParam String startDt,
                       @RequestParam String endDt,
                       @RequestParam(required = false) String scopeCd,
                       @RequestParam(required = false) String color,
                       @RequestParam(required = false) String memo) {
        CalEventVO vo = new CalEventVO();
        vo.setEvtId(evtId);
        vo.setTitle(title);
        vo.setStartDt(LocalDateTime.parse(startDt));
        vo.setEndDt(LocalDateTime.parse(endDt));
        vo.setScopeCd(scopeCd);
        vo.setColor(color);
        vo.setMemo(memo);
        if ("DEPT".equals(scopeCd)) vo.setDeptId(me.getDeptId());
        if (evtId == null) service.create(vo, me.getUserId());
        else service.update(vo, me.getUserId(), me.getRoleCd());
        return "redirect:/calendar/main.do";
    }

    @PostMapping("/calendar/delete.do")
    public String delete(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long evtId) {
        service.delete(evtId, me.getUserId());
        return "redirect:/calendar/main.do";
    }

    private LocalDateTime parse(String s) {
        try {
            // FullCalendar 가 보내는 ISO with offset 또는 단순 LocalDateTime 둘 다 허용
            return OffsetDateTime.parse(s).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e1) {
            try { return LocalDateTime.parse(s); } catch (Exception e2) { return LocalDateTime.now(); }
        }
    }

    private String colorFor(CalEventVO e) {
        if (e.getColor() != null && !e.getColor().isBlank()) return e.getColor();
        return switch (e.getScopeCd()) {
            case "COMPANY" -> "#dc3545";
            case "DEPT"    -> "#0d6efd";
            default        -> "#198754";
        };
    }
}
