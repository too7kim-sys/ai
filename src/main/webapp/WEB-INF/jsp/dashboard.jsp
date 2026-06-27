<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>대시보드</title>

<%-- 시간대별 인사말 + 빠른 검색 안내 --%>
<div class="d-flex justify-content-between align-items-end flex-wrap mb-3">
    <div>
        <h4 class="mb-1" id="greetingLine">안녕하세요, <strong>${me.name}</strong>님</h4>
        <div class="text-muted small">
            <i class="bi bi-calendar3 me-1"></i><span id="todayLabel"></span>
            <span class="ms-3 d-none d-md-inline">
                <kbd>/</kbd> <span class="text-muted">로 직원 검색</span>
                <span class="mx-2">·</span>
                <kbd>?</kbd> <span class="text-muted">로 단축키 도움말</span>
            </span>
        </div>
    </div>
</div>

<%-- ───── 빠른 액션 ───── --%>
<div class="card mb-3 quick-actions">
    <div class="card-body py-3">
        <div class="row g-2 text-center">
            <div class="col-6 col-md">
                <a class="quick-action" href="${pageContext.request.contextPath}/leave/write.do">
                    <span class="qa-icon bg-info-subtle text-info"><i class="bi bi-airplane"></i></span>
                    <div class="qa-label">휴가 신청</div>
                </a>
            </div>
            <div class="col-6 col-md">
                <a class="quick-action" href="${pageContext.request.contextPath}/approval/write.do">
                    <span class="qa-icon bg-primary-subtle text-primary"><i class="bi bi-file-earmark-text"></i></span>
                    <div class="qa-label">기안 작성</div>
                </a>
            </div>
            <div class="col-6 col-md">
                <a class="quick-action" href="${pageContext.request.contextPath}/message/write.do">
                    <span class="qa-icon bg-success-subtle text-success"><i class="bi bi-chat-dots"></i></span>
                    <div class="qa-label">쪽지 쓰기</div>
                </a>
            </div>
            <div class="col-6 col-md">
                <a class="quick-action" href="${pageContext.request.contextPath}/calendar/main.do">
                    <span class="qa-icon bg-warning-subtle text-warning"><i class="bi bi-calendar-plus"></i></span>
                    <div class="qa-label">일정 추가</div>
                </a>
            </div>
            <div class="col-6 col-md">
                <a class="quick-action" href="${pageContext.request.contextPath}/attendance/my.do">
                    <span class="qa-icon bg-danger-subtle text-danger"><i class="bi bi-clock"></i></span>
                    <div class="qa-label">출퇴근</div>
                </a>
            </div>
            <div class="col-6 col-md">
                <a class="quick-action" href="${pageContext.request.contextPath}/expense/write.do">
                    <span class="qa-icon bg-secondary-subtle text-secondary"><i class="bi bi-credit-card"></i></span>
                    <div class="qa-label">지출결의</div>
                </a>
            </div>
        </div>
    </div>
</div>

<%-- ───── 통계 위젯 4개 ───── --%>
<div class="row g-3">
    <div class="col-6 col-xl-3">
        <a href="${pageContext.request.contextPath}/approval/pending.do" class="text-decoration-none">
        <div class="card h-100 stat-card"><div class="card-body d-flex align-items-center justify-content-between">
            <div>
                <div class="widget-label">결재 대기</div>
                <div class="widget-value">바로가기</div>
                <div class="widget-trend text-muted">받은 결재함</div>
            </div>
            <span class="widget-icon bg-primary-subtle text-primary"><i class="bi bi-file-earmark-check"></i></span>
        </div></div></a>
    </div>
    <div class="col-6 col-xl-3">
        <a href="${pageContext.request.contextPath}/message/inbox.do" class="text-decoration-none">
        <div class="card h-100 stat-card"><div class="card-body d-flex align-items-center justify-content-between">
            <div>
                <div class="widget-label">안 읽은 쪽지</div>
                <div class="widget-value">${unreadMsgCount}<small class="text-muted fs-6 ms-1">건</small></div>
                <div class="widget-trend text-muted">받은 쪽지함</div>
            </div>
            <span class="widget-icon bg-success-subtle text-success"><i class="bi bi-chat-dots"></i></span>
        </div></div></a>
    </div>
    <div class="col-6 col-xl-3">
        <a href="${pageContext.request.contextPath}/leave/balance.do" class="text-decoration-none">
        <div class="card h-100 stat-card"><div class="card-body d-flex align-items-center justify-content-between">
            <div>
                <div class="widget-label">내 연차 잔여</div>
                <div class="widget-value">${leaveBalance.remaining()}<small class="text-muted fs-6 ms-1">일</small></div>
                <div class="widget-trend text-muted">사용 ${leaveBalance.annualUsed} / 부여 ${leaveBalance.annualGiven}</div>
            </div>
            <span class="widget-icon bg-info-subtle text-info"><i class="bi bi-airplane"></i></span>
        </div></div></a>
    </div>
    <div class="col-6 col-xl-3">
        <a href="${pageContext.request.contextPath}/calendar/main.do" class="text-decoration-none">
        <div class="card h-100 stat-card"><div class="card-body d-flex align-items-center justify-content-between">
            <div>
                <div class="widget-label">오늘 일정</div>
                <div class="widget-value">${todayEventCount}<small class="text-muted fs-6 ms-1">건</small></div>
                <div class="widget-trend text-muted" id="todayWeekday"></div>
            </div>
            <span class="widget-icon bg-warning-subtle text-warning"><i class="bi bi-calendar3"></i></span>
        </div></div></a>
    </div>
</div>

<div class="row g-3">
    <div class="col-lg-6">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-megaphone text-primary"></i> 최근 공지</span>
                <a href="${pageContext.request.contextPath}/notice/list.do" class="small text-decoration-none">전체보기 →</a>
            </div>
            <ul class="list-group list-group-flush">
                <c:forEach var="n" items="${notices}">
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        <a class="text-decoration-none text-truncate me-2 text-body"
                           href="${pageContext.request.contextPath}/notice/detail.do?noticeId=${n.noticeId}">
                            <c:if test="${n.pinnedYn eq 'Y'}"><span class="badge text-bg-danger me-1">고정</span></c:if>
                            ${n.title}
                        </a>
                        <small class="text-muted flex-shrink-0">${fn:substring(n.createdAt, 0, 10)}</small>
                    </li>
                </c:forEach>
                <c:if test="${empty notices}">
                    <li class="list-group-item empty-state">
                        <i class="bi bi-megaphone empty-state-icon"></i>
                        <div class="empty-state-title">아직 등록된 공지가 없습니다</div>
                        <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER','MANAGER')">
                            <a class="btn btn-sm btn-outline-primary mt-2"
                               href="${pageContext.request.contextPath}/notice/write.do">
                                <i class="bi bi-plus"></i> 공지 작성
                            </a>
                        </sec:authorize>
                    </li>
                </c:if>
            </ul>
        </div>
    </div>
    <div class="col-lg-6">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-bell text-warning"></i> 최근 알림</span>
                <a href="${pageContext.request.contextPath}/notification/list.do" class="small text-decoration-none">전체보기 →</a>
            </div>
            <ul class="list-group list-group-flush">
                <c:forEach var="n" items="${recentNotifications}">
                    <li class="list-group-item">
                        <span class="badge text-bg-light me-1">${n.typeCd}</span>
                        <c:choose>
                            <c:when test="${not empty n.linkUrl}">
                                <a class="text-decoration-none text-body" href="${pageContext.request.contextPath}${n.linkUrl}">${n.title}</a>
                            </c:when>
                            <c:otherwise>${n.title}</c:otherwise>
                        </c:choose>
                        <small class="text-muted d-block mt-1">${n.createdAt}</small>
                    </li>
                </c:forEach>
                <c:if test="${empty recentNotifications}">
                    <li class="list-group-item empty-state">
                        <i class="bi bi-bell-slash empty-state-icon"></i>
                        <div class="empty-state-title">새 알림이 없습니다</div>
                        <div class="empty-state-desc small text-muted">결재·메일·휴가 등에서 알림이 도착하면 여기에 표시됩니다.</div>
                    </li>
                </c:if>
            </ul>
        </div>
    </div>
</div>

<script>
(function () {
    var h = new Date().getHours();
    var g = h < 6 ? '늦은 밤' : h < 11 ? '좋은 아침' : h < 14 ? '점심 시간' : h < 18 ? '좋은 오후' : h < 22 ? '편안한 저녁' : '늦은 밤';
    var greet = document.getElementById('greetingLine');
    if (greet) greet.innerHTML = g + ', <strong>${me.name}</strong>님';

    var weekday = ['일','월','화','수','목','금','토'];
    var d = new Date();
    var label = d.getFullYear() + '년 ' + (d.getMonth()+1) + '월 ' + d.getDate() + '일 (' + weekday[d.getDay()] + ')';
    var td = document.getElementById('todayLabel');     if (td) td.textContent = label;
    var tw = document.getElementById('todayWeekday');   if (tw) tw.textContent = weekday[d.getDay()] + '요일';
})();
</script>
