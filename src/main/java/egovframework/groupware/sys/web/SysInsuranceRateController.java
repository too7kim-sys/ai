package egovframework.groupware.sys.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.payroll.service.InsuranceRateVO;
import egovframework.groupware.payroll.service.PayrollService;
import egovframework.groupware.sys.service.AuditLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 4대보험 요율 관리 — 매년 변경되는 국민연금/건강보험/장기요양/고용보험/산재 요율을
 * effective_from/to 단위로 누적 관리한다. 급여 계산은 급여월 1일 시점의 유효 요율을
 * 자동 선택하므로, 이 화면에서 새 행을 INSERT 만 하면 다음 해 1월부터 적용된다.
 */
@Controller
@PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
public class SysInsuranceRateController {

    private final PayrollService payrollService;
    private final AuditLogService auditService;

    public SysInsuranceRateController(PayrollService payrollService, AuditLogService auditService) {
        this.payrollService = payrollService;
        this.auditService = auditService;
    }

    @GetMapping("/sys/insurance-rate.do")
    public String list(Model model) {
        model.addAttribute("rates", payrollService.listAllRates());
        model.addAttribute("activeRates", payrollService.findActiveRates(LocalDate.now()));
        return "sys/insurance-rate";
    }

    @PostMapping("/sys/insurance-rate.do")
    public String create(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam String insuranceCd,
                         @RequestParam String effectiveFrom,
                         @RequestParam(required = false) String effectiveTo,
                         @RequestParam BigDecimal employeeRate,
                         @RequestParam BigDecimal employerRate,
                         @RequestParam(required = false) BigDecimal baseMin,
                         @RequestParam(required = false) BigDecimal baseMax,
                         @RequestParam(required = false) String note,
                         HttpServletRequest req,
                         RedirectAttributes ra) {
        InsuranceRateVO vo = build(null, insuranceCd, effectiveFrom, effectiveTo,
                employeeRate, employerRate, baseMin, baseMax, note);
        Long id = payrollService.createRate(vo);
        auditService.log(me.getUserId(), "INSURANCE_RATE_CREATE", "INSURANCE_RATE",
                String.valueOf(id), null, toJson(vo), ip(req));
        ra.addFlashAttribute("flashMsg", insuranceCd + " 요율이 추가되었습니다.");
        ra.addFlashAttribute("flashType", "success");
        return "redirect:/sys/insurance-rate.do";
    }

    @PostMapping("/sys/insurance-rate/edit.do")
    public String edit(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam Long rateId,
                       @RequestParam String insuranceCd,
                       @RequestParam String effectiveFrom,
                       @RequestParam(required = false) String effectiveTo,
                       @RequestParam BigDecimal employeeRate,
                       @RequestParam BigDecimal employerRate,
                       @RequestParam(required = false) BigDecimal baseMin,
                       @RequestParam(required = false) BigDecimal baseMax,
                       @RequestParam(required = false) String note,
                       HttpServletRequest req,
                       RedirectAttributes ra) {
        InsuranceRateVO before = payrollService.findRate(rateId);
        InsuranceRateVO vo = build(rateId, insuranceCd, effectiveFrom, effectiveTo,
                employeeRate, employerRate, baseMin, baseMax, note);
        payrollService.updateRate(vo);
        auditService.log(me.getUserId(), "INSURANCE_RATE_EDIT", "INSURANCE_RATE",
                String.valueOf(rateId), toJson(before), toJson(vo), ip(req));
        ra.addFlashAttribute("flashMsg", "요율이 수정되었습니다.");
        ra.addFlashAttribute("flashType", "success");
        return "redirect:/sys/insurance-rate.do";
    }

    @PostMapping("/sys/insurance-rate/delete.do")
    public String delete(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long rateId,
                         HttpServletRequest req,
                         RedirectAttributes ra) {
        InsuranceRateVO before = payrollService.findRate(rateId);
        payrollService.deleteRate(rateId);
        auditService.log(me.getUserId(), "INSURANCE_RATE_DELETE", "INSURANCE_RATE",
                String.valueOf(rateId), toJson(before), null, ip(req));
        ra.addFlashAttribute("flashMsg", "요율이 삭제되었습니다.");
        ra.addFlashAttribute("flashType", "success");
        return "redirect:/sys/insurance-rate.do";
    }

    private InsuranceRateVO build(Long rateId, String insuranceCd, String effectiveFrom,
                                   String effectiveTo, BigDecimal employeeRate,
                                   BigDecimal employerRate, BigDecimal baseMin,
                                   BigDecimal baseMax, String note) {
        InsuranceRateVO vo = new InsuranceRateVO();
        vo.setRateId(rateId);
        vo.setInsuranceCd(insuranceCd);
        vo.setEffectiveFrom(LocalDate.parse(effectiveFrom));
        if (effectiveTo != null && !effectiveTo.isBlank()) {
            vo.setEffectiveTo(LocalDate.parse(effectiveTo));
        }
        vo.setEmployeeRate(employeeRate);
        vo.setEmployerRate(employerRate);
        vo.setBaseMin(baseMin);
        vo.setBaseMax(baseMax);
        vo.setNote(note);
        return vo;
    }

    private static String toJson(InsuranceRateVO v) {
        if (v == null) return null;
        return "{"
                + "\"insuranceCd\":\"" + n(v.getInsuranceCd()) + "\""
                + ",\"effectiveFrom\":\"" + v.getEffectiveFrom() + "\""
                + ",\"effectiveTo\":\"" + v.getEffectiveTo() + "\""
                + ",\"employeeRate\":" + v.getEmployeeRate()
                + ",\"employerRate\":" + v.getEmployerRate()
                + ",\"baseMin\":" + v.getBaseMin()
                + ",\"baseMax\":" + v.getBaseMax()
                + "}";
    }

    private static String n(String s) { return s == null ? "" : s.replace("\"", "\\\""); }

    private String ip(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) return h.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}
