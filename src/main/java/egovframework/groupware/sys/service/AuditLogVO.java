package egovframework.groupware.sys.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuditLogVO {
    private Long auditId;
    private Long userId;
    private String userName;
    private String userEmail;
    /** USER_UNLOCK | USER_RESET_PWD | USER_TOGGLE | USER_ROLE | CODE_EDIT | MENU_EDIT | ... */
    private String actionType;
    private String entityName;
    private String entityId;
    private String beforeJson;
    private String afterJson;
    private String ip;
    private LocalDateTime actionAt;
}
