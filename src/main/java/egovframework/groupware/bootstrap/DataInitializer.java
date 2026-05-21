package egovframework.groupware.bootstrap;

import egovframework.groupware.attach.service.AttachService;
import egovframework.groupware.attach.service.AttachVO;
import egovframework.groupware.attendance.mapper.AttendanceMapper;
import egovframework.groupware.attendance.service.AttendanceVO;
import egovframework.groupware.board.mapper.BoardMapper;
import egovframework.groupware.board.service.BoardVO;
import egovframework.groupware.board.service.PostCommentVO;
import egovframework.groupware.board.service.PostVO;
import egovframework.groupware.calendar.mapper.CalendarMapper;
import egovframework.groupware.calendar.service.CalEventVO;
import egovframework.groupware.doc.mapper.DocMapper;
import egovframework.groupware.doc.service.DocFileVO;
import egovframework.groupware.evaluation.mapper.EvaluationMapper;
import egovframework.groupware.evaluation.service.EvalPeriodVO;
import egovframework.groupware.hr.mapper.HrMapper;
import egovframework.groupware.hr.service.FamilyVO;
import egovframework.groupware.hr.service.HrHistoryVO;
import egovframework.groupware.hr.service.HrRecordVO;
import egovframework.groupware.mail.mapper.MailMapper;
import egovframework.groupware.mail.service.MailLogVO;
import egovframework.groupware.message.mapper.MessageMapper;
import egovframework.groupware.message.service.MessageVO;
import egovframework.groupware.notice.mapper.NoticeMapper;
import egovframework.groupware.notice.service.NoticeVO;
import egovframework.groupware.performance.mapper.PerfMapper;
import egovframework.groupware.performance.service.PerfVO;
import egovframework.groupware.room.mapper.RoomMapper;
import egovframework.groupware.room.service.RoomReservationVO;
import egovframework.groupware.user.mapper.UserMapper;
import egovframework.groupware.user.service.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 첫 기동 시 시드 사용자 20명 + 인사기록/이력, 평가기간, 부양가족, KPI 샘플을 삽입한다.
 * 이미 admin@company.com이 존재하면 skip.
 */
@Component
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String DEFAULT_PWD = "Demo!2025";

    private final UserMapper userMapper;
    private final HrMapper hrMapper;
    private final EvaluationMapper evaluationMapper;
    private final PerfMapper perfMapper;
    private final NoticeMapper noticeMapper;
    private final CalendarMapper calendarMapper;
    private final AttendanceMapper attendanceMapper;
    private final MessageMapper messageMapper;
    private final RoomMapper roomMapper;
    private final BoardMapper boardMapper;
    private final DocMapper docMapper;
    private final MailMapper mailMapper;
    private final AttachService attachService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper,
                           HrMapper hrMapper,
                           EvaluationMapper evaluationMapper,
                           PerfMapper perfMapper,
                           NoticeMapper noticeMapper,
                           CalendarMapper calendarMapper,
                           AttendanceMapper attendanceMapper,
                           MessageMapper messageMapper,
                           RoomMapper roomMapper,
                           BoardMapper boardMapper,
                           DocMapper docMapper,
                           MailMapper mailMapper,
                           AttachService attachService,
                           PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.hrMapper = hrMapper;
        this.evaluationMapper = evaluationMapper;
        this.perfMapper = perfMapper;
        this.noticeMapper = noticeMapper;
        this.calendarMapper = calendarMapper;
        this.attendanceMapper = attendanceMapper;
        this.messageMapper = messageMapper;
        this.roomMapper = roomMapper;
        this.boardMapper = boardMapper;
        this.docMapper = docMapper;
        this.mailMapper = mailMapper;
        this.attachService = attachService;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void initialize() {
        if (userMapper.findByEmail("admin@company.com") != null) {
            log.info("Seed users already exist — skipping initialization.");
            return;
        }
        log.info("Seeding demo users ...");
        seed("admin@company.com",            "관리자",   "ADMIN",            null,  7);
        seed("hr@company.com",               "이인사",   "HR_MANAGER",       3L,    6);
        seed("hr.assist@company.com",        "박인사",   "HR_MANAGER",       3L,    4);
        seed("finance@company.com",          "김재무",   "FINANCE_MANAGER",  4L,    6);
        seed("manager.dev1@company.com",     "정개발",   "MANAGER",          6L,    6);
        seed("manager.dev2@company.com",     "최개발",   "MANAGER",          7L,    6);
        seed("manager.design@company.com",   "송디자인", "MANAGER",          8L,    5);
        seed("manager.marketing@company.com","오마케",   "MANAGER",          9L,    5);
        seed("manager.sales@company.com",    "임영업",   "MANAGER",         10L,    5);
        seed("emp.dev1.kim@company.com",     "김개발",   "EMPLOYEE",         6L,    3);
        seed("emp.dev1.lee@company.com",     "이개발",   "EMPLOYEE",         6L,    2);
        seed("emp.dev1.park@company.com",    "박개발",   "EMPLOYEE",         6L,    1);
        seed("emp.dev2.han@company.com",     "한개발",   "EMPLOYEE",         7L,    3);
        seed("emp.dev2.choi@company.com",    "최코더",   "EMPLOYEE",         7L,    2);
        seed("emp.design.yoo@company.com",   "유디자이너","EMPLOYEE",         8L,    2);
        seed("emp.design.jang@company.com",  "장디자이너","EMPLOYEE",         8L,    1);
        seed("emp.marketing.kim@company.com","김마케",   "EMPLOYEE",         9L,    3);
        seed("emp.sales.lee@company.com",    "이영업",   "EMPLOYEE",        10L,    3);
        seed("emp.sales.kang@company.com",   "강영업",   "EMPLOYEE",        10L,    2);
        seed("emp.fin.yoon@company.com",     "윤회계",   "EMPLOYEE",         4L,    2);
        log.info("Seeded 20 demo users (password: {})", DEFAULT_PWD);

        seedHrAndEvaluation();
        seedCollab();
        seedBoardAndDocsAndMail();
    }

    private void seedBoardAndDocsAndMail() {
        log.info("Seeding board posts / doc samples / mail logs ...");

        UserVO admin = userMapper.findByEmail("admin@company.com");
        UserVO hr = userMapper.findByEmail("hr@company.com");
        UserVO mgrDev = userMapper.findByEmail("manager.dev1@company.com");
        UserVO empKim = userMapper.findByEmail("emp.dev1.kim@company.com");
        UserVO empLee = userMapper.findByEmail("emp.dev1.lee@company.com");

        BoardVO bdFree = boardMapper.findBoardByCd("FREE");
        BoardVO bdQna = boardMapper.findBoardByCd("QNA");
        BoardVO bdAnon = boardMapper.findBoardByCd("VOICE");

        // === 게시판 시드 ===
        if (bdFree != null) {
            seedPost(bdFree.getBoardId(), empKim.getUserId(), "N", "N",
                    "🎉 5월 사내 행사 후기",
                    "지난 주말 한강 워크샵 정말 즐거웠습니다. 좋은 시간 만들어주신 인사팀에게 감사드려요!");
            seedPost(bdFree.getBoardId(), empLee.getUserId(), "N", "N",
                    "점심 추천 맛집 공유",
                    "회사 근처 새로 오픈한 일본식 라멘집 정말 맛있습니다. 한 번 가보세요.");
            seedPost(bdFree.getBoardId(), mgrDev.getUserId(), "Y", "N",
                    "[공지] 6월 부서 회식 일정",
                    "6월 12일(금) 18:30, 강남 OO식당에서 6월 회식 진행합니다. 참석 여부는 댓글로 부탁드려요.");
        }
        if (bdQna != null) {
            Long qid = seedPost(bdQna.getBoardId(), empKim.getUserId(), "N", "N",
                    "GitHub Actions 사용 권한 신청은 어디서 하나요?",
                    "사내 GitHub 조직에 GitHub Actions 사용 권한을 신청하려고 합니다. 신청 방법 안내 부탁드립니다.");
            if (qid != null) {
                seedComment(qid, admin.getUserId(),
                        "헬프데스크 → '개발 도구 권한 요청' 폼으로 신청하시면 1영업일 내 처리됩니다.");
                boardMapper.markAnswered(qid);
            }
            seedPost(bdQna.getBoardId(), empLee.getUserId(), "N", "N",
                    "원격 근무 시 출퇴근 등록은 어떻게 하나요?",
                    "재택근무일에도 그룹웨어 근태 메뉴에서 출/퇴근 버튼만 눌러주시면 정상 등록됩니다.");
        }
        if (bdAnon != null) {
            seedPost(bdAnon.getBoardId(), empKim.getUserId(), "N", "Y",
                    "회의실 예약 시간 30분 단위로 줄여주세요",
                    "현재 1시간 단위인데, 30분 단위로 예약할 수 있으면 좋겠습니다.");
        }

        // === 자료실 시드 (텍스트 파일 작성 + 자료 등록) ===
        try {
            if (admin != null) {
                seedDocFile(1L, admin.getUserId(), "2026년 사내 가이드 v1.0",
                        "사내 시스템 사용 가이드", "guide.txt",
                        "# 사내 시스템 가이드\n\n이 문서는 신규 입사자를 위한 사내 시스템 사용 가이드입니다.\n...");
                seedDocFile(2L, admin.getUserId(), "휴가 신청서 양식",
                        "휴가 신청 시 사용", "leave-form.txt",
                        "휴가 신청서\n\n신청자: \n기간: \n사유: \n");
                seedDocFile(3L, admin.getUserId(), "세금계산서 발행 매뉴얼",
                        "회계팀 매뉴얼", "tax-invoice.txt",
                        "세금계산서 발행 매뉴얼\n\n1. 거래처 정보 확인\n2. 사업자 계약서 검토\n3. ...");
                seedDocFile(4L, admin.getUserId(), "노트북 셋업 가이드",
                        "IT팀 매뉴얼", "laptop-setup.txt",
                        "노트북 셋업 가이드\n\n1. Windows 업데이트\n2. 사내 인증서 설치\n3. ...");
            }
        } catch (Exception e) {
            log.warn("자료실 시드 실패: {}", e.getMessage());
        }

        // === 메일 로그 시드 (다양한 상태) ===
        seedMail("admin@company.com", "[시스템 점검] 그룹웨어 정기 점검 안내", "SENT", null);
        seedMail("hr@company.com", "[HR] 2026년 1분기 평가 마감 안내", "SENT", "EVAL_REMINDER");
        seedMail("emp.dev1.kim@company.com", "[휴가] 휴가 신청 결재 완료", "SENT", "LEAVE_APPROVED");
        seedMail("emp.dev1.lee@company.com", "[근태] 지각 누계 안내", "QUEUED", null);
        seedMail("ex-employee@old-company.com", "[휴가] 잔여 연차 안내", "FAILED",
                "LEAVE_REMINDER");
    }

    private Long seedPost(Long boardId, Long authorId, String pinnedYn, String anonymousYn,
                          String title, String content) {
        PostVO p = new PostVO();
        p.setBoardId(boardId);
        p.setAuthorId(authorId);
        p.setPinnedYn(pinnedYn);
        p.setAnonymousYn(anonymousYn);
        p.setTitle(title);
        p.setContent(content);
        boardMapper.insertPost(p);
        return p.getPostId();
    }

    private void seedComment(Long postId, Long authorId, String content) {
        PostCommentVO c = new PostCommentVO();
        c.setPostId(postId);
        c.setAuthorId(authorId);
        c.setContent(content);
        c.setAnonymousYn("N");
        boardMapper.insertComment(c);
    }

    /**
     * 자료실 시드용 텍스트 파일을 storeBytes 로 저장하고 doc 등록.
     */
    private void seedDocFile(Long folderId, Long ownerId, String title, String desc,
                             String filename, String content) throws java.io.IOException {
        Long groupId = attachService.createGroup("DOC", folderId.toString());
        AttachVO att = attachService.storeBytes(groupId, filename, "text/plain",
                content.getBytes(java.nio.charset.StandardCharsets.UTF_8), ownerId);
        DocFileVO doc = new DocFileVO();
        doc.setFolderId(folderId);
        doc.setAttachId(att.getAttachId());
        doc.setTitle(title);
        doc.setDescription(desc);
        doc.setOwnerId(ownerId);
        docMapper.insertFile(doc);
    }

    private void seedMail(String toEmail, String subject, String status, String templateCd) {
        MailLogVO log = new MailLogVO();
        log.setToEmail(toEmail);
        log.setSubject(subject);
        log.setBodyPreview(subject.length() > 100 ? subject.substring(0, 100) : subject);
        log.setTemplateCd(templateCd);
        log.setStatusCd(status);
        log.setRetryCnt("FAILED".equals(status) ? 3 : 0);
        if ("FAILED".equals(status)) log.setErrorMessage("SMTP 550: 수신자 도메인이 존재하지 않습니다");
        mailMapper.insertLog(log);
        if ("SENT".equals(status)) {
            mailMapper.updateLogSent(log.getMailId());
        }
    }

    private void seedHrAndEvaluation() {
        log.info("Seeding HR records / evaluation / KPI ...");

        // 1) 인사기록(HIRE) + 인사이력(HIRE) 모든 사용자
        for (UserVO u : userMapper.listAll()) {
            HrRecordVO rec = new HrRecordVO();
            rec.setUserId(u.getUserId());
            rec.setCategoryCd("HIRE");
            rec.setTitle("신규 입사");
            rec.setContent(u.getDeptNm() + " " + u.getPositionNm() + " 입사");
            rec.setEventDt(u.getHireDate());
            hrMapper.insertRecord(rec);

            HrHistoryVO his = new HrHistoryVO();
            his.setUserId(u.getUserId());
            his.setChangeTypeCd("HIRE");
            his.setBeforeJson("{}");
            his.setAfterJson(String.format("{\"deptId\":%s,\"positionId\":%s,\"roleCd\":\"%s\"}",
                    u.getDeptId(), u.getPositionId(), nullSafe(u.getRoleCd())));
            his.setEffectiveDt(u.getHireDate());
            hrMapper.insertHistory(his);
        }

        // 2) 일부 직원에게 승진/이동 이력 + 인사기록 추가
        UserVO promoted = userMapper.findByEmail("emp.dev1.lee@company.com");
        if (promoted != null) {
            HrRecordVO rec = new HrRecordVO();
            rec.setUserId(promoted.getUserId());
            rec.setCategoryCd("PROMOTION");
            rec.setTitle("주임 → 대리 승진");
            rec.setContent("2025년 1월 1일자 직급 변경");
            rec.setEventDt(LocalDate.of(2025, 1, 1));
            hrMapper.insertRecord(rec);

            HrHistoryVO h = new HrHistoryVO();
            h.setUserId(promoted.getUserId());
            h.setChangeTypeCd("POSITION_CHANGE");
            h.setBeforeJson("{\"positionId\":2,\"positionNm\":\"주임\"}");
            h.setAfterJson("{\"positionId\":3,\"positionNm\":\"대리\"}");
            h.setEffectiveDt(LocalDate.of(2025, 1, 1));
            hrMapper.insertHistory(h);
        }

        UserVO transferred = userMapper.findByEmail("emp.dev2.han@company.com");
        if (transferred != null) {
            HrHistoryVO h = new HrHistoryVO();
            h.setUserId(transferred.getUserId());
            h.setChangeTypeCd("DEPT_CHANGE");
            h.setBeforeJson("{\"deptId\":6,\"deptNm\":\"개발1팀\"}");
            h.setAfterJson("{\"deptId\":7,\"deptNm\":\"개발2팀\"}");
            h.setEffectiveDt(LocalDate.of(2024, 7, 1));
            hrMapper.insertHistory(h);

            HrRecordVO rec = new HrRecordVO();
            rec.setUserId(transferred.getUserId());
            rec.setCategoryCd("TRANSFER");
            rec.setTitle("개발1팀 → 개발2팀 이동");
            rec.setEventDt(LocalDate.of(2024, 7, 1));
            hrMapper.insertRecord(rec);
        }

        // 교육이수 / 자격 / 포상 예시
        UserVO emp1 = userMapper.findByEmail("emp.dev1.kim@company.com");
        if (emp1 != null) {
            HrRecordVO r1 = new HrRecordVO();
            r1.setUserId(emp1.getUserId());
            r1.setCategoryCd("EDUCATION");
            r1.setTitle("AWS Architect 교육 이수");
            r1.setEventDt(LocalDate.of(2025, 9, 15));
            hrMapper.insertRecord(r1);

            HrRecordVO r2 = new HrRecordVO();
            r2.setUserId(emp1.getUserId());
            r2.setCategoryCd("AWARD");
            r2.setTitle("우수사원 포상");
            r2.setContent("2025년 상반기 우수사원으로 선정");
            r2.setEventDt(LocalDate.of(2025, 7, 1));
            hrMapper.insertRecord(r2);
        }

        // 3) 부양가족 샘플 (HR 매니저, 일부 직원)
        UserVO hrUser = userMapper.findByEmail("hr@company.com");
        if (hrUser != null) {
            insertFamily(hrUser.getUserId(), "SPOUSE", "김배우자", LocalDate.of(1986, 5, 10), "Y", "N", "N");
            insertFamily(hrUser.getUserId(), "CHILD", "김첫째", LocalDate.of(2015, 3, 4), "Y", "N", "N");
            insertFamily(hrUser.getUserId(), "CHILD", "김둘째", LocalDate.of(2018, 8, 22), "Y", "N", "N");
        }
        UserVO manager = userMapper.findByEmail("manager.dev1@company.com");
        if (manager != null) {
            insertFamily(manager.getUserId(), "SPOUSE", "정배우자", LocalDate.of(1984, 11, 1), "Y", "N", "N");
            insertFamily(manager.getUserId(), "PARENT", "정아버님", LocalDate.of(1950, 1, 15), "Y", "Y", "N");
        }

        // 4) 평가 기간
        EvalPeriodVO q1 = new EvalPeriodVO();
        q1.setPeriodNm("2026년 1분기 평가");
        q1.setStartDt(LocalDate.of(2026, 1, 1));
        q1.setEndDt(LocalDate.of(2026, 3, 31));
        q1.setStatusCd("CLOSED");
        evaluationMapper.insertPeriod(q1);

        EvalPeriodVO q2 = new EvalPeriodVO();
        q2.setPeriodNm("2026년 2분기 평가");
        q2.setStartDt(LocalDate.of(2026, 4, 1));
        q2.setEndDt(LocalDate.of(2026, 6, 30));
        q2.setStatusCd("IN_PROGRESS");
        evaluationMapper.insertPeriod(q2);

        // 5) 샘플 KPI — 활성 기간(Q2) 기준
        if (q2.getPeriodId() != null) {
            seedKpi("emp.dev1.kim@company.com", q2.getPeriodId(), "JS 마이그레이션",
                    "마이그레이션 완료 모듈 수", "건", 12, 9, 40);
            seedKpi("emp.dev1.kim@company.com", q2.getPeriodId(), "코드 리뷰",
                    "리뷰 처리 건수", "건", 40, 35, 30);
            seedKpi("emp.dev1.kim@company.com", q2.getPeriodId(), "버그 처리",
                    "처리한 버그 수", "건", 20, 22, 30);

            seedKpi("emp.dev1.lee@company.com", q2.getPeriodId(), "신규 API 개발",
                    "릴리스 API 수", "개", 8, 6, 50);
            seedKpi("emp.dev1.lee@company.com", q2.getPeriodId(), "테스트 커버리지",
                    "유닛 테스트 커버리지", "%", 80, 75, 50);

            seedKpi("emp.sales.lee@company.com", q2.getPeriodId(), "매출 확보",
                    "분기 매출(백만원)", "백만원", 500, 420, 70);
            seedKpi("emp.sales.lee@company.com", q2.getPeriodId(), "신규 고객",
                    "신규 계약 건수", "건", 5, 6, 30);

            seedKpi("emp.marketing.kim@company.com", q2.getPeriodId(), "리드 생성",
                    "신규 리드", "건", 200, 220, 60);
            seedKpi("emp.marketing.kim@company.com", q2.getPeriodId(), "콘텐츠 발행",
                    "블로그 발행 수", "건", 20, 18, 40);

            seedKpi("emp.design.yoo@company.com", q2.getPeriodId(), "UI 리뉴얼",
                    "리뉴얼 페이지 수", "페이지", 30, 25, 100);
        }

        log.info("HR/평가/KPI 시드 완료.");
    }

    private void insertFamily(Long userId, String relation, String name, LocalDate birth,
                              String dep, String elderly, String disabled) {
        FamilyVO f = new FamilyVO();
        f.setUserId(userId);
        f.setRelationCd(relation);
        f.setName(name);
        f.setBirthDt(birth);
        f.setDependentYn(dep);
        f.setElderlyYn(elderly);
        f.setDisabledYn(disabled);
        hrMapper.insertFamily(f);
    }

    private void seedKpi(String email, Long periodId, String goal, String kpi, String unit,
                         double target, double actual, double weight) {
        UserVO u = userMapper.findByEmail(email);
        if (u == null) return;
        PerfVO p = new PerfVO();
        p.setUserId(u.getUserId());
        p.setPeriodId(periodId);
        p.setGoal(goal);
        p.setKpi(kpi);
        p.setUnit(unit);
        p.setTargetVal(BigDecimal.valueOf(target));
        p.setActualVal(BigDecimal.valueOf(actual));
        p.setWeight(BigDecimal.valueOf(weight));
        if (target > 0) {
            p.setAchRate(BigDecimal.valueOf(actual).multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(target), 2, RoundingMode.HALF_UP));
        }
        perfMapper.insert(p);
    }

    private void seed(String email, String name, String roleCd, Long deptId, int positionId) {
        UserVO u = new UserVO();
        u.setEmail(email);
        u.setName(name);
        u.setRoleCd(roleCd);
        u.setDeptId(deptId);
        u.setPositionId((long) positionId);
        u.setHireDate(LocalDate.of(2020 + (int)(Math.random()*5), 1 + (int)(Math.random()*12), 1));
        u.setPhone("010-" + (1000 + (int)(Math.random()*9000)) + "-" + (1000 + (int)(Math.random()*9000)));
        u.setBankCd("088");
        u.setBankAccount("110-" + System.currentTimeMillis() % 1000000);
        u.setPasswordHash(passwordEncoder.encode(DEFAULT_PWD));
        LocalDateTime now = LocalDateTime.now();
        u.setPwdChangedAt(now);
        u.setPwdExpireAt(now.plusDays(90));
        userMapper.insert(u);
    }

    private String nullSafe(String s) { return s == null ? "" : s; }

    private void seedCollab() {
        log.info("Seeding notices / calendar / attendance / messages / room reservations ...");

        UserVO admin = userMapper.findByEmail("admin@company.com");
        UserVO hr = userMapper.findByEmail("hr@company.com");
        UserVO mgrDev = userMapper.findByEmail("manager.dev1@company.com");
        UserVO empKim = userMapper.findByEmail("emp.dev1.kim@company.com");
        UserVO empLee = userMapper.findByEmail("emp.dev1.lee@company.com");
        UserVO empSales = userMapper.findByEmail("emp.sales.lee@company.com");

        /* === 공지 5건 === */
        if (admin != null) seedNotice(admin.getUserId(), "Y", "ALL",
                "🎉 2026년 새해 인사",
                "임직원 여러분, 2026년 새해 복 많이 받으세요.\n올해도 한 해 동안 함께 큰 성과를 만들어 봅시다.");
        if (hr != null) seedNotice(hr.getUserId(), "Y", "ALL",
                "[HR] 2026년 연차 부여 안내",
                "2026년 연차 15일이 일괄 부여되었습니다. /leave/balance.do 에서 확인 가능합니다.");
        if (hr != null) seedNotice(hr.getUserId(), "N", "ALL",
                "[HR] 1분기 평가 마감",
                "2026년 1분기 평가가 3월 31일자로 마감되었습니다. 결과는 평가관리 메뉴에서 확인하실 수 있습니다.");
        if (admin != null) seedNotice(admin.getUserId(), "N", "ALL",
                "사무실 보안 점검 안내",
                "5월 28일(목) 19:00 ~ 22:00 전사 보안 점검 진행 예정입니다.\n해당 시간에는 사내망 일시 차단이 있을 수 있습니다.");
        if (mgrDev != null) seedNotice(mgrDev.getUserId(), "N", "DEPT",
                "[개발1팀] 주간 회의 일정 변경",
                "이번 주 정기 회의는 수요일 11:00로 조정합니다.");

        /* === 캘린더 이벤트 === */
        LocalDate today = LocalDate.now();
        if (admin != null) {
            seedEvent(admin.getUserId(), null, "COMPANY", "전사 워크샵 (분기 OKR 리뷰)",
                    today.plusDays(7).atTime(9, 0), today.plusDays(7).atTime(18, 0), "#dc3545");
            seedEvent(admin.getUserId(), null, "COMPANY", "보안 점검",
                    today.plusDays(8).atTime(19, 0), today.plusDays(8).atTime(22, 0), "#fd7e14");
        }
        if (mgrDev != null) {
            seedEvent(mgrDev.getUserId(), mgrDev.getDeptId(), "DEPT", "개발1팀 주간 회의",
                    today.atTime(11, 0), today.atTime(12, 0), "#0d6efd");
            seedEvent(mgrDev.getUserId(), mgrDev.getDeptId(), "DEPT", "스프린트 리뷰",
                    today.plusDays(4).atTime(15, 0), today.plusDays(4).atTime(17, 0), "#0d6efd");
        }
        if (empKim != null) {
            seedEvent(empKim.getUserId(), null, "PERSONAL", "1:1 매니저 미팅",
                    today.plusDays(1).atTime(14, 0), today.plusDays(1).atTime(15, 0), "#198754");
        }

        /* === 근태 (최근 5영업일) === */
        for (UserVO u : userMapper.listAll()) {
            for (int back = 1; back <= 5; back++) {
                LocalDate d = today.minusDays(back);
                if (d.getDayOfWeek().getValue() >= 6) continue;
                seedAttendance(u.getUserId(), d, u.getUserId() % 5 == 0);
            }
        }

        /* === 쪽지 (몇 건 미리 주고받음) === */
        if (hr != null && empKim != null) {
            seedMessage(hr.getUserId(), empKim.getUserId(),
                    "안녕하세요 김개발님, 6월 워크숍 참석 가능 여부 알려주세요.");
        }
        if (mgrDev != null && empKim != null) {
            seedMessage(mgrDev.getUserId(), empKim.getUserId(),
                    "오늘 스탠드업 후 5분 시간 가능할까요? 신규 기능 관련 논의 드릴게요.");
        }
        if (admin != null && hr != null) {
            seedMessage(admin.getUserId(), hr.getUserId(),
                    "2분기 평가 일정 확정 후 공지 부탁드립니다.");
        }

        /* === 회의실 예약 === */
        if (mgrDev != null) {
            seedRoomReservation(1L, mgrDev.getUserId(),
                    today.atTime(11, 0), today.atTime(12, 0),
                    "개발1팀 주간 회의");
        }
        if (empSales != null) {
            seedRoomReservation(3L, empSales.getUserId(),
                    today.plusDays(1).atTime(14, 0), today.plusDays(1).atTime(15, 30),
                    "고객 미팅");
        }
        if (admin != null) {
            seedRoomReservation(4L, admin.getUserId(),
                    today.plusDays(7).atTime(9, 0), today.plusDays(7).atTime(18, 0),
                    "전사 워크샵");
        }

        log.info("협업 모듈 시드 완료.");
    }

    private void seedNotice(Long authorId, String pinnedYn, String scope, String title, String content) {
        NoticeVO n = new NoticeVO();
        n.setAuthorId(authorId);
        n.setTitle(title);
        n.setContent(content);
        n.setPinnedYn(pinnedYn);
        n.setDeptScope(scope);
        noticeMapper.insert(n);
    }

    private void seedEvent(Long ownerId, Long deptId, String scope, String title,
                           LocalDateTime start, LocalDateTime end, String color) {
        CalEventVO e = new CalEventVO();
        e.setOwnerId(ownerId);
        e.setDeptId(deptId);
        e.setScopeCd(scope);
        e.setTitle(title);
        e.setStartDt(start);
        e.setEndDt(end);
        e.setColor(color);
        calendarMapper.insert(e);
    }

    private void seedAttendance(Long userId, LocalDate day, boolean late) {
        LocalDateTime checkIn = day.atTime(late ? 9 : 8, late ? 15 : 55);
        LocalDateTime checkOut = day.atTime(18, 10);
        AttendanceVO a = new AttendanceVO();
        a.setUserId(userId);
        a.setWorkDt(day);
        a.setCheckIn(checkIn);
        a.setStatusCd(late ? "LATE" : "NORMAL");
        attendanceMapper.insert(a);
        AttendanceVO saved = attendanceMapper.findByUserAndDate(userId, day);
        int totalMin = (int) java.time.temporal.ChronoUnit.MINUTES.between(checkIn, checkOut);
        int workMin = Math.min(480, totalMin - 60);
        int otMin = Math.max(0, totalMin - 60 - 480);
        attendanceMapper.updateCheckOut(saved.getAttId(), checkOut, workMin, otMin, 0, late ? "LATE" : "NORMAL");
    }

    private void seedMessage(Long senderId, Long receiverId, String content) {
        MessageVO m = new MessageVO();
        m.setSenderId(senderId);
        m.setReceiverId(receiverId);
        m.setContent(content);
        messageMapper.insert(m);
    }

    private void seedRoomReservation(Long roomId, Long userId,
                                     LocalDateTime start, LocalDateTime end, String purpose) {
        RoomReservationVO r = new RoomReservationVO();
        r.setRoomId(roomId);
        r.setUserId(userId);
        r.setStartDt(start);
        r.setEndDt(end);
        r.setPurpose(purpose);
        roomMapper.insertReservation(r);
    }

    private static List<Long> seq(long... ids) {
        return java.util.Arrays.stream(ids).boxed().toList();
    }
}
