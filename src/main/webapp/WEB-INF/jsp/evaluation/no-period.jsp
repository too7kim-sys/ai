<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>평가 기간 없음</title>
<h2 class="mb-3"><i class="bi bi-graph-up"></i> 평가</h2>
<div class="alert alert-warning">
    <i class="bi bi-exclamation-triangle"></i>
    현재 진행 중인 평가 기간이 없습니다. HR 관리자에게 문의하세요.
</div>
<c:if test="${not empty periods}">
<div class="card">
    <div class="card-header">과거 평가 기간</div>
    <table class="table mb-0">
        <thead class="table-light"><tr><th>기간명</th><th>시작</th><th>종료</th><th>상태</th></tr></thead>
        <tbody>
        <c:forEach var="p" items="${periods}">
            <tr>
                <td><a href="?periodId=${p.periodId}">${p.periodNm}</a></td>
                <td>${p.startDt}</td><td>${p.endDt}</td>
                <td><span class="badge bg-secondary">${p.statusCd}</span></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</c:if>
