package egovframework.groupware.cmm.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import egovframework.groupware.cmm.json.JacksonObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 저장형/반사형 XSS 방어 — 모든 요청 파라미터 값을 HTML 이스케이프한다.
 *
 * <p>게시판·공지·쪽지·결재 등에서 입력한 본문에 {@code <script>} 등이 섞여도
 * 입력 시점에 {@code &lt;script&gt;}로 치환되어 저장되므로, 출력 화면이
 * 그대로 EL 로 렌더링해도 스크립트가 실행되지 않는다.
 *
 * <p>비밀번호 파라미터는 원본 그대로 인증에 사용되어야 하므로 제외한다.
 *
 * <p>{@code application/json} 요청 바디도 동일하게 보호한다 — 향후 REST API
 * 도입 시 JSON 으로 전송된 본문 텍스트가 그대로 화면에 출력되어 XSS 가 되는
 * 것을 차단. JSON 파싱이 실패하면 원본을 통과시켜 컨트롤러가 거부하게 둔다.
 */
public class XssFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(XssFilter.class);
    private static final ObjectMapper JSON = JacksonObjectMapperFactory.create();

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
        HttpServletRequest http = (HttpServletRequest) request;
        if (isJsonBody(http)) {
            byte[] cleaned = cleanJsonBody(readAll(http.getInputStream()));
            HttpServletRequest withBody = (cleaned == null) ? http : new BodyOverrideWrapper(http, cleaned);
            chain.doFilter(new XssRequestWrapper(withBody), response);
        } else {
            chain.doFilter(new XssRequestWrapper(http), response);
        }
    }

    private static boolean isJsonBody(HttpServletRequest req) {
        String ct = req.getContentType();
        return ct != null && ct.toLowerCase(Locale.ROOT).startsWith("application/json");
    }

    private static byte[] readAll(InputStream in) throws IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
        return out.toByteArray();
    }

    /** JSON 문자열 값에 HTML escape 적용. 파싱 실패 시 원본 반환(컨트롤러가 거부). */
    private static byte[] cleanJsonBody(byte[] raw) {
        if (raw == null || raw.length == 0) return raw;
        try {
            JsonNode root = JSON.readTree(raw);
            JsonNode cleaned = cleanNode(root, JsonNodeFactory.instance);
            return JSON.writeValueAsBytes(cleaned);
        } catch (Exception ex) {
            log.debug("XssFilter: JSON 파싱 실패 — 원본 통과 ({})", ex.getMessage());
            return raw;
        }
    }

    private static JsonNode cleanNode(JsonNode n, JsonNodeFactory f) {
        if (n == null || n.isNull()) return n;
        if (n.isObject()) {
            ObjectNode out = f.objectNode();
            Iterator<Map.Entry<String, JsonNode>> it = n.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> e = it.next();
                String key = e.getKey();
                if (skip(key)) {
                    out.set(key, e.getValue());
                } else {
                    out.set(key, cleanNode(e.getValue(), f));
                }
            }
            return out;
        }
        if (n.isArray()) {
            ArrayNode out = f.arrayNode();
            for (JsonNode child : n) out.add(cleanNode(child, f));
            return out;
        }
        if (n.isTextual()) {
            return f.textNode(HtmlUtils.htmlEscape(n.asText()));
        }
        return n;
    }

    private static boolean skip(String name) {
        return name != null && name.toLowerCase().contains("password");
    }

    private static String clean(String value) {
        return value == null ? null : HtmlUtils.htmlEscape(value);
    }

    /** 파싱·정제된 JSON 바이트를 새 InputStream 으로 제공해 컨트롤러가 그 본문을 보게 한다. */
    static final class BodyOverrideWrapper extends HttpServletRequestWrapper {
        private final byte[] body;
        BodyOverrideWrapper(HttpServletRequest req, byte[] body) {
            super(req);
            this.body = body;
        }
        @Override public int getContentLength() { return body.length; }
        @Override public long getContentLengthLong() { return body.length; }
        @Override public ServletInputStream getInputStream() {
            ByteArrayInputStream bin = new ByteArrayInputStream(body);
            return new ServletInputStream() {
                @Override public int read() { return bin.read(); }
                @Override public boolean isFinished() { return bin.available() == 0; }
                @Override public boolean isReady() { return true; }
                @Override public void setReadListener(ReadListener l) { /* sync mode */ }
            };
        }
        @Override public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
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
