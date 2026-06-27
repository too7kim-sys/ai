package egovframework.groupware.notification.service.impl;

import egovframework.groupware.notification.mapper.NotificationMapper;
import egovframework.groupware.notification.service.NotificationService;
import egovframework.groupware.notification.service.NotificationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper mapper;

    public NotificationServiceImpl(NotificationMapper mapper) { this.mapper = mapper; }

    @Override
    @Transactional
    public Long notify(Long userId, String typeCd, String title, String body, String linkUrl) {
        if (userId == null) return null;
        NotificationVO vo = new NotificationVO();
        vo.setUserId(userId);
        vo.setTypeCd(typeCd == null ? "SYSTEM" : typeCd);
        vo.setTitle(title);
        vo.setBody(body);
        vo.setLinkUrl(linkUrl);
        mapper.insert(vo);
        return vo.getNotiId();
    }

    @Override
    @Transactional
    public int notifyMany(Collection<Long> userIds, Long actorId, String typeCd,
                          String title, String body, String linkUrl) {
        if (userIds == null || userIds.isEmpty()) return 0;
        int n = 0;
        for (Long uid : userIds) {
            if (uid == null) continue;
            if (Objects.equals(uid, actorId)) continue;
            notify(uid, typeCd, title, body, linkUrl);
            n++;
        }
        return n;
    }

    @Override
    public List<NotificationVO> findByUser(Long userId, boolean onlyUnread, int limit) {
        return mapper.listByUser(userId, onlyUnread, limit > 0 ? limit : 50);
    }

    @Override public long countUnread(Long userId) { return mapper.countUnread(userId); }

    @Override
    @Transactional
    public void markRead(Long notiId, Long userId) { mapper.markRead(notiId, userId); }

    @Override
    @Transactional
    public void markAllRead(Long userId) { mapper.markAllRead(userId); }
}
