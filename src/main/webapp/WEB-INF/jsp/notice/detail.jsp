<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${notice.title}</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-megaphone"></i> 공지사항</h2>
    <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/notice/list.do">
        <i class="bi bi-arrow-left"></i> 목록
    </a>
</div>

<div class="card mb-3">
    <div class="card-body">
        <h3>
            <c:if test="${notice.pinnedYn eq 'Y'}"><span class="badge bg-danger me-2">📌 고정</span></c:if>
            ${notice.title}
        </h3>
        <div class="text-muted small mb-3">
            ${notice.authorName} · ${notice.createdAt} · 조회 ${notice.viewCnt}
        </div>
        <div class="border-top pt-3" style="white-space: pre-wrap;">${notice.content}</div>
    </div>
    <div class="card-footer d-flex justify-content-end gap-2">
        <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER','MANAGER')">
            <a class="btn btn-sm btn-outline-primary"
               href="${pageContext.request.contextPath}/notice/write.do?noticeId=${notice.noticeId}">
                <i class="bi bi-pencil"></i> 수정
            </a>
            <form method="post" action="${pageContext.request.contextPath}/notice/delete.do" class="d-inline"
                  onsubmit="return confirm('삭제하시겠습니까?')">
                <sec:csrfInput/>
                <input type="hidden" name="noticeId" value="${notice.noticeId}"/>
                <button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i> 삭제</button>
            </form>
        </sec:authorize>
    </div>
</div>

<div class="card">
    <div class="card-header"><i class="bi bi-chat-left-text"></i> 댓글 ${fn:length(comments)}</div>
    <ul class="list-group list-group-flush">
        <c:forEach var="c" items="${comments}">
            <li class="list-group-item">
                <div class="d-flex justify-content-between">
                    <div>
                        <strong>${c.authorName}</strong>
                        <span class="text-muted small ms-2">${c.createdAt}</span>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/notice/comment/delete.do" class="d-inline"
                          onsubmit="return confirm('댓글을 삭제할까요?')">
                        <sec:csrfInput/>
                        <input type="hidden" name="cmtId" value="${c.cmtId}"/>
                        <input type="hidden" name="noticeId" value="${notice.noticeId}"/>
                        <button class="btn btn-sm btn-link text-danger p-0">삭제</button>
                    </form>
                </div>
                <div class="mt-1">${c.content}</div>
            </li>
        </c:forEach>
        <c:if test="${empty comments}">
            <li class="list-group-item text-center text-muted">아직 댓글이 없습니다.</li>
        </c:if>
    </ul>
    <div class="card-footer">
        <form method="post" action="${pageContext.request.contextPath}/notice/comment/add.do" class="d-flex gap-2">
            <sec:csrfInput/>
            <input type="hidden" name="noticeId" value="${notice.noticeId}"/>
            <input type="text" name="content" class="form-control" placeholder="댓글을 입력하세요" required/>
            <button class="btn btn-primary"><i class="bi bi-send"></i> 등록</button>
        </form>
    </div>
</div>
