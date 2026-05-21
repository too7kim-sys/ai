package egovframework.groupware.hr.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.hr.service.FamilyVO;
import egovframework.groupware.hr.service.HrRecordVO;
import egovframework.groupware.hr.service.HrService;
import egovframework.groupware.user.service.UserService;
import egovframework.groupware.user.service.UserVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class HrController {

    private final HrService hrService;
    private final UserService userService;

    public HrController(HrService hrService, UserService userService) {
        this.hrService = hrService;
        this.userService = userService;
    }

    /* ===== 조직도 ===== */
    @GetMapping("/hr/org.do")
    public String org(@RequestParam(required = false) Long deptId, Model model) {
        model.addAttribute("flat", hrService.findDeptFlat());
        if (deptId != null) {
            model.addAttribute("dept", deptId);
            model.addAttribute("members", userService.listByDept(deptId));
        }
        return "hr/org";
    }

    /* ===== 인사기록 ===== */
    @GetMapping("/hr/record.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String records(@RequestParam(required = false) Long userId, Model model) {
        if (userId != null) {
            model.addAttribute("user", userService.findById(userId));
            model.addAttribute("records", hrService.findRecordsByUser(userId));
        } else {
            model.addAttribute("recent", hrService.findRecentRecords(50));
        }
        model.addAttribute("users", userService.listAll());
        model.addAttribute("userId", userId);
        return "hr/record";
    }

    @PostMapping("/hr/record.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String addRecord(@RequestParam Long userId,
                            @RequestParam String categoryCd,
                            @RequestParam String title,
                            @RequestParam(required = false) String content,
                            @RequestParam(required = false) String eventDt) {
        HrRecordVO vo = new HrRecordVO();
        vo.setUserId(userId);
        vo.setCategoryCd(categoryCd);
        vo.setTitle(title);
        vo.setContent(content);
        if (eventDt != null && !eventDt.isBlank()) vo.setEventDt(LocalDate.parse(eventDt));
        hrService.createRecord(vo);
        return "redirect:/hr/record.do?userId=" + userId;
    }

    @PostMapping("/hr/record/delete.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String deleteRecord(@AuthenticationPrincipal CustomUserDetails me,
                               @RequestParam Long recId,
                               @RequestParam Long userId) {
        hrService.deleteRecord(recId, me.getUserId(), me.getRoleCd());
        return "redirect:/hr/record.do?userId=" + userId;
    }

    /* ===== 인사이력 ===== */
    @GetMapping("/hr/history.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String history(@RequestParam(required = false) Long userId,
                          @RequestParam(required = false) String changeTypeCd,
                          Model model) {
        if (userId != null) {
            model.addAttribute("user", userService.findById(userId));
            model.addAttribute("histories", hrService.findHistoryByUser(userId));
        } else {
            model.addAttribute("histories", hrService.findAllHistory(changeTypeCd, 200));
        }
        model.addAttribute("users", userService.listAll());
        model.addAttribute("depts", hrService.findAllDepts());
        model.addAttribute("userId", userId);
        model.addAttribute("changeTypeCd", changeTypeCd);
        return "hr/history";
    }

    /* HR 인사발령 (부서/직급/역할 변경 → 자동 이력 기록) */
    @PostMapping("/hr/admin/transfer.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String transfer(@AuthenticationPrincipal CustomUserDetails me,
                           @RequestParam Long userId,
                           @RequestParam(required = false) Long deptId,
                           @RequestParam(required = false) Long positionId,
                           @RequestParam(required = false) String roleCd,
                           @RequestParam(required = false) String effectiveDt) {
        LocalDate eff = (effectiveDt == null || effectiveDt.isBlank())
                ? LocalDate.now() : LocalDate.parse(effectiveDt);
        hrService.applyHrChange(userId, deptId, positionId, roleCd, eff, me.getUserId());
        return "redirect:/hr/history.do?userId=" + userId;
    }

    /* ===== 부양가족 ===== */
    @GetMapping("/hr/family.do")
    public String family(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        UserVO user = userService.findById(me.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("families", hrService.findFamilyByUser(me.getUserId()));
        return "hr/family";
    }

    @PostMapping("/hr/family.do")
    public String addFamily(@AuthenticationPrincipal CustomUserDetails me,
                            @RequestParam String relationCd,
                            @RequestParam String name,
                            @RequestParam(required = false) String birthDt,
                            @RequestParam(required = false) String dependentYn,
                            @RequestParam(required = false) String elderlyYn,
                            @RequestParam(required = false) String disabledYn) {
        FamilyVO vo = new FamilyVO();
        vo.setUserId(me.getUserId());
        vo.setRelationCd(relationCd);
        vo.setName(name);
        if (birthDt != null && !birthDt.isBlank()) vo.setBirthDt(LocalDate.parse(birthDt));
        vo.setDependentYn("Y".equalsIgnoreCase(dependentYn) ? "Y" : "N");
        vo.setElderlyYn("Y".equalsIgnoreCase(elderlyYn) ? "Y" : "N");
        vo.setDisabledYn("Y".equalsIgnoreCase(disabledYn) ? "Y" : "N");
        hrService.createFamily(vo);
        return "redirect:/hr/family.do";
    }

    @PostMapping("/hr/family/delete.do")
    public String deleteFamily(@AuthenticationPrincipal CustomUserDetails me,
                               @RequestParam Long famId) {
        hrService.deleteFamily(famId, me.getUserId());
        return "redirect:/hr/family.do";
    }
}
