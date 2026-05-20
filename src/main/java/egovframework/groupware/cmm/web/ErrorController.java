package egovframework.groupware.cmm.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping("/error/403.do")
    public String forbidden() { return "error/403"; }

    @RequestMapping("/error/404.do")
    public String notFound() { return "error/404"; }

    @RequestMapping("/error/500.do")
    public String serverError() { return "error/500"; }
}
