package egovframework.groupware.user.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.cmm.Paging;
import egovframework.groupware.hr.service.HrService;
import egovframework.groupware.sys.service.CodeService;
import egovframework.groupware.user.service.UserService;
import egovframework.groupware.user.service.UserVO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        boolean isHr = "ADMIN".equals(me.getRoleCd()) || "HR_MANAGER".equals(me.getRoleCd());
        boolean isOwn = me.getUserId().equals(userId);
        // canManage : 경력/학력/교육이수 카드의 추가/삭제 폼 노출 여부.
        //             본인도 자기 정보를 직접 입력할 수 있도록 포함.
        // canEditAward : 상벌은 본인이 변경 불가 — HR/ADMIN 만.
        boolean canManage    = isOwn || isHr;
        boolean canEditAward = isHr;
        model.addAttribute("canManage", canManage);
        model.addAttribute("isOwn", isOwn);
        model.addAttribute("canEditAward", canEditAward);
        if (canViewSensitive) {
            model.addAttribute("histories", hrService.findHistoryByUser(userId));
            model.addAttribute("records", hrService.findRecordsByUser(userId));
            model.addAttribute("families", hrService.findFamilyByUser(userId));
            model.addAttribute("careers", hrService.findCareerByUser(userId));
            model.addAttribute("educations", hrService.findEducationByUser(userId));
            model.addAttribute("trainings", hrService.findTrainingByUser(userId));
            model.addAttribute("awards", hrService.findAwardByUser(userId));
            model.addAttribute("projects", hrService.findProjectByUser(userId));
        }
        if (canManage) {
            model.addAttribute("degreeCodes",  codeService.findCodes("HR_DEGREE"));
            model.addAttribute("eduStatusCodes", codeService.findCodes("HR_EDU_STATUS"));
            model.addAttribute("kosaGradeCodes", codeService.findCodes("KOSA_GRADE"));
        }
        if (canEditAward) {
            model.addAttribute("awardTypeCodes", codeService.findCodes("HR_AWARD_TYPE"));
        }
        return "user/profile";
    }

    /** 본인 정보 수정 — 이메일·연락처·은행/계좌. 역할/사용/잠금 등 권한 필드는 변경 안 함. */
    @GetMapping("/user/me.do")
    public String myEditForm(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        UserVO u = userService.findById(me.getUserId());
        if (u == null) throw new ApiException("NOT_FOUND", "사용자를 찾을 수 없습니다");
        model.addAttribute("u", u);
        return "user/me-edit";
    }

    @PostMapping("/user/me.do")
    public String myEdit(@AuthenticationPrincipal CustomUserDetails me,
                        @RequestParam(required = false) String email,
                        @RequestParam(required = false) String phone,
                        @RequestParam(required = false) String bankCd,
                        @RequestParam(required = false) String bankAccount,
                        RedirectAttributes ra) {
        UserVO before = userService.findById(me.getUserId());
        if (before == null) throw new ApiException("NOT_FOUND", "사용자를 찾을 수 없습니다");
        // 이메일을 바꿀 때는 중복 검사
        if (email != null && !email.isBlank() && !email.equals(before.getEmail())) {
            if (userService.findByEmail(email) != null) {
                throw new ApiException("EMAIL_DUPLICATED", "이미 사용 중인 이메일입니다: " + email);
            }
        }
        // 변경 가능 필드만 set. roleCd / hireDate / 권한 필드는 기존값 유지.
        UserVO vo = new UserVO();
        vo.setUserId(me.getUserId());
        vo.setName(before.getName());
        vo.setPhone(phone);
        vo.setDeptId(before.getDeptId());
        vo.setPositionId(before.getPositionId());
        vo.setRoleCd(before.getRoleCd());
        vo.setHireDate(before.getHireDate());
        vo.setResignDate(before.getResignDate());
        vo.setResignReason(before.getResignReason());
        vo.setBankCd(bankCd);
        vo.setBankAccount(bankAccount);
        userService.update(vo);
        if (email != null && !email.isBlank() && !email.equals(before.getEmail())) {
            userService.changeEmail(me.getUserId(), email);
        }
        ra.addFlashAttribute("flashMsg", "내 정보가 저장되었습니다.");
        ra.addFlashAttribute("flashType", "success");
        return "redirect:/user/profile.do?userId=" + me.getUserId();
    }
}
