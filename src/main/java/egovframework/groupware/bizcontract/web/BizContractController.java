package egovframework.groupware.bizcontract.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.bizcontract.service.BizContractService;
import egovframework.groupware.bizcontract.service.BizContractVO;
import egovframework.groupware.cmm.Paging;
import egovframework.groupware.vendor.service.VendorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasAnyRole('ADMIN','FINANCE_MANAGER','MANAGER')")
public class BizContractController {

    private final BizContractService service;
    private final VendorService vendorService;

    public BizContractController(BizContractService service, VendorService vendorService) {
        this.service = service;
        this.vendorService = vendorService;
    }

    @GetMapping("/biz-contract/list.do")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String statusCd,
                       @RequestParam(required = false) Long vendorId,
                       @RequestParam(required = false, defaultValue = "1") int page,
                       Model model) {
        Paging p = new Paging();
        p.setPage(Math.max(1, page));
        p.setSize(20);
        model.addAttribute("list", service.search(keyword, statusCd, vendorId, p.getOffset(), p.getSize()));
        p.setTotal(service.count(keyword, statusCd, vendorId));
        model.addAttribute("paging", p);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusCd", statusCd);
        model.addAttribute("vendorId", vendorId);
        model.addAttribute("expiringSoon", service.findExpiringSoon(30));
        return "bizcontract/list";
    }

    @GetMapping("/biz-contract/detail.do")
    public String detail(@RequestParam Long bizContractId, Model model) {
        BizContractVO c = service.findById(bizContractId);
        if (c == null) return "redirect:/biz-contract/list.do";
        model.addAttribute("c", c);
        model.addAttribute("schedules", service.findSchedules(bizContractId));
        return "bizcontract/detail";
    }

    @GetMapping("/biz-contract/edit.do")
    public String editForm(@RequestParam(required = false) Long bizContractId, Model model) {
        BizContractVO c = bizContractId == null ? new BizContractVO() : service.findById(bizContractId);
        if (c == null) c = new BizContractVO();
        model.addAttribute("c", c);
        model.addAttribute("vendors", vendorService.list(null, null));
        return "bizcontract/edit";
    }

    @PostMapping("/biz-contract/edit.do")
    public String save(@AuthenticationPrincipal CustomUserDetails me,
                       @ModelAttribute("c") BizContractVO vo) {
        if (vo.getBizContractId() == null) {
            Long id = service.create(vo, me.getUserId());
            return "redirect:/biz-contract/detail.do?bizContractId=" + id;
        }
        service.update(vo);
        return "redirect:/biz-contract/detail.do?bizContractId=" + vo.getBizContractId();
    }

    @PostMapping("/biz-contract/sign.do")
    public String sign(@RequestParam Long bizContractId) {
        service.sign(bizContractId);
        // 체결 시 청구 스케줄 자동 생성
        service.regenerateSchedules(bizContractId);
        return "redirect:/biz-contract/detail.do?bizContractId=" + bizContractId;
    }

    @PostMapping("/biz-contract/terminate.do")
    public String terminate(@RequestParam Long bizContractId,
                            @RequestParam(required = false) String reason) {
        service.terminate(bizContractId, reason);
        return "redirect:/biz-contract/detail.do?bizContractId=" + bizContractId;
    }

    @PostMapping("/biz-contract/schedule/regenerate.do")
    public String regenerate(@RequestParam Long bizContractId) {
        service.regenerateSchedules(bizContractId);
        return "redirect:/biz-contract/detail.do?bizContractId=" + bizContractId;
    }

    @PostMapping("/biz-contract/delete.do")
    public String delete(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long bizContractId) {
        service.delete(bizContractId, me.getUserId());
        return "redirect:/biz-contract/list.do";
    }
}
