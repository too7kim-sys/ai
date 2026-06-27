package egovframework.groupware.sys.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.sys.service.AuditLogService;
import egovframework.groupware.sys.service.CodeGroupVO;
import egovframework.groupware.sys.service.CodeService;
import egovframework.groupware.sys.service.CodeVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 시스템 관리 - 공통 코드 관리 (ADMIN 전용).
 */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class SysCodeController {

    private final CodeService service;
    private final AuditLogService auditService;

    public SysCodeController(CodeService service, AuditLogService auditService) {
        this.service = service;
        this.auditService = auditService;
    }

    @GetMapping("/sys/code.do")
    public String list(@RequestParam(required = false) String groupCd, Model model) {
        var groups = service.findAllGroups();
        model.addAttribute("groups", groups);
        if (groupCd == null && !groups.isEmpty()) groupCd = groups.get(0).getGroupCd();
        if (groupCd != null) {
            model.addAttribute("group", service.findGroup(groupCd));
            model.addAttribute("codes", service.findCodes(groupCd));
        }
        model.addAttribute("groupCd", groupCd);
        return "sys/code";
    }

    @PostMapping("/sys/code/group/save.do")
    public String saveGroup(@AuthenticationPrincipal CustomUserDetails me,
                            @RequestParam String groupCd,
                            @RequestParam String groupNm,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false, defaultValue = "false") boolean isNew) {
        CodeGroupVO vo = new CodeGroupVO();
        vo.setGroupCd(groupCd);
        vo.setGroupNm(groupNm);
        vo.setDescription(description);
        vo.setUseYn("Y");
        service.saveGroup(vo, isNew);
        auditService.log(me.getUserId(), "CODE_GROUP_SAVE", "CODE_GROUP", groupCd,
                null, null, null);
        return "redirect:/sys/code.do?groupCd=" + groupCd;
    }

    @PostMapping("/sys/code/group/delete.do")
    public String deleteGroup(@AuthenticationPrincipal CustomUserDetails me,
                              @RequestParam String groupCd) {
        service.deleteGroup(groupCd);
        auditService.log(me.getUserId(), "CODE_GROUP_DELETE", "CODE_GROUP", groupCd,
                null, null, null);
        return "redirect:/sys/code.do";
    }

    @PostMapping("/sys/code/save.do")
    public String saveCode(@AuthenticationPrincipal CustomUserDetails me,
                           @RequestParam(required = false) Long codeId,
                           @RequestParam String groupCd,
                           @RequestParam String codeVal,
                           @RequestParam String codeNm,
                           @RequestParam(required = false, defaultValue = "0") Integer sortNo,
                           @RequestParam(required = false) String extraVal,
                           @RequestParam(required = false) String useYn) {
        CodeVO vo = new CodeVO();
        vo.setCodeId(codeId);
        vo.setGroupCd(groupCd);
        vo.setCodeVal(codeVal);
        vo.setCodeNm(codeNm);
        vo.setSortNo(sortNo);
        vo.setExtraVal(extraVal);
        vo.setUseYn("N".equals(useYn) ? "N" : "Y");
        Long savedId = service.saveCode(vo, codeId == null);
        auditService.log(me.getUserId(), "CODE_SAVE", "CODE", String.valueOf(savedId),
                null, null, null);
        return "redirect:/sys/code.do?groupCd=" + groupCd;
    }

    @PostMapping("/sys/code/delete.do")
    public String deleteCode(@AuthenticationPrincipal CustomUserDetails me,
                             @RequestParam Long codeId,
                             @RequestParam String groupCd) {
        service.deleteCode(codeId);
        auditService.log(me.getUserId(), "CODE_DELETE", "CODE", codeId.toString(),
                null, null, null);
        return "redirect:/sys/code.do?groupCd=" + groupCd;
    }
}
