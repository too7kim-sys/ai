<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>평가 결과</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-table"></i> 평가 결과</h2>
    <form method="get" action="${pageContext.request.contextPath}/evaluation/admin/results.do" class="d-flex">
        <select name="periodId" class="form-select form-select-sm" onchange="this.form.submit()">
            <c:forEach var="p" items="${periods}">
                <option value="${p.periodId}" <c:if test="${period.periodId == p.periodId}">selected</c:if>>${p.periodNm}</option>
            </c:forEach>
        </select>
    </form>
</div>

<c:if test="${not empty period}">
<div class="alert alert-secondary small">
    <strong>${period.periodNm}</strong> · ${period.startDt} ~ ${period.endDt} ·
    상태 <span class="badge bg-info">${period.statusCd}</span>
</div>
</c:if>

<div class="card">
    <table class="table mb-0">
        <thead class="table-light">
        <tr><th>피평가자</th><th>부서</th><th>평가자</th><th>점수</th><th>등급</th><th>코멘트</th></tr>
        </thead>
        <tbody>
        <c:forEach var="e" items="${results}">
            <tr>
                <td><strong>${e.evaluateeName}</strong></td>
                <td class="small text-muted">${e.evaluateeDept}</td>
                <td>${e.evaluatorName}</td>
                <td>${e.finalScore}</td>
                <td>
                    <c:choose>
                        <c:when test="${e.gradeCd eq 'S'}"><span class="badge bg-success">S</span></c:when>
                        <c:when test="${e.gradeCd eq 'A'}"><span class="badge bg-primary">A</span></c:when>
                        <c:when test="${e.gradeCd eq 'B'}"><span class="badge bg-info">B</span></c:when>
                        <c:when test="${e.gradeCd eq 'C'}"><span class="badge bg-warning text-dark">C</span></c:when>
                        <c:otherwise><span class="badge bg-danger">${e.gradeCd}</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="small text-muted">${e.comment}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty results}">
            <tr><td colspan="6" class="text-center text-muted py-4">결과가 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
