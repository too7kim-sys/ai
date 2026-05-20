package egovframework.groupware.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginLogMapper {

    int insert(@Param("userId") Long userId,
               @Param("emailInput") String emailInput,
               @Param("ip") String ip,
               @Param("userAgent") String userAgent,
               @Param("successYn") String successYn,
               @Param("failReason") String failReason);
}
