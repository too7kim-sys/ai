package egovframework.groupware.notification.service;

import java.util.Collection;
import java.util.List;

public interface NotificationService {

    /** 단일 사용자에게 알림 발송. */
    Long notify(Long userId, String typeCd, String title, String body, String linkUrl);

    /** 여러 사용자에게 동일 알림 발송 (자기 자신은 자동 제외). */
    int notifyMany(Collection<Long> userIds, Long actorId, String typeCd,
                   String title, String body, String linkUrl);

    List<NotificationVO> findByUser(Long userId, boolean onlyUnread, int limit);

    long countUnread(Long userId);

    void markRead(Long notiId, Long userId);

    void markAllRead(Long userId);
}
