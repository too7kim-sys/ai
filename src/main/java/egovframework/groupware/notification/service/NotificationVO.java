package egovframework.groupware.notification.service;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationVO {
    private Long notiId;
    private Long userId;
    /** NOTICE | APPROVAL | LEAVE | MESSAGE | ROOM | CALENDAR | EXPENSE | SYSTEM */
    private String typeCd;
    private String title;
    private String body;
    private String linkUrl;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
