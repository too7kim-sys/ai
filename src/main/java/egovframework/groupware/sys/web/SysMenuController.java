package egovframework.groupware.sys.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.sys.service.AuditLogService;
import egovframework.groupware.sys.service.MenuService;
import egovframework.groupware.sys.service.MenuVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

/**
 * 시스템 관리 - 메뉴 관리 (ADMIN 전용).
 */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class SysMenuController {

    private static final List<String> AVAILABLE_ROLES = Arrays.asList(
            "ADMIN", "HR_MANAGER", "FINANCE_MANAGER", "MANAGER", "EMPLOYEE");

    private final MenuService service;
    private final AuditLogService auditService;

    public SysMenuController(MenuService service, AuditLogService auditService) {
        this.service = service;
        this.auditService = auditService;
    }

    @GetMapping("/sys/menu.do")
    public String list(@RequestParam(required = false) Long menuId, Model model) {
        model.addAttribute("list", service.findAll());
        model.addAttribute("availableRoles", AVAILABLE_ROLES);
        if (menuId != null) {
            MenuVO m = service.findById(menuId);
            if (m != null) {
                m.setRoles(service.findRoles(menuId));
                model.addAttribute("menu", m);
            }
        }
        return "sys/menu";
    }

    @PostMapping("/sys/menu/save.do")
    public String save(@AuthenticationPrincipal CustomUserDetails me,
                       @RequestParam(required = false) Long menuId,
                       @RequestParam String menuNm,
                       @RequestParam(required = false) String url,
                       @RequestParam(required = false) Long parentId,
                       @RequestParam(required = false, defaultValue = "0") Integer sortNo,
                       @RequestParam(required = false) String icon,
                       @RequestParam(required = false) String useYn,
                       @RequestParam(value = "roles", required = false) List<String> roles) {
        MenuVO vo = new MenuVO();
        vo.setMenuId(menuId);
        vo.setMenuNm(menuNm);
        vo.setUrl(url);
        vo.setParentId(parentId);
        vo.setSortNo(sortNo);
        vo.setIcon(icon);
        vo.setUseYn("N".equals(useYn) ? "N" : "Y");
        Long savedId = service.save(vo, roles, menuId == null);
        auditService.log(me.getUserId(), "MENU_SAVE", "MENU", String.valueOf(savedId),
                null, null, null);
        return "redirect:/sys/menu.do?menuId=" + savedId;
    }

    @PostMapping("/sys/menu/delete.do")
    public String delete(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long menuId) {
        service.delete(menuId);
        auditService.log(me.getUserId(), "MENU_DELETE", "MENU", menuId.toString(),
                null, null, null);
        return "redirect:/sys/menu.do";
    }
}
