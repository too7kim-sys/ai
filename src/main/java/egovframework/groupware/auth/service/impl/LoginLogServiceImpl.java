package egovframework.groupware.auth.service.impl;

import egovframework.groupware.auth.mapper.LoginLogMapper;
import egovframework.groupware.auth.service.LoginLogService;
import org.springframework.stereotype.Service;

@Service
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogMapper mapper;

    public LoginLogServiceImpl(LoginLogMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void writeSuccess(Long userId, String email, String ip, String userAgent) {
        mapper.insert(userId, email, ip, trunc(userAgent, 500), "Y", null);
    }

    @Override
    public void writeFailure(String email, String ip, String userAgent, String reason) {
        mapper.insert(null, email, ip, trunc(userAgent, 500), "N", reason);
    }

    private String trunc(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}
