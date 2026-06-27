<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${box eq 'inbox' ? '받은 쪽지' : '보낸 쪽지'}</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-chat-dots"></i> 쪽지함</h2>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/message/write.do">
        <i class="bi bi-pencil"></i> 새 쪽지
    </a>
</div>

<ul class="nav nav-tabs mb-3">
    <li class="nav-item"><a class="nav-link <c:if test='${box eq "inbox"}'>active</c:if>"
                            href="${pageContext.request.contextPath}/message/inbox.do">받은 쪽지함</a></li>
    <li class="nav-item"><a class="nav-link <c:if test='${box eq "sent"}'>active</c:if>"
                            href="${pageContext.request.contextPath}/message/sent.do">보낸 쪽지함</a></li>
</ul>

<div class="card">
    <table class="table table-hover mb-0">
        <thead class="table-light">
        <tr>
            <th>${box eq 'inbox' ? '보낸 사람' : '받는 사람'}</th>
            <th>내용</th>
            <th style="width:140px;">시각</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="m" items="${list}">
            <tr class="<c:if test='${box eq "inbox" and m.readAt == null}'>fw-bold</c:if>">
                <td>
                    <c:choose>
                        <c:when test="${box eq 'inbox'}">${m.senderName}<small class="text-muted ms-1">${m.senderDept}</small></c:when>
                        <c:otherwise>${m.receiverName}</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <a class="text-decoration-none" href="${pageContext.request.contextPath}/message/detail.do?msgId=${m.msgId}">
                        <c:choose>
                            <c:when test="${fn:length(m.content) > 60}">${fn:substring(m.content, 0, 60)}...</c:when>
                            <c:otherwise>${m.content}</c:otherwise>
                        </c:choose>
                    </a>
                </td>
                <td class="small text-muted">${m.createdAt}</td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/message/delete.do" class="d-inline"
                          onsubmit="return confirm('삭제하시겠습니까?')">
                        <sec:csrfInput/>
                        <input type="hidden" name="msgId" value="${m.msgId}"/>
                        <input type="hidden" name="box" value="${box}"/>
                        <button class="btn btn-sm btn-link text-danger p-0">삭제</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="4" class="text-center text-muted py-4">쪽지가 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
