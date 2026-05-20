<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<title>내 급여명세서</title>
<h2 class="mb-4"><i class="bi bi-cash"></i> 내 급여명세서</h2>
<div class="card shadow-sm">
    <table class="table table-hover mb-0">
        <thead class="table-light">
        <tr><th>지급월</th><th class="text-end">총 지급액</th><th class="text-end">공제</th><th class="text-end">실 수령</th><th>상태</th><th>지급일</th><th></th></tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${list}">
            <tr>
                <td>${p.payMonth}</td>
                <td class="text-end"><fmt:formatNumber value="${p.grossPay}"/></td>
                <td class="text-end"><fmt:formatNumber value="${p.deductionTotal}"/></td>
                <td class="text-end fw-bold text-primary"><fmt:formatNumber value="${p.netPay}"/></td>
                <td><span class="badge bg-${p.statusCd == 'PAID' ? 'success' : 'secondary'}">${p.statusCd}</span></td>
                <td><c:if test="${p.paidDt != null}">${p.paidDt}</c:if></td>
                <td>
                    <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/payroll/my/detail.do?payMonth=${p.payMonth}">상세</a>
                    <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/payroll/my/pdf.do?payMonth=${p.payMonth}">PDF</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="7" class="text-center text-muted py-4">발행된 명세서가 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
