package egovframework.groupware.user.service;

import java.util.List;

public interface UserService {

    UserVO findByEmail(String email);

    UserVO findById(Long userId);

    List<UserVO> search(String keyword, Long deptId, int offset, int limit);

    long count(String keyword, Long deptId);

    Long createUser(UserVO vo, String rawPassword);

    void recordLoginSuccess(Long userId, String ip);

    void recordLoginFailure(String email);

    void unlock(Long userId);

    /** 비밀번호를 새 평문으로 초기화 (해시 적용). */
    void resetPassword(Long userId, String rawPassword);

    /** Y/N. N이면 로그인 불가. */
    void setUseYn(Long userId, String useYn);

    /** 역할 코드 변경 (예: EMPLOYEE → MANAGER). */
    void setRole(Long userId, String roleCd);

    List<UserVO> listByDept(Long deptId);

    List<UserVO> listAll();

    void update(UserVO vo);
}
