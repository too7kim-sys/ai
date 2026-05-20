<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>${v.companyNm}</title>
<h2 class="mb-3"><i class="bi bi-building"></i> ${v.companyNm}</h2>
<table class="table">
    <tr><th width="160">사업자번호</th><td>${v.bizNo}</td></tr>
    <tr><th>대표자</th><td>${v.ceoNm}</td></tr>
    <tr><th>업태/종목</th><td>${v.bizKind} / ${v.bizItem}</td></tr>
    <tr><th>주소</th><td>${v.address}</td></tr>
    <tr><th>담당자</th><td>${v.contactNm} (${v.contactPhone}, ${v.contactEmail})</td></tr>
    <tr><th>계좌</th><td>${v.bankCd} ${v.bankAccount} (${v.bankHolder})</td></tr>
    <tr><th>과세유형</th><td>${v.taxTypeCd}</td></tr>
    <tr><th>메모</th><td>${v.memo}</td></tr>
</table>
<a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/vendor/edit.do?vendorId=${v.vendorId}">수정</a>
<a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/vendor/list.do">목록</a>
