package egovframework.groupware.finance.web;

import egovframework.groupware.bizcontract.service.BizContractService;
import egovframework.groupware.invoice.service.InvoiceService;
import egovframework.groupware.payment.service.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 재무 대시보드 (ADMIN/FINANCE_MANAGER).
 */
@Controller
@PreAuthorize("hasAnyRole('ADMIN','FINANCE_MANAGER')")
public class FinanceDashboardController {

    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final BizContractService contractService;

    public FinanceDashboardController(InvoiceService invoiceService,
                                      PaymentService paymentService,
                                      BizContractService contractService) {
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
        this.contractService = contractService;
    }

    @GetMapping("/finance/ar.do")
    public String ar(Model model) {
        invoiceService.markOverdue();
        // 매출(OUT)
        model.addAttribute("salesSummary", invoiceService.sumByDirection("OUT"));
        model.addAttribute("salesOutstanding", invoiceService.sumOutstanding("OUT"));
        model.addAttribute("salesMonthly", invoiceService.monthlyByDirection("OUT", 12));
        model.addAttribute("salesTopVendors", invoiceService.topVendorsByAmount("OUT", 5));
        // 매입(IN)
        model.addAttribute("purchaseSummary", invoiceService.sumByDirection("IN"));
        model.addAttribute("purchaseOutstanding", invoiceService.sumOutstanding("IN"));
        model.addAttribute("purchaseMonthly", invoiceService.monthlyByDirection("IN", 12));
        model.addAttribute("purchaseTopVendors", invoiceService.topVendorsByAmount("IN", 5));
        // 입출금
        model.addAttribute("sumIncoming", paymentService.sumByType("INCOMING"));
        model.addAttribute("sumOutgoing", paymentService.sumByType("OUTGOING"));
        // 만료/만기 임박
        model.addAttribute("expiringContracts", contractService.findExpiringSoon(60));
        model.addAttribute("upcomingSchedules", contractService.findUpcomingSchedules(30));
        return "finance/ar";
    }
}
