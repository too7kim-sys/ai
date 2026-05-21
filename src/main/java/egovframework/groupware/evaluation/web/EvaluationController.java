package egovframework.groupware.evaluation.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.evaluation.service.EvalPeriodVO;
import egovframework.groupware.evaluation.service.EvalVO;
import egovframework.groupware.evaluation.service.EvaluationService;
import egovframework.groupware.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final UserService userService;

    public EvaluationController(EvaluationService evaluationService, UserService userService) {
        this.evaluationService = evaluationService;
        this.userService = userService;
    }

    /* ===== 평가자(매니저/HR) 평가 작성 화면 ===== */
    @GetMapping("/evaluation/sheet.do")
    public String sheet(@AuthenticationPrincipal CustomUserDetails me,
                        @RequestParam(required = false) Long periodId,
                        Model model) {
        EvalPeriodVO active = periodId != null
                ? evaluationService.findPeriod(periodId)
                : evaluationService.findActivePeriod();
        if (active == null) {
            model.addAttribute("periods", evaluationService.findAllPeriods());
            return "evaluation/no-period";
        }
        model.addAttribute("period", active);
        model.addAttribute("evaluations",
                evaluationService.findMyEvaluations(active.getPeriodId(), me.getUserId()));
        model.addAttribute("myResults", evaluationService.findMyResults(me.getUserId()));
        model.addAttribute("periods", evaluationService.findAllPeriods());
        return "evaluation/sheet";
    }

    @GetMapping("/evaluation/edit.do")
    public String edit(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam Long periodId,
                       @RequestParam Long evaluateeId,
                       Model model) {
        EvalVO eval = evaluationService.findOrInit(periodId, evaluateeId, me.getUserId());
        model.addAttribute("eval", eval);
        model.addAttribute("period", evaluationService.findPeriod(periodId));
        model.addAttribute("forms", evaluationService.findAllForms());
        if (!evaluationService.findAllForms().isEmpty()) {
            model.addAttribute("items",
                    evaluationService.parseItems(evaluationService.findAllForms().get(0).getItemsJson()));
        }
        return "evaluation/edit";
    }

    @PostMapping("/evaluation/save.do")
    public String save(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam Long periodId,
                       @RequestParam Long evaluateeId,
                       @RequestParam(required = false) String comment,
                       @RequestParam Map<String, String> body) {
        Map<String, Integer> scores = new HashMap<>();
        Map<String, Integer> weights = new HashMap<>();
        for (Map.Entry<String, String> e : body.entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            if (v == null || v.isBlank()) continue;
            try {
                if (k.startsWith("s_")) scores.put(k.substring(2), Integer.parseInt(v));
                else if (k.startsWith("w_")) weights.put(k.substring(2), Integer.parseInt(v));
            } catch (NumberFormatException ignore) {}
        }
        evaluationService.saveEvaluation(periodId, evaluateeId, me.getUserId(), scores, weights, comment);
        return "redirect:/evaluation/sheet.do?periodId=" + periodId;
    }

    /* ===== 관리자: 평가 기간/양식 관리 + 전체 결과 ===== */
    @GetMapping("/evaluation/admin/periods.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String periods(Model model) {
        model.addAttribute("periods", evaluationService.findAllPeriods());
        model.addAttribute("forms", evaluationService.findAllForms());
        return "evaluation/admin-periods";
    }

    @PostMapping("/evaluation/admin/period/create.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String createPeriod(@RequestParam String periodNm,
                               @RequestParam String startDt,
                               @RequestParam String endDt) {
        evaluationService.createPeriod(periodNm, LocalDate.parse(startDt), LocalDate.parse(endDt));
        return "redirect:/evaluation/admin/periods.do";
    }

    @PostMapping("/evaluation/admin/period/status.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String changePeriodStatus(@RequestParam Long periodId,
                                     @RequestParam String statusCd) {
        evaluationService.changePeriodStatus(periodId, statusCd);
        return "redirect:/evaluation/admin/periods.do";
    }

    @PostMapping("/evaluation/admin/form/create.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String createForm(@RequestParam String formNm,
                             @RequestParam String itemsJson) {
        evaluationService.createForm(formNm, itemsJson);
        return "redirect:/evaluation/admin/periods.do";
    }

    @GetMapping("/evaluation/admin/results.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String results(@RequestParam(required = false) Long periodId, Model model) {
        if (periodId == null) {
            EvalPeriodVO active = evaluationService.findActivePeriod();
            if (active != null) periodId = active.getPeriodId();
        }
        model.addAttribute("periods", evaluationService.findAllPeriods());
        model.addAttribute("period", periodId == null ? null : evaluationService.findPeriod(periodId));
        model.addAttribute("results", periodId == null
                ? java.util.Collections.emptyList()
                : evaluationService.findAllByPeriod(periodId));
        return "evaluation/admin-results";
    }
}
