package egovframework.groupware.cmm.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard.do")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        if (me != null) {
            model.addAttribute("me", me.getUser());
        }
        return "dashboard";
    }
}
