package egovframework.groupware.auth.service;

public interface LoginLogService {

    void writeSuccess(Long userId, String email, String ip, String userAgent);

    void writeFailure(String email, String ip, String userAgent, String reason);
}
