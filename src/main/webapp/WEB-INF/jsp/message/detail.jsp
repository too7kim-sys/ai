<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>쪽지</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-envelope"></i> 쪽지</h2>
    <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/message/inbox.do">
        <i class="bi bi-arrow-left"></i> 목록
    </a>
</div>

<div class="card">
    <div class="card-body">
        <div class="small text-muted mb-1">보낸 사람</div>
        <div class="mb-2"><strong>${msg.senderName}</strong> <small class="text-muted">${msg.senderDept}</small></div>
        <div class="small text-muted mb-1">받는 사람</div>
        <div class="mb-2"><strong>${msg.receiverName}</strong></div>
        <div class="small text-muted mb-1">시각</div>
        <div class="mb-3">${msg.createdAt}</div>
        <div class="border-top pt-3" style="white-space: pre-wrap;">${msg.content}</div>
    </div>
    <div class="card-footer d-flex justify-content-between">
        <a class="btn btn-sm btn-outline-primary"
           href="${pageContext.request.contextPath}/message/write.do?receiverId=${msg.senderId}">
            <i class="bi bi-reply"></i> 답장
        </a>
        <form method="post" action="${pageContext.request.contextPath}/message/delete.do" class="d-inline"
              onsubmit="return confirm('삭제하시겠습니까?')">
            <sec:csrfInput/>
            <input type="hidden" name="msgId" value="${msg.msgId}"/>
            <button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i> 삭제</button>
        </form>
    </div>
</div>
