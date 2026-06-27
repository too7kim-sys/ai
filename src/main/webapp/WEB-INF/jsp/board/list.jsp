<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${board.boardNm}</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-chat-square-text"></i> ${board.boardNm}</h2>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/board/write.do?boardCd=${board.boardCd}">
        <i class="bi bi-pencil-square"></i> 글쓰기
    </a>
</div>

<!-- 게시판 탭 -->
<ul class="nav nav-tabs mb-3">
    <c:forEach var="b" items="${boards}">
        <li class="nav-item">
            <a class="nav-link <c:if test='${b.boardCd eq board.boardCd}'>active</c:if>"
               href="${pageContext.request.contextPath}/board/list.do?boardCd=${b.boardCd}">
                ${b.boardNm}
                <c:if test="${b.boardType eq 'ANONYMOUS'}"><i class="bi bi-incognito ms-1"></i></c:if>
                <c:if test="${b.boardType eq 'QNA'}"><i class="bi bi-question-circle ms-1"></i></c:if>
            </a>
        </li>
    </c:forEach>
</ul>

<c:if test="${not empty board.description}">
    <div class="alert alert-info py-2 small">${board.description}</div>
</c:if>

<form class="row g-2 mb-3" method="get">
    <input type="hidden" name="boardCd" value="${board.boardCd}"/>
    <div class="col-md-4"><input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="제목/내용 검색"/></div>
    <div class="col-md-2"><button class="btn btn-outline-primary"><i class="bi bi-search"></i> 검색</button></div>
    <div class="col-md-6 text-end text-muted small">총 ${paging.total}건</div>
</form>

<div class="card">
    <table class="table table-hover mb-0">
        <thead class="table-light">
        <tr>
            <th style="width:50px;">고정</th>
            <th>제목</th>
            <th style="width:140px;">작성자</th>
            <th style="width:80px;">조회</th>
            <th style="width:120px;">작성일</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${list}">
            <tr>
                <td>
                    <c:if test="${p.pinnedYn eq 'Y'}"><span class="badge bg-danger">📌</span></c:if>
                </td>
                <td>
                    <c:if test="${board.boardType eq 'QNA'}">
                        <c:choose>
                            <c:when test="${p.answeredYn eq 'Y'}"><span class="badge bg-success me-1">답변</span></c:when>
                            <c:otherwise><span class="badge bg-warning text-dark me-1">대기</span></c:otherwise>
                        </c:choose>
                    </c:if>
                    <a class="text-decoration-none" href="${pageContext.request.contextPath}/board/detail.do?postId=${p.postId}">${p.title}</a>
                    <c:if test="${p.attachGroupId != null}">
                        <i class="bi bi-paperclip text-muted ms-1"></i>
                    </c:if>
                </td>
                <td>
                    <c:if test="${p.anonymousYn eq 'Y'}"><i class="bi bi-incognito"></i> </c:if>
                    ${p.authorName}
                    <c:if test="${p.anonymousYn ne 'Y' and not empty p.authorDept}">
                        <small class="text-muted d-block">${p.authorDept}</small>
                    </c:if>
                </td>
                <td>${p.viewCnt}</td>
                <td class="small text-muted">${p.createdAt}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="5" class="text-center text-muted py-4">게시글이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<c:if test="${paging.totalPages > 1}">
<nav class="mt-3"><ul class="pagination justify-content-center">
    <c:forEach var="i" begin="1" end="${paging.totalPages}">
        <li class="page-item <c:if test='${i == paging.page}'>active</c:if>">
            <a class="page-link" href="?boardCd=${board.boardCd}&page=${i}&keyword=${keyword}">${i}</a>
        </li>
    </c:forEach>
</ul></nav>
</c:if>
