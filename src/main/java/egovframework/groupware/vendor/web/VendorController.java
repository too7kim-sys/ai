package egovframework.groupware.vendor.web;

import egovframework.groupware.vendor.service.VendorService;
import egovframework.groupware.vendor.service.VendorVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("hasAnyRole('ADMIN','FINANCE_MANAGER')")
public class VendorController {

    private final VendorService service;

    public VendorController(VendorService service) { this.service = service; }

    @GetMapping("/vendor/list.do")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String type,
                       Model model) {
        model.addAttribute("list", service.list(keyword, type));
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        return "vendor/list";
    }

    @GetMapping("/vendor/edit.do")
    public String editForm(@RequestParam(required = false) Long vendorId, Model model) {
        VendorVO vo = vendorId == null ? new VendorVO() : service.findById(vendorId);
        if (vo == null) vo = new VendorVO();
        model.addAttribute("vo", vo);
        return "vendor/edit";
    }

    @PostMapping("/vendor/edit.do")
    public String save(@ModelAttribute("vo") VendorVO vo) {
        if (vo.getVendorId() == null) service.create(vo);
        else service.update(vo);
        return "redirect:/vendor/list.do";
    }

    @GetMapping("/vendor/detail.do")
    public String detail(@RequestParam Long vendorId, Model model) {
        model.addAttribute("v", service.findById(vendorId));
        return "vendor/detail";
    }
}
