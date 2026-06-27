package egovframework.groupware.leave.web;

import egovframework.groupware.leave.service.LeaveService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.Year;

@Controller
@RequestMapping("/leave/admin")
public class LeaveAdminController {

    private final LeaveService service;

    public LeaveAdminController(LeaveService service) {
        this.service = service;
    }

    @GetMapping("/balance.do")
    public String list(@RequestParam(required = false) Integer year,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        int y = (year != null) ? year : Year.now().getValue();
        model.addAttribute("year", y);
        model.addAttribute("keyword", keyword);
        model.addAttribute("list", service.listBalances(y, keyword));
        return "leave/admin/balance";
    }

    @PostMapping("/balance/update.do")
    public String update(@RequestParam Long userId,
                         @RequestParam int year,
                         @RequestParam BigDecimal days,
                         @RequestParam(required = false) String keyword,
                         RedirectAttributes ra) {
        if (days.signum() < 0) days = BigDecimal.ZERO;
        service.grantInitialBalance(userId, year, days);
        ra.addAttribute("year", year);
        if (keyword != null && !keyword.isEmpty()) ra.addAttribute("keyword", keyword);
        ra.addFlashAttribute("msg", "부여 일수가 수정되었습니다.");
        return "redirect:/leave/admin/balance.do";
    }

    @PostMapping("/balance/grant-all.do")
    public String grantAll(@RequestParam int year,
                           @RequestParam BigDecimal days,
                           RedirectAttributes ra) {
        if (days.signum() < 0) days = BigDecimal.ZERO;
        int n = service.grantAll(year, days);
        ra.addAttribute("year", year);
        ra.addFlashAttribute("msg", n + "명에게 " + days + "일을 일괄 부여했습니다.");
        return "redirect:/leave/admin/balance.do";
    }

    @PostMapping("/balance/grant-by-tenure.do")
    public String grantByTenure(@RequestParam int year, RedirectAttributes ra) {
        int n = service.grantByTenure(year);
        ra.addAttribute("year", year);
        ra.addFlashAttribute("msg", n + "명에게 입사일 기반 표준 연차를 부여했습니다.");
        return "redirect:/leave/admin/balance.do";
    }
}
