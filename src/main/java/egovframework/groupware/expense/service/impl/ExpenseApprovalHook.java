package egovframework.groupware.expense.service.impl;

import egovframework.groupware.approval.service.ApprovalCompletionHook;
import egovframework.groupware.approval.service.ApprovalDocVO;
import egovframework.groupware.expense.mapper.ExpenseMapper;
import egovframework.groupware.expense.service.ExpenseItemVO;
import egovframework.groupware.expense.service.ExpenseReportVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * EXPENSE 양식 결재 완료 시:
 *  - APPROVED: 상태 APPROVED, 부서 예산 USED_AMOUNT 갱신
 *  - REJECTED: 상태 REJECTED
 */
@Component
public class ExpenseApprovalHook implements ApprovalCompletionHook {

    private static final Logger log = LoggerFactory.getLogger(ExpenseApprovalHook.class);

    private final ExpenseMapper mapper;

    public ExpenseApprovalHook(ExpenseMapper mapper) { this.mapper = mapper; }

    @Override public String supportedFormCd() { return "EXPENSE"; }

    @Override
    public void onCompleted(ApprovalDocVO doc, boolean approved) {
        ExpenseReportVO r = mapper.findByApprovalDoc(doc.getDocId());
        if (r == null) {
            log.warn("Expense report not found for approval doc {}", doc.getDocId());
            return;
        }
        if (!approved) {
            mapper.updateReportStatus(r.getReportId(), "REJECTED", null, null);
            return;
        }
        mapper.updateReportStatus(r.getReportId(), "APPROVED", null, null);

        // 부서 예산 갱신 (계정과목별 합산)
        if (r.getDeptId() != null) {
            var items = mapper.findItems(r.getReportId());
            Map<String, BigDecimal> sums = new HashMap<>();
            int year = LocalDate.now().getYear();
            int month = LocalDate.now().getMonthValue();
            for (ExpenseItemVO it : items) {
                if (it.getAccountCd() == null || it.getTotalAmount() == null) continue;
                sums.merge(it.getAccountCd(), it.getTotalAmount(), BigDecimal::add);
            }
            for (var e : sums.entrySet()) {
                mapper.addBudgetUsed(r.getDeptId(), e.getKey(), year, month, e.getValue());
            }
        }
        log.info("Expense approved (reportId={}, total={})", r.getReportId(), r.getTotalAmount());
    }
}
