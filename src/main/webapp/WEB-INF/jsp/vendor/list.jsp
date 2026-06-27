<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>거래처</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-building"></i> 거래처</h2>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/vendor/edit.do"><i class="bi bi-plus"></i> 신규 거래처</a>
</div>
<form method="get" class="d-flex gap-2 mb-3">
    <input class="form-control" name="keyword" value="${keyword}" placeholder="상호 / 사업자번호" style="max-width:300px"/>
    <select name="type" class="form-select" style="max-width:160px">
        <option value="">전체</option>
        <option value="CUSTOMER" ${type=='CUSTOMER'?'selected':''}>고객</option>
        <option value="SUPPLIER" ${type=='SUPPLIER'?'selected':''}>공급사</option>
    </select>
    <button class="btn btn-outline-secondary">검색</button>
</form>
<div class="card"><table class="table table-hover mb-0">
    <thead class="table-light"><tr><th>구분</th><th>사업자번호</th><th>상호</th><th>대표자</th><th>업태</th><th>담당자</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="v" items="${list}">
        <tr>
            <td><span class="badge bg-secondary">${v.vendorTypeCd}</span></td>
            <td>${v.bizNo}</td>
            <td><a href="${pageContext.request.contextPath}/vendor/detail.do?vendorId=${v.vendorId}">${v.companyNm}</a></td>
            <td>${v.ceoNm}</td>
            <td>${v.bizKind}</td>
            <td>${v.contactNm}</td>
            <td><a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/vendor/edit.do?vendorId=${v.vendorId}">수정</a></td>
        </tr>
    </c:forEach>
    <c:if test="${empty list}"><tr><td colspan="7" class="text-center text-muted py-4">거래처가 없습니다.</td></tr></c:if>
    </tbody>
</table></div>
