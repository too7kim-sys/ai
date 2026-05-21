package egovframework.groupware.notification.mapper;

import egovframework.groupware.notification.service.NotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    int insert(NotificationVO vo);

    List<NotificationVO> listByUser(@Param("userId") Long userId,
                                    @Param("onlyUnread") boolean onlyUnread,
                                    @Param("limit") int limit);

    long countUnread(@Param("userId") Long userId);

    int markRead(@Param("notiId") Long notiId, @Param("userId") Long userId);

    int markAllRead(@Param("userId") Long userId);
}
