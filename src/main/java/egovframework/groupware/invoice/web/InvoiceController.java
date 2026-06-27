package egovframework.groupware.invoice.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.bizcontract.service.BizContractService;
import egovframework.groupware.cmm.Paging;
import egovframework.groupware.invoice.service.InvoiceService;
import egovframework.groupware.invoice.service.InvoiceVO;
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
@PreAuthorize("hasAnyRole('ADMIN','FINANCE_MANAGER')")
public class InvoiceController {

    private final InvoiceService service;
    private final VendorService vendorService;
    private final BizContractService contractService;

    public InvoiceController(InvoiceService service,
                             VendorService vendorService,
                             BizContractService contractService) {
        this.service = service;
        this.vendorService = vendorService;
        this.contractService = contractService;
    }

    @GetMapping("/invoice/out.do")
    public String outList(@RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String statusCd,
                          @RequestParam(required = false) Long vendorId,
                          @RequestParam(required = false, defaultValue = "1") int page,
                          Model model) {
        return commonList("OUT", keyword, statusCd, vendorId, page, model);
    }

    @GetMapping("/invoice/in.do")
    public String inList(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false) String statusCd,
                         @RequestParam(required = false) Long vendorId,
                         @RequestParam(required = false, defaultValue = "1") int page,
                         Model model) {
        return commonList("IN", keyword, statusCd, vendorId, page, model);
    }

    private String commonList(String direction, String keyword, String statusCd, Long vendorId,
                              int page, Model model) {
        // 만기 지난 인보이스 자동 OVERDUE 처리
        service.markOverdue();
        Paging p = new Paging();
        p.setPage(Math.max(1, page));
        p.setSize(20);
        model.addAttribute("list", service.search(direction, keyword, statusCd, vendorId, p.getOffset(), p.getSize()));
        p.setTotal(service.count(direction, keyword, statusCd, vendorId));
        model.addAttribute("paging", p);
        model.addAttribute("direction", direction);
        model.addAttribute("keyword", keyword);
        model.addAttribute("statusCd", statusCd);
        model.addAttribute("summary", service.sumByDirection(direction));
        model.addAttribute("outstanding", service.sumOutstanding(direction));
        return "invoice/list";
    }

    @GetMapping("/invoice/detail.do")
    public String detail(@RequestParam Long invoiceId, Model model) {
        InvoiceVO inv = service.findById(invoiceId);
        if (inv == null) return "redirect:/invoice/out.do";
        model.addAttribute("inv", inv);
        return "invoice/detail";
    }

    @GetMapping("/invoice/edit.do")
    public String editForm(@RequestParam(required = false) Long invoiceId,
                           @RequestParam(required = false, defaultValue = "OUT") String direction,
                           Model model) {
        InvoiceVO inv;
        if (invoiceId == null) {
            inv = new InvoiceVO();
            inv.setDirectionCd(direction);
        } else {
            inv = service.findById(invoiceId);
            if (inv == null) inv = new InvoiceVO();
        }
        model.addAttribute("inv", inv);
        model.addAttribute("vendors", vendorService.list(null, null));
        model.addAttribute("contracts", contractService.search(null, "ACTIVE", null, 0, 200));
        return "invoice/edit";
    }

    @PostMapping("/invoice/edit.do")
    public String save(@AuthenticationPrincipal CustomUserDetails me,
                       @ModelAttribute("inv") InvoiceVO vo) {
        if (vo.getInvoiceId() == null) {
            Long id = service.create(vo, me.getUserId());
            return "redirect:/invoice/detail.do?invoiceId=" + id;
        }
        service.update(vo);
        return "redirect:/invoice/detail.do?invoiceId=" + vo.getInvoiceId();
    }

    @PostMapping("/invoice/issue.do")
    public String issue(@RequestParam Long invoiceId) {
        service.issue(invoiceId);
        return "redirect:/invoice/detail.do?invoiceId=" + invoiceId;
    }

    @PostMapping("/invoice/cancel.do")
    public String cancel(@RequestParam Long invoiceId) {
        service.cancel(invoiceId);
        return "redirect:/invoice/detail.do?invoiceId=" + invoiceId;
    }
}
