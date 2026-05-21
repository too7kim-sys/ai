package egovframework.groupware.message.service;

import java.util.List;

public interface MessageService {

    Long send(Long senderId, Long receiverId, String content);

    MessageVO read(Long msgId, Long userId);

    void delete(Long msgId, Long userId);

    List<MessageVO> inbox(Long userId);

    List<MessageVO> sent(Long userId);

    long countUnread(Long userId);
}
