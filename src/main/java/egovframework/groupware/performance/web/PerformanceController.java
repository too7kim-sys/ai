package egovframework.groupware.performance.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.evaluation.service.EvalPeriodVO;
import egovframework.groupware.evaluation.service.EvaluationService;
import egovframework.groupware.performance.service.PerfVO;
import egovframework.groupware.performance.service.PerformanceService;
import egovframework.groupware.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class PerformanceController {

    private final PerformanceService perfService;
    private final EvaluationService evalService;
    private final UserService userService;

    public PerformanceController(PerformanceService perfService,
                                 EvaluationService evalService,
                                 UserService userService) {
        this.perfService = perfService;
        this.evalService = evalService;
        this.userService = userService;
    }

    /* ===== 내 KPI ===== */
    @GetMapping("/performance/my.do")
    public String my(@AuthenticationPrincipal CustomUserDetails me,
                     @RequestParam(required = false) Long periodId,
                     Model model) {
        EvalPeriodVO period = resolvePeriod(periodId);
        model.addAttribute("periods", evalService.findAllPeriods());
        model.addAttribute("period", period);
        List<PerfVO> items = period == null
                ? List.of()
                : perfService.findByUserAndPeriod(me.getUserId(), period.getPeriodId());
        model.addAttribute("items", items);
        model.addAttribute("avgAch", perfService.weightedAchievement(items));
        return "performance/my";
    }

    @PostMapping("/performance/save.do")
    public String save(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam(required = false) Long perfId,
                       @RequestParam Long periodId,
                       @RequestParam(required = false) Long userId,
                       @RequestParam String goal,
                       @RequestParam String kpi,
                       @RequestParam(required = false) String unit,
                       @RequestParam(required = false) BigDecimal targetVal,
                       @RequestParam(required = false) BigDecimal actualVal,
                       @RequestParam(required = false) BigDecimal weight) {
        PerfVO vo = new PerfVO();
        vo.setPerfId(perfId);
        vo.setPeriodId(periodId);
        vo.setUserId(userId != null ? userId : me.getUserId());
        vo.setGoal(goal);
        vo.setKpi(kpi);
        vo.setUnit(unit);
        vo.setTargetVal(targetVal);
        vo.setActualVal(actualVal);
        vo.setWeight(weight);
        perfService.save(vo, me.getUserId(), me.getRoleCd());
        if (userId != null && !userId.equals(me.getUserId())) {
            return "redirect:/performance/team.do?periodId=" + periodId;
        }
        return "redirect:/performance/my.do?periodId=" + periodId;
    }

    @PostMapping("/performance/delete.do")
    public String delete(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long perfId,
                         @RequestParam(required = false) Long periodId) {
        perfService.delete(perfId, me.getUserId(), me.getRoleCd());
        return "redirect:/performance/my.do" + (periodId != null ? "?periodId=" + periodId : "");
    }

    /* ===== 팀 KPI (매니저/HR/관리자) ===== */
    @GetMapping("/performance/team.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER','MANAGER')")
    public String team(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam(required = false) Long periodId,
                       @RequestParam(required = false) Long deptId,
                       Model model) {
        EvalPeriodVO period = resolvePeriod(periodId);
        model.addAttribute("periods", evalService.findAllPeriods());
        model.addAttribute("period", period);
        Long targetDept = deptId != null ? deptId : me.getDeptId();
        model.addAttribute("deptId", targetDept);
        model.addAttribute("members", userService.listByDept(targetDept));
        if (period != null && targetDept != null) {
            List<PerfVO> items = perfService.findByDeptAndPeriod(targetDept, period.getPeriodId());
            model.addAttribute("items", items);
            model.addAttribute("avgAch", perfService.weightedAchievement(items));
        } else {
            model.addAttribute("items", List.of());
            model.addAttribute("avgAch", BigDecimal.ZERO);
        }
        return "performance/team";
    }

    /* ===== 전사 KPI (HR/관리자) ===== */
    @GetMapping("/performance/admin/all.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String all(@RequestParam(required = false) Long periodId, Model model) {
        EvalPeriodVO period = resolvePeriod(periodId);
        model.addAttribute("periods", evalService.findAllPeriods());
        model.addAttribute("period", period);
        List<PerfVO> items = period == null ? List.of() : perfService.findByPeriod(period.getPeriodId());
        model.addAttribute("items", items);
        model.addAttribute("avgAch", perfService.weightedAchievement(items));
        return "performance/admin-all";
    }

    private EvalPeriodVO resolvePeriod(Long periodId) {
        if (periodId != null) return evalService.findPeriod(periodId);
        EvalPeriodVO active = evalService.findActivePeriod();
        if (active != null) return active;
        List<EvalPeriodVO> all = evalService.findAllPeriods();
        return all.isEmpty() ? null : all.get(0);
    }
}
