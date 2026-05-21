package egovframework.groupware.message.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageVO {
    private Long msgId;
    private Long senderId;
    private String senderName;
    private String senderDept;
    private Long receiverId;
    private String receiverName;
    private String content;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
