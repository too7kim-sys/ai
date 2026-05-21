package egovframework.groupware.message.mapper;

import egovframework.groupware.message.service.MessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {

    int insert(MessageVO vo);

    int markRead(@Param("msgId") Long msgId, @Param("receiverId") Long receiverId);

    int softDelete(@Param("msgId") Long msgId, @Param("userId") Long userId);

    MessageVO findById(@Param("msgId") Long msgId);

    List<MessageVO> inbox(@Param("userId") Long userId);

    List<MessageVO> sent(@Param("userId") Long userId);

    long countUnread(@Param("userId") Long userId);
}
