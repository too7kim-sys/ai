package egovframework.groupware.sys.mapper;

import egovframework.groupware.sys.service.AuditLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AuditLogMapper {

    int insert(AuditLogVO vo);

    List<AuditLogVO> search(@Param("actionType") String actionType,
                            @Param("entityName") String entityName,
                            @Param("userId") Long userId,
                            @Param("from") LocalDateTime from,
                            @Param("to") LocalDateTime to,
                            @Param("offset") int offset,
                            @Param("limit") int limit);

    long count(@Param("actionType") String actionType,
               @Param("entityName") String entityName,
               @Param("userId") Long userId,
               @Param("from") LocalDateTime from,
               @Param("to") LocalDateTime to);

    /** 구분 필터용 distinct 값 조회. */
    List<String> distinctActionTypes();

    List<String> distinctEntityNames();
}
