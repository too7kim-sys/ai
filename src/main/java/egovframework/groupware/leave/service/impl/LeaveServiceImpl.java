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
import java.time.LocalTime;
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
    /** 시간연차 신청 가능한 업무 시간대 시작/종료. */
    private static final LocalTime WORK_START = LocalTime.of(9, 0);
    private static final LocalTime WORK_END   = LocalTime.of(18, 0);
    /** 점심 시간 (시간연차 신청 불가 구간). */
    private static final LocalTime LUNCH_START = LocalTime.of(12, 0);
    private static final LocalTime LUNCH_END   = LocalTime.of(13, 0);

    @Override
    @Transactional
    public Long apply(Long userId, String leaveTypeCd, LocalDate start, LocalDate end,
                      LocalDateTime startAt, LocalDateTime endAt,
                      String reason, List<Long> approverIds,
                      String halfTypeCd) {
        boolean hourly = "HOURLY".equals(leaveTypeCd);
        boolean half   = "HALF".equals(leaveTypeCd);
        BigDecimal days;

        if (hourly) {
            if (startAt == null || endAt == null || !endAt.isAfter(startAt)) {
                throw new ApiException("INVALID_TIME", "시간연차의 시작/종료 시각이 올바르지 않습니다");
            }
            if (!startAt.toLocalDate().equals(endAt.toLocalDate())) {
                throw new ApiException("INVALID_TIME", "시간연차는 동일 일자 안에서만 신청할 수 있습니다");
            }
            LocalTime st = startAt.toLocalTime();
            LocalTime et = endAt.toLocalTime();
            if (st.isBefore(WORK_START) || et.isAfter(WORK_END)) {
                throw new ApiException("INVALID_TIME",
                    "시간연차는 업무 시간(09:00~18:00) 안에서만 신청할 수 있습니다");
            }
            // 점심 시간(12:00~13:00) 과 교집합이 있으면 차단
            if (st.isBefore(LUNCH_END) && et.isAfter(LUNCH_START)) {
                throw new ApiException("INVALID_TIME",
                    "시간연차는 점심 시간(12:00~13:00)을 포함할 수 없습니다");
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
            if (half) {
                days = new BigDecimal("0.5");
                if (!start.equals(end)) {
                    throw new ApiException("INVALID_DATE", "반차는 단일 일자에만 신청할 수 있습니다");
                }
                if (halfTypeCd == null || !(halfTypeCd.equals("AM") || halfTypeCd.equals("PM"))) {
                    throw new ApiException("INVALID_HALF", "반차는 오전(AM) 또는 오후(PM)를 선택해야 합니다");
                }
            } else {
                halfTypeCd = null;
            }
            startAt = null;
            endAt   = null;
        }

        // 과거 일자 차단 (오늘은 허용 — 당일 오전 반차 / 사후 결재 등 운영 케이스)
        if (start.isBefore(LocalDate.now())) {
            throw new ApiException("PAST_DATE", "과거 일자는 휴가 신청할 수 없습니다");
        }

        // 같은 일자 중복 신청(IN_PROGRESS/APPROVED) 차단
        int dup = hourly
                ? mapper.countOverlappingHourly(userId, startAt, endAt)
                : mapper.countOverlappingDay(userId, start, end);
        if (dup > 0) {
            throw new ApiException("DUPLICATED",
                "같은 일자에 이미 신청한 휴가가 있습니다(결재 중 또는 승인됨).");
        }

        int year = Year.now().getValue();
        LeaveBalanceVO bal = mapper.findBalance(userId, year);
        if (bal == null) {
            mapper.upsertBalance(userId, year, new BigDecimal("15"));
            bal = mapper.findBalance(userId, year);
        }
        // 잔여 검증: 연차 계열(ANNUAL/HALF/HOURLY) 은 잔여에서 차감되므로 사전 검증.
        boolean deductible = "ANNUAL".equals(leaveTypeCd) || hourly || half;
        if (deductible && bal.remaining().compareTo(days) < 0) {
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
        req.setHalfTypeCd(halfTypeCd);
        req.setStatusCd("IN_PROGRESS");
        req.setApprovalDocId(docId);
        mapper.insertRequest(req);

        // 3) 잔여 임시 차감 — 결재 중에도 잔여에 즉시 반영하여 이중 신청을 막는다.
        //    승인 시에는 추가 차감 없음(LeaveApprovalHook), 반려/취소 시 복구.
        if (deductible) {
            mapper.addUsed(userId, year, days);
        }
        return req.getLeaveId();
    }

    @Override
    @Transactional
    public void cancel(Long leaveId, Long requesterId) {
        LeaveRequestVO r = mapper.findById(leaveId);
        if (r == null) throw new ApiException("NOT_FOUND", "휴가 신청을 찾을 수 없습니다");
        if (!r.getUserId().equals(requesterId)) {
            throw new ApiException("FORBIDDEN", "본인 신청만 취소할 수 있습니다");
        }
        String s = r.getStatusCd();
        if (!"IN_PROGRESS".equals(s) && !"APPROVED".equals(s)) {
            throw new ApiException("INVALID_STATE", "취소할 수 없는 상태입니다: " + s);
        }
        // 결재 문서가 IN_PROGRESS 면 함께 회수
        if ("IN_PROGRESS".equals(s) && r.getApprovalDocId() != null) {
            try { approvalService.cancel(r.getApprovalDocId(), requesterId); }
            catch (Exception ignore) { /* 이미 처리 중 등은 무시 */ }
        }
        mapper.updateRequestStatus(leaveId, "CANCELED", null);
        // 잔여 복구 — apply 에서 즉시 차감했으므로 음수 더하기로 복구.
        boolean deductible = isDeductible(r.getLeaveTypeCd());
        if (deductible) {
            int year = r.getStartDt() != null ? r.getStartDt().getYear() : Year.now().getValue();
            mapper.addUsed(requesterId, year, r.getDays().negate());
        }
    }

    private static boolean isDeductible(String typeCd) {
        return "ANNUAL".equals(typeCd) || "HALF".equals(typeCd) || "HOURLY".equals(typeCd);
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

    @Override
    public String findFullDayLeaveTypeOn(Long userId, LocalDate date) {
        if (userId == null || date == null) return null;
        return mapper.findFullDayLeaveTypeOn(userId, date);
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
