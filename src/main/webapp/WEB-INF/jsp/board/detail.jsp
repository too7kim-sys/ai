<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${post.title}</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-chat-square-text"></i> ${post.boardNm}</h2>
    <a class="btn btn-sm btn-outline-secondary"
       href="${pageContext.request.contextPath}/board/list.do?boardCd=${post.boardCd}">
        <i class="bi bi-arrow-left"></i> 목록
    </a>
</div>

<div class="card mb-3">
    <div class="card-body">
        <h3 class="mb-2">
            <c:if test="${post.pinnedYn eq 'Y'}"><span class="badge bg-danger me-2">📌 고정</span></c:if>
            <c:if test="${post.boardType eq 'QNA' and post.answeredYn eq 'Y'}"><span class="badge bg-success me-2">답변완료</span></c:if>
            ${post.title}
        </h3>
        <div class="text-muted small mb-3">
            <c:if test="${post.anonymousYn eq 'Y'}"><i class="bi bi-incognito"></i> </c:if>
            ${post.authorName}
            <c:if test="${post.anonymousYn ne 'Y'}">· ${post.authorDept}</c:if>
            · ${post.createdAt}
            · 조회 ${post.viewCnt}
        </div>
        <div class="border-top pt-3" style="white-space: pre-wrap;">${post.content}</div>

        <c:if test="${not empty attachments}">
            <div class="border-top mt-3 pt-3">
                <strong><i class="bi bi-paperclip"></i> 첨부파일</strong>
                <ul class="list-unstyled mt-2 mb-0">
                    <c:forEach var="a" items="${attachments}">
                        <li>
                            <a href="${pageContext.request.contextPath}/board/attach/download.do?attachId=${a.attachId}">
                                <i class="bi bi-file-earmark"></i> ${a.fileNm}
                            </a>
                            <small class="text-muted">(${a.fileSize / 1024} KB)</small>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
    </div>
    <div class="card-footer d-flex justify-content-end gap-2">
        <sec:authentication property="principal.userId" var="myId"/>
        <c:if test="${post.authorId == myId}">
            <a class="btn btn-sm btn-outline-primary"
               href="${pageContext.request.contextPath}/board/write.do?boardCd=${post.boardCd}&postId=${post.postId}">
                <i class="bi bi-pencil"></i> 수정
            </a>
            <form method="post" action="${pageContext.request.contextPath}/board/delete.do" class="d-inline"
                  onsubmit="return confirm('삭제하시겠습니까?')">
                <sec:csrfInput/>
                <input type="hidden" name="postId" value="${post.postId}"/>
                <input type="hidden" name="boardCd" value="${post.boardCd}"/>
                <button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i> 삭제</button>
            </form>
        </c:if>
    </div>
</div>

<div class="card">
    <div class="card-header"><i class="bi bi-chat-left-text"></i> 댓글 ${fn:length(comments)}</div>
    <ul class="list-group list-group-flush">
        <c:forEach var="c" items="${comments}">
            <li class="list-group-item">
                <div class="d-flex justify-content-between">
                    <div>
                        <strong>
                            <c:if test="${c.anonymousYn eq 'Y'}"><i class="bi bi-incognito"></i> </c:if>
                            ${c.authorName}
                        </strong>
                        <span class="text-muted small ms-2">${c.createdAt}</span>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/board/comment/delete.do" class="d-inline"
                          onsubmit="return confirm('댓글을 삭제할까요?')">
                        <sec:csrfInput/>
                        <input type="hidden" name="cmtId" value="${c.cmtId}"/>
                        <input type="hidden" name="postId" value="${post.postId}"/>
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
        <form method="post" action="${pageContext.request.contextPath}/board/comment/add.do">
            <sec:csrfInput/>
            <input type="hidden" name="postId" value="${post.postId}"/>
            <div class="input-group">
                <input type="text" name="content" class="form-control" placeholder="댓글을 입력하세요" required maxlength="1000"/>
                <c:if test="${post.boardType eq 'ANONYMOUS'}">
                    <div class="input-group-text">
                        <input type="checkbox" name="anonymousYn" value="Y" class="form-check-input mt-0 me-2"/>
                        익명
                    </div>
                </c:if>
                <button class="btn btn-primary"><i class="bi bi-send"></i></button>
            </div>
        </form>
    </div>
</div>
