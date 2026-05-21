package egovframework.groupware.message.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.message.mapper.MessageMapper;
import egovframework.groupware.message.service.MessageService;
import egovframework.groupware.message.service.MessageVO;
import egovframework.groupware.notification.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageMapper mapper;
    private final NotificationService notificationService;

    public MessageServiceImpl(MessageMapper mapper, NotificationService notificationService) {
        this.mapper = mapper;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Long send(Long senderId, Long receiverId, String content) {
        if (receiverId == null) throw new ApiException("INVALID", "받는 사람을 선택하세요");
        if (content == null || content.isBlank()) throw new ApiException("INVALID", "내용을 입력하세요");
        if (senderId.equals(receiverId)) throw new ApiException("SELF_SEND", "자기 자신에게는 보낼 수 없습니다");
        MessageVO vo = new MessageVO();
        vo.setSenderId(senderId);
        vo.setReceiverId(receiverId);
        vo.setContent(content);
        mapper.insert(vo);

        // 받는 사람에게 인앱 알림
        notificationService.notify(receiverId, "MESSAGE",
                "새 쪽지가 도착했습니다",
                content.length() > 100 ? content.substring(0, 100) + "..." : content,
                "/message/detail.do?msgId=" + vo.getMsgId());
        return vo.getMsgId();
    }

    @Override
    @Transactional
    public MessageVO read(Long msgId, Long userId) {
        MessageVO m = mapper.findById(msgId);
        if (m == null) return null;
        if (m.getReceiverId().equals(userId)) {
            mapper.markRead(msgId, userId);
        } else if (!m.getSenderId().equals(userId)) {
            throw new ApiException("FORBIDDEN", "쪽지 열람 권한이 없습니다");
        }
        return mapper.findById(msgId);
    }

    @Override
    @Transactional
    public void delete(Long msgId, Long userId) { mapper.softDelete(msgId, userId); }

    @Override public List<MessageVO> inbox(Long userId) { return mapper.inbox(userId); }
    @Override public List<MessageVO> sent(Long userId) { return mapper.sent(userId); }
    @Override public long countUnread(Long userId) { return mapper.countUnread(userId); }
}
