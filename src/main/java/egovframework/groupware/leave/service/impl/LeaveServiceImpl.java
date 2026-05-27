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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    /** 1일 표준 근무 시간. 시간연차 → 일수 환산 기준. */
    private static final BigDecimal HOURS_PER_DAY = new BigDecimal("8");

    @Override
    @Transactional
    public Long apply(Long userId, String leaveTypeCd, LocalDate start, LocalDate end,
                      LocalDateTime startAt, LocalDateTime endAt,
                      String reason, List<Long> approverIds) {
        boolean hourly = "HOURLY".equals(leaveTypeCd);
        BigDecimal days;

        if (hourly) {
            if (startAt == null || endAt == null || !endAt.isAfter(startAt)) {
                throw new ApiException("INVALID_TIME", "시간연차의 시작/종료 시각이 올바르지 않습니다");
            }
            if (!startAt.toLocalDate().equals(endAt.toLocalDate())) {
                throw new ApiException("INVALID_TIME", "시간연차는 동일 일자 안에서만 신청할 수 있습니다");
            }
            long minutes = ChronoUnit.MINUTES.between(startAt, endAt);
            if (minutes < 60 || minutes % 60 != 0) {
                throw new ApiException("INVALID_TIME", "시간연차는 1시간 단위로 신청해야 합니다");
            }
            // days = hours / 8, NUMERIC(6,3) 에 맞춰 3자리 반올림
            days = new BigDecimal(minutes).divide(new BigDecimal(60), 0, RoundingMode.HALF_UP)
                    .divide(HOURS_PER_DAY, 3, RoundingMode.HALF_UP);
            start = startAt.toLocalDate();
            end   = endAt.toLocalDate();
        } else {
            if (start == null || end == null || end.isBefore(start)) {
                throw new ApiException("INVALID_DATE", "휴가 기간이 올바르지 않습니다");
            }
            days = BigDecimal.valueOf(ChronoUnit.DAYS.between(start, end) + 1);
            if ("HALF".equals(leaveTypeCd)) days = new BigDecimal("0.5");
            startAt = null;
            endAt   = null;
        }

        int year = Year.now().getValue();
        LeaveBalanceVO bal = mapper.findBalance(userId, year);
        if (bal == null) {
            mapper.upsertBalance(userId, year, new BigDecimal("15"));
            bal = mapper.findBalance(userId, year);
        }
        if (("ANNUAL".equals(leaveTypeCd) || hourly)
                && bal.remaining().compareTo(days) < 0) {
            throw new ApiException("INSUFFICIENT_BALANCE",
                "연차 잔여(" + bal.remaining() + "일)가 신청일수(" + days + "일)보다 적습니다");
        }

        // 1) 결재 문서 생성 + 상신
        ApprovalDocVO doc = new ApprovalDocVO();
        doc.setFormCd("LEAVE");
        String title = hourly
                ? "휴가 신청 - 시간연차 " + start + " "
                    + startAt.toLocalTime() + "~" + endAt.toLocalTime()
                : "휴가 신청 - " + leaveTypeCd + " " + start + "~" + end;
        doc.setTitle(title);
        StringBuilder json = new StringBuilder();
        json.append("{\"leaveTypeCd\":\"").append(leaveTypeCd).append("\"")
            .append(",\"start\":\"").append(start).append("\"")
            .append(",\"end\":\"").append(end).append("\"");
        if (hourly) {
            json.append(",\"startAt\":\"").append(startAt).append("\"")
                .append(",\"endAt\":\"").append(endAt).append("\"");
        }
        json.append(",\"days\":").append(days)
            .append(",\"reason\":\"").append(escape(reason)).append("\"}");
        doc.setContentJson(json.toString());
        doc.setDrafterId(userId);
        Long docId = approvalService.createDoc(doc, approverIds);
        approvalService.submit(docId);

        // 2) 휴가 신청 행 생성 (상태 IN_PROGRESS)
        LeaveRequestVO req = new LeaveRequestVO();
        req.setUserId(userId);
        req.setLeaveTypeCd(leaveTypeCd);
        req.setStartDt(start);
        req.setEndDt(end);
        req.setStartAt(startAt);
        req.setEndAt(endAt);
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
