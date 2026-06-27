<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>공지사항</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-megaphone"></i> 공지사항</h2>
    <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER','MANAGER')">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/notice/write.do">
            <i class="bi bi-plus"></i> 새 공지
        </a>
    </sec:authorize>
</div>

<form class="row g-2 mb-3" method="get" action="${pageContext.request.contextPath}/notice/list.do">
    <div class="col-md-4"><input type="text" name="keyword" value="${keyword}"
                                  class="form-control" placeholder="제목/내용 검색"/></div>
    <div class="col-md-2"><button class="btn btn-outline-primary"><i class="bi bi-search"></i> 검색</button></div>
    <div class="col-md-6 text-end text-muted small">총 ${paging.total}건</div>
</form>

<div class="card">
    <table class="table table-hover mb-0">
        <thead class="table-light"><tr><th style="width:60px">고정</th><th>제목</th><th>작성자</th><th>조회</th><th>작성일</th></tr></thead>
        <tbody>
        <c:forEach var="n" items="${list}">
            <tr>
                <td>
                    <c:if test="${n.pinnedYn eq 'Y'}"><span class="badge bg-danger">📌</span></c:if>
                </td>
                <td><a class="text-decoration-none" href="${pageContext.request.contextPath}/notice/detail.do?noticeId=${n.noticeId}">${n.title}</a></td>
                <td>${n.authorName}</td>
                <td>${n.viewCnt}</td>
                <td class="small text-muted">${n.createdAt}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="5" class="text-center text-muted py-4">공지가 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<c:if test="${paging.totalPages > 1}">
<nav class="mt-3"><ul class="pagination justify-content-center">
    <c:forEach var="i" begin="1" end="${paging.totalPages}">
        <li class="page-item <c:if test='${i == paging.page}'>active</c:if>">
            <a class="page-link" href="?page=${i}&keyword=${keyword}">${i}</a>
        </li>
    </c:forEach>
</ul></nav>
</c:if>
