package egovframework.groupware.bootstrap;

import egovframework.groupware.evaluation.mapper.EvaluationMapper;
import egovframework.groupware.evaluation.service.EvalPeriodVO;
import egovframework.groupware.hr.mapper.HrMapper;
import egovframework.groupware.hr.service.FamilyVO;
import egovframework.groupware.hr.service.HrHistoryVO;
import egovframework.groupware.hr.service.HrRecordVO;
import egovframework.groupware.performance.mapper.PerfMapper;
import egovframework.groupware.performance.service.PerfVO;
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
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper,
                           HrMapper hrMapper,
                           EvaluationMapper evaluationMapper,
                           PerfMapper perfMapper,
                           PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.hrMapper = hrMapper;
        this.evaluationMapper = evaluationMapper;
        this.perfMapper = perfMapper;
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

    private static List<Long> seq(long... ids) {
        return java.util.Arrays.stream(ids).boxed().toList();
    }
}
