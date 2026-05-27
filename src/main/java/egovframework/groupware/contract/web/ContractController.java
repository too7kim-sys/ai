package egovframework.groupware.contract.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.contract.service.ContractService;
import egovframework.groupware.contract.service.EmploymentContractVO;
import egovframework.groupware.payroll.service.PayrollService;
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

@Controller
public class ContractController {

    private final ContractService service;
    private final PayrollService payrollService;

    public ContractController(ContractService service, PayrollService payrollService) {
        this.service = service;
        this.payrollService = payrollService;
    }

    /* ------------------ 사원 ------------------ */

    @GetMapping("/contract/my.do")
    public String my(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.listByUser(me.getUserId()));
        return "contract/my";
    }

    @GetMapping("/contract/my/sign.do")
    public String signForm(@AuthenticationPrincipal CustomUserDetails me,
                           @RequestParam Long contractId, Model model) {
        EmploymentContractVO c = service.findById(contractId);
        if (c == null || !c.getUserId().equals(me.getUserId())) return "error/403";
        model.addAttribute("c", c);
        model.addAttribute("body", service.renderBody(contractId));
        return "contract/sign";
    }

    @PostMapping("/contract/my/sign.do")
    public String sign(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam Long contractId,
                       @RequestParam(required = false) String signature) {
        service.sign(contractId, me.getUserId(), signature);
        return "redirect:/contract/my.do";
    }

    @GetMapping("/contract/my/pdf.do")
    public ResponseEntity<byte[]> myPdf(@AuthenticationPrincipal CustomUserDetails me,
                                        @RequestParam Long contractId) {
        EmploymentContractVO c = service.findById(contractId);
        if (c == null || !c.getUserId().equals(me.getUserId())) return ResponseEntity.status(403).build();
        byte[] pdf = service.generatePdf(contractId);
        String fname = URLEncoder.encode("근로계약서-" + c.getContractNo() + ".pdf", StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fname)
            .contentType(MediaType.APPLICATION_PDF).body(pdf);
    }

    /* ------------------ HR 관리 ------------------ */

    @GetMapping("/contract/admin/list.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String adminList(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String status,
                            Model model) {
        model.addAttribute("list", service.listAll(keyword, status));
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "contract/admin-list";
    }

    @GetMapping("/contract/admin/write.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String writeForm(@RequestParam(required = false) Long userId, Model model) {
        model.addAttribute("templates", service.listTemplates());
        model.addAttribute("userId", userId);
        return "contract/admin-write";
    }

    @PostMapping("/contract/admin/write.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String write(@RequestParam Long userId,
                        @RequestParam Long templateId,
                        @RequestParam String contractTypeCd,
                        @RequestParam String startDt,
                        @RequestParam(required = false) String endDt,
                        @RequestParam String workplace,
                        @RequestParam String jobDescription,
                        @RequestParam(required = false, defaultValue = "40") String workHoursPerWeek,
                        @RequestParam(required = false) String workStartTime,
                        @RequestParam(required = false) String workEndTime,
                        @RequestParam(required = false) Integer breakMinutes,
                        @RequestParam(required = false) String weeklyHoliday,
                        @RequestParam(required = false) Integer annualPaidLeaveDays,
                        @RequestParam(required = false) Integer probationMonths,
                        @RequestParam(required = false) String specialTerms,
                        // 연봉(급여계약) 자동 생성용
                        @RequestParam(required = false) BigDecimal annualSalary,
                        @RequestParam(required = false) BigDecimal monthlyBaseSal,
                        @RequestParam(required = false, defaultValue = "MONTHLY") String divisionTypeCd,
                        @RequestParam(required = false, defaultValue = "25") Integer paymentDay,
                        // 4대보험 적용 여부 (기본 미체크 = false)
                        @RequestParam(required = false, defaultValue = "false") boolean applyNp,
                        @RequestParam(required = false, defaultValue = "false") boolean applyHi,
                        @RequestParam(required = false, defaultValue = "false") boolean applyEi,
                        @RequestParam(required = false, defaultValue = "false") boolean applyWc) {

        LocalDate start = LocalDate.parse(startDt);
        LocalDate end   = (endDt != null && !endDt.isBlank()) ? LocalDate.parse(endDt) : null;

        // 1) 연봉이 주어지면 급여(연봉)계약을 먼저 생성하고 ID 를 받아둔다.
        Long salaryContractId = null;
        if (annualSalary != null && annualSalary.signum() > 0) {
            SalaryContractVO sc = new SalaryContractVO();
            sc.setUserId(userId);
            sc.setStartDt(start);
            sc.setEndDt(end);
            sc.setAnnualSalary(annualSalary);
            sc.setMonthlyBaseSal(monthlyBaseSal);    // 미입력 시 service 가 연봉/12 로 자동 채움
            sc.setDivisionTypeCd(divisionTypeCd);
            sc.setPaymentDay(paymentDay);
            sc.setNote("근로계약 작성과 함께 자동 생성");
            salaryContractId = payrollService.createContract(sc);
        }

        // 2) 근로계약 본체
        EmploymentContractVO vo = new EmploymentContractVO();
        vo.setUserId(userId);
        vo.setTemplateId(templateId);
        vo.setContractTypeCd(contractTypeCd);
        vo.setStartDt(start);
        vo.setEndDt(end);
        vo.setWorkplace(workplace);
        vo.setJobDescription(jobDescription);
        vo.setWorkHoursPerWeek(new BigDecimal(workHoursPerWeek));
        vo.setWorkStartTime(workStartTime);
        vo.setWorkEndTime(workEndTime);
        vo.setBreakMinutes(breakMinutes);
        vo.setWeeklyHoliday(weeklyHoliday);
        vo.setAnnualPaidLeaveDays(annualPaidLeaveDays);
        vo.setProbationMonths(probationMonths);
        vo.setSpecialTerms(specialTerms);
        vo.setSalaryContractId(salaryContractId);
        vo.setAnnualSalary(annualSalary);            // 참조용 사본
        vo.setInsuranceAppliedJson(insuranceJson(applyNp, applyHi, applyEi, applyWc));
        vo.setStatusCd("DRAFT");
        Long id = service.create(vo);
        return "redirect:/contract/admin/preview.do?contractId=" + id;
    }

    private static String insuranceJson(boolean np, boolean hi, boolean ei, boolean wc) {
        return "{\"NP\":" + np + ",\"HI\":" + hi + ",\"EI\":" + ei + ",\"WC\":" + wc + "}";
    }

    @GetMapping("/contract/admin/preview.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String preview(@RequestParam Long contractId, Model model) {
        EmploymentContractVO c = service.findById(contractId);
        model.addAttribute("c", c);
        model.addAttribute("body", service.renderBody(contractId));
        return "contract/admin-preview";
    }

    @PostMapping("/contract/admin/sign-request.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String sendSignRequest(@RequestParam Long contractId) {
        service.sendSignRequest(contractId);
        return "redirect:/contract/admin/preview.do?contractId=" + contractId;
    }

    @PostMapping("/contract/admin/activate.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String activate(@RequestParam Long contractId) {
        service.activate(contractId);
        return "redirect:/contract/admin/preview.do?contractId=" + contractId;
    }
}
