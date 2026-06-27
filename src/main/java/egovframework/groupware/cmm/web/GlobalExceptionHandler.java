package egovframework.groupware.cmm.web;

import egovframework.groupware.cmm.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 업무 규칙 위반(ApiException) 처리.
     * <ul>
     *   <li>AJAX 요청 — JSON 으로 code/message 반환 (기존 동작).</li>
     *   <li>일반 폼 요청 — 직전 화면으로 돌아가 오류를 토스트로 표시 (raw JSON 노출 방지).</li>
     * </ul>
     */
    @ExceptionHandler(ApiException.class)
    public Object handleApi(ApiException ex, HttpServletRequest request) {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("code", ex.getCode(), "message", ex.getMessage()));
        }
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap != null) {
            flashMap.put("flashMsg", ex.getMessage());
            flashMap.put("flashType", "danger");
        }
        return new ModelAndView("redirect:" + safeReferer(request));
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) throws Exception {
        // 접근 거부는 Spring Security 의 access-denied-handler(403)가 처리하도록 그대로 전파.
        if (ex instanceof AccessDeniedException) throw ex;
        // 상세 예외는 서버 로그에만 기록하고, 사용자에게는 노출하지 않는다
        // (SQL 단편·내부 경로·스택 정보 노출 및 반사형 XSS 방지).
        log.error("Unhandled exception", ex);
        ModelAndView mv = new ModelAndView("error/500");
        mv.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mv;
    }

    /**
     * Referer 가 같은 호스트일 때만 그 경로로 복귀 — 오픈 리다이렉트 방지.
     * 반환 경로는 컨텍스트 경로를 제외한다 ({@code redirect:} 뷰가 다시 prepend 하므로).
     */
    private String safeReferer(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                URI uri = URI.create(referer);
                if (request.getServerName().equals(uri.getHost()) && uri.getPath() != null) {
                    String path = uri.getPath();
                    String ctx = request.getContextPath();
                    if (ctx != null && !ctx.isEmpty() && path.startsWith(ctx)) {
                        path = path.substring(ctx.length());
                    }
                    if (path.isEmpty()) path = "/dashboard.do";
                    return path + (uri.getQuery() != null ? "?" + uri.getQuery() : "");
                }
            } catch (Exception ignore) {
                // 잘못된 Referer — 기본 경로로
            }
        }
        return "/dashboard.do";
    }
}
