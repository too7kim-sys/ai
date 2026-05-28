package egovframework.groupware.cmm.web;

import egovframework.groupware.user.service.UserService;
import egovframework.groupware.user.service.UserVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 공통 컴포넌트용 JSON API.
 *
 * <p>현재는 사원 선택 모달({@code _user-picker.jsp})이 사용한다.
 * 다른 공통 picker(부서/거래처 등) 가 추가되면 같은 컨트롤러에 모은다.
 */
@Controller
public class CmmController {

    private final UserService userService;

    public CmmController(UserService userService) { this.userService = userService; }

    /**
     * 사원 선택 모달용 검색 JSON.
     * 비밀번호 해시·잠금 등 민감 필드는 응답에서 제외하고 표시에 필요한 컬럼만 노출.
     */
    @GetMapping("/cmm/user-picker.json")
    @ResponseBody
    public List<Map<String, Object>> searchUsers(@RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) Long deptId,
                                                 @RequestParam(required = false, defaultValue = "0") int offset,
                                                 @RequestParam(required = false, defaultValue = "50") int limit) {
        if (limit <= 0 || limit > 200) limit = 50;
        if (offset < 0) offset = 0;
        List<UserVO> users = userService.search(keyword, deptId, offset, limit);
        List<Map<String, Object>> out = new ArrayList<>(users.size());
        for (UserVO u : users) {
            Map<String, Object> m = new HashMap<>();
            m.put("userId", u.getUserId());
            m.put("name", u.getName());
            m.put("email", u.getEmail());
            m.put("deptNm", u.getDeptNm());
            m.put("positionNm", u.getPositionNm());
            m.put("roleNm", u.getRoleNm());
            out.add(m);
        }
        return out;
    }
}
