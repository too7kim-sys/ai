package egovframework.groupware.user.web;

import egovframework.groupware.cmm.Paging;
import egovframework.groupware.hr.service.HrService;
import egovframework.groupware.user.service.UserService;
import egovframework.groupware.user.service.UserVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserDirectoryController {

    private final UserService userService;
    private final HrService hrService;

    public UserDirectoryController(UserService userService, HrService hrService) {
        this.userService = userService;
        this.hrService = hrService;
    }

    @GetMapping("/user/list.do")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long deptId,
                       @RequestParam(required = false, defaultValue = "1") int page,
                       Model model) {
        Paging paging = new Paging();
        paging.setPage(Math.max(1, page));
        paging.setSize(20);
        List<UserVO> users = userService.search(keyword, deptId,
                paging.getOffset(), paging.getSize());
        long total = userService.count(keyword, deptId);
        paging.setTotal(total);
        model.addAttribute("users", users);
        model.addAttribute("paging", paging);
        model.addAttribute("keyword", keyword);
        model.addAttribute("deptId", deptId);
        model.addAttribute("depts", hrService.findAllDepts());
        return "user/list";
    }

    @GetMapping("/user/profile.do")
    public String profile(@RequestParam Long userId, Model model) {
        UserVO user = userService.findById(userId);
        if (user == null) return "redirect:/user/list.do";
        model.addAttribute("user", user);
        model.addAttribute("histories", hrService.findHistoryByUser(userId));
        model.addAttribute("records", hrService.findRecordsByUser(userId));
        model.addAttribute("families", hrService.findFamilyByUser(userId));
        return "user/profile";
    }
}
