<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>내 KPI</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-bullseye"></i> 내 KPI / 성과</h2>
    <form method="get" action="${pageContext.request.contextPath}/performance/my.do" class="d-flex">
        <select name="periodId" class="form-select form-select-sm" onchange="this.form.submit()">
            <c:forEach var="p" items="${periods}">
                <option value="${p.periodId}" <c:if test="${period.periodId == p.periodId}">selected</c:if>>${p.periodNm}</option>
            </c:forEach>
        </select>
    </form>
</div>

<c:if test="${empty period}">
    <div class="alert alert-warning"><i class="bi bi-exclamation-triangle"></i> 평가 기간이 등록되지 않았습니다. HR 관리자에게 문의하세요.</div>
</c:if>

<c:if test="${not empty period}">
<div class="row g-3 mb-3">
    <div class="col-md-4">
        <div class="card text-center">
            <div class="card-body">
                <div class="text-muted small">현재 평가 기간</div>
                <div class="h4">${period.periodNm}</div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card text-center">
            <div class="card-body">
                <div class="text-muted small">등록된 KPI</div>
                <div class="display-6 text-primary">${fn:length(items)}</div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card text-center">
            <div class="card-body">
                <div class="text-muted small">가중 평균 달성률</div>
                <div class="display-6 text-success">${avgAch}%</div>
            </div>
        </div>
    </div>
</div>

<div class="card mb-3">
    <div class="card-header"><i class="bi bi-plus-circle"></i> KPI 추가</div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/performance/save.do" class="row g-2">
            <sec:csrfInput/>
            <input type="hidden" name="periodId" value="${period.periodId}"/>
            <div class="col-md-4"><input type="text" name="goal" class="form-control" placeholder="목표 (예: 신규 고객 확보)" required/></div>
            <div class="col-md-3"><input type="text" name="kpi" class="form-control" placeholder="KPI 지표 (예: 신규 계약 건수)" required/></div>
            <div class="col-md-1"><input type="text" name="unit" class="form-control" placeholder="단위"/></div>
            <div class="col-md-1"><input type="number" step="0.01" name="targetVal" class="form-control" placeholder="목표"/></div>
            <div class="col-md-1"><input type="number" step="0.01" name="actualVal" class="form-control" placeholder="실적"/></div>
            <div class="col-md-1"><input type="number" step="0.01" name="weight" class="form-control" placeholder="가중치" value="100"/></div>
            <div class="col-md-1 d-grid"><button class="btn btn-primary">추가</button></div>
        </form>
    </div>
</div>

<div class="card">
    <table class="table mb-0">
        <thead class="table-light">
        <tr><th>목표</th><th>KPI</th><th>단위</th><th>목표</th><th>실적</th><th>달성률</th><th>가중치</th><th></th></tr>
        </thead>
        <tbody>
        <c:forEach var="i" items="${items}">
            <tr>
                <td>${i.goal}</td>
                <td>${i.kpi}</td>
                <td>${i.unit}</td>
                <td>${i.targetVal}</td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/performance/save.do" class="d-flex gap-1">
                        <sec:csrfInput/>
                        <input type="hidden" name="perfId" value="${i.perfId}"/>
                        <input type="hidden" name="periodId" value="${period.periodId}"/>
                        <input type="hidden" name="goal" value="${i.goal}"/>
                        <input type="hidden" name="kpi" value="${i.kpi}"/>
                        <input type="hidden" name="unit" value="${i.unit}"/>
                        <input type="hidden" name="targetVal" value="${i.targetVal}"/>
                        <input type="hidden" name="weight" value="${i.weight}"/>
                        <input type="number" step="0.01" name="actualVal" value="${i.actualVal}"
                               class="form-control form-control-sm" style="width:90px;"/>
                        <button class="btn btn-sm btn-outline-success">저장</button>
                    </form>
                </td>
                <td>
                    <c:if test="${not empty i.achRate}">
                        <strong class="${i.achRate >= 100 ? 'text-success' : (i.achRate >= 80 ? 'text-primary' : 'text-warning')}">${i.achRate}%</strong>
                    </c:if>
                </td>
                <td>${i.weight}</td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/performance/delete.do" class="d-inline"
                          onsubmit="return confirm('삭제하시겠습니까?')">
                        <sec:csrfInput/>
                        <input type="hidden" name="perfId" value="${i.perfId}"/>
                        <input type="hidden" name="periodId" value="${period.periodId}"/>
                        <button class="btn btn-sm btn-outline-danger">삭제</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty items}">
            <tr><td colspan="8" class="text-center text-muted py-4">등록된 KPI가 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
</c:if>
