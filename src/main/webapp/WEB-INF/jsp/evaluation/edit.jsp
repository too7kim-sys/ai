<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>평가 작성</title>
<h2 class="mb-3"><i class="bi bi-pencil-square"></i> 평가 작성</h2>

<div class="alert alert-info">
    <strong>${eval.evaluateeName != null ? eval.evaluateeName : eval.evaluateeId}</strong>
    (${eval.evaluateeDept}) · 평가 기간 <strong>${period.periodNm}</strong>
    <span class="badge bg-secondary ms-2">${period.statusCd}</span>
</div>

<form method="post" action="${pageContext.request.contextPath}/evaluation/save.do">
    <sec:csrfInput/>
    <input type="hidden" name="periodId" value="${period.periodId}"/>
    <input type="hidden" name="evaluateeId" value="${eval.evaluateeId}"/>

    <div class="card mb-3">
        <div class="card-header"><i class="bi bi-list-check"></i> 평가 항목 (1~5점)</div>
        <table class="table mb-0">
            <thead class="table-light"><tr><th>항목</th><th style="width:120px;">가중치</th><th style="width:200px;">점수</th></tr></thead>
            <tbody>
            <c:choose>
                <c:when test="${not empty items}">
                    <c:forEach var="it" items="${items}">
                        <tr>
                            <td>${it.l}</td>
                            <td><input type="number" name="w_${it.k}" value="${it.w != null ? it.w : 10}"
                                       min="1" max="100" class="form-control form-control-sm"/></td>
                            <td>
                                <select name="s_${it.k}" class="form-select form-select-sm">
                                    <option value="5">5 — 매우 우수</option>
                                    <option value="4">4 — 우수</option>
                                    <option value="3" selected>3 — 보통</option>
                                    <option value="2">2 — 미흡</option>
                                    <option value="1">1 — 매우 미흡</option>
                                </select>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <c:forTokens var="row" items="communication:커뮤니케이션,ownership:주인의식,expertise:전문성,collaboration:협업,growth:성장 의지" delims=",">
                        <c:set var="pos" value="${fn:indexOf(row, ':')}"/>
                        <c:set var="k" value="${fn:substring(row, 0, pos)}"/>
                        <c:set var="label" value="${fn:substring(row, pos + 1, -1)}"/>
                        <tr>
                            <td>${label}</td>
                            <td><input type="number" name="w_${k}" value="20" min="1" max="100" class="form-control form-control-sm"/></td>
                            <td>
                                <select name="s_${k}" class="form-select form-select-sm">
                                    <option value="5">5 — 매우 우수</option>
                                    <option value="4">4 — 우수</option>
                                    <option value="3" selected>3 — 보통</option>
                                    <option value="2">2 — 미흡</option>
                                    <option value="1">1 — 매우 미흡</option>
                                </select>
                            </td>
                        </tr>
                    </c:forTokens>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>

    <div class="card mb-3">
        <div class="card-header"><i class="bi bi-chat-left-text"></i> 종합 코멘트</div>
        <div class="card-body">
            <textarea name="comment" rows="4" class="form-control">${eval.comment}</textarea>
        </div>
    </div>

    <button class="btn btn-primary"><i class="bi bi-save"></i> 저장</button>
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/evaluation/sheet.do?periodId=${period.periodId}">취소</a>
</form>
