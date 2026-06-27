package egovframework.groupware.expense.service.impl;

import egovframework.groupware.approval.service.ApprovalDocVO;
import egovframework.groupware.approval.service.ApprovalService;
import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.expense.mapper.ExpenseMapper;
import egovframework.groupware.expense.service.ExpenseItemVO;
import egovframework.groupware.expense.service.ExpenseReportVO;
import egovframework.groupware.expense.service.ExpenseService;
import egovframework.groupware.mail.service.MailRequest;
import egovframework.groupware.mail.service.MailService;
import egovframework.groupware.user.service.UserService;
import egovframework.groupware.user.service.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private static final NumberFormat WON = NumberFormat.getNumberInstance(Locale.KOREA);

    private final ExpenseMapper mapper;
    private final ApprovalService approvalService;
    private final MailService mailService;
    private final UserService userService;
    private final egovframework.groupware.payment.mapper.PaymentMapper paymentMapper;

    public ExpenseServiceImpl(ExpenseMapper mapper, ApprovalService approvalService,
                              MailService mailService, UserService userService,
                              egovframework.groupware.payment.mapper.PaymentMapper paymentMapper) {
        this.mapper = mapper;
        this.approvalService = approvalService;
        this.mailService = mailService;
        this.userService = userService;
        this.paymentMapper = paymentMapper;
    }

    @Override
    @Transactional
    public Long create(ExpenseReportVO report, List<ExpenseItemVO> items, List<Long> approverIds) {
        if (items == null || items.isEmpty()) throw new ApiException("NO_ITEMS", "지출 라인 1건 이상 필요합니다");

        // 합계 산출
        BigDecimal totNet = BigDecimal.ZERO, totVat = BigDecimal.ZERO, tot = BigDecimal.ZERO;
        boolean needReimburse = false;
        for (ExpenseItemVO it : items) {
            if (it.getNetAmount() == null) it.setNetAmount(BigDecimal.ZERO);
            if (it.getVatAmount() == null) it.setVatAmount(BigDecimal.ZERO);
            if (it.getTotalAmount() == null)
                it.setTotalAmount(it.getNetAmount().add(it.getVatAmount()));
            // 음수 금액은 환불처럼 동작해 예산 사용액을 차감시키는 데이터 부정합을 만든다.
            // 환불은 별도 액션으로 처리해야 하며, 신청 라인 자체는 0 이상만 허용.
            if (it.getNetAmount().signum() < 0 || it.getVatAmount().signum() < 0
                    || it.getTotalAmount().signum() < 0) {
                throw new ApiException("INVALID_AMOUNT",
                        "금액은 0 이상이어야 합니다 (환불은 별도 처리)");
            }
            totNet = totNet.add(it.getNetAmount());
            totVat = totVat.add(it.getVatAmount());
            tot = tot.add(it.getTotalAmount());
            if ("PERSONAL_CARD".equals(it.getPaymentMethodCd()) || "CASH".equals(it.getPaymentMethodCd())) {
                needReimburse = true;
            }
        }
        report.setTotalNet(totNet);
        report.setTotalVat(totVat);
        report.setTotalAmount(tot);
        report.setReimburseRequiredYn(needReimburse ? "Y" : "N");
        report.setReportNo("EXP-" + System.currentTimeMillis() % 100000000);

        // 결재 자동 상신
        ApprovalDocVO doc = new ApprovalDocVO();
        doc.setFormCd("EXPENSE");
        doc.setTitle("지출결의 - " + report.getTitle());
        doc.setContentJson("{\"reportTitle\":\"" + escape(report.getTitle())
                + "\",\"totalAmount\":" + tot + ",\"itemCount\":" + items.size() + "}");
        doc.setDrafterId(report.getDrafterId());
        Long docId = approvalService.createDoc(doc, approverIds);
        approvalService.submit(docId);
        report.setApprovalDocId(docId);
        report.setStatusCd("IN_APPROVAL");

        mapper.insertReport(report);

        int seq = 1;
        for (ExpenseItemVO it : items) {
            it.setReportId(report.getReportId());
            it.setSeqNo(seq++);
            mapper.insertItem(it);
        }
        return report.getReportId();
    }

    @Override
    public ExpenseReportVO findById(Long reportId) {
        ExpenseReportVO r = mapper.findById(reportId);
        if (r != null) r.getItems().addAll(mapper.findItems(reportId));
        return r;
    }

    @Override public List<ExpenseReportVO> listMine(Long userId) { return mapper.listMine(userId); }
    @Override public List<ExpenseReportVO> listAdmin(String status, Long deptId) {
        return mapper.listAdmin(status, deptId);
    }

    @Override
    @Transactional
    public List<Long> reimburse(List<Long> reportIds) {
        List<Long> done = new ArrayList<>();
        for (Long id : reportIds) {
            ExpenseReportVO r = findById(id);
            if (r == null) continue;
            if (!"APPROVED".equals(r.getStatusCd())) continue;
            if (!"Y".equals(r.getReimburseRequiredYn())) {
                mapper.updateReportStatus(id, "REIMBURSED", null, LocalDateTime.now());
                done.add(id);
                continue;
            }
            // 환급 대상 라인 (개인카드/현금) 합계
            BigDecimal amt = BigDecimal.ZERO;
            for (ExpenseItemVO it : r.getItems()) {
                if ("PERSONAL_CARD".equals(it.getPaymentMethodCd()) || "CASH".equals(it.getPaymentMethodCd())) {
                    amt = amt.add(it.getTotalAmount() == null ? BigDecimal.ZERO : it.getTotalAmount());
                }
            }
            if (amt.signum() <= 0) {
                mapper.updateReportStatus(id, "REIMBURSED", null, LocalDateTime.now());
                done.add(id);
                continue;
            }
            UserVO u = userService.findById(r.getDrafterId());
            // GW_PAYMENT 출금 거래 생성 (invoice 없이 환급용)
            Long paymentId = paymentMapper.insertOutgoing(amt, u == null ? null : u.getBankCd(),
                u == null ? null : u.getBankAccount(),
                u == null ? null : u.getName(),
                "지출결의 환급 - " + r.getReportNo(),
                r.getDrafterId());
            mapper.updateReportStatus(id, "REIMBURSED", paymentId, LocalDateTime.now());

            // 메일 발송 + 인앱 알림은 생략 (DataInitializer에서 NotificationService 추가 시 통합)
            if (u != null && u.getEmail() != null && !u.getEmail().isBlank()) {
                mailService.enqueue(MailRequest.builder()
                    .templateCd("EXPENSE_REIMBURSED")
                    .to(List.of(u.getEmail()))
                    .vars(Map.of(
                        "userName", u.getName(),
                        "reportNo", r.getReportNo(),
                        "amount",   WON.format(amt)
                    ))
                    .relatedEntity("EXPENSE")
                    .relatedId(String.valueOf(id))
                    .build());
            }
            done.add(id);
        }
        return done;
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "");
    }
}
