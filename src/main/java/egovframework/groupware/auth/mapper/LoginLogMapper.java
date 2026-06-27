package egovframework.groupware.auth.mapper;

import egovframework.groupware.auth.service.LoginLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LoginLogMapper {

    int insert(@Param("userId") Long userId,
               @Param("emailInput") String emailInput,
               @Param("ip") String ip,
               @Param("userAgent") String userAgent,
               @Param("successYn") String successYn,
               @Param("failReason") String failReason);

    List<LoginLogVO> search(@Param("emailKeyword") String emailKeyword,
                            @Param("successYn") String successYn,
                            @Param("from") LocalDateTime from,
                            @Param("to") LocalDateTime to,
                            @Param("offset") int offset,
                            @Param("limit") int limit);

    long count(@Param("emailKeyword") String emailKeyword,
               @Param("successYn") String successYn,
               @Param("from") LocalDateTime from,
               @Param("to") LocalDateTime to);
}
