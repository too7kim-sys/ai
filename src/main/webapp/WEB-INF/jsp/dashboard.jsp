<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<title>대시보드</title>
<div class="container-fluid">
    <h2 class="mb-4"><i class="bi bi-speedometer2"></i> 대시보드</h2>
    <p class="lead">안녕하세요, <strong>${me.name}</strong>님. 오늘도 좋은 하루 되세요.</p>

    <div class="row g-3">
        <div class="col-md-3">
            <a href="${pageContext.request.contextPath}/approval/pending.do" class="text-decoration-none">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <div class="text-muted small">결재 대기</div>
                    <div class="display-6 text-primary"><i class="bi bi-file-earmark-check"></i></div>
                    <div class="small">바로가기 →</div>
                </div>
            </div></a>
        </div>
        <div class="col-md-3">
            <a href="${pageContext.request.contextPath}/message/inbox.do" class="text-decoration-none">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <div class="text-muted small">안 읽은 쪽지</div>
                    <div class="display-6 text-success">${unreadMsgCount}</div>
                    <div class="small">바로가기 →</div>
                </div>
            </div></a>
        </div>
        <div class="col-md-3">
            <a href="${pageContext.request.contextPath}/leave/balance.do" class="text-decoration-none">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <div class="text-muted small">내 연차 잔여</div>
                    <div class="display-6 text-info">${leaveBalance.remaining()}일</div>
                    <div class="small">바로가기 →</div>
                </div>
            </div></a>
        </div>
        <div class="col-md-3">
            <a href="${pageContext.request.contextPath}/calendar/main.do" class="text-decoration-none">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <div class="text-muted small">오늘 일정</div>
                    <div class="display-6 text-warning">${todayEventCount}건</div>
                    <div class="small">바로가기 →</div>
                </div>
            </div></a>
        </div>
    </div>

    <div class="row g-3 mt-3">
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-header d-flex justify-content-between">
                    <span><i class="bi bi-megaphone"></i> 최근 공지</span>
                    <a href="${pageContext.request.contextPath}/notice/list.do" class="small">전체보기</a>
                </div>
                <ul class="list-group list-group-flush">
                    <c:forEach var="n" items="${notices}">
                        <li class="list-group-item d-flex justify-content-between">
                            <a class="text-decoration-none" href="${pageContext.request.contextPath}/notice/detail.do?noticeId=${n.noticeId}">
                                <c:if test="${n.pinnedYn eq 'Y'}"><span class="badge bg-danger me-1">📌</span></c:if>
                                ${n.title}
                            </a>
                            <small class="text-muted">${fn:substring(n.createdAt, 0, 10)}</small>
                        </li>
                    </c:forEach>
                    <c:if test="${empty notices}">
                        <li class="list-group-item text-center text-muted">공지가 없습니다.</li>
                    </c:if>
                </ul>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-header d-flex justify-content-between">
                    <span><i class="bi bi-bell"></i> 최근 알림</span>
                    <a href="${pageContext.request.contextPath}/notification/list.do" class="small">전체보기</a>
                </div>
                <ul class="list-group list-group-flush">
                    <c:forEach var="n" items="${recentNotifications}">
                        <li class="list-group-item">
                            <span class="badge bg-secondary me-1">${n.typeCd}</span>
                            <c:choose>
                                <c:when test="${not empty n.linkUrl}">
                                    <a class="text-decoration-none" href="${pageContext.request.contextPath}${n.linkUrl}">${n.title}</a>
                                </c:when>
                                <c:otherwise>${n.title}</c:otherwise>
                            </c:choose>
                            <small class="text-muted d-block">${n.createdAt}</small>
                        </li>
                    </c:forEach>
                    <c:if test="${empty recentNotifications}">
                        <li class="list-group-item text-center text-muted">새 알림이 없습니다.</li>
                    </c:if>
                </ul>
            </div>
        </div>
    </div>
</div>
