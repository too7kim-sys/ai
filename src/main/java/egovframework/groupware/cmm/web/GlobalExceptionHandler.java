package egovframework.groupware.cmm.web;

import egovframework.groupware.cmm.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApi(ApiException ex) {
        return ResponseEntity.badRequest().body(Map.of("code", ex.getCode(), "message", ex.getMessage()));
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
}
