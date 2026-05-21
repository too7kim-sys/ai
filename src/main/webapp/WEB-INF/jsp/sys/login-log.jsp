<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>시스템 - 로그인 이력</title>
<h2 class="mb-3"><i class="bi bi-gear"></i> 시스템 관리</h2>
<c:set var="active" value="login" scope="request"/>
<jsp:include page="_nav.jsp"/>

<form class="row g-2 mb-3" method="get">
    <div class="col-md-3"><input type="text" name="emailKeyword" value="${emailKeyword}" class="form-control" placeholder="이메일 검색"/></div>
    <div class="col-md-2">
        <select name="successYn" class="form-select">
            <option value="">전체</option>
            <option value="Y" <c:if test="${successYn eq 'Y'}">selected</c:if>>성공</option>
            <option value="N" <c:if test="${successYn eq 'N'}">selected</c:if>>실패</option>
        </select>
    </div>
    <div class="col-md-2"><input type="date" name="fromDate" value="${fromDate}" class="form-control"/></div>
    <div class="col-md-2"><input type="date" name="toDate" value="${toDate}" class="form-control"/></div>
    <div class="col-md-1"><button class="btn btn-outline-primary w-100">조회</button></div>
    <div class="col-md-2 text-end text-muted small align-self-center">총 ${paging.total}건</div>
</form>

<div class="card">
    <table class="table mb-0">
        <thead class="table-light">
        <tr><th style="width:60px;">결과</th><th>일시</th><th>사용자</th><th>입력 이메일</th><th>IP</th><th>실패 사유</th></tr>
        </thead>
        <tbody>
        <c:forEach var="l" items="${list}">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${l.successYn eq 'Y'}"><span class="badge bg-success">성공</span></c:when>
                        <c:otherwise><span class="badge bg-danger">실패</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="small text-muted">${l.loginDt}</td>
                <td>
                    <c:if test="${not empty l.userName}">${l.userName}<small class="text-muted ms-1">${l.userEmail}</small></c:if>
                </td>
                <td class="small">${l.emailInput}</td>
                <td class="small text-muted">${l.ip}</td>
                <td class="small text-muted">${l.failReason}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="6" class="text-center text-muted py-4">기록이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<c:if test="${paging.totalPages > 1}">
<nav class="mt-3"><ul class="pagination justify-content-center">
    <c:forEach var="i" begin="1" end="${paging.totalPages}">
        <li class="page-item <c:if test='${i == paging.page}'>active</c:if>">
            <a class="page-link" href="?page=${i}&emailKeyword=${emailKeyword}&successYn=${successYn}&fromDate=${fromDate}&toDate=${toDate}">${i}</a>
        </li>
    </c:forEach>
</ul></nav>
</c:if>
