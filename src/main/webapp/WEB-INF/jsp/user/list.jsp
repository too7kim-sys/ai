<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>직원 디렉토리</title>
<h2 class="mb-3"><i class="bi bi-people"></i> 직원 디렉토리</h2>

<form class="row g-2 mb-3" method="get" action="${pageContext.request.contextPath}/user/list.do">
    <div class="col-md-3">
        <input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="이름/이메일 검색"/>
    </div>
    <div class="col-md-3">
        <select name="deptId" class="form-select">
            <option value="">전체 부서</option>
            <c:forEach var="d" items="${depts}">
                <option value="${d.deptId}" <c:if test="${deptId == d.deptId}">selected</c:if>>${d.deptNm}</option>
            </c:forEach>
        </select>
    </div>
    <div class="col-md-2">
        <button class="btn btn-outline-primary"><i class="bi bi-search"></i> 검색</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/user/list.do">초기화</a>
    </div>
    <div class="col-md-4 text-end text-muted small">
        총 ${paging.total}명
    </div>
</form>

<div class="card">
    <table class="table table-hover mb-0">
        <thead class="table-light">
        <tr>
            <th>이름</th>
            <th>이메일</th>
            <th>부서</th>
            <th>직급</th>
            <th>역할</th>
            <th>연락처</th>
            <th>입사일</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${users}">
            <tr>
                <td><strong>${u.name}</strong></td>
                <td>${u.email}</td>
                <td>${u.deptNm}</td>
                <td>${u.positionNm}</td>
                <td><span class="badge bg-secondary">${u.roleNm}</span></td>
                <td>${u.phone}</td>
                <td>${u.hireDate}</td>
                <td>
                    <a class="btn btn-sm btn-outline-primary"
                       href="${pageContext.request.contextPath}/user/profile.do?userId=${u.userId}">프로필</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty users}">
            <tr><td colspan="8" class="text-center text-muted py-4">검색 결과가 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<c:if test="${paging.totalPages > 1}">
<nav class="mt-3">
    <ul class="pagination justify-content-center">
        <c:forEach var="i" begin="1" end="${paging.totalPages}">
            <li class="page-item <c:if test='${i == paging.page}'>active</c:if>">
                <a class="page-link"
                   href="?page=${i}&keyword=${keyword}<c:if test='${not empty deptId}'>&deptId=${deptId}</c:if>">${i}</a>
            </li>
        </c:forEach>
    </ul>
</nav>
</c:if>
