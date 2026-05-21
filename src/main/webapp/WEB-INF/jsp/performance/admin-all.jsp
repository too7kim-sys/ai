<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<title>전사 KPI</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-bar-chart"></i> 전사 KPI 현황</h2>
    <form method="get" action="${pageContext.request.contextPath}/performance/admin/all.do" class="d-flex">
        <select name="periodId" class="form-select form-select-sm" onchange="this.form.submit()">
            <c:forEach var="p" items="${periods}">
                <option value="${p.periodId}" <c:if test="${period.periodId == p.periodId}">selected</c:if>>${p.periodNm}</option>
            </c:forEach>
        </select>
    </form>
</div>

<div class="row g-3 mb-3">
    <div class="col-md-6">
        <div class="card text-center"><div class="card-body">
            <div class="text-muted small">총 KPI 건수</div>
            <div class="display-6 text-primary">${fn:length(items)}</div>
        </div></div>
    </div>
    <div class="col-md-6">
        <div class="card text-center"><div class="card-body">
            <div class="text-muted small">전사 가중 평균 달성률</div>
            <div class="display-6 text-success">${avgAch}%</div>
        </div></div>
    </div>
</div>

<div class="card">
    <table class="table mb-0">
        <thead class="table-light">
        <tr><th>부서</th><th>구성원</th><th>목표</th><th>KPI</th><th>달성률</th><th>가중치</th></tr>
        </thead>
        <tbody>
        <c:forEach var="i" items="${items}">
            <tr>
                <td>${i.deptNm}</td>
                <td>${i.userName}</td>
                <td>${i.goal}</td>
                <td>${i.kpi}</td>
                <td>
                    <c:if test="${not empty i.achRate}">
                        <strong class="${i.achRate >= 100 ? 'text-success' : (i.achRate >= 80 ? 'text-primary' : 'text-warning')}">${i.achRate}%</strong>
                    </c:if>
                </td>
                <td>${i.weight}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty items}">
            <tr><td colspan="6" class="text-center text-muted py-4">KPI 데이터가 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
