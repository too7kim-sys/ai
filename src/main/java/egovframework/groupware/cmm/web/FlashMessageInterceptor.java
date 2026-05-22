package egovframework.groupware.cmm.web;

import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * POST → redirect (PRG 패턴) 응답에 작업 결과 피드백을 자동으로 부여한다.
 *
 * <p>컨트롤러가 {@code flashMsg} 를 직접 지정하지 않은 경우에만 기본 메시지를
 * 채우므로, 개별 컨트롤러는 필요 시 더 구체적인 메시지를 줄 수 있다.
 * 화면단(decorator.jsp)이 {@code flashMsg}/{@code flashType} 를 읽어 토스트로 표시한다.
 */
public class FlashMessageInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        if (modelAndView == null) return;
        if (!"POST".equalsIgnoreCase(request.getMethod())) return;
        String view = modelAndView.getViewName();
        if (view == null || !view.startsWith("redirect:")) return;

        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap == null) return;
        flashMap.putIfAbsent("flashMsg", "처리되었습니다.");
        flashMap.putIfAbsent("flashType", "success");
    }
}
