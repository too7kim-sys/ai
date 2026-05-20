package egovframework.groupware.cmm.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HealthController {

    private final DataSource dataSource;

    @Autowired
    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/health.do")
    @ResponseBody
    public Map<String, Object> health() {
        Map<String, Object> res = new HashMap<>();
        res.put("time", Instant.now().toString());
        try {
            Integer one = new JdbcTemplate(dataSource).queryForObject("select 1", Integer.class);
            res.put("db", one != null && one == 1 ? "UP" : "DOWN");
        } catch (Exception ex) {
            res.put("db", "DOWN");
            res.put("error", ex.getMessage());
        }
        res.put("status", "UP");
        return res;
    }
}
