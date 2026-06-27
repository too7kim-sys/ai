package egovframework.groupware.sys.service;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {

    /** 짧은 텍스트 메시지를 after_json 에 저장. */
    void log(Long actorId, String actionType, String entityName, String entityId,
             String beforeJson, String afterJson, String ip);

    /** 단순 메시지 로그 (before/after JSON 없이). */
    default void info(Long actorId, String actionType, String entityName, String entityId, String message) {
        log(actorId, actionType, entityName, entityId, null,
                message == null ? null : "{\"msg\":\"" + message.replace("\"", "\\\"") + "\"}", null);
    }

    List<AuditLogVO> search(String actionType, String entityName, Long userId,
                            LocalDateTime from, LocalDateTime to, int offset, int limit);

    long count(String actionType, String entityName, Long userId,
               LocalDateTime from, LocalDateTime to);

    List<String> distinctActionTypes();
    List<String> distinctEntityNames();
}
