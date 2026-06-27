package egovframework.groupware.expense.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.expense.service.ExpenseItemVO;
import egovframework.groupware.expense.service.ExpenseReportVO;
import egovframework.groupware.expense.service.ExpenseService;
import egovframework.groupware.user.service.UserService;
import egovframework.groupware.user.service.UserVO;
import org.springframework.security.access.AccessDeniedException;
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
        // 결재선은 폼에서 명시적으로 선택해야 한다. 과거엔 미지정 시 finance@/userId=1 로
        // 강제 할당해 무관한 사용자에게 결재가 발송되는 결함이 있었다.
        if (approverIds == null || approverIds.isEmpty()) {
            throw new egovframework.groupware.cmm.ApiException("NO_APPROVERS",
                    "결재선을 1명 이상 선택하세요");
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
    public String detail(@AuthenticationPrincipal CustomUserDetails me,
                         @RequestParam Long reportId, Model model) {
        ExpenseReportVO r = service.findById(reportId);
        // 기안자 본인 또는 재무 관리자만 열람 — reportId 열거로 타인 지출내역 조회 차단.
        boolean allowed = r != null && (me.getUserId().equals(r.getDrafterId())
                || "ADMIN".equals(me.getRoleCd())
                || "FINANCE_MANAGER".equals(me.getRoleCd()));
        if (!allowed) {
            throw new AccessDeniedException("지출결의서 열람 권한이 없습니다");
        }
        model.addAttribute("r", r);
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
