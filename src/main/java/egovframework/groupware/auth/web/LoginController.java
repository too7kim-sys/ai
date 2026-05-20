package egovframework.groupware.auth.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping({"/", "/login.do"})
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        @RequestParam(value = "expired", required = false) String expired,
                        Model model) {
        if (error != null) {
            model.addAttribute("errorMessage",
                "locked".equals(error)
                    ? "계정이 잠겼습니다. 관리자에게 문의하세요."
                    : "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        if (logout != null) model.addAttribute("infoMessage", "로그아웃되었습니다.");
        if (expired != null) model.addAttribute("errorMessage", "세션이 만료되었습니다. 다시 로그인해주세요.");
        return "login";
    }
}
