<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>시스템 - 감사 로그</title>
<h2 class="mb-3"><i class="bi bi-gear"></i> 시스템 관리</h2>
<c:set var="active" value="audit" scope="request"/>
<jsp:include page="_nav.jsp"/>

<form class="row g-2 mb-3" method="get">
    <div class="col-md-2">
        <select name="actionType" class="form-select">
            <option value="">전체 액션</option>
            <c:forEach var="t" items="${actionTypes}">
                <option value="${t}" <c:if test="${t eq actionType}">selected</c:if>>${t}</option>
            </c:forEach>
        </select>
    </div>
    <div class="col-md-2">
        <select name="entityName" class="form-select">
            <option value="">전체 엔티티</option>
            <c:forEach var="e" items="${entityNames}">
                <option value="${e}" <c:if test="${e eq entityName}">selected</c:if>>${e}</option>
            </c:forEach>
        </select>
    </div>
    <div class="col-md-2"><input type="number" name="userId" value="${userId}" class="form-control" placeholder="사용자 ID"/></div>
    <div class="col-md-2"><input type="date" name="fromDate" value="${fromDate}" class="form-control"/></div>
    <div class="col-md-2"><input type="date" name="toDate" value="${toDate}" class="form-control"/></div>
    <div class="col-md-1"><button class="btn btn-outline-primary w-100">조회</button></div>
    <div class="col-md-1 text-end text-muted small align-self-center">${paging.total}건</div>
</form>

<div class="card">
    <table class="table mb-0">
        <thead class="table-light">
        <tr><th>일시</th><th>액션</th><th>엔티티</th><th>대상 ID</th><th>사용자</th><th>IP</th><th>Before</th><th>After</th></tr>
        </thead>
        <tbody>
        <c:forEach var="a" items="${list}">
            <tr>
                <td class="small text-muted">${a.actionAt}</td>
                <td><span class="badge bg-info text-dark">${a.actionType}</span></td>
                <td class="small">${a.entityName}</td>
                <td class="small">${a.entityId}</td>
                <td class="small">
                    <c:if test="${not empty a.userName}">${a.userName}<small class="text-muted ms-1">#${a.userId}</small></c:if>
                </td>
                <td class="small text-muted">${a.ip}</td>
                <td class="small" style="max-width:300px;"><code>${a.beforeJson}</code></td>
                <td class="small" style="max-width:300px;"><code>${a.afterJson}</code></td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="8" class="text-center text-muted py-4">기록이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<c:if test="${paging.totalPages > 1}">
<nav class="mt-3"><ul class="pagination justify-content-center">
    <c:forEach var="i" begin="1" end="${paging.totalPages}">
        <li class="page-item <c:if test='${i == paging.page}'>active</c:if>">
            <a class="page-link" href="?page=${i}&actionType=${actionType}&entityName=${entityName}&userId=${userId}&fromDate=${fromDate}&toDate=${toDate}">${i}</a>
        </li>
    </c:forEach>
</ul></nav>
</c:if>
