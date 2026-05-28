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
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/payroll/admin/list.do"><i class="bi bi-cash-stack"></i><span>급여대장 (HR)</span></a></li>
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/payroll/admin/bonus.do"><i class="bi bi-gift"></i><span>상여 관리 (HR)</span></a></li>
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/payroll/admin/year-end.do"><i class="bi bi-receipt-cutoff"></i><span>연말정산 (HR)</span></a></li>
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/payroll/admin/severance.do"><i class="bi bi-box-arrow-right"></i><span>퇴직정산 (HR)</span></a></li>
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/payroll/admin/insurance-rate.do"><i class="bi bi-percent"></i><span>4대보험 요율 (HR)</span></a></li>
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/contract/admin/list.do"><i class="bi bi-file-earmark-text"></i><span>근로계약 관리 (HR)</span></a></li>
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/performance/admin/all.do"><i class="bi bi-bullseye"></i><span>전사 KPI (HR)</span></a></li>
            </sec:authorize>

            <li class="menu-title">근태 · 결재</li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/attendance/my.do"><i class="bi bi-clock"></i><span>근태</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/leave/my.do"><i class="bi bi-airplane"></i><span>휴가</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/approval/pending.do"><i class="bi bi-file-earmark-check"></i><span>전자결재</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/payroll/my.do"><i class="bi bi-cash"></i><span>급여</span></a></li>
            <li><a class="nav-link" href="${pageContext.request.contextPath}/expense/my.do"><i class="bi bi-credit-card"></i><span>지출결의</span></a></li>
            <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER','MANAGER')">
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/attendance/admin/report.do"><i class="bi bi-clipboard-data"></i><span>근태 리포트 (HR)</span></a></li>
            </sec:authorize>
            <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
            <li><a class="nav-link text-success" href="${pageContext.request.contextPath}/leave/admin/balance.do"><i class="bi bi-calendar-plus"></i><span>휴가 일수 관리 (HR)</span></a></li>
            </sec:authorize>
            <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER')">
            <li><a class="nav-link text-primary" href="${pageContext.request.contextPath}/expense/admin/list.do"><i class="bi bi-card-checklist"></i><span>지출결의 관리</span></a></li>
            </sec:authorize>

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

            <!-- 통합 검색 (직원 디렉토리) — 클릭하면 사원 선택 팝업이 열리고
                 선택 즉시 해당 직원 프로필로 이동한다. -->
            <div class="topbar-search d-none d-md-flex" role="search">
                <i class="bi bi-search"></i>
                <input type="text" id="topbarUserSearch" class="topbar-search-trigger"
                       placeholder="직원 검색..." readonly autocomplete="off"
                       onclick="openUserPicker({onSelect: function(u){ location.href='${pageContext.request.contextPath}/user/profile.do?userId=' + u.userId; }})"
                       style="cursor:pointer"/>
            </div>

            <div class="ms-auto d-flex align-items-center gap-1">
                <!-- 검색 (모바일) -->
                <a class="topbar-icon d-md-none" href="${pageContext.request.contextPath}/user/list.do" title="검색">
                    <i class="bi bi-search"></i>
                </a>
                <!-- 다크모드 토글 -->
                <button class="topbar-icon" type="button" id="themeToggle" title="다크/라이트 모드">
                    <i class="bi bi-moon-stars" id="themeIcon"></i>
                </button>
                <!-- 전체화면 -->
                <button class="topbar-icon d-none d-md-inline" type="button" id="fullscreenToggle" title="전체화면">
                    <i class="bi bi-arrows-fullscreen" id="fullscreenIcon"></i>
                </button>
                <!-- 알림 드롭다운 -->
                <div class="dropdown">
                    <button class="topbar-icon" type="button" data-bs-toggle="dropdown" data-bs-auto-close="true" title="알림">
                        <i class="bi bi-bell"></i>
                        <span class="topbar-badge badge rounded-pill bg-danger" id="notiBadge"
                              <c:if test="${unreadNotiCount == 0}">style="display:none"</c:if>>${unreadNotiCount}</span>
                    </button>
                    <div class="dropdown-menu dropdown-menu-end shadow notif-dropdown">
                        <div class="notif-head d-flex justify-content-between align-items-center">
                            <strong>알림</strong>
                            <span class="badge bg-soft-danger"><span id="notiHeadCount">${unreadNotiCount}</span> 신규</span>
                        </div>
                        <div class="notif-body" id="notiBody">
                            <c:forEach var="n" items="${topbarNotifications}">
                                <a class="notif-item ${empty n.readAt ? 'unread' : ''}"
                                   href="${pageContext.request.contextPath}<c:choose><c:when test='${not empty n.linkUrl}'>${n.linkUrl}</c:when><c:otherwise>/notification/list.do</c:otherwise></c:choose>">
                                    <span class="notif-dot"></span>
                                    <span class="notif-text">
                                        <span class="d-block text-truncate">${n.title}</span>
                                        <small class="text-muted">${n.typeCd} · ${fn:substring(n.createdAt,0,16)}</small>
                                    </span>
                                </a>
                            </c:forEach>
                            <c:if test="${empty topbarNotifications}">
                                <div class="text-center text-muted py-4 small">알림이 없습니다.</div>
                            </c:if>
                        </div>
                        <a class="notif-foot" href="${pageContext.request.contextPath}/notification/list.do">
                            전체 알림 보기
                        </a>
                    </div>
                </div>
                <!-- 쪽지 -->
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

        <!-- 상단 그룹 탭 (1차 카테고리). 클릭하면 좌측 사이드바가 해당 그룹의 메뉴만 표시한다. -->
        <nav class="groupbar" id="groupbar">
            <a class="group-tab" data-group="메인" href="${pageContext.request.contextPath}/dashboard.do">
                <i class="bi bi-speedometer2"></i><span>대시보드</span>
            </a>
            <a class="group-tab" data-group="인사 · 평가">
                <i class="bi bi-people"></i><span>인사 · 평가</span>
            </a>
            <a class="group-tab" data-group="근태 · 결재">
                <i class="bi bi-clipboard-check"></i><span>근태 · 결재</span>
            </a>
            <a class="group-tab" data-group="협업">
                <i class="bi bi-chat-square-text"></i><span>협업</span>
            </a>
            <a class="group-tab" data-group="자산 · 차량">
                <i class="bi bi-laptop"></i><span>자산 · 차량</span>
            </a>
            <sec:authorize access="hasAnyRole('ADMIN','FINANCE_MANAGER','MANAGER')">
            <a class="group-tab" data-group="재무 · 회계">
                <i class="bi bi-bank"></i><span>재무 · 회계</span>
            </a>
            </sec:authorize>
            <sec:authorize access="hasRole('ADMIN')">
            <a class="group-tab" data-group="시스템 관리">
                <i class="bi bi-gear"></i><span>시스템</span>
            </a>
            </sec:authorize>
        </nav>

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

        <%-- 작업 결과 토스트 (PRG 피드백) --%>
        <c:if test="${not empty flashMsg}">
        <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index:1200">
            <div id="flashToast" class="toast align-items-center text-bg-${empty flashType ? 'success' : flashType} border-0"
                 role="alert" data-bs-delay="3500">
                <div class="d-flex">
                    <div class="toast-body">
                        <i class="bi ${flashType eq 'danger' ? 'bi-exclamation-triangle' : 'bi-check-circle'} me-1"></i>
                        <c:out value="${flashMsg}"/>
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        </div>
        </c:if>

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
<script>
    document.addEventListener('DOMContentLoaded', function () {
        var el = document.getElementById('flashToast');
        if (el && window.bootstrap) new bootstrap.Toast(el).show();
    });

    // ===== 알림 자동 갱신 (near-real-time) — 30초 주기 폴링 =====
    (function () {
        var ctx = '${pageContext.request.contextPath}';
        var badge = document.getElementById('notiBadge');
        var headCount = document.getElementById('notiHeadCount');
        var body = document.getElementById('notiBody');
        if (!badge) return;
        var lastUnread = parseInt(badge.textContent || '0', 10) || 0;

        function ensureToastContainer() {
            var c = document.querySelector('.toast-container');
            if (!c) {
                c = document.createElement('div');
                c.className = 'toast-container position-fixed top-0 end-0 p-3';
                c.style.zIndex = '1200';
                document.body.appendChild(c);
            }
            return c;
        }
        function notifyToast(added) {
            if (!window.bootstrap) return;
            var c = ensureToastContainer();
            var t = document.createElement('div');
            t.className = 'toast align-items-center text-bg-primary border-0';
            var d = document.createElement('div'); d.className = 'd-flex';
            var b = document.createElement('div'); b.className = 'toast-body';
            b.textContent = '🔔 새 알림 ' + added + '건이 도착했습니다.';
            var btn = document.createElement('button');
            btn.type = 'button'; btn.className = 'btn-close btn-close-white me-2 m-auto';
            btn.setAttribute('data-bs-dismiss', 'toast');
            d.appendChild(b); d.appendChild(btn); t.appendChild(d); c.appendChild(t);
            new bootstrap.Toast(t, { delay: 6000 }).show();
            t.addEventListener('hidden.bs.toast', function () { t.remove(); });
        }
        function renderRecent(recent) {
            if (!body) return;
            body.textContent = '';
            if (!recent || recent.length === 0) {
                var empty = document.createElement('div');
                empty.className = 'text-center text-muted py-4 small';
                empty.textContent = '알림이 없습니다.';
                body.appendChild(empty);
                return;
            }
            recent.forEach(function (n) {
                var a = document.createElement('a');
                a.className = 'notif-item' + (n.read ? '' : ' unread');
                a.href = ctx + (n.linkUrl ? n.linkUrl : '/notification/list.do');
                var dot = document.createElement('span'); dot.className = 'notif-dot';
                var txt = document.createElement('span'); txt.className = 'notif-text';
                var ttl = document.createElement('span');
                ttl.className = 'd-block text-truncate';
                ttl.textContent = n.title || '';
                var sub = document.createElement('small');
                sub.className = 'text-muted';
                sub.textContent = (n.typeCd || '') + ' · ' + (n.createdAt || '').replace('T', ' ').substring(0, 16);
                txt.appendChild(ttl); txt.appendChild(sub);
                a.appendChild(dot); a.appendChild(txt);
                body.appendChild(a);
            });
        }
        function poll() {
            fetch(ctx + '/notification/unread-count.do', { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
                .then(function (r) { return r.ok ? r.json() : null; })
                .then(function (data) {
                    if (!data) return;
                    var n = data.unread || 0;
                    if (n > 0) { badge.textContent = n; badge.style.display = ''; }
                    else { badge.style.display = 'none'; }
                    if (headCount) headCount.textContent = n;
                    if (n > lastUnread) notifyToast(n - lastUnread);
                    lastUnread = n;
                    renderRecent(data.recent);
                })
                .catch(function () { /* 네트워크 일시 오류는 무시 */ });
        }
        setInterval(poll, 30000);
    })();
</script>

<script>
/* ────────────────────────────────────────────────────────────────
 * 전역 Enter → submit 차단
 *
 * 일반적인 입력 화면에서 사용자가 입력 도중 Enter 키를 누르면 폼이
 * 의도치 않게 제출되어 빈 hidden 값·잘못된 LocalDate 등으로 인해
 * 500 이 발생하던 패턴을 방지한다.
 *
 * 정책:
 *  - GET 메서드 form (대부분 검색 폼)             — Enter 허용
 *  - role="search" / data-enter-submit="true"      — Enter 허용
 *  - textarea, button, [type=submit/button]        — 자체 동작
 *  - input 이 1 개뿐인 인라인 POST form (댓글·일괄수정 류) — Enter 허용
 *  - 그 외 POST form 안의 input 에서 Enter         — 차단
 *
 * form="..." 속성으로 외부 form 에 격리된 입력은 closest('form') 이
 * 찾지 못해 자연스럽게 통과한다(예: 사원 선택 모달 검색창).
 * ──────────────────────────────────────────────────────────────── */
document.addEventListener('keydown', function (e) {
    if (e.key !== 'Enter') return;
    var t = e.target;
    if (!t || !t.tagName) return;
    var tag = t.tagName.toLowerCase();
    if (tag === 'textarea' || tag === 'button') return;
    var type = (t.type || '').toLowerCase();
    if (type === 'submit' || type === 'button') return;
    var form = (t.form && t.form.tagName) ? t.form : (t.closest ? t.closest('form') : null);
    if (!form) return;
    var method = (form.method || form.getAttribute('method') || 'get').toLowerCase();
    if (method === 'get') return;
    if (form.getAttribute('role') === 'search') return;
    if (form.dataset && form.dataset.enterSubmit === 'true') return;
    // 비-hidden 단순 input 이 1 개뿐이면 인라인 단축 submit 으로 간주해 허용
    var inputs = form.querySelectorAll(
        'input:not([type=hidden]):not([type=submit]):not([type=button]):not([type=reset]):not([type=checkbox]):not([type=radio]):not([disabled]):not([readonly])');
    if (inputs.length <= 1) return;
    e.preventDefault();
    e.stopPropagation();
}, true);
</script>

<%-- 공통 사원 선택 모달 (전 화면 공유, openUserPicker(opts) 로 호출) --%>
<jsp:include page="/WEB-INF/jsp/cmm/_user-picker.jsp"/>
</body>
</html>
