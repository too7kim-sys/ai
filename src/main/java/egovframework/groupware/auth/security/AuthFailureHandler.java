package egovframework.groupware.auth.security;

import egovframework.groupware.auth.service.LoginLogService;
import egovframework.groupware.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired private UserService userService;
    @Autowired private LoginLogService loginLogService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {
        String email = request.getParameter("email");
        String ip = resolveIp(request);
        if (email != null && !email.isBlank()) {
            userService.recordLoginFailure(email);
        }
        loginLogService.writeFailure(email, ip, request.getHeader("User-Agent"), exception.getClass().getSimpleName());
        String msg = exception instanceof LockedException ? "locked" : "bad-credentials";
        response.sendRedirect("/login.do?error=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
    }

    private String resolveIp(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) return h.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}
