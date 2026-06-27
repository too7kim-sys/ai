<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>공지 ${notice == null ? '작성' : '수정'}</title>
<h2 class="mb-3"><i class="bi bi-megaphone"></i> 공지 ${notice == null ? '작성' : '수정'}</h2>

<form method="post" action="${pageContext.request.contextPath}/notice/write.do">
    <sec:csrfInput/>
    <c:if test="${notice != null}">
        <input type="hidden" name="noticeId" value="${notice.noticeId}"/>
    </c:if>
    <div class="card">
        <div class="card-body">
            <div class="mb-3">
                <label class="form-label">제목</label>
                <input type="text" name="title" class="form-control"
                       value="${notice.title}" required maxlength="200"/>
            </div>
            <div class="mb-3">
                <label class="form-label">내용</label>
                <textarea name="content" class="form-control" rows="12" required>${notice.content}</textarea>
            </div>
            <div class="row g-2">
                <div class="col-md-4">
                    <div class="form-check">
                        <input type="checkbox" name="pinnedYn" value="Y" class="form-check-input" id="pinned"
                               <c:if test="${notice.pinnedYn eq 'Y'}">checked</c:if>/>
                        <label class="form-check-label" for="pinned">📌 상단 고정</label>
                    </div>
                </div>
                <div class="col-md-4">
                    <label class="form-label small">공개 범위</label>
                    <select name="deptScope" class="form-select form-select-sm">
                        <option value="ALL" <c:if test="${notice.deptScope eq 'ALL'}">selected</c:if>>전사</option>
                        <option value="DEPT" <c:if test="${notice.deptScope eq 'DEPT'}">selected</c:if>>부서</option>
                    </select>
                </div>
            </div>
        </div>
        <div class="card-footer">
            <button class="btn btn-primary"><i class="bi bi-send"></i> 저장</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/notice/list.do">취소</a>
        </div>
    </div>
</form>
