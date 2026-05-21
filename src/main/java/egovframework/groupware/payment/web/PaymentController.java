package egovframework.groupware.payment.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.cmm.Paging;
import egovframework.groupware.invoice.service.InvoiceService;
import egovframework.groupware.invoice.service.InvoiceVO;
import egovframework.groupware.payment.service.PaymentService;
import egovframework.groupware.payment.service.PaymentVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@PreAuthorize("hasAnyRole('ADMIN','FINANCE_MANAGER')")
public class PaymentController {

    private final PaymentService service;
    private final InvoiceService invoiceService;

    public PaymentController(PaymentService service, InvoiceService invoiceService) {
        this.service = service;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/payment/list.do")
    public String list(@RequestParam(required = false) String payTypeCd,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String fromDt,
                       @RequestParam(required = false) String toDt,
                       @RequestParam(required = false, defaultValue = "1") int page,
                       Model model) {
        LocalDate from = parse(fromDt);
        LocalDate to = parse(toDt);
        Paging p = new Paging();
        p.setPage(Math.max(1, page));
        p.setSize(30);
        model.addAttribute("list", service.search(blank(payTypeCd), keyword, from, to,
                p.getOffset(), p.getSize()));
        p.setTotal(service.count(blank(payTypeCd), keyword, from, to));
        model.addAttribute("paging", p);
        model.addAttribute("payTypeCd", payTypeCd);
        model.addAttribute("keyword", keyword);
        model.addAttribute("fromDt", fromDt);
        model.addAttribute("toDt", toDt);
        model.addAttribute("sumIncoming", service.sumByType("INCOMING"));
        model.addAttribute("sumOutgoing", service.sumByType("OUTGOING"));
        return "payment/list";
    }

    @GetMapping("/payment/edit.do")
    public String editForm(@RequestParam(required = false) Long invoiceId,
                           @RequestParam(required = false, defaultValue = "INCOMING") String payTypeCd,
                           Model model) {
        PaymentVO vo = new PaymentVO();
        vo.setPayTypeCd(payTypeCd);
        if (invoiceId != null) {
            InvoiceVO inv = invoiceService.findById(invoiceId);
            if (inv != null) {
                vo.setInvoiceId(invoiceId);
                vo.setAmount(inv.getRemainingAmount());
                vo.setCounterpartNm(inv.getVendorNm());
                vo.setPayTypeCd("OUT".equals(inv.getDirectionCd()) ? "INCOMING" : "OUTGOING");
                model.addAttribute("invoice", inv);
            }
        }
        model.addAttribute("vo", vo);
        return "payment/edit";
    }

    @PostMapping("/payment/edit.do")
    public String save(@AuthenticationPrincipal CustomUserDetails me,
                       @ModelAttribute("vo") PaymentVO vo) {
        Long id = service.create(vo, me.getUserId());
        if (vo.getInvoiceId() != null) {
            return "redirect:/invoice/detail.do?invoiceId=" + vo.getInvoiceId();
        }
        return "redirect:/payment/list.do";
    }

    @PostMapping("/payment/delete.do")
    public String delete(@RequestParam Long paymentId) {
        service.delete(paymentId);
        return "redirect:/payment/list.do";
    }

    private LocalDate parse(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s);
    }

    private String blank(String s) { return s == null || s.isBlank() ? null : s; }
}
