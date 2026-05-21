package egovframework.groupware.sys.service.impl;

import egovframework.groupware.sys.mapper.AuditLogMapper;
import egovframework.groupware.sys.service.AuditLogService;
import egovframework.groupware.sys.service.AuditLogVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogMapper mapper;

    public AuditLogServiceImpl(AuditLogMapper mapper) { this.mapper = mapper; }

    @Override
    @Transactional
    public void log(Long actorId, String actionType, String entityName, String entityId,
                    String beforeJson, String afterJson, String ip) {
        AuditLogVO vo = new AuditLogVO();
        vo.setUserId(actorId);
        vo.setActionType(actionType);
        vo.setEntityName(entityName);
        vo.setEntityId(entityId);
        vo.setBeforeJson(beforeJson);
        vo.setAfterJson(afterJson);
        vo.setIp(ip);
        mapper.insert(vo);
    }

    @Override
    public List<AuditLogVO> search(String actionType, String entityName, Long userId,
                                   LocalDateTime from, LocalDateTime to, int offset, int limit) {
        return mapper.search(actionType, entityName, userId, from, to, offset, limit);
    }

    @Override
    public long count(String actionType, String entityName, Long userId,
                      LocalDateTime from, LocalDateTime to) {
        return mapper.count(actionType, entityName, userId, from, to);
    }

    @Override public List<String> distinctActionTypes() { return mapper.distinctActionTypes(); }
    @Override public List<String> distinctEntityNames() { return mapper.distinctEntityNames(); }
}
