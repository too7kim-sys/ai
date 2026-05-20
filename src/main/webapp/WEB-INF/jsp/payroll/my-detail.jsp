<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<title>${p.payMonth} 명세서</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-receipt"></i> ${p.payMonth} 급여명세서</h2>
    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/payroll/my/pdf.do?payMonth=${p.payMonth}"><i class="bi bi-file-pdf"></i> PDF 다운로드</a>
</div>
<div class="row g-3 mb-3">
    <div class="col-md-3"><div class="card"><div class="card-body"><div class="text-muted small">총 지급액</div><div class="h4"><fmt:formatNumber value="${p.grossPay}"/> 원</div></div></div></div>
    <div class="col-md-3"><div class="card"><div class="card-body"><div class="text-muted small">과세 지급</div><div class="h4"><fmt:formatNumber value="${p.taxablePay}"/> 원</div></div></div></div>
    <div class="col-md-3"><div class="card"><div class="card-body"><div class="text-muted small">공제 합계</div><div class="h4 text-danger"><fmt:formatNumber value="${p.deductionTotal}"/> 원</div></div></div></div>
    <div class="col-md-3"><div class="card bg-primary text-white"><div class="card-body"><div class="small">실 수령액</div><div class="h3"><fmt:formatNumber value="${p.netPay}"/> 원</div></div></div></div>
</div>
<div class="row g-3">
    <div class="col-md-6">
        <div class="card"><div class="card-header bg-light"><i class="bi bi-arrow-down-circle"></i> 지급내역</div>
        <table class="table mb-0">
            <thead><tr><th>항목</th><th class="text-end">금액</th><th class="text-center">구분</th></tr></thead>
            <tbody>
            <c:forEach var="i" items="${p.items}">
                <c:if test="${i.kindCd == 'PAYMENT'}">
                <tr>
                    <td>${i.itemNm}</td>
                    <td class="text-end"><fmt:formatNumber value="${i.amount}"/></td>
                    <td class="text-center"><span class="badge bg-${i.taxableYn == 'Y' ? 'warning text-dark' : 'success'}">${i.taxableYn == 'Y' ? '과세' : '비과세'}</span></td>
                </tr>
                </c:if>
            </c:forEach>
            </tbody>
        </table></div>
    </div>
    <div class="col-md-6">
        <div class="card"><div class="card-header bg-light"><i class="bi bi-arrow-up-circle"></i> 공제내역</div>
        <table class="table mb-0">
            <thead><tr><th>항목</th><th class="text-end">금액</th></tr></thead>
            <tbody>
            <c:forEach var="i" items="${p.items}">
                <c:if test="${i.kindCd == 'DEDUCTION'}">
                <tr><td>${i.itemNm}</td><td class="text-end"><fmt:formatNumber value="${i.amount}"/></td></tr>
                </c:if>
            </c:forEach>
            </tbody>
        </table></div>
    </div>
</div>
<c:if test="${not empty p.employerCosts}">
<div class="card mt-3">
    <div class="card-header bg-light"><i class="bi bi-info-circle"></i> 회사 부담분 (참고)</div>
    <table class="table mb-0">
        <thead><tr><th>구분</th><th class="text-end">금액</th></tr></thead>
        <tbody>
        <c:forEach var="c" items="${p.employerCosts}">
            <tr><td>${c.insuranceCd}</td><td class="text-end"><fmt:formatNumber value="${c.amount}"/></td></tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</c:if>
