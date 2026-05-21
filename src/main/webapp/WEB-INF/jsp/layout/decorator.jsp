<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title><sitemesh:write property='title'/> | 사내 그룹웨어</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css"/>
    <sitemesh:write property='head'/>
</head>
<body>
<sec:authorize access="isAuthenticated()">
<nav class="navbar navbar-dark bg-dark px-3">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard.do">
        <i class="bi bi-grid-3x3-gap-fill"></i> 사내 그룹웨어
    </a>
    <div class="d-flex align-items-center text-white-50">
        <a class="btn btn-sm btn-outline-light me-2 position-relative"
           href="${pageContext.request.contextPath}/notification/list.do" title="알림">
            <i class="bi bi-bell"></i>
            <c:if test="${unreadNotiCount > 0}">
                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">${unreadNotiCount}</span>
            </c:if>
        </a>
        <a class="btn btn-sm btn-outline-light me-3 position-relative"
           href="${pageContext.request.contextPath}/message/inbox.do" title="쪽지">
            <i class="bi bi-chat-dots"></i>
            <c:if test="${unreadMsgCount > 0}">
                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-warning text-dark">${unreadMsgCount}</span>
            </c:if>
        </a>
        <span class="me-3">
            <i class="bi bi-person-circle"></i>
            <sec:authentication property="principal.user.name"/>
            (<sec:authentication property="principal.user.roleNm"/>)
        </span>
        <form action="${pageContext.request.contextPath}/auth/logout" method="post" class="d-inline">
            <sec:csrfInput/>
            <button type="submit" class="btn btn-sm btn-outline-light">
                <i class="bi bi-box-arrow-right"></i> 로그아웃
            </button>
        </form>
    </div>
</nav>
<div class="d-flex">
    <aside class="sidebar bg-light border-end">
        <ul class="nav flex-column p-2">
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dashboard.do"><i class="bi bi-speedometer2"></i> 대시보드</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/user/list.do"><i class="bi bi-people"></i> 직원 디렉토리</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/notice/list.do"><i class="bi bi-megaphone"></i> 공지사항</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/calendar/main.do"><i class="bi bi-calendar3"></i> 일정</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/approval/pending.do"><i class="bi bi-file-earmark-check"></i> 전자결재</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/leave/my.do"><i class="bi bi-airplane"></i> 휴가</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/attendance/my.do"><i class="bi bi-clock"></i> 근태</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/payroll/my.do"><i class="bi bi-cash"></i> 급여</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/contract/my.do"><i class="bi bi-file-text"></i> 근로계약</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/hr/org.do"><i class="bi bi-diagram-3"></i> 조직도</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/hr/family.do"><i class="bi bi-people-fill"></i> 부양가족</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/evaluation/sheet.do"><i class="bi bi-graph-up"></i> 평가</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/performance/my.do"><i class="bi bi-bullseye"></i> KPI</a></li>
            <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER','MANAGER')">
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/performance/team.do"><i class="bi bi-people"></i> 팀 KPI</a></li>
            </sec:authorize>
            <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
            <li class="nav-item"><a class="nav-link text-success" href="${pageContext.request.contextPath}/hr/record.do"><i class="bi bi-journal-text"></i> 인사기록(HR)</a></li>
            <li class="nav-item"><a class="nav-link text-success" href="${pageContext.request.contextPath}/hr/history.do"><i class="bi bi-clock-history"></i> 인사발령(HR)</a></li>
            <li class="nav-item"><a class="nav-link text-success" href="${pageContext.request.contextPath}/evaluation/admin/periods.do"><i class="bi bi-calendar-event"></i> 평가관리(HR)</a></li>
            </sec:authorize>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/room/list.do"><i class="bi bi-door-open"></i> 회의실</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/message/inbox.do"><i class="bi bi-chat-dots"></i> 쪽지</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/board/list.do?boardCd=FREE"><i class="bi bi-chat-square-text"></i> 게시판</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/doc/list.do"><i class="bi bi-folder2-open"></i> 자료실</a></li>
            <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
            <li class="nav-item"><a class="nav-link text-success" href="${pageContext.request.contextPath}/mail/log.do"><i class="bi bi-envelope"></i> 메일 발송 이력(HR)</a></li>
            </sec:authorize>
            <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER')">
            <hr class="my-2"/>
            <li class="nav-item"><a class="nav-link text-primary" href="${pageContext.request.contextPath}/vendor/list.do"><i class="bi bi-building"></i> 거래처</a></li>
            <li class="nav-item"><a class="nav-link text-primary" href="${pageContext.request.contextPath}/biz-contract/list.do"><i class="bi bi-briefcase"></i> 사업자 계약</a></li>
            <li class="nav-item"><a class="nav-link text-primary" href="${pageContext.request.contextPath}/invoice/out.do"><i class="bi bi-receipt"></i> 인보이스</a></li>
            <li class="nav-item"><a class="nav-link text-primary" href="${pageContext.request.contextPath}/payment/list.do"><i class="bi bi-bank"></i> 입출금</a></li>
            <li class="nav-item"><a class="nav-link text-primary" href="${pageContext.request.contextPath}/finance/ar.do"><i class="bi bi-bar-chart"></i> 재무 대시보드</a></li>
            </sec:authorize>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/expense/my.do"><i class="bi bi-credit-card"></i> 지출결의</a></li>
            <sec:authorize access="hasRole('ADMIN')">
            <hr class="my-2"/>
            <li class="nav-item"><a class="nav-link text-danger" href="${pageContext.request.contextPath}/sys/user/list.do"><i class="bi bi-gear"></i> 시스템 관리</a></li>
            </sec:authorize>
        </ul>
    </aside>
    <main class="content flex-grow-1 p-4">
        <sitemesh:write property='body'/>
    </main>
</div>
</sec:authorize>
<sec:authorize access="!isAuthenticated()">
    <sitemesh:write property='body'/>
</sec:authorize>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
