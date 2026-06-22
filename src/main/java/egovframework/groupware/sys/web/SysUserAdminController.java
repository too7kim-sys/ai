package egovframework.groupware.sys.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.hr.service.HrService;
import egovframework.groupware.sys.service.AuditLogService;
import egovframework.groupware.user.service.UserService;
import egovframework.groupware.user.service.UserVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

/**
 * 시스템 - 사용자 등록 / 수정 (ADMIN, HR_MANAGER).
 *
 * <p>잠금해제·PW 초기화·역할변경·활성화 토글은 {@link SysUserController} 가
 * ADMIN 전용으로 담당하고, 본 컨트롤러는 신규 가입과 프로필 수정만 제공한다.
 */
@Controller
@PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
public class SysUserAdminController {

    private final UserService userService;
    private final HrService hrService;
    private final AuditLogService auditService;

    public SysUserAdminController(UserService userService, HrService hrService,
                                  AuditLogService auditService) {
        this.userService = userService;
        this.hrService = hrService;
        this.auditService = auditService;
    }

    @GetMapping("/sys/user/create.do")
    public String createForm(Model model) {
        model.addAttribute("depts", hrService.findAllDepts());
        model.addAttribute("positions", hrService.findAllPositions());
        return "sys/user-create";
    }

    @PostMapping("/sys/user/create.do")
    public String create(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam String email,
                         @RequestParam String name,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false) Long deptId,
                         @RequestParam(required = false) Long positionId,
                         @RequestParam String roleCd,
                         @RequestParam(required = false) String hireDate,
                         @RequestParam String password,
                         HttpServletRequest req,
                         RedirectAttributes ra) {
        if (userService.findByEmail(email) != null) {
            throw new ApiException("EMAIL_DUPLICATED", "이미 등록된 이메일입니다: " + email);
        }
        UserVO vo = new UserVO();
        vo.setEmail(email);
        vo.setName(name);
        vo.setPhone(phone);
        vo.setDeptId(deptId);
        vo.setPositionId(positionId);
        vo.setRoleCd(roleCd);
        if (hireDate != null && !hireDate.isBlank()) vo.setHireDate(LocalDate.parse(hireDate));
        Long newId = userService.createUser(vo, password);

        String afterJson = "{"
                + "\"email\":" + jsonStr(email)
                + ",\"role\":" + jsonStr(roleCd)
                + ",\"hireDate\":" + jsonStr(vo.getHireDate())
                + "}";
        auditService.log(me.getUserId(), "USER_CREATE", "USER", newId.toString(),
                null, afterJson, ip(req));
        ra.addFlashAttribute("flashMsg", name + "(" + email + ") 사용자가 등록되었습니다.");
        ra.addFlashAttribute("flashType", "success");
        return "redirect:/sys/user/list.do";
    }

    @GetMapping("/sys/user/edit.do")
    public String editForm(@RequestParam Long userId, Model model) {
        UserVO u = userService.findById(userId);
        if (u == null) return "redirect:/sys/user/list.do";
        model.addAttribute("u", u);
        model.addAttribute("depts", hrService.findAllDepts());
        model.addAttribute("positions", hrService.findAllPositions());
        return "sys/user-edit";
    }

    @PostMapping("/sys/user/edit.do")
    public String edit(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam Long userId,
                       @RequestParam String name,
                       @RequestParam(required = false) String phone,
                       @RequestParam(required = false) Long deptId,
                       @RequestParam(required = false) Long positionId,
                       @RequestParam(required = false) String hireDate,
                       @RequestParam(required = false) String resignDate,
                       @RequestParam(required = false) String resignReason,
                       @RequestParam(required = false) String bankCd,
                       @RequestParam(required = false) String bankAccount,
                       HttpServletRequest req,
                       RedirectAttributes ra) {
        UserVO before = userService.findById(userId);
        if (before == null) return "redirect:/sys/user/list.do";

        UserVO vo = new UserVO();
        vo.setUserId(userId);
        vo.setName(name);
        vo.setPhone(phone);
        vo.setDeptId(deptId);
        vo.setPositionId(positionId);
        vo.setRoleCd(before.getRoleCd());            // 역할 변경은 별도 액션
        if (hireDate != null && !hireDate.isBlank()) vo.setHireDate(LocalDate.parse(hireDate));
        if (resignDate != null && !resignDate.isBlank()) vo.setResignDate(LocalDate.parse(resignDate));
        vo.setResignReason(resignReason);
        vo.setBankCd(bankCd);
        vo.setBankAccount(bankAccount);
        userService.update(vo);

        // 인사 변경 추적 — 입사일/퇴사일 등은 휴가일수·급여 일할계산·퇴직금 산정의 기초가 되므로
        // before/after 모두 기록해 변경 시점을 사후 감사할 수 있게 한다.
        String beforeJson = "{"
                + "\"name\":" + jsonStr(before.getName())
                + ",\"hireDate\":" + jsonStr(before.getHireDate())
                + ",\"resignDate\":" + jsonStr(before.getResignDate())
                + ",\"resignReason\":" + jsonStr(before.getResignReason())
                + "}";
        String afterJson = "{"
                + "\"name\":" + jsonStr(name)
                + ",\"hireDate\":" + jsonStr(vo.getHireDate())
                + ",\"resignDate\":" + jsonStr(vo.getResignDate())
                + ",\"resignReason\":" + jsonStr(resignReason)
                + "}";
        auditService.log(me.getUserId(), "USER_EDIT", "USER", userId.toString(),
                beforeJson, afterJson, ip(req));
        ra.addFlashAttribute("flashMsg", "사용자 정보가 수정되었습니다.");
        ra.addFlashAttribute("flashType", "success");
        return "redirect:/user/profile.do?userId=" + userId;
    }

    private String ip(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) return h.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    /** audit log JSON 조립용 — null 은 "null", 그 외는 escape 한 큰따옴표 문자열로 인코딩. */
    private static String jsonStr(Object v) {
        if (v == null) return "null";
        String s = v.toString().replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "");
        return "\"" + s + "\"";
    }
}
