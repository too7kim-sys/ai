package egovframework.groupware.leave.service.impl;

import egovframework.groupware.approval.service.ApprovalDocVO;
import egovframework.groupware.approval.service.ApprovalService;
import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.leave.mapper.LeaveMapper;
import egovframework.groupware.leave.service.LeaveBalanceRow;
import egovframework.groupware.leave.service.LeaveBalanceVO;
import egovframework.groupware.leave.service.LeaveRequestVO;
import egovframework.groupware.leave.service.LeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveServiceImpl implements LeaveService {

    private final LeaveMapper mapper;
    private final ApprovalService approvalService;

    public LeaveServiceImpl(LeaveMapper mapper, ApprovalService approvalService) {
        this.mapper = mapper;
        this.approvalService = approvalService;
    }

    @Override
    @Transactional
    public Long apply(Long userId, String leaveTypeCd, LocalDate start, LocalDate end,
                      String reason, List<Long> approverIds) {
        if (start == null || end == null || end.isBefore(start)) {
            throw new ApiException("INVALID_DATE", "휴가 기간이 올바르지 않습니다");
        }
        BigDecimal days = BigDecimal.valueOf(ChronoUnit.DAYS.between(start, end) + 1);
        if ("HALF".equals(leaveTypeCd)) days = new BigDecimal("0.5");

        int year = Year.now().getValue();
        LeaveBalanceVO bal = mapper.findBalance(userId, year);
        if (bal == null) {
            mapper.upsertBalance(userId, year, new BigDecimal("15"));
            bal = mapper.findBalance(userId, year);
        }
        if ("ANNUAL".equals(leaveTypeCd) && bal.remaining().compareTo(days) < 0) {
            throw new ApiException("INSUFFICIENT_BALANCE",
                "연차 잔여(" + bal.remaining() + "일)가 신청일수(" + days + "일)보다 적습니다");
        }

        // 1) 결재 문서 생성 + 상신
        ApprovalDocVO doc = new ApprovalDocVO();
        doc.setFormCd("LEAVE");
        doc.setTitle("휴가 신청 - " + leaveTypeCd + " " + start + "~" + end);
        doc.setContentJson("{\"leaveTypeCd\":\"" + leaveTypeCd + "\",\"start\":\"" + start
                + "\",\"end\":\"" + end + "\",\"days\":" + days + ",\"reason\":\""
                + escape(reason) + "\"}");
        doc.setDrafterId(userId);
        Long docId = approvalService.createDoc(doc, approverIds);
        approvalService.submit(docId);

        // 2) 휴가 신청 행 생성 (상태 IN_PROGRESS)
        LeaveRequestVO req = new LeaveRequestVO();
        req.setUserId(userId);
        req.setLeaveTypeCd(leaveTypeCd);
        req.setStartDt(start);
        req.setEndDt(end);
        req.setDays(days);
        req.setReason(reason);
        req.setStatusCd("IN_PROGRESS");
        req.setApprovalDocId(docId);
        mapper.insertRequest(req);
        return req.getLeaveId();
    }

    @Override public List<LeaveRequestVO> listMine(Long userId) { return mapper.listByUser(userId); }

    @Override
    public LeaveBalanceVO findBalance(Long userId, int year) {
        LeaveBalanceVO b = mapper.findBalance(userId, year);
        if (b == null) {
            mapper.upsertBalance(userId, year, new BigDecimal("15"));
            b = mapper.findBalance(userId, year);
        }
        return b;
    }

    @Override
    @Transactional
    public void grantInitialBalance(Long userId, int year, BigDecimal days) {
        mapper.upsertBalance(userId, year, days);
    }

    @Override
    public List<LeaveBalanceRow> listBalances(int year, String keyword) {
        return mapper.listBalancesForYear(year, keyword);
    }

    @Override
    @Transactional
    public int grantAll(int year, BigDecimal days) {
        List<LeaveBalanceRow> rows = mapper.listBalancesForYear(year, null);
        for (LeaveBalanceRow r : rows) mapper.upsertBalance(r.getUserId(), year, days);
        return rows.size();
    }

    @Override
    @Transactional
    public int grantByTenure(int year) {
        List<LeaveBalanceRow> rows = mapper.listBalancesForYear(year, null);
        for (LeaveBalanceRow r : rows) {
            mapper.upsertBalance(r.getUserId(), year, calcDaysByTenure(r.getHireDate(), year));
        }
        return rows.size();
    }

    /** 입사일과 대상 연도(1/1 기준)의 근속 연수로 표준 연차 일수를 계산. */
    static BigDecimal calcDaysByTenure(LocalDate hire, int targetYear) {
        if (hire == null) return new BigDecimal("15");
        long years = ChronoUnit.YEARS.between(hire, LocalDate.of(targetYear, 1, 1));
        if (years < 1) return new BigDecimal("11");
        int cap = (int) Math.min(15 + (years - 1) / 2, 25);
        return new BigDecimal(cap);
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "");
    }
}
