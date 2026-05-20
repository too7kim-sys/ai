package egovframework.groupware.auth.security;

import egovframework.groupware.auth.service.LoginLogService;
import egovframework.groupware.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired private UserService userService;
    @Autowired private LoginLogService loginLogService;

    public AuthSuccessHandler() {
        setDefaultTargetUrl("/dashboard.do");
        setAlwaysUseDefaultTargetUrl(false);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        if (authentication.getPrincipal() instanceof CustomUserDetails details) {
            String ip = resolveIp(request);
            userService.recordLoginSuccess(details.getUserId(), ip);
            loginLogService.writeSuccess(details.getUserId(), details.getUsername(), ip, request.getHeader("User-Agent"));
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private String resolveIp(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) return h.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}
