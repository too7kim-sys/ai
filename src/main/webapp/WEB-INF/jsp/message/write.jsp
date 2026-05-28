<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>쪽지 쓰기</title>
<h2 class="mb-3"><i class="bi bi-pencil"></i> 쪽지 쓰기</h2>

<form method="post" action="${pageContext.request.contextPath}/message/write.do" class="card">
    <sec:csrfInput/>
    <div class="card-body">
        <div class="mb-3">
            <label class="form-label">받는 사람</label>
            <div class="input-group">
                <input type="hidden" id="msgReceiverId" name="receiverId" value="${receiverId}" required/>
                <input type="text" id="msgReceiverNm" class="form-control" readonly placeholder="사원 선택"/>
                <button type="button" class="btn btn-outline-primary"
                        onclick="openUserPicker({hidden:'msgReceiverId', display:'msgReceiverNm'})">
                    <i class="bi bi-person-search"></i> 사원 선택
                </button>
            </div>
        </div>
        <div class="mb-3">
            <label class="form-label">내용</label>
            <textarea name="content" class="form-control" rows="8" required maxlength="2000"></textarea>
        </div>
    </div>
    <div class="card-footer">
        <button class="btn btn-primary"><i class="bi bi-send"></i> 전송</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/message/inbox.do">취소</a>
    </div>
</form>
