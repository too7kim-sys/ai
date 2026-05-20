package egovframework.groupware.cmm.web;

import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * 요청별 trace ID와 IP, 사용자 ID(가능한 경우)를 MDC에 채워 Log4j2 패턴에 노출시킨다.
 */
public class MdcFilter implements Filter {

    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_USER_ID  = "userId";
    public static final String MDC_IP       = "ip";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            MDC.put(MDC_TRACE_ID, UUID.randomUUID().toString().substring(0, 8));
            if (request instanceof HttpServletRequest http) {
                MDC.put(MDC_IP, resolveIp(http));
            }
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private String resolveIp(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) return h.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}
