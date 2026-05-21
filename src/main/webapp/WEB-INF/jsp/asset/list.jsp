<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>자산 관리</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-laptop"></i> 자산 관리</h2>
    <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/asset/edit.do">
            <i class="bi bi-plus"></i> 자산 등록
        </a>
    </sec:authorize>
</div>

<!-- 상태별 통계 카드 -->
<div class="row g-2 mb-3">
    <c:forEach var="s" items="${statusStats}">
        <div class="col-md-3">
            <div class="card shadow-sm"><div class="card-body py-2">
                <small class="text-muted">
                    <c:choose>
                        <c:when test="${s.status_cd eq 'IN_USE'}">사용 중</c:when>
                        <c:when test="${s.status_cd eq 'IN_STOCK'}">재고</c:when>
                        <c:when test="${s.status_cd eq 'REPAIRING'}">수리 중</c:when>
                        <c:when test="${s.status_cd eq 'DISPOSED'}">폐기</c:when>
                        <c:otherwise>${s.status_cd}</c:otherwise>
                    </c:choose>
                </small>
                <div class="h4 mb-0">${s.cnt}<small class="text-muted">대</small></div>
                <small class="text-muted"><fmt:formatNumber value="${s.total_amount}" type="number"/>원</small>
            </div></div>
        </div>
    </c:forEach>
</div>

<form class="row g-2 mb-3" method="get">
    <div class="col-md-3"><input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="자산번호/이름/시리얼/사용자"/></div>
    <div class="col-md-2">
        <select name="categoryCd" class="form-select">
            <option value="">전체 분류</option>
            <option value="LAPTOP"    <c:if test="${categoryCd eq 'LAPTOP'}">selected</c:if>>노트북</option>
            <option value="DESKTOP"   <c:if test="${categoryCd eq 'DESKTOP'}">selected</c:if>>데스크탑</option>
            <option value="MONITOR"   <c:if test="${categoryCd eq 'MONITOR'}">selected</c:if>>모니터</option>
            <option value="PHONE"     <c:if test="${categoryCd eq 'PHONE'}">selected</c:if>>휴대폰</option>
            <option value="PRINTER"   <c:if test="${categoryCd eq 'PRINTER'}">selected</c:if>>프린터</option>
            <option value="HEADSET"   <c:if test="${categoryCd eq 'HEADSET'}">selected</c:if>>헤드셋</option>
            <option value="FURNITURE" <c:if test="${categoryCd eq 'FURNITURE'}">selected</c:if>>가구</option>
            <option value="OTHER"     <c:if test="${categoryCd eq 'OTHER'}">selected</c:if>>기타</option>
        </select>
    </div>
    <div class="col-md-2">
        <select name="statusCd" class="form-select">
            <option value="">전체 상태</option>
            <option value="IN_STOCK"  <c:if test="${statusCd eq 'IN_STOCK'}">selected</c:if>>재고</option>
            <option value="IN_USE"    <c:if test="${statusCd eq 'IN_USE'}">selected</c:if>>사용 중</option>
            <option value="REPAIRING" <c:if test="${statusCd eq 'REPAIRING'}">selected</c:if>>수리 중</option>
            <option value="DISPOSED"  <c:if test="${statusCd eq 'DISPOSED'}">selected</c:if>>폐기</option>
        </select>
    </div>
    <div class="col-md-2"><button class="btn btn-outline-primary w-100">검색</button></div>
    <div class="col-md-3 text-end text-muted small align-self-center">총 ${paging.total}건</div>
</form>

<div class="card">
    <table class="table table-hover mb-0">
        <thead class="table-light">
        <tr><th>자산번호</th><th>이름</th><th>분류</th><th>모델</th><th>위치</th>
            <th>사용자</th><th>상태</th><th class="text-end">취득가</th></tr>
        </thead>
        <tbody>
        <c:forEach var="a" items="${list}">
            <tr>
                <td class="small"><a href="${pageContext.request.contextPath}/asset/detail.do?assetId=${a.assetId}"><code>${a.assetNo}</code></a></td>
                <td>${a.assetNm}</td>
                <td><span class="badge bg-light text-dark">${a.categoryCd}</span></td>
                <td class="small text-muted">${a.brand} ${a.modelNm}</td>
                <td class="small">${a.location}</td>
                <td class="small">${a.assignedUserName}
                    <c:if test="${not empty a.assignedUserDept}"><small class="d-block text-muted">${a.assignedUserDept}</small></c:if></td>
                <td>
                    <c:choose>
                        <c:when test="${a.statusCd eq 'IN_USE'}"><span class="badge bg-success">사용</span></c:when>
                        <c:when test="${a.statusCd eq 'IN_STOCK'}"><span class="badge bg-secondary">재고</span></c:when>
                        <c:when test="${a.statusCd eq 'REPAIRING'}"><span class="badge bg-warning text-dark">수리</span></c:when>
                        <c:when test="${a.statusCd eq 'DISPOSED'}"><span class="badge bg-dark">폐기</span></c:when>
                    </c:choose>
                </td>
                <td class="text-end small"><fmt:formatNumber value="${a.purchaseAmount}" type="number"/></td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="8" class="text-center text-muted py-4">자산이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<c:if test="${paging.totalPages > 1}">
<nav class="mt-3"><ul class="pagination justify-content-center">
    <c:forEach var="i" begin="1" end="${paging.totalPages}">
        <li class="page-item <c:if test='${i == paging.page}'>active</c:if>">
            <a class="page-link" href="?page=${i}&keyword=${keyword}&categoryCd=${categoryCd}&statusCd=${statusCd}">${i}</a>
        </li>
    </c:forEach>
</ul></nav>
</c:if>
