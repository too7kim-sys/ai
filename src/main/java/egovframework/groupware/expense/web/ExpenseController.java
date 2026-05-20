package egovframework.groupware.expense.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.expense.service.ExpenseItemVO;
import egovframework.groupware.expense.service.ExpenseReportVO;
import egovframework.groupware.expense.service.ExpenseService;
import egovframework.groupware.user.service.UserService;
import egovframework.groupware.user.service.UserVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ExpenseController {

    private final ExpenseService service;
    private final UserService userService;

    public ExpenseController(ExpenseService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping("/expense/write.do")
    public String writeForm(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("approvers", userService.search(null, null, 0, 200));
        return "expense/write";
    }

    @PostMapping("/expense/write.do")
    public String write(@AuthenticationPrincipal CustomUserDetails me,
                        @RequestParam String title,
                        @RequestParam(required = false) String purpose,
                        @RequestParam(name = "expenseDt") List<String> expenseDts,
                        @RequestParam(name = "categoryCd") List<String> categoryCds,
                        @RequestParam(name = "accountCd") List<String> accountCds,
                        @RequestParam(name = "vendorNm") List<String> vendorNms,
                        @RequestParam(name = "netAmount") List<Long> netAmounts,
                        @RequestParam(name = "vatAmount") List<Long> vatAmounts,
                        @RequestParam(name = "paymentMethodCd") List<String> paymentMethods,
                        @RequestParam(name = "receiptTypeCd", required = false) List<String> receiptTypes,
                        @RequestParam(name = "approverIds", required = false) List<Long> approverIds) {
        ExpenseReportVO r = new ExpenseReportVO();
        r.setDrafterId(me.getUserId());
        r.setDeptId(me.getDeptId());
        r.setTitle(title);
        r.setPurpose(purpose);

        List<ExpenseItemVO> items = new ArrayList<>();
        int n = expenseDts.size();
        for (int i = 0; i < n; i++) {
            if (netAmounts.get(i) == null || netAmounts.get(i) == 0L) continue;
            ExpenseItemVO it = new ExpenseItemVO();
            it.setExpenseDt(LocalDate.parse(expenseDts.get(i)));
            it.setCategoryCd(categoryCds.get(i));
            it.setAccountCd(accountCds.get(i));
            it.setVendorNm(vendorNms.get(i));
            BigDecimal net = BigDecimal.valueOf(netAmounts.get(i));
            BigDecimal vat = BigDecimal.valueOf(vatAmounts.get(i) == null ? 0L : vatAmounts.get(i));
            it.setNetAmount(net);
            it.setVatAmount(vat);
            it.setTotalAmount(net.add(vat));
            it.setPaymentMethodCd(paymentMethods.get(i));
            if (receiptTypes != null && i < receiptTypes.size()) it.setReceiptTypeCd(receiptTypes.get(i));
            items.add(it);
        }
        if (approverIds == null || approverIds.isEmpty()) {
            approverIds = new ArrayList<>();
            for (UserVO u : userService.search("finance@", null, 0, 1)) approverIds.add(u.getUserId());
            if (approverIds.isEmpty()) approverIds.add(1L);
        }
        service.create(r, items, approverIds);
        return "redirect:/expense/my.do";
    }

    @GetMapping("/expense/my.do")
    public String my(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("list", service.listMine(me.getUserId()));
        return "expense/my";
    }

    @GetMapping("/expense/detail.do")
    public String detail(@RequestParam Long reportId, Model model) {
        model.addAttribute("r", service.findById(reportId));
        return "expense/detail";
    }

    @GetMapping("/expense/admin/list.do")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE_MANAGER')")
    public String adminList(@RequestParam(required = false) String status, Model model) {
        model.addAttribute("list", service.listAdmin(status, null));
        model.addAttribute("status", status);
        return "expense/admin-list";
    }

    @PostMapping("/expense/admin/reimburse.do")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE_MANAGER')")
    public String reimburse(@RequestParam("reportIds") List<Long> reportIds) {
        service.reimburse(reportIds);
        return "redirect:/expense/admin/list.do?status=APPROVED";
    }
}
