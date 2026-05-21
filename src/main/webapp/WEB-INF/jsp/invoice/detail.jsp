<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${inv.invoiceNo} - 인보이스 상세</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-receipt"></i> 인보이스 상세</h2>
    <a class="btn btn-sm btn-outline-secondary"
       href="${pageContext.request.contextPath}/invoice/${inv.directionCd eq 'OUT' ? 'out' : 'in'}.do">
        <i class="bi bi-arrow-left"></i> 목록
    </a>
</div>

<div class="card mb-3">
    <div class="card-header d-flex justify-content-between">
        <div>
            <code>${inv.invoiceNo}</code>
            <span class="badge bg-secondary ms-2">${inv.directionCd eq 'OUT' ? '매출 (발행)' : '매입 (수령)'}</span>
            <c:choose>
                <c:when test="${inv.statusCd eq 'DRAFT'}"><span class="badge bg-secondary ms-1">작성중</span></c:when>
                <c:when test="${inv.statusCd eq 'ISSUED'}"><span class="badge bg-info text-dark ms-1">발행</span></c:when>
                <c:when test="${inv.statusCd eq 'PARTIALLY_PAID'}"><span class="badge bg-warning text-dark ms-1">일부 결제</span></c:when>
                <c:when test="${inv.statusCd eq 'PAID'}"><span class="badge bg-success ms-1">완납</span></c:when>
                <c:when test="${inv.statusCd eq 'OVERDUE'}"><span class="badge bg-danger ms-1">연체</span></c:when>
                <c:when test="${inv.statusCd eq 'CANCELED'}"><span class="badge bg-light text-dark ms-1">취소</span></c:when>
            </c:choose>
        </div>
        <div class="d-flex gap-2">
            <c:if test="${inv.statusCd eq 'DRAFT'}">
                <form method="post" action="${pageContext.request.contextPath}/invoice/issue.do" class="d-inline">
                    <sec:csrfInput/><input type="hidden" name="invoiceId" value="${inv.invoiceId}"/>
                    <button class="btn btn-sm btn-success"><i class="bi bi-send"></i> 발행</button>
                </form>
                <a class="btn btn-sm btn-outline-primary"
                   href="${pageContext.request.contextPath}/invoice/edit.do?invoiceId=${inv.invoiceId}">
                    <i class="bi bi-pencil"></i> 수정
                </a>
            </c:if>
            <c:if test="${inv.remainingAmount > 0 and inv.statusCd ne 'CANCELED' and inv.statusCd ne 'DRAFT'}">
                <a class="btn btn-sm btn-primary"
                   href="${pageContext.request.contextPath}/payment/edit.do?invoiceId=${inv.invoiceId}">
                    <i class="bi bi-bank"></i> ${inv.directionCd eq 'OUT' ? '입금' : '지급'} 등록
                </a>
            </c:if>
            <c:if test="${inv.statusCd ne 'CANCELED' and inv.statusCd ne 'PAID'}">
                <form method="post" action="${pageContext.request.contextPath}/invoice/cancel.do" class="d-inline"
                      onsubmit="return confirm('인보이스를 취소할까요?')">
                    <sec:csrfInput/><input type="hidden" name="invoiceId" value="${inv.invoiceId}"/>
                    <button class="btn btn-sm btn-outline-danger"><i class="bi bi-x-circle"></i> 취소</button>
                </form>
            </c:if>
        </div>
    </div>
    <div class="card-body">
        <table class="table table-sm mb-0">
            <tr><th class="bg-light" style="width:160px;">거래처</th><td>${inv.vendorNm}</td>
                <th class="bg-light" style="width:160px;">담당자</th><td>${inv.ownerName}</td></tr>
            <tr><th class="bg-light">발행일</th><td>${inv.issueDt}</td>
                <th class="bg-light">만기일</th><td>${inv.dueDt}</td></tr>
            <tr><th class="bg-light">연결 계약</th>
                <td><c:if test="${not empty inv.bizContractId}">
                    <a href="${pageContext.request.contextPath}/biz-contract/detail.do?bizContractId=${inv.bizContractId}">${inv.contractTitle}</a>
                </c:if></td>
                <th class="bg-light">세금계산서</th><td>${inv.taxInvoiceYn eq 'Y' ? '발행 완료' : '미발행'}</td></tr>
            <tr><th class="bg-light">공급가액</th><td class="text-end"><fmt:formatNumber value="${inv.amountNet}" type="number"/></td>
                <th class="bg-light">VAT</th><td class="text-end"><fmt:formatNumber value="${inv.vatAmount}" type="number"/></td></tr>
            <tr><th class="bg-light">총액</th><td class="text-end fw-bold"><fmt:formatNumber value="${inv.amountTotal}" type="number"/></td>
                <th class="bg-light">${inv.directionCd eq 'OUT' ? '수금' : '지급'}</th><td class="text-end text-success"><fmt:formatNumber value="${inv.paidAmount}" type="number"/></td></tr>
            <tr><th class="bg-light">잔액</th><td class="text-end fw-bold text-danger"><fmt:formatNumber value="${inv.remainingAmount}" type="number"/></td>
                <th class="bg-light"></th><td></td></tr>
            <c:if test="${not empty inv.memo}">
                <tr><th class="bg-light">메모</th><td colspan="3" style="white-space: pre-wrap;">${inv.memo}</td></tr>
            </c:if>
        </table>
    </div>
</div>
