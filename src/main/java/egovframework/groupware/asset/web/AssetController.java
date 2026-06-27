package egovframework.groupware.asset.web;

import egovframework.groupware.asset.service.AssetService;
import egovframework.groupware.asset.service.AssetVO;
import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.cmm.Paging;
import egovframework.groupware.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AssetController {

    private final AssetService service;
    private final UserService userService;

    public AssetController(AssetService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping("/asset/list.do")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String categoryCd,
                       @RequestParam(required = false) String statusCd,
                       @RequestParam(required = false, defaultValue = "1") int page,
                       Model model) {
        Paging p = new Paging();
        p.setPage(Math.max(1, page));
        p.setSize(20);
        model.addAttribute("list", service.search(keyword, blank(categoryCd), blank(statusCd), null,
                p.getOffset(), p.getSize()));
        p.setTotal(service.count(keyword, blank(categoryCd), blank(statusCd), null));
        model.addAttribute("paging", p);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryCd", categoryCd);
        model.addAttribute("statusCd", statusCd);
        model.addAttribute("statusStats", service.statsByStatus());
        model.addAttribute("categoryStats", service.statsByCategory());
        return "asset/list";
    }

    @GetMapping("/asset/my.do")
    public String myAssets(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.search(null, null, null, me.getUserId(), 0, 100));
        return "asset/my";
    }

    @GetMapping("/asset/detail.do")
    public String detail(@RequestParam Long assetId, Model model) {
        AssetVO a = service.findById(assetId);
        if (a == null) return "redirect:/asset/list.do";
        model.addAttribute("a", a);
        model.addAttribute("history", service.findHistory(assetId));
        model.addAttribute("users", userService.listAll());
        return "asset/detail";
    }

    @GetMapping("/asset/edit.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String editForm(@RequestParam(required = false) Long assetId, Model model) {
        AssetVO a = assetId == null ? new AssetVO() : service.findById(assetId);
        if (a == null) a = new AssetVO();
        model.addAttribute("a", a);
        return "asset/edit";
    }

    @PostMapping("/asset/edit.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String save(@AuthenticationPrincipal CustomUserDetails me,
                       @ModelAttribute("a") AssetVO vo) {
        if (vo.getAssetId() == null) {
            Long id = service.create(vo, me.getUserId());
            return "redirect:/asset/detail.do?assetId=" + id;
        }
        service.update(vo);
        return "redirect:/asset/detail.do?assetId=" + vo.getAssetId();
    }

    @PostMapping("/asset/assign.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String assign(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long assetId,
                         @RequestParam Long userId,
                         @RequestParam(required = false) String memo) {
        service.assign(assetId, userId, me.getUserId(), memo);
        return "redirect:/asset/detail.do?assetId=" + assetId;
    }

    @PostMapping("/asset/return.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String doReturn(@AuthenticationPrincipal CustomUserDetails me,
                           @RequestParam Long assetId,
                           @RequestParam(required = false) String memo) {
        service.doReturn(assetId, me.getUserId(), memo);
        return "redirect:/asset/detail.do?assetId=" + assetId;
    }

    @PostMapping("/asset/repair.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String repair(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long assetId,
                         @RequestParam(required = false) String memo) {
        service.repair(assetId, me.getUserId(), memo);
        return "redirect:/asset/detail.do?assetId=" + assetId;
    }

    @PostMapping("/asset/dispose.do")
    @PreAuthorize("hasAnyRole('ADMIN','HR_MANAGER')")
    public String dispose(@AuthenticationPrincipal CustomUserDetails me,
                          @RequestParam Long assetId,
                          @RequestParam(required = false) String memo) {
        service.dispose(assetId, me.getUserId(), memo);
        return "redirect:/asset/detail.do?assetId=" + assetId;
    }

    private String blank(String s) { return s == null || s.isBlank() ? null : s; }
}
