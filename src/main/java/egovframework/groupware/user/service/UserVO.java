package egovframework.groupware.user.service;

import egovframework.groupware.cmm.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserVO extends BaseVO {
    private Long userId;
    private String email;
    private String passwordHash;
    private String name;
    private String phone;
    private Long deptId;
    private String deptNm;
    private Long positionId;
    private String positionNm;
    private String roleCd;
    private String roleNm;
    private LocalDate hireDate;
    private LocalDate resignDate;
    private String resignReason;
    private String bankCd;
    private String bankAccount;
    private String useYn;
    private Integer pwdFailCnt;
    private String lockedYn;
    private LocalDateTime lockedAt;
    private LocalDateTime pwdChangedAt;
    private LocalDateTime pwdExpireAt;
    private LocalDateTime lastLoginAt;
    private String lastLoginIp;
}
