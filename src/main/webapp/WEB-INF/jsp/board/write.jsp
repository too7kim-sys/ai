<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${post == null ? '글쓰기' : '수정'}</title>
<h2 class="mb-3"><i class="bi bi-pencil-square"></i> ${board.boardNm} - ${post == null ? '글쓰기' : '수정'}</h2>

<form method="post" action="${pageContext.request.contextPath}/board/write.do" enctype="multipart/form-data">
    <sec:csrfInput/>
    <input type="hidden" name="boardCd" value="${board.boardCd}"/>
    <c:if test="${post != null}">
        <input type="hidden" name="postId" value="${post.postId}"/>
    </c:if>
    <div class="card">
        <div class="card-body">
            <div class="mb-3">
                <label class="form-label">제목</label>
                <input type="text" name="title" class="form-control" value="${post.title}" required maxlength="200"/>
            </div>
            <div class="mb-3">
                <label class="form-label">내용</label>
                <textarea name="content" class="form-control" rows="12" required>${post.content}</textarea>
            </div>
            <div class="row g-2 align-items-center">
                <div class="col-md-4">
                    <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER','MANAGER')">
                        <div class="form-check">
                            <input type="checkbox" name="pinnedYn" value="Y" class="form-check-input" id="pinned"
                                   <c:if test="${post.pinnedYn eq 'Y'}">checked</c:if>/>
                            <label class="form-check-label" for="pinned">📌 상단 고정</label>
                        </div>
                    </sec:authorize>
                </div>
                <c:if test="${board.boardType eq 'ANONYMOUS' and post == null}">
                    <div class="col-md-4">
                        <div class="form-check">
                            <input type="checkbox" name="anonymousYn" value="Y" class="form-check-input" id="anon" checked/>
                            <label class="form-check-label" for="anon"><i class="bi bi-incognito"></i> 익명으로 등록</label>
                        </div>
                    </div>
                </c:if>
            </div>
            <c:if test="${post == null}">
                <div class="mt-3">
                    <label class="form-label">첨부파일 (선택, 50MB 이하)</label>
                    <input type="file" name="files" class="form-control" multiple/>
                </div>
            </c:if>
        </div>
        <div class="card-footer">
            <button class="btn btn-primary"><i class="bi bi-send"></i> 저장</button>
            <a class="btn btn-outline-secondary"
               href="${pageContext.request.contextPath}/board/list.do?boardCd=${board.boardCd}">취소</a>
        </div>
    </div>
</form>
