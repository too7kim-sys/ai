<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>인보이스 - ${direction eq 'OUT' ? '발행' : '수령'}</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-receipt"></i> 인보이스 -
        <c:choose>
            <c:when test="${direction eq 'OUT'}">매출/발행</c:when>
            <c:otherwise>매입/수령</c:otherwise>
        </c:choose>
    </h2>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/invoice/edit.do?direction=${direction}">
        <i class="bi bi-plus"></i> 새 인보이스
    </a>
</div>

<ul class="nav nav-tabs mb-3">
    <li class="nav-item"><a class="nav-link <c:if test='${direction eq "OUT"}'>active</c:if>"
                            href="${pageContext.request.contextPath}/invoice/out.do">매출 (발행)</a></li>
    <li class="nav-item"><a class="nav-link <c:if test='${direction eq "IN"}'>active</c:if>"
                            href="${pageContext.request.contextPath}/invoice/in.do">매입 (수령)</a></li>
</ul>

<div class="row g-3 mb-3">
    <div class="col-md-3">
        <div class="card shadow-sm text-center"><div class="card-body">
            <div class="text-muted small">총 ${direction eq 'OUT' ? '매출' : '매입'}</div>
            <div class="h4 mb-0"><fmt:formatNumber value="${summary.total_amount}" type="number"/> 원</div>
            <small class="text-muted">${summary.cnt} 건</small>
        </div></div>
    </div>
    <div class="col-md-3">
        <div class="card shadow-sm text-center"><div class="card-body">
            <div class="text-muted small">${direction eq 'OUT' ? '수금' : '지급'} 완료</div>
            <div class="h4 mb-0 text-success"><fmt:formatNumber value="${summary.paid_amount}" type="number"/> 원</div>
        </div></div>
    </div>
    <div class="col-md-3">
        <div class="card shadow-sm text-center"><div class="card-body">
            <div class="text-muted small">${direction eq 'OUT' ? '미수금' : '미지급'}</div>
            <div class="h4 mb-0 text-danger"><fmt:formatNumber value="${outstanding.remaining_amount}" type="number"/> 원</div>
            <small class="text-muted">${outstanding.cnt} 건</small>
        </div></div>
    </div>
</div>

<form class="row g-2 mb-3" method="get">
    <div class="col-md-4"><input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="인보이스 번호/거래처"/></div>
    <div class="col-md-2">
        <select name="statusCd" class="form-select">
            <option value="">전체 상태</option>
            <option value="DRAFT"           <c:if test="${statusCd eq 'DRAFT'}">selected</c:if>>작성중</option>
            <option value="ISSUED"          <c:if test="${statusCd eq 'ISSUED'}">selected</c:if>>발행</option>
            <option value="PARTIALLY_PAID"  <c:if test="${statusCd eq 'PARTIALLY_PAID'}">selected</c:if>>일부 결제</option>
            <option value="PAID"            <c:if test="${statusCd eq 'PAID'}">selected</c:if>>완납</option>
            <option value="OVERDUE"         <c:if test="${statusCd eq 'OVERDUE'}">selected</c:if>>연체</option>
            <option value="CANCELED"        <c:if test="${statusCd eq 'CANCELED'}">selected</c:if>>취소</option>
        </select>
    </div>
    <div class="col-md-2"><button class="btn btn-outline-primary"><i class="bi bi-search"></i> 검색</button></div>
    <div class="col-md-4 text-end text-muted small align-self-center">총 ${paging.total}건</div>
</form>

<div class="card">
    <table class="table table-hover mb-0">
        <thead class="table-light">
        <tr><th>번호</th><th>거래처</th><th>발행일</th><th>만기일</th>
            <th class="text-end">총액</th><th class="text-end">${direction eq 'OUT' ? '입금' : '지급'}</th>
            <th class="text-end">잔액</th><th>상태</th><th>세금계산서</th></tr>
        </thead>
        <tbody>
        <c:forEach var="i" items="${list}">
            <tr>
                <td class="small"><a href="${pageContext.request.contextPath}/invoice/detail.do?invoiceId=${i.invoiceId}"><code>${i.invoiceNo}</code></a></td>
                <td>${i.vendorNm}</td>
                <td class="small">${i.issueDt}</td>
                <td class="small">${i.dueDt}</td>
                <td class="text-end"><fmt:formatNumber value="${i.amountTotal}" type="number"/></td>
                <td class="text-end text-success"><fmt:formatNumber value="${i.paidAmount}" type="number"/></td>
                <td class="text-end fw-bold"><fmt:formatNumber value="${i.remainingAmount}" type="number"/></td>
                <td>
                    <c:choose>
                        <c:when test="${i.statusCd eq 'DRAFT'}"><span class="badge bg-secondary">작성중</span></c:when>
                        <c:when test="${i.statusCd eq 'ISSUED'}"><span class="badge bg-info text-dark">발행</span></c:when>
                        <c:when test="${i.statusCd eq 'PARTIALLY_PAID'}"><span class="badge bg-warning text-dark">일부</span></c:when>
                        <c:when test="${i.statusCd eq 'PAID'}"><span class="badge bg-success">완납</span></c:when>
                        <c:when test="${i.statusCd eq 'OVERDUE'}"><span class="badge bg-danger">연체</span></c:when>
                        <c:when test="${i.statusCd eq 'CANCELED'}"><span class="badge bg-light text-dark">취소</span></c:when>
                    </c:choose>
                </td>
                <td><c:if test="${i.taxInvoiceYn eq 'Y'}"><i class="bi bi-check-circle text-success"></i></c:if></td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="9" class="text-center text-muted py-4">인보이스가 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<c:if test="${paging.totalPages > 1}">
<nav class="mt-3"><ul class="pagination justify-content-center">
    <c:forEach var="i" begin="1" end="${paging.totalPages}">
        <li class="page-item <c:if test='${i == paging.page}'>active</c:if>">
            <a class="page-link" href="?page=${i}&keyword=${keyword}&statusCd=${statusCd}">${i}</a>
        </li>
    </c:forEach>
</ul></nav>
</c:if>
