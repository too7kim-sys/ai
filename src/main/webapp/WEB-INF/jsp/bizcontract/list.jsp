<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>사업자 계약</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-briefcase"></i> 사업자 계약</h2>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/biz-contract/edit.do">
        <i class="bi bi-plus"></i> 새 계약
    </a>
</div>

<c:if test="${not empty expiringSoon}">
    <div class="alert alert-warning">
        <i class="bi bi-exclamation-triangle"></i>
        <strong>30일 내 만료 예정 계약 ${fn:length(expiringSoon)}건</strong>
        <ul class="mb-0 mt-2">
            <c:forEach var="e" items="${expiringSoon}">
                <li><a href="${pageContext.request.contextPath}/biz-contract/detail.do?bizContractId=${e.bizContractId}">${e.title}</a>
                    · ${e.vendorNm} · 만료일 ${e.endDt}</li>
            </c:forEach>
        </ul>
    </div>
</c:if>

<form class="row g-2 mb-3" method="get">
    <div class="col-md-4"><input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="계약명/계약번호/거래처 검색"/></div>
    <div class="col-md-2">
        <select name="statusCd" class="form-select">
            <option value="">전체 상태</option>
            <option value="DRAFT"      <c:if test="${statusCd eq 'DRAFT'}">selected</c:if>>작성중</option>
            <option value="ACTIVE"     <c:if test="${statusCd eq 'ACTIVE'}">selected</c:if>>유효</option>
            <option value="TERMINATED" <c:if test="${statusCd eq 'TERMINATED'}">selected</c:if>>해지</option>
            <option value="EXPIRED"    <c:if test="${statusCd eq 'EXPIRED'}">selected</c:if>>만료</option>
        </select>
    </div>
    <div class="col-md-2"><button class="btn btn-outline-primary"><i class="bi bi-search"></i> 검색</button></div>
    <div class="col-md-4 text-end text-muted small align-self-center">총 ${paging.total}건</div>
</form>

<div class="card">
    <table class="table table-hover mb-0">
        <thead class="table-light">
        <tr><th>계약번호</th><th>제목</th><th>거래처</th><th>기간</th><th class="text-end">총액</th><th>상태</th></tr>
        </thead>
        <tbody>
        <c:forEach var="c" items="${list}">
            <tr>
                <td class="small"><code>${c.contractNo}</code></td>
                <td><a class="text-decoration-none" href="${pageContext.request.contextPath}/biz-contract/detail.do?bizContractId=${c.bizContractId}">${c.title}</a></td>
                <td class="small">${c.vendorNm}</td>
                <td class="small text-muted">${c.startDt} ~ ${empty c.endDt ? '무기한' : c.endDt}</td>
                <td class="text-end"><fmt:formatNumber value="${c.amountTotal}" type="number"/> ${c.currencyCd}</td>
                <td>
                    <c:choose>
                        <c:when test="${c.statusCd eq 'DRAFT'}"><span class="badge bg-secondary">작성중</span></c:when>
                        <c:when test="${c.statusCd eq 'ACTIVE'}"><span class="badge bg-success">유효</span></c:when>
                        <c:when test="${c.statusCd eq 'TERMINATED'}"><span class="badge bg-danger">해지</span></c:when>
                        <c:when test="${c.statusCd eq 'EXPIRED'}"><span class="badge bg-warning text-dark">만료</span></c:when>
                        <c:otherwise><span class="badge bg-light text-dark">${c.statusCd}</span></c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="6" class="text-center text-muted py-4">계약이 없습니다.</td></tr>
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
