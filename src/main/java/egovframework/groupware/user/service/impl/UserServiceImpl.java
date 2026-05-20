package egovframework.groupware.user.service.impl;

import egovframework.groupware.user.mapper.UserMapper;
import egovframework.groupware.user.service.UserService;
import egovframework.groupware.user.service.UserVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final int maxFail;
    private final int pwdExpireDays;

    public UserServiceImpl(UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           @Value("${policy.login.max-fail:5}") int maxFail,
                           @Value("${policy.password.expire-days:90}") int pwdExpireDays) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.maxFail = maxFail;
        this.pwdExpireDays = pwdExpireDays;
    }

    @Override
    public UserVO findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public UserVO findById(Long userId) {
        return userMapper.findById(userId);
    }

    @Override
    public List<UserVO> search(String keyword, Long deptId, int offset, int limit) {
        return userMapper.search(keyword, deptId, offset, limit);
    }

    @Override
    public long count(String keyword, Long deptId) {
        return userMapper.count(keyword, deptId);
    }

    @Override
    public Long createUser(UserVO vo, String rawPassword) {
        vo.setPasswordHash(passwordEncoder.encode(rawPassword));
        LocalDateTime now = LocalDateTime.now();
        vo.setPwdChangedAt(now);
        vo.setPwdExpireAt(now.plusDays(pwdExpireDays));
        userMapper.insert(vo);
        return vo.getUserId();
    }

    @Override
    public void recordLoginSuccess(Long userId, String ip) {
        userMapper.updateLoginSuccess(userId, ip);
    }

    @Override
    public void recordLoginFailure(String email) {
        userMapper.incrementFail(email, maxFail);
    }

    @Override
    public void unlock(Long userId) {
        userMapper.unlock(userId);
    }
}
