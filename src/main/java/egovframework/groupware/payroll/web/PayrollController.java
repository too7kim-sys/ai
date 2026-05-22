package egovframework.groupware.payroll.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.cmm.web.DownloadSupport;
import egovframework.groupware.payroll.service.PayrollService;
import egovframework.groupware.payroll.service.PayrollVO;
import egovframework.groupware.payroll.service.SalaryContractVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
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
    public void myPdf(@AuthenticationPrincipal CustomUserDetails me,
                      @RequestParam String payMonth,
                      HttpServletResponse resp) throws IOException {
        PayrollVO p = payrollService.findByUserAndMonth(me.getUserId(), payMonth);
        if (p == null) { resp.sendError(404); return; }
        byte[] pdf = payrollService.generatePayslipPdf(p.getPayId());
        DownloadSupport.write(resp, "급여명세서-" + payMonth + ".pdf", "application/pdf", pdf);
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
        model.addAttribute("deptSummary", payrollService.deptCostSummary(payMonth));
        return "payroll/admin-list";
    }

    /** 급여대장 CSV 다운로드. */
    @GetMapping("/payroll/admin/export.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public void exportCsv(@RequestParam String payMonth, HttpServletResponse resp) throws IOException {
        List<PayrollVO> list = payrollService.listPayrolls(payMonth, null, 0, 1000);
        StringBuilder sb = new StringBuilder("﻿"); // UTF-8 BOM (엑셀 한글)
        sb.append("급여월,부서,성명,과세,비과세,지급총액,공제총액,실수령액,상태\n");
        for (PayrollVO p : list) {
            sb.append(payMonth).append(',')
              .append(csv(p.getDeptNm())).append(',')
              .append(csv(p.getUserName())).append(',')
              .append(n(p.getTaxablePay())).append(',')
              .append(n(p.getNonTaxablePay())).append(',')
              .append(n(p.getGrossPay())).append(',')
              .append(n(p.getDeductionTotal())).append(',')
              .append(n(p.getNetPay())).append(',')
              .append(csv(p.getStatusCd())).append('\n');
        }
        DownloadSupport.write(resp, "급여대장-" + payMonth + ".csv",
                "text/csv; charset=UTF-8", sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    /** 급여이체 집계파일 — 확정/지급 상태 명세의 은행·계좌·실수령액. */
    @GetMapping("/payroll/admin/transfer-file.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public void transferFile(@RequestParam String payMonth, HttpServletResponse resp) throws IOException {
        List<PayrollVO> list = payrollService.listPayrolls(payMonth, null, 0, 1000);
        StringBuilder sb = new StringBuilder("﻿");
        sb.append("은행코드,계좌번호,예금주,이체금액,적요\n");
        long total = 0;
        int cnt = 0;
        for (PayrollVO p : list) {
            if (!"CONFIRMED".equals(p.getStatusCd()) && !"PAID".equals(p.getStatusCd())) continue;
            long amt = p.getNetPay() == null ? 0 : p.getNetPay().longValueExact();
            sb.append(csv(p.getBankCd())).append(',')
              .append(csv(p.getBankAccount())).append(',')
              .append(csv(p.getUserName())).append(',')
              .append(amt).append(',')
              .append(payMonth).append(" 급여\n");
            total += amt; cnt++;
        }
        sb.append(",,합계 ").append(cnt).append("건,").append(total).append(",\n");
        DownloadSupport.write(resp, "급여이체-" + payMonth + ".csv",
                "text/csv; charset=UTF-8", sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String csv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }
    private static long n(BigDecimal v) { return v == null ? 0 : v.longValueExact(); }

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
