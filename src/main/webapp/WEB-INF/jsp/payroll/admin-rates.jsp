<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<title>4대보험 요율</title>
<h2 class="mb-3"><i class="bi bi-percent"></i> 4대보험 요율</h2>
<div class="card"><table class="table mb-0">
    <thead class="table-light"><tr><th>구분</th><th>시행일</th><th class="text-end">근로자</th><th class="text-end">회사</th><th class="text-end">하한</th><th class="text-end">상한</th><th>비고</th></tr></thead>
    <tbody>
    <c:forEach var="r" items="${rates}">
        <tr>
            <td><strong>${r.insuranceCd}</strong></td>
            <td>${r.effectiveFrom}</td>
            <td class="text-end"><fmt:formatNumber value="${r.employeeRate * 100}" maxFractionDigits="3"/>%</td>
            <td class="text-end"><fmt:formatNumber value="${r.employerRate * 100}" maxFractionDigits="3"/>%</td>
            <td class="text-end"><c:if test="${r.baseMin != null}"><fmt:formatNumber value="${r.baseMin}"/></c:if></td>
            <td class="text-end"><c:if test="${r.baseMax != null}"><fmt:formatNumber value="${r.baseMax}"/></c:if></td>
            <td>${r.note}</td>
        </tr>
    </c:forEach>
    </tbody>
</table></div>
