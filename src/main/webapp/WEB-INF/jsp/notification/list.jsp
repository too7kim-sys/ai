<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>알림</title>
<div class="d-flex justify-content-between align-items-center mb-3">
    <h2><i class="bi bi-bell"></i> 알림 <span class="badge bg-danger">${unread}</span></h2>
    <div class="d-flex gap-2">
        <div class="btn-group">
            <a class="btn btn-sm <c:choose><c:when test='${!onlyUnread}'>btn-primary</c:when><c:otherwise>btn-outline-secondary</c:otherwise></c:choose>"
               href="?onlyUnread=false">전체</a>
            <a class="btn btn-sm <c:choose><c:when test='${onlyUnread}'>btn-primary</c:when><c:otherwise>btn-outline-secondary</c:otherwise></c:choose>"
               href="?onlyUnread=true">읽지 않음</a>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/notification/read-all.do" class="d-inline">
            <sec:csrfInput/>
            <button class="btn btn-sm btn-outline-success"><i class="bi bi-check-all"></i> 모두 읽음</button>
        </form>
    </div>
</div>

<div class="card">
    <ul class="list-group list-group-flush">
        <c:forEach var="n" items="${list}">
            <li class="list-group-item d-flex justify-content-between align-items-start
                       <c:if test='${empty n.readAt}'>bg-light fw-bold</c:if>">
                <div>
                    <span class="badge bg-secondary me-2">${n.typeCd}</span>
                    <c:if test="${not empty n.linkUrl}">
                        <a href="${pageContext.request.contextPath}${n.linkUrl}" class="text-decoration-none">${n.title}</a>
                    </c:if>
                    <c:if test="${empty n.linkUrl}">${n.title}</c:if>
                    <c:if test="${not empty n.body}">
                        <div class="small text-muted mt-1">${n.body}</div>
                    </c:if>
                    <div class="small text-muted">${n.createdAt}</div>
                </div>
                <c:if test="${empty n.readAt}">
                    <form method="post" action="${pageContext.request.contextPath}/notification/read.do" class="d-inline">
                        <sec:csrfInput/>
                        <input type="hidden" name="notiId" value="${n.notiId}"/>
                        <input type="hidden" name="redirect" value="/notification/list.do?onlyUnread=${onlyUnread}"/>
                        <button class="btn btn-sm btn-outline-success">읽음</button>
                    </form>
                </c:if>
            </li>
        </c:forEach>
        <c:if test="${empty list}">
            <li class="list-group-item text-center text-muted py-4">알림이 없습니다.</li>
        </c:if>
    </ul>
</div>
