package egovframework.groupware.auth.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LoginLogVO {
    private Long logId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String emailInput;
    private LocalDateTime loginDt;
    private String ip;
    private String userAgent;
    private String successYn;
    private String failReason;
}
