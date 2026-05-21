package egovframework.groupware.cmm.web;

import egovframework.groupware.auth.security.CustomUserDetails;
import egovframework.groupware.message.service.MessageService;
import egovframework.groupware.notification.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 모든 뷰에서 안 읽은 알림/쪽지 카운트를 즉시 사용할 수 있도록 노출한다.
 * 비로그인/익명 사용자는 0을 반환한다.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    private final NotificationService notificationService;
    private final MessageService messageService;

    public GlobalModelAttributes(NotificationService notificationService, MessageService messageService) {
        this.notificationService = notificationService;
        this.messageService = messageService;
    }

    @ModelAttribute("unreadNotiCount")
    public long unreadNoti() {
        Long uid = currentUserId();
        return uid == null ? 0 : notificationService.countUnread(uid);
    }

    @ModelAttribute("unreadMsgCount")
    public long unreadMsg() {
        Long uid = currentUserId();
        return uid == null ? 0 : messageService.countUnread(uid);
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails details && details.getUser() != null) {
            return details.getUserId();
        }
        return null;
    }
}
