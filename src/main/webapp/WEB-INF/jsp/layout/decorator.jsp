<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover"/>
    <meta name="theme-color" content="#313a46"/>
    <title><sitemesh:write property='title'/> | 사내 그룹웨어</title>
    <link rel="preconnect" href="https://fonts.googleapis.com"/>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;500;600;700&display=swap"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css"/>
    <sitemesh:write property='head'/>
</head>
<body>
<sec:authorize access="isAuthenticated()">
<div class="wrapper">

    <!-- ============ 좌측 사이드바 ============ -->
    <aside class="leftside-menu" id="sideMenu">
        <a class="side-logo" href="${pageContext.request.contextPath}/dashboard.do">
            <i class="bi bi-grid-3x3-gap-fill"></i> 사내 그룹웨어
        </a>
        <ul class="side-nav">
            <li class="menu-title">메인</li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/dashboard.do"><i class="bi bi-speedometer2"></i><span>대시보드</span></a></li>

            <li class="menu-title">인사 · 평가</li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/user/list.do"><i class="bi bi-people"></i><span>직원 디렉토리</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/hr/org.do"><i class="bi bi-diagram-3"></i><span>조직도</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/hr/family.do"><i class="bi bi-people-fill"></i><span>부양가족</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/contract/my.do"><i class="bi bi-file-text"></i><span>근로계약</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/evaluation/sheet.do"><i class="bi bi-graph-up"></i><span>평가</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/performance/my.do"><i class="bi bi-bullseye"></i><span>KPI</span></a></li>
            <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER','MANAGER')">
            <li><a class="nav-link" href="${pageContext.request.contextPath}/performance/team.do"><i class="bi bi-people"></i><span>팀 KPI</span></a></li>
            </sec:authorize>
            <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/hr/record.do"><i class="bi bi-journal-text"></i><span>인사기록 (HR)</span></a></li>
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/hr/history.do"><i class="bi bi-clock-history"></i><span>인사발령 (HR)</span></a></li>
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/evaluation/admin/periods.do"><i class="bi bi-calendar-event"></i><span>평가관리 (HR)</span></a></li>
            </sec:authorize>

            <li class="menu-title">근태 · 결재</li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/attendance/my.do"><i class="bi bi-clock"></i><span>근태</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/leave/my.do"><i class="bi bi-airplane"></i><span>휴가</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/approval/pending.do"><i class="bi bi-file-earmark-check"></i><span>전자결재</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/payroll/my.do"><i class="bi bi-cash"></i><span>급여</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/expense/my.do"><i class="bi bi-credit-card"></i><span>지출결의</span></a></li>

            <li class="menu-title">협업</li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/notice/list.do"><i class="bi bi-megaphone"></i><span>공지사항</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/calendar/main.do"><i class="bi bi-calendar3"></i><span>일정</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/board/list.do?boardCd=FREE"><i class="bi bi-chat-square-text"></i><span>게시판</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/doc/list.do"><i class="bi bi-folder2-open"></i><span>자료실</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/message/inbox.do"><i class="bi bi-chat-dots"></i><span>쪽지</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/room/list.do"><i class="bi bi-door-open"></i><span>회의실</span></a></li>

            <li class="menu-title">자산 · 차량</li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/asset/my.do"><i class="bi bi-laptop"></i><span>내 자산</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/vehicle/list.do"><i class="bi bi-car-front"></i><span>차량 예약</span></a></li>
            <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/asset/list.do"><i class="bi bi-list-check"></i><span>자산 관리 (HR)</span></a></li>
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/mail/log.do"><i class="bi bi-envelope"></i><span>메일 이력 (HR)</span></a></li>
            </sec:authorize>

            <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER','MANAGER')">
            <li class="menu-title">재무 · 회계</li>
            </sec:authorize>
            <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER')">
            <li><a class="nav-link text-primary" href="${pageContext.request.contextPath}/vendor/list.do"><i class="bi bi-building"></i><span>거래처</span></a></li>
            </sec:authorize>
            <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER','MANAGER')">
            <li><a class="nav-link text-primary" href="${pageContext.request.contextPath}/biz-contract/list.do"><i class="bi bi-briefcase"></i><span>사업자 계약</span></a></li>
            <li><a class="nav-link text-primary" href="${pageContext.request.contextPath}/invoice/out.do"><i class="bi bi-receipt"></i><span>인보이스</span></a></li>
            <li><a class="nav-link text-primary" href="${pageContext.request.contextPath}/payment/list.do"><i class="bi bi-bank"></i><span>입출금</span></a></li>
            <li><a class="nav-link text-primary" href="${pageContext.request.contextPath}/finance/ar.do"><i class="bi bi-bar-chart"></i><span>재무 대시보드</span></a></li>
            </sec:authorize>

            <sec:authorize access="hasRole('ADMIN')">
            <li class="menu-title">시스템 관리</li>
            <li><a class="nav-link text-danger" href="${pageContext.request.contextPath}/sys/user/list.do"><i class="bi bi-people"></i><span>사용자 관리</span></a></li>
            <li><a class="nav-link text-danger" href="${pageContext.request.contextPath}/sys/login-log.do"><i class="bi bi-shield-lock"></i><span>로그인 이력</span></a></li>
            <li><a class="nav-link text-danger" href="${pageContext.request.contextPath}/sys/audit-log.do"><i class="bi bi-journal-text"></i><span>감사 로그</span></a></li>
            <li><a class="nav-link text-danger" href="${pageContext.request.contextPath}/sys/code.do"><i class="bi bi-tags"></i><span>공통 코드</span></a></li>
            <li><a class="nav-link text-danger" href="${pageContext.request.contextPath}/sys/menu.do"><i class="bi bi-list-ul"></i><span>메뉴 관리</span></a></li>
            <li><a class="nav-link text-danger" href="${pageContext.request.contextPath}/sys/info.do"><i class="bi bi-info-circle"></i><span>시스템 정보</span></a></li>
            </sec:authorize>
        </ul>
    </aside>
    <div class="sidebar-backdrop" id="sidebarBackdrop"></div>

    <!-- ============ 컨텐츠 영역 ============ -->
    <div class="content-page">

        <!-- 토픽바 -->
        <header class="topbar">
            <button class="menu-toggle" type="button" id="menuToggle" aria-label="메뉴">
                <i class="bi bi-list"></i>
            </button>
            <div class="ms-auto d-flex align-items-center gap-1">
                <a class="topbar-icon" href="${pageContext.request.contextPath}/notification/list.do" title="알림">
                    <i class="bi bi-bell"></i>
                    <c:if test="${unreadNotiCount > 0}">
                        <span class="topbar-badge badge rounded-pill bg-danger">${unreadNotiCount}</span>
                    </c:if>
                </a>
                <a class="topbar-icon me-2" href="${pageContext.request.contextPath}/message/inbox.do" title="쪽지">
                    <i class="bi bi-chat-dots"></i>
                    <c:if test="${unreadMsgCount > 0}">
                        <span class="topbar-badge badge rounded-pill bg-warning text-dark">${unreadMsgCount}</span>
                    </c:if>
                </a>
                <div class="dropdown">
                    <button class="btn d-flex align-items-center gap-2 p-1" type="button" data-bs-toggle="dropdown">
                        <span class="avatar-circle">
                            <sec:authentication property="principal.user.name" scope="page" var="meName"/>${fn:substring(meName,0,1)}
                        </span>
                        <span class="text-start d-none d-sm-block">
                            <span class="profile-name d-block" style="line-height:1.1;">
                                <sec:authentication property="principal.user.name"/>
                            </span>
                            <span class="profile-role"><sec:authentication property="principal.user.roleNm"/></span>
                        </span>
                        <i class="bi bi-chevron-down text-muted small"></i>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end shadow">
                        <li class="dropdown-item-text small text-muted">
                            <sec:authentication property="principal.user.email"/>
                        </li>
                        <li><hr class="dropdown-divider"/></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/notification/list.do">
                            <i class="bi bi-bell me-2"></i> 알림</a></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/attendance/my.do">
                            <i class="bi bi-clock me-2"></i> 내 근태</a></li>
                        <li><hr class="dropdown-divider"/></li>
                        <li>
                            <form action="${pageContext.request.contextPath}/auth/logout" method="post">
                                <sec:csrfInput/>
                                <button type="submit" class="dropdown-item text-danger">
                                    <i class="bi bi-box-arrow-right me-2"></i> 로그아웃
                                </button>
                            </form>
                        </li>
                    </ul>
                </div>
            </div>
        </header>

        <!-- 본문 -->
        <main class="content">
            <div class="page-title-box">
                <h4 class="page-title"><sitemesh:write property='title'/></h4>
                <div class="text-muted small d-none d-md-block">
                    <i class="bi bi-grid-3x3-gap-fill text-primary"></i> 사내 그룹웨어
                </div>
            </div>
            <sitemesh:write property='body'/>
        </main>

        <footer class="app-footer">
            &copy; 2026 사내 그룹웨어 · eGovFrame 기반 통합 업무 시스템
        </footer>
    </div>
</div>

<!-- 모바일 하단 빠른 액세스 -->
<nav class="mobile-bottom-nav">
    <a href="${pageContext.request.contextPath}/dashboard.do"><i class="bi bi-house"></i><span>홈</span></a>
    <a href="${pageContext.request.contextPath}/notice/list.do"><i class="bi bi-megaphone"></i><span>공지</span></a>
    <a href="${pageContext.request.contextPath}/attendance/my.do"><i class="bi bi-clock"></i><span>근태</span></a>
    <a href="${pageContext.request.contextPath}/leave/my.do"><i class="bi bi-airplane"></i><span>휴가</span></a>
    <a href="${pageContext.request.contextPath}/approval/pending.do"><i class="bi bi-file-earmark-check"></i><span>결재</span></a>
</nav>
</sec:authorize>
<sec:authorize access="!isAuthenticated()">
    <sitemesh:write property='body'/>
</sec:authorize>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
