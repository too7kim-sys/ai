<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${p.payMonth} - ${p.userName}</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-pencil-square"></i> ${p.payMonth} - ${p.userName} (${p.deptNm})</h2>
    <div>
        <c:if test="${p.statusCd == 'DRAFT'}">
            <form method="post" action="${pageContext.request.contextPath}/payroll/admin/confirm.do" class="d-inline">
                <sec:csrfInput/><input type="hidden" name="payId" value="${p.payId}"/>
                <button class="btn btn-info"><i class="bi bi-check2"></i> 확정</button>
            </form>
        </c:if>
        <c:if test="${p.statusCd == 'CONFIRMED'}">
            <form method="post" action="${pageContext.request.contextPath}/payroll/admin/pay.do" class="d-inline">
                <sec:csrfInput/><input type="hidden" name="payId" value="${p.payId}"/>
                <button class="btn btn-success"><i class="bi bi-bank"></i> 지급 처리</button>
            </form>
        </c:if>
    </div>
</div>
<div class="alert alert-secondary small">
    상태: <strong>${p.statusCd}</strong>
    | 총지급 <strong><fmt:formatNumber value="${p.grossPay}"/></strong>
    | 공제 <strong><fmt:formatNumber value="${p.deductionTotal}"/></strong>
    | 실수령 <strong class="text-primary"><fmt:formatNumber value="${p.netPay}"/></strong>
    <c:if test="${p.paidDt != null}"> | 지급일: ${p.paidDt}</c:if>
</div>
<form method="post" action="${pageContext.request.contextPath}/payroll/admin/recalculate.do" class="row g-3">
    <sec:csrfInput/>
    <input type="hidden" name="payId" value="${p.payId}"/>
    <div class="col-md-6">
        <div class="card"><div class="card-header bg-light"><i class="bi bi-arrow-down-circle"></i> 수동 지급 항목 추가 (코드 = 금액)</div>
        <div class="card-body">
            <div class="row g-2">
                <div class="col-6"><label class="form-label small">직책수당 (POSITION_ALLOW)</label><input class="form-control" name="m_POSITION_ALLOW" placeholder="0"/></div>
                <div class="col-6"><label class="form-label small">식대 (MEAL) - 20만 비과세</label><input class="form-control" name="m_MEAL" placeholder="0"/></div>
                <div class="col-6"><label class="form-label small">자가운전 (VEHICLE) - 20만 비과세</label><input class="form-control" name="m_VEHICLE" placeholder="0"/></div>
                <div class="col-6"><label class="form-label small">출산보육 (CHILDCARE)</label><input class="form-control" name="m_CHILDCARE" placeholder="0"/></div>
                <div class="col-6"><label class="form-label small">명절상여 (HOLIDAY_BONUS)</label><input class="form-control" name="m_HOLIDAY_BONUS" placeholder="0"/></div>
                <div class="col-6"><label class="form-label small">성과급 (PERFORMANCE_BONUS)</label><input class="form-control" name="m_PERFORMANCE_BONUS" placeholder="0"/></div>
            </div>
            <button class="btn btn-primary mt-3"><i class="bi bi-arrow-clockwise"></i> 자동 재계산</button>
        </div></div>
    </div>
    <div class="col-md-6">
        <div class="card"><div class="card-header bg-light">현재 명세 라인</div>
        <table class="table mb-0 small">
            <thead><tr><th>구분</th><th>항목</th><th class="text-end">금액</th><th>비고</th></tr></thead>
            <tbody>
            <c:forEach var="i" items="${p.items}">
                <tr>
                    <td><span class="badge bg-${i.kindCd == 'PAYMENT' ? 'primary' : 'danger'}">${i.kindCd}</span></td>
                    <td>${i.itemNm} <small class="text-muted">${i.codeVal}</small></td>
                    <td class="text-end"><fmt:formatNumber value="${i.amount}"/></td>
                    <td><c:if test="${i.taxableYn == 'N'}"><span class="badge bg-success">비과세</span></c:if></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        </div>
        <div class="card mt-2"><div class="card-header bg-light small">회사 부담분</div>
        <table class="table mb-0 small">
            <c:forEach var="c" items="${p.employerCosts}">
                <tr><td width="80">${c.insuranceCd}</td><td class="text-end"><fmt:formatNumber value="${c.amount}"/></td></tr>
            </c:forEach>
        </table>
        </div>
    </div>
</form>
