<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>입출금 이력</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-bank"></i> 입출금 이력</h2>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/payment/edit.do">
        <i class="bi bi-plus"></i> 거래 등록
    </a>
</div>

<div class="row g-3 mb-3">
    <div class="col-md-4">
        <div class="card shadow-sm text-center"><div class="card-body">
            <div class="text-muted small">총 입금 (INCOMING)</div>
            <div class="h4 mb-0 text-success"><fmt:formatNumber value="${sumIncoming.total_amount}" type="number"/> 원</div>
            <small class="text-muted">${sumIncoming.cnt} 건</small>
        </div></div>
    </div>
    <div class="col-md-4">
        <div class="card shadow-sm text-center"><div class="card-body">
            <div class="text-muted small">총 출금 (OUTGOING)</div>
            <div class="h4 mb-0 text-danger"><fmt:formatNumber value="${sumOutgoing.total_amount}" type="number"/> 원</div>
            <small class="text-muted">${sumOutgoing.cnt} 건</small>
        </div></div>
    </div>
    <div class="col-md-4">
        <div class="card shadow-sm text-center"><div class="card-body">
            <div class="text-muted small">순현금흐름</div>
            <div class="h4 mb-0"><fmt:formatNumber value="${sumIncoming.total_amount - sumOutgoing.total_amount}" type="number"/> 원</div>
        </div></div>
    </div>
</div>

<form class="row g-2 mb-3" method="get">
    <div class="col-md-2">
        <select name="payTypeCd" class="form-select">
            <option value="">전체</option>
            <option value="INCOMING" <c:if test="${payTypeCd eq 'INCOMING'}">selected</c:if>>입금</option>
            <option value="OUTGOING" <c:if test="${payTypeCd eq 'OUTGOING'}">selected</c:if>>출금</option>
        </select>
    </div>
    <div class="col-md-3"><input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="거래처/메모/계좌 검색"/></div>
    <div class="col-md-2"><input type="date" name="fromDt" value="${fromDt}" class="form-control"/></div>
    <div class="col-md-2"><input type="date" name="toDt" value="${toDt}" class="form-control"/></div>
    <div class="col-md-1"><button class="btn btn-outline-primary w-100">조회</button></div>
    <div class="col-md-2 text-end text-muted small align-self-center">총 ${paging.total}건</div>
</form>

<div class="card">
    <table class="table table-hover mb-0">
        <thead class="table-light">
        <tr><th>유형</th><th>일자</th><th class="text-end">금액</th><th>방법</th><th>은행/계좌</th><th>거래 상대</th><th>메모</th><th>인보이스</th><th></th></tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${list}">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${p.payTypeCd eq 'INCOMING'}"><span class="badge bg-success">입금</span></c:when>
                        <c:when test="${p.payTypeCd eq 'OUTGOING'}"><span class="badge bg-danger">출금</span></c:when>
                        <c:otherwise><span class="badge bg-light text-dark">${p.payTypeCd}</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="small">${p.payDt}</td>
                <td class="text-end fw-bold"><fmt:formatNumber value="${p.amount}" type="number"/></td>
                <td class="small">${p.methodCd}</td>
                <td class="small">${p.bankCd} ${p.bankAccount}</td>
                <td class="small">${p.counterpartNm}</td>
                <td class="small text-muted">${p.memo}</td>
                <td class="small">
                    <c:if test="${not empty p.invoiceId}">
                        <a href="${pageContext.request.contextPath}/invoice/detail.do?invoiceId=${p.invoiceId}">#${p.invoiceId}</a>
                    </c:if>
                </td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/payment/delete.do" class="d-inline"
                          onsubmit="return confirm('거래 기록을 삭제하면 연결된 인보이스 잔액도 복구됩니다. 계속할까요?')">
                        <sec:csrfInput/><input type="hidden" name="paymentId" value="${p.paymentId}"/>
                        <button class="btn btn-sm btn-link text-danger p-0">삭제</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="9" class="text-center text-muted py-4">거래 기록이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<c:if test="${paging.totalPages > 1}">
<nav class="mt-3"><ul class="pagination justify-content-center">
    <c:forEach var="i" begin="1" end="${paging.totalPages}">
        <li class="page-item <c:if test='${i == paging.page}'>active</c:if>">
            <a class="page-link" href="?page=${i}&payTypeCd=${payTypeCd}&keyword=${keyword}&fromDt=${fromDt}&toDt=${toDt}">${i}</a>
        </li>
    </c:forEach>
</ul></nav>
</c:if>
