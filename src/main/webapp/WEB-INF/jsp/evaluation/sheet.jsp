<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>평가표</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-graph-up"></i> 평가</h2>
    <form class="d-flex" method="get" action="${pageContext.request.contextPath}/evaluation/sheet.do">
        <select name="periodId" class="form-select form-select-sm" onchange="this.form.submit()">
            <c:forEach var="p" items="${periods}">
                <option value="${p.periodId}" <c:if test="${period.periodId == p.periodId}">selected</c:if>>
                    ${p.periodNm} (${p.statusCd})
                </option>
            </c:forEach>
        </select>
    </form>
</div>

<div class="alert alert-secondary small">
    <strong>${period.periodNm}</strong> · ${period.startDt} ~ ${period.endDt} ·
    상태: <span class="badge bg-info">${period.statusCd}</span>
</div>

<div class="row g-3">
    <div class="col-md-7">
        <div class="card">
            <div class="card-header"><i class="bi bi-pencil-square"></i> 내가 평가할 대상</div>
            <table class="table mb-0">
                <thead class="table-light">
                <tr><th>대상자</th><th>부서</th><th>점수</th><th>등급</th><th></th></tr>
                </thead>
                <tbody>
                <c:forEach var="e" items="${evaluations}">
                    <tr>
                        <td><strong>${e.evaluateeName}</strong></td>
                        <td class="small text-muted">${e.evaluateeDept}</td>
                        <td><c:if test="${not empty e.finalScore}">${e.finalScore}</c:if></td>
                        <td>
                            <c:if test="${not empty e.gradeCd}">
                                <span class="badge bg-primary">${e.gradeCd}</span>
                            </c:if>
                        </td>
                        <td>
                            <a class="btn btn-sm btn-outline-primary"
                               href="${pageContext.request.contextPath}/evaluation/edit.do?periodId=${period.periodId}&evaluateeId=${e.evaluateeId}">
                                <c:choose>
                                    <c:when test="${empty e.evalId}">평가하기</c:when>
                                    <c:otherwise>수정</c:otherwise>
                                </c:choose>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty evaluations}">
                    <tr><td colspan="5" class="text-center text-muted py-4">평가할 대상이 없습니다.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <div class="col-md-5">
        <div class="card">
            <div class="card-header"><i class="bi bi-person-check"></i> 내가 받은 평가</div>
            <table class="table table-sm mb-0">
                <thead class="table-light"><tr><th>기간</th><th>평가자</th><th>점수</th><th>등급</th></tr></thead>
                <tbody>
                <c:forEach var="r" items="${myResults}">
                    <tr>
                        <td class="small">${r.periodNm}</td>
                        <td>${r.evaluatorName}</td>
                        <td>${r.finalScore}</td>
                        <td><span class="badge bg-primary">${r.gradeCd}</span></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty myResults}">
                    <tr><td colspan="4" class="text-center text-muted py-4">받은 평가 없음</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
