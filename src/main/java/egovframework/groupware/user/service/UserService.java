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

    List<UserVO> listByDept(Long deptId);

    List<UserVO> listAll();

    void update(UserVO vo);
}
