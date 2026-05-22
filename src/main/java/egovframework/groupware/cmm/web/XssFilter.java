package egovframework.groupware.cmm.web;

import org.springframework.web.util.HtmlUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 저장형/반사형 XSS 방어 — 모든 요청 파라미터 값을 HTML 이스케이프한다.
 *
 * <p>게시판·공지·쪽지·결재 등에서 입력한 본문에 {@code <script>} 등이 섞여도
 * 입력 시점에 {@code &lt;script&gt;}로 치환되어 저장되므로, 출력 화면이
 * 그대로 EL 로 렌더링해도 스크립트가 실행되지 않는다.
 *
 * <p>비밀번호 파라미터는 원본 그대로 인증에 사용되어야 하므로 제외한다.
 */
public class XssFilter implements Filter {

    // Tomcat 7(Servlet 3.0)에서는 init/destroy 가 default 메서드가 아니므로 명시적으로 구현한다.
    @Override
    public void init(FilterConfig filterConfig) {
        // no-op
    }

    @Override
    public void destroy() {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
    }

    private static boolean skip(String name) {
        return name != null && name.toLowerCase().contains("password");
    }

    private static String clean(String value) {
        return value == null ? null : HtmlUtils.htmlEscape(value);
    }

    static final class XssRequestWrapper extends HttpServletRequestWrapper {

        XssRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return skip(name) ? value : clean(value);
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null || skip(name)) return values;
            String[] cleaned = new String[values.length];
            for (int i = 0; i < values.length; i++) cleaned[i] = clean(values[i]);
            return cleaned;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> original = super.getParameterMap();
            Map<String, String[]> result = new LinkedHashMap<>();
            for (Map.Entry<String, String[]> entry : original.entrySet()) {
                String name = entry.getKey();
                String[] values = entry.getValue();
                if (skip(name)) {
                    result.put(name, values);
                    continue;
                }
                String[] cleaned = new String[values.length];
                for (int i = 0; i < values.length; i++) cleaned[i] = clean(values[i]);
                result.put(name, cleaned);
            }
            return result;
        }
    }
}
