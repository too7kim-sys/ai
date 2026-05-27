package egovframework.groupware.user.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.cmm.Paging;
import egovframework.groupware.hr.service.HrService;
import egovframework.groupware.sys.service.CodeService;
import egovframework.groupware.user.service.UserService;
import egovframework.groupware.user.service.UserVO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserDirectoryController {

    private final UserService userService;
    private final HrService hrService;
    private final CodeService codeService;

    public UserDirectoryController(UserService userService, HrService hrService,
                                   CodeService codeService) {
        this.userService = userService;
        this.hrService = hrService;
        this.codeService = codeService;
    }

    @GetMapping("/user/list.do")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long deptId,
                       @RequestParam(required = false, defaultValue = "1") int page,
                       Model model) {
        Paging paging = new Paging();
        paging.setPage(Math.max(1, page));
        paging.setSize(20);
        List<UserVO> users = userService.search(keyword, deptId,
                paging.getOffset(), paging.getSize());
        long total = userService.count(keyword, deptId);
        paging.setTotal(total);
        model.addAttribute("users", users);
        model.addAttribute("paging", paging);
        model.addAttribute("keyword", keyword);
        model.addAttribute("deptId", deptId);
        model.addAttribute("depts", hrService.findAllDepts());
        return "user/list";
    }

    @GetMapping("/user/profile.do")
    public String profile(@AuthenticationPrincipal CustomUserDetails me,
                          @RequestParam Long userId, Model model) {
        UserVO user = userService.findById(userId);
        if (user == null) return "redirect:/user/list.do";
        // 인사이력·인사기록·부양가족·계좌번호는 민감정보 — 본인 또는 인사권자만 열람.
        boolean canViewSensitive = me.getUserId().equals(userId)
                || "ADMIN".equals(me.getRoleCd())
                || "HR_MANAGER".equals(me.getRoleCd());
        model.addAttribute("user", user);
        model.addAttribute("canViewSensitive", canViewSensitive);
        boolean canManage = "ADMIN".equals(me.getRoleCd()) || "HR_MANAGER".equals(me.getRoleCd());
        model.addAttribute("canManage", canManage);
        if (canViewSensitive) {
            model.addAttribute("histories", hrService.findHistoryByUser(userId));
            model.addAttribute("records", hrService.findRecordsByUser(userId));
            model.addAttribute("families", hrService.findFamilyByUser(userId));
            model.addAttribute("careers", hrService.findCareerByUser(userId));
            model.addAttribute("educations", hrService.findEducationByUser(userId));
            model.addAttribute("trainings", hrService.findTrainingByUser(userId));
            model.addAttribute("awards", hrService.findAwardByUser(userId));
        }
        if (canManage) {
            model.addAttribute("degreeCodes",  codeService.findCodes("HR_DEGREE"));
            model.addAttribute("eduStatusCodes", codeService.findCodes("HR_EDU_STATUS"));
            model.addAttribute("awardTypeCodes", codeService.findCodes("HR_AWARD_TYPE"));
        }
        return "user/profile";
    }
}
