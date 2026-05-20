package egovframework.groupware.payroll.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.payroll.service.PayrollService;
import egovframework.groupware.payroll.service.PayrollVO;
import egovframework.groupware.payroll.service.SalaryContractVO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    /* ======================= 사원 화면 ======================= */

    @GetMapping("/payroll/my.do")
    public String my(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", payrollService.listMyPayrolls(me.getUserId()));
        return "payroll/my";
    }

    @GetMapping("/payroll/my/detail.do")
    public String myDetail(@AuthenticationPrincipal CustomUserDetails me,
                           @RequestParam String payMonth, Model model) {
        PayrollVO p = payrollService.findByUserAndMonth(me.getUserId(), payMonth);
        if (p == null) {
            model.addAttribute("payMonth", payMonth);
            return "payroll/my-empty";
        }
        PayrollVO detail = payrollService.findPayrollWithDetails(p.getPayId());
        model.addAttribute("p", detail);
        return "payroll/my-detail";
    }

    @GetMapping("/payroll/my/pdf.do")
    public ResponseEntity<byte[]> myPdf(@AuthenticationPrincipal CustomUserDetails me,
                                        @RequestParam String payMonth) {
        PayrollVO p = payrollService.findByUserAndMonth(me.getUserId(), payMonth);
        if (p == null) return ResponseEntity.notFound().build();
        byte[] pdf = payrollService.generatePayslipPdf(p.getPayId());
        String fname = URLEncoder.encode("급여명세서-" + payMonth + ".pdf", StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fname)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    /* ======================= 관리자 화면 ======================= */

    @GetMapping("/payroll/admin/period.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String adminPeriod() { return "payroll/admin-period"; }

    @PostMapping("/payroll/admin/generate.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String generate(@RequestParam String payMonth) {
        payrollService.generateForMonth(payMonth);
        return "redirect:/payroll/admin/list.do?payMonth=" + payMonth;
    }

    @GetMapping("/payroll/admin/list.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String adminList(@RequestParam(required = false) String payMonth,
                            @RequestParam(required = false) String status,
                            Model model) {
        if (payMonth == null || payMonth.isBlank()) payMonth = YearMonth.now().toString();
        List<PayrollVO> list = payrollService.listPayrolls(payMonth, status, 0, 200);
        model.addAttribute("list", list);
        model.addAttribute("payMonth", payMonth);
        model.addAttribute("status", status);
        return "payroll/admin-list";
    }

    @GetMapping("/payroll/admin/edit.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String adminEdit(@RequestParam Long payId, Model model) {
        PayrollVO p = payrollService.findPayrollWithDetails(payId);
        model.addAttribute("p", p);
        return "payroll/admin-edit";
    }

    @PostMapping("/payroll/admin/recalculate.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String recalculate(@RequestParam Long payId,
                              @RequestParam Map<String, String> body) {
        Map<String, Long> manual = new HashMap<>();
        for (Map.Entry<String, String> e : body.entrySet()) {
            if (e.getKey().startsWith("m_") && e.getValue() != null && !e.getValue().isBlank()) {
                try { manual.put(e.getKey().substring(2), Long.parseLong(e.getValue().replace(",", ""))); }
                catch (NumberFormatException ignore) {}
            }
        }
        payrollService.recalculate(payId, manual);
        return "redirect:/payroll/admin/edit.do?payId=" + payId;
    }

    @PostMapping("/payroll/admin/confirm.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String confirm(@RequestParam Long payId) {
        payrollService.confirm(payId);
        return "redirect:/payroll/admin/edit.do?payId=" + payId;
    }

    @PostMapping("/payroll/admin/pay.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String pay(@RequestParam Long payId,
                      @RequestParam(required = false) String paidDt) {
        LocalDate d = (paidDt == null || paidDt.isBlank()) ? LocalDate.now() : LocalDate.parse(paidDt);
        payrollService.markPaid(payId, d);
        return "redirect:/payroll/admin/edit.do?payId=" + payId;
    }

    @PostMapping("/payroll/admin/send-mail.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String sendMail(@RequestParam("payIds") List<Long> payIds,
                           @RequestParam(required = false) String payMonth) {
        payrollService.sendPayslipsByMail(payIds);
        return "redirect:/payroll/admin/list.do?payMonth=" + (payMonth == null ? YearMonth.now().toString() : payMonth);
    }

    @GetMapping("/payroll/admin/contract.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String contracts(@RequestParam(required = false) Long userId, Model model) {
        model.addAttribute("list", payrollService.listContracts(userId));
        model.addAttribute("userId", userId);
        return "payroll/admin-contract";
    }

    @PostMapping("/payroll/admin/contract.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String createContract(@RequestParam Long userId,
                                 @RequestParam String startDt,
                                 @RequestParam Long annualSalary,
                                 @RequestParam(required = false) String divisionTypeCd) {
        SalaryContractVO vo = new SalaryContractVO();
        vo.setUserId(userId);
        vo.setStartDt(LocalDate.parse(startDt));
        vo.setAnnualSalary(BigDecimal.valueOf(annualSalary));
        vo.setDivisionTypeCd(divisionTypeCd);
        payrollService.createContract(vo);
        return "redirect:/payroll/admin/contract.do?userId=" + userId;
    }

    @GetMapping("/payroll/admin/insurance-rate.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String rates(Model model) {
        model.addAttribute("rates", payrollService.findActiveRates(LocalDate.now()));
        return "payroll/admin-rates";
    }
}
