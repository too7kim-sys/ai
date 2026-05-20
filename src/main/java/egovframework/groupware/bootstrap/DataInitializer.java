package egovframework.groupware.bootstrap;

import egovframework.groupware.user.mapper.UserMapper;
import egovframework.groupware.user.service.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 첫 기동 시 시드 사용자 20명을 삽입한다.
 * 이미 admin@company.com이 존재하면 skip.
 *
 * 트랜잭션 시드(공지/일정/휴가/결재/근태/급여/거래처/계약/인보이스/지출결의 등)는
 * 단계적으로 추가될 예정이다.
 */
@Component
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String DEFAULT_PWD = "Demo!2025";

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
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
}
