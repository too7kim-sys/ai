package egovframework.groupware.user.mapper;

import egovframework.groupware.user.service.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    UserVO findByEmail(@Param("email") String email);

    UserVO findById(@Param("userId") Long userId);

    List<UserVO> search(@Param("keyword") String keyword,
                        @Param("deptId") Long deptId,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

    long count(@Param("keyword") String keyword,
               @Param("deptId") Long deptId);

    int insert(UserVO vo);

    int update(UserVO vo);

    int updateLoginSuccess(@Param("userId") Long userId,
                           @Param("ip") String ip);

    int incrementFail(@Param("email") String email,
                      @Param("maxFail") int maxFail);

    int unlock(@Param("userId") Long userId);

    int updatePassword(@Param("userId") Long userId,
                       @Param("passwordHash") String passwordHash);
}
