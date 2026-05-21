package egovframework.groupware.notice.service.impl;

import egovframework.groupware.cmm.ApiException;
import egovframework.groupware.notice.mapper.NoticeMapper;
import egovframework.groupware.notice.service.NoticeCommentVO;
import egovframework.groupware.notice.service.NoticeService;
import egovframework.groupware.notice.service.NoticeVO;
import egovframework.groupware.notification.service.NotificationService;
import egovframework.groupware.user.mapper.UserMapper;
import egovframework.groupware.user.service.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper mapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    public NoticeServiceImpl(NoticeMapper mapper, UserMapper userMapper,
                             NotificationService notificationService) {
        this.mapper = mapper;
        this.userMapper = userMapper;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Long create(NoticeVO vo, Long authorId, String roleCd) {
        if (!canWrite(roleCd)) throw new ApiException("FORBIDDEN", "공지 작성 권한이 없습니다");
        if (vo.getTitle() == null || vo.getTitle().isBlank())
            throw new ApiException("INVALID", "제목을 입력하세요");
        vo.setAuthorId(authorId);
        mapper.insert(vo);

        // 전사 공지인 경우 모든 활성 사용자에게 알림 발송
        if ("ALL".equals(vo.getDeptScope()) || vo.getDeptScope() == null) {
            List<Long> userIds = new ArrayList<>();
            for (UserVO u : userMapper.listAll()) userIds.add(u.getUserId());
            notificationService.notifyMany(userIds, authorId, "NOTICE",
                    "[공지] " + vo.getTitle(), null,
                    "/notice/detail.do?noticeId=" + vo.getNoticeId());
        }
        return vo.getNoticeId();
    }

    @Override
    @Transactional
    public void update(NoticeVO vo, Long actorId, String roleCd) {
        NoticeVO existing = mapper.findById(vo.getNoticeId());
        if (existing == null) throw new ApiException("NOT_FOUND", "공지를 찾을 수 없습니다");
        if (!canEdit(existing, actorId, roleCd))
            throw new ApiException("FORBIDDEN", "수정 권한이 없습니다");
        vo.setAuthorId(actorId);
        mapper.update(vo);
    }

    @Override
    @Transactional
    public void delete(Long noticeId, Long actorId, String roleCd) {
        NoticeVO existing = mapper.findById(noticeId);
        if (existing == null) return;
        if (!canEdit(existing, actorId, roleCd))
            throw new ApiException("FORBIDDEN", "삭제 권한이 없습니다");
        mapper.softDelete(noticeId, actorId);
    }

    @Override
    @Transactional
    public NoticeVO findById(Long noticeId, boolean incrementView) {
        if (incrementView) mapper.incrementViewCnt(noticeId);
        return mapper.findById(noticeId);
    }

    @Override public List<NoticeVO> search(String keyword, int offset, int limit) {
        return mapper.list(keyword, offset, limit);
    }

    @Override public long count(String keyword) { return mapper.count(keyword); }

    @Override public List<NoticeVO> findLatest(int limit) { return mapper.listLatest(limit); }

    @Override
    @Transactional
    public Long addComment(Long noticeId, Long authorId, String content) {
        if (content == null || content.isBlank()) throw new ApiException("INVALID", "내용을 입력하세요");
        NoticeVO notice = mapper.findById(noticeId);
        if (notice == null) throw new ApiException("NOT_FOUND", "공지를 찾을 수 없습니다");
        NoticeCommentVO c = new NoticeCommentVO();
        c.setNoticeId(noticeId);
        c.setAuthorId(authorId);
        c.setContent(content);
        mapper.insertComment(c);

        // 작성자에게 댓글 알림
        if (!authorId.equals(notice.getAuthorId())) {
            notificationService.notify(notice.getAuthorId(), "NOTICE",
                    "공지 '" + notice.getTitle() + "'에 댓글이 달렸습니다",
                    content.length() > 80 ? content.substring(0, 80) + "..." : content,
                    "/notice/detail.do?noticeId=" + noticeId);
        }
        return c.getCmtId();
    }

    @Override
    @Transactional
    public void deleteComment(Long cmtId, Long authorId, String roleCd) {
        mapper.deleteComment(cmtId, authorId, roleCd);
    }

    @Override public List<NoticeCommentVO> findComments(Long noticeId) {
        return mapper.listComments(noticeId);
    }

    private boolean canWrite(String roleCd) {
        return "ADMIN".equals(roleCd) || "HR_MANAGER".equals(roleCd) || "MANAGER".equals(roleCd);
    }

    private boolean canEdit(NoticeVO notice, Long actorId, String roleCd) {
        if ("ADMIN".equals(roleCd) || "HR_MANAGER".equals(roleCd)) return true;
        return notice.getAuthorId().equals(actorId);
    }
}
