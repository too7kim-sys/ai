<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<title>대시보드</title>

<p class="text-muted mb-3">안녕하세요, <strong>${me.name}</strong>님. 오늘도 좋은 하루 되세요.</p>

<!-- 통계 위젯 -->
<div class="row g-3">
    <div class="col-6 col-xl-3">
        <a href="${pageContext.request.contextPath}/approval/pending.do" class="text-decoration-none">
        <div class="card h-100"><div class="card-body d-flex align-items-center justify-content-between">
            <div>
                <div class="widget-label">결재 대기</div>
                <div class="widget-value">바로가기</div>
            </div>
            <span class="widget-icon" style="background:rgba(114,124,245,.18);color:#727cf5;">
                <i class="bi bi-file-earmark-check"></i>
            </span>
        </div></div></a>
    </div>
    <div class="col-6 col-xl-3">
        <a href="${pageContext.request.contextPath}/message/inbox.do" class="text-decoration-none">
        <div class="card h-100"><div class="card-body d-flex align-items-center justify-content-between">
            <div>
                <div class="widget-label">안 읽은 쪽지</div>
                <div class="widget-value">${unreadMsgCount}</div>
            </div>
            <span class="widget-icon" style="background:rgba(10,207,151,.18);color:#0acf97;">
                <i class="bi bi-chat-dots"></i>
            </span>
        </div></div></a>
    </div>
    <div class="col-6 col-xl-3">
        <a href="${pageContext.request.contextPath}/leave/balance.do" class="text-decoration-none">
        <div class="card h-100"><div class="card-body d-flex align-items-center justify-content-between">
            <div>
                <div class="widget-label">내 연차 잔여</div>
                <div class="widget-value">${leaveBalance.remaining()}<small class="text-muted fs-6">일</small></div>
            </div>
            <span class="widget-icon" style="background:rgba(57,175,209,.18);color:#39afd1;">
                <i class="bi bi-airplane"></i>
            </span>
        </div></div></a>
    </div>
    <div class="col-6 col-xl-3">
        <a href="${pageContext.request.contextPath}/calendar/main.do" class="text-decoration-none">
        <div class="card h-100"><div class="card-body d-flex align-items-center justify-content-between">
            <div>
                <div class="widget-label">오늘 일정</div>
                <div class="widget-value">${todayEventCount}<small class="text-muted fs-6">건</small></div>
            </div>
            <span class="widget-icon" style="background:rgba(255,188,0,.18);color:#e0a600;">
                <i class="bi bi-calendar3"></i>
            </span>
        </div></div></a>
    </div>
</div>

<div class="row g-3">
    <div class="col-lg-6">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-megaphone text-primary"></i> 최근 공지</span>
                <a href="${pageContext.request.contextPath}/notice/list.do" class="small">전체보기 →</a>
            </div>
            <ul class="list-group list-group-flush">
                <c:forEach var="n" items="${notices}">
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        <a class="text-decoration-none text-truncate me-2"
                           href="${pageContext.request.contextPath}/notice/detail.do?noticeId=${n.noticeId}">
                            <c:if test="${n.pinnedYn eq 'Y'}"><span class="badge bg-soft-danger me-1">고정</span></c:if>
                            ${n.title}
                        </a>
                        <small class="text-muted flex-shrink-0">${fn:substring(n.createdAt, 0, 10)}</small>
                    </li>
                </c:forEach>
                <c:if test="${empty notices}">
                    <li class="list-group-item text-center text-muted py-4">공지가 없습니다.</li>
                </c:if>
            </ul>
        </div>
    </div>
    <div class="col-lg-6">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-bell text-warning"></i> 최근 알림</span>
                <a href="${pageContext.request.contextPath}/notification/list.do" class="small">전체보기 →</a>
            </div>
            <ul class="list-group list-group-flush">
                <c:forEach var="n" items="${recentNotifications}">
                    <li class="list-group-item">
                        <span class="badge bg-soft-primary me-1">${n.typeCd}</span>
                        <c:choose>
                            <c:when test="${not empty n.linkUrl}">
                                <a class="text-decoration-none" href="${pageContext.request.contextPath}${n.linkUrl}">${n.title}</a>
                            </c:when>
                            <c:otherwise>${n.title}</c:otherwise>
                        </c:choose>
                        <small class="text-muted d-block mt-1">${n.createdAt}</small>
                    </li>
                </c:forEach>
                <c:if test="${empty recentNotifications}">
                    <li class="list-group-item text-center text-muted py-4">새 알림이 없습니다.</li>
                </c:if>
            </ul>
        </div>
    </div>
</div>
