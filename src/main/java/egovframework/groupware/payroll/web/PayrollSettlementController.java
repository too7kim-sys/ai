package egovframework.groupware.payroll.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.payroll.service.BonusService;
import egovframework.groupware.payroll.service.BonusVO;
import egovframework.groupware.payroll.service.SettlementService;
import egovframework.groupware.payroll.service.SeveranceVO;
import egovframework.groupware.payroll.service.YearEndTaxVO;
import egovframework.groupware.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

/**
 * 급여 부가 기능 — 상여·성과급 / 연말정산 / 퇴직정산 (ADMIN·HR_MANAGER).
 */
@Controller
@PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
public class PayrollSettlementController {

    private final BonusService bonusService;
    private final SettlementService settlementService;
    private final UserService userService;

    public PayrollSettlementController(BonusService bonusService,
                                       SettlementService settlementService,
                                       UserService userService) {
        this.bonusService = bonusService;
        this.settlementService = settlementService;
        this.userService = userService;
    }

    /* ==================== 상여 / 성과급 ==================== */

    @GetMapping("/payroll/admin/bonus.do")
    public String bonusList(@RequestParam(required = false) String payMonth,
                            @RequestParam(required = false) String bonusTypeCd,
                            Model model) {
        if (payMonth == null || payMonth.isBlank()) payMonth = YearMonth.now().toString();
        model.addAttribute("list", bonusService.search(payMonth, bonusTypeCd));
        model.addAttribute("payMonth", payMonth);
        model.addAttribute("bonusTypeCd", bonusTypeCd);
        model.addAttribute("users", userService.listAll());
        return "payroll/admin-bonus";
    }

    @PostMapping("/payroll/admin/bonus/create.do")
    public String bonusCreate(@AuthenticationPrincipal CustomUserDetails me,
                              @RequestParam String payMonth,
                              @RequestParam String bonusTypeCd,
                              @RequestParam("userIds") List<Long> userIds,
                              @RequestParam Long amount,
                              @RequestParam(required = false) String taxableYn,
                              @RequestParam(required = false) String memo) {
        bonusService.bulkCreate(payMonth, bonusTypeCd, userIds,
                BigDecimal.valueOf(amount), taxableYn, memo, me.getUserId());
        return "redirect:/payroll/admin/bonus.do?payMonth=" + payMonth;
    }

    @PostMapping("/payroll/admin/bonus/delete.do")
    public String bonusDelete(@RequestParam Long bonusId,
                              @RequestParam String payMonth) {
        bonusService.delete(bonusId);
        return "redirect:/payroll/admin/bonus.do?payMonth=" + payMonth;
    }

    /* ==================== 연말정산 ==================== */

    @GetMapping("/payroll/admin/year-end.do")
    public String yearEndList(@RequestParam(required = false) Integer taxYear, Model model) {
        int year = taxYear == null ? Year.now().getValue() - 1 : taxYear;
        model.addAttribute("taxYear", year);
        model.addAttribute("list", settlementService.listYearEnd(year));
        model.addAttribute("users", userService.listAll());
        return "payroll/admin-year-end";
    }

    /** 계산 미리보기 (저장 안 함). */
    @GetMapping("/payroll/admin/year-end/compute.do")
    public String yearEndCompute(@RequestParam Long userId,
                                 @RequestParam int taxYear,
                                 Model model) {
        model.addAttribute("taxYear", taxYear);
        model.addAttribute("preview", settlementService.computeYearEnd(userId, taxYear));
        model.addAttribute("list", settlementService.listYearEnd(taxYear));
        model.addAttribute("users", userService.listAll());
        return "payroll/admin-year-end";
    }

    @PostMapping("/payroll/admin/year-end/save.do")
    public String yearEndSave(@RequestParam Long userId, @RequestParam int taxYear) {
        YearEndTaxVO vo = settlementService.computeYearEnd(userId, taxYear);
        settlementService.saveYearEnd(vo);
        return "redirect:/payroll/admin/year-end.do?taxYear=" + taxYear;
    }

    @PostMapping("/payroll/admin/year-end/confirm.do")
    public String yearEndConfirm(@RequestParam Long userId, @RequestParam int taxYear) {
        settlementService.confirmYearEnd(userId, taxYear);
        return "redirect:/payroll/admin/year-end.do?taxYear=" + taxYear;
    }

    /* ==================== 퇴직정산 ==================== */

    @GetMapping("/payroll/admin/severance.do")
    public String severanceList(Model model) {
        model.addAttribute("list", settlementService.listSeverance());
        model.addAttribute("users", userService.listAll());
        return "payroll/admin-severance";
    }

    /** 퇴직금 계산 미리보기. */
    @GetMapping("/payroll/admin/severance/compute.do")
    public String severanceCompute(@RequestParam Long userId,
                                   @RequestParam String leaveDate,
                                   Model model) {
        model.addAttribute("preview", settlementService.computeSeverance(userId, LocalDate.parse(leaveDate)));
        model.addAttribute("list", settlementService.listSeverance());
        model.addAttribute("users", userService.listAll());
        return "payroll/admin-severance";
    }

    @PostMapping("/payroll/admin/severance/save.do")
    public String severanceSave(@AuthenticationPrincipal CustomUserDetails me,
                                @RequestParam Long userId,
                                @RequestParam String leaveDate) {
        SeveranceVO vo = settlementService.computeSeverance(userId, LocalDate.parse(leaveDate));
        settlementService.saveSeverance(vo, me.getUserId());
        return "redirect:/payroll/admin/severance.do";
    }

    @PostMapping("/payroll/admin/severance/pay.do")
    public String severancePay(@RequestParam Long sevId,
                               @RequestParam(required = false) String paidDt) {
        settlementService.markSeverancePaid(sevId,
                paidDt == null || paidDt.isBlank() ? LocalDate.now() : LocalDate.parse(paidDt));
        return "redirect:/payroll/admin/severance.do";
    }
}
