package egovframework.groupware.sys.web;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 시스템 정보 (ADMIN 전용).
 */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class SysInfoController {

    private final DataSource dataSource;

    public SysInfoController(DataSource dataSource) { this.dataSource = dataSource; }

    @GetMapping("/sys/info.do")
    public String info(Model model) {
        Map<String, String> sys = new LinkedHashMap<>();
        sys.put("Java 버전", System.getProperty("java.version"));
        sys.put("Java 벤더", System.getProperty("java.vendor"));
        sys.put("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        sys.put("프로세서", System.getProperty("os.arch") + " · " + Runtime.getRuntime().availableProcessors() + " core");
        sys.put("사용자 디렉토리", System.getProperty("user.dir"));
        sys.put("기본 시간대", java.util.TimeZone.getDefault().getID());
        sys.put("기본 로케일", java.util.Locale.getDefault().toString());

        Runtime rt = Runtime.getRuntime();
        long mb = 1024 * 1024;
        Map<String, String> mem = new LinkedHashMap<>();
        mem.put("Heap (최대)", (rt.maxMemory() / mb) + " MB");
        mem.put("Heap (할당)", (rt.totalMemory() / mb) + " MB");
        mem.put("Heap (사용 중)", ((rt.totalMemory() - rt.freeMemory()) / mb) + " MB");
        mem.put("Heap (여유)", (rt.freeMemory() / mb) + " MB");

        Map<String, String> db = new LinkedHashMap<>();
        try (var c = dataSource.getConnection()) {
            db.put("DB 제품", c.getMetaData().getDatabaseProductName());
            db.put("DB 버전", c.getMetaData().getDatabaseProductVersion());
            db.put("드라이버", c.getMetaData().getDriverName() + " " + c.getMetaData().getDriverVersion());
            db.put("URL", c.getMetaData().getURL());
            db.put("사용자", c.getMetaData().getUserName());
        } catch (Exception e) {
            db.put("오류", e.getMessage());
        }

        JdbcTemplate jt = new JdbcTemplate(dataSource);
        Map<String, Object> counts = new LinkedHashMap<>();
        counts.put("사용자 (활성)", jt.queryForObject("SELECT COUNT(*) FROM gw_user WHERE use_yn='Y'", Long.class));
        counts.put("부서", jt.queryForObject("SELECT COUNT(*) FROM gw_dept", Long.class));
        counts.put("공지", jt.queryForObject("SELECT COUNT(*) FROM gw_notice WHERE deleted_at IS NULL", Long.class));
        counts.put("결재 문서", jt.queryForObject("SELECT COUNT(*) FROM gw_approval_doc", Long.class));
        counts.put("로그인 이력", jt.queryForObject("SELECT COUNT(*) FROM gw_login_log", Long.class));
        counts.put("감사 로그", jt.queryForObject("SELECT COUNT(*) FROM gw_audit_log", Long.class));

        model.addAttribute("sys", sys);
        model.addAttribute("mem", mem);
        model.addAttribute("db", db);
        model.addAttribute("counts", counts);
        return "sys/info";
    }
}
