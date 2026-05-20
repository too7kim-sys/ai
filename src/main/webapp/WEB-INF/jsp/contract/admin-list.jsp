<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>근로계약 관리</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-file-text"></i> 근로계약 관리</h2>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/contract/admin/write.do"><i class="bi bi-plus"></i> 신규 계약</a>
</div>
<form method="get" class="d-flex gap-2 mb-3">
    <input class="form-control" name="keyword" value="${keyword}" placeholder="성명 / 계약번호" style="max-width:240px"/>
    <select name="status" class="form-select" style="max-width:160px">
        <option value="">전체</option>
        <option value="DRAFT" ${status=='DRAFT'?'selected':''}>DRAFT</option>
        <option value="SENT" ${status=='SENT'?'selected':''}>SENT</option>
        <option value="SIGNED" ${status=='SIGNED'?'selected':''}>SIGNED</option>
        <option value="ACTIVE" ${status=='ACTIVE'?'selected':''}>ACTIVE</option>
        <option value="TERMINATED" ${status=='TERMINATED'?'selected':''}>TERMINATED</option>
    </select>
    <button class="btn btn-outline-secondary">검색</button>
</form>
<div class="card"><table class="table table-hover mb-0">
    <thead class="table-light"><tr><th>계약번호</th><th>사원</th><th>부서</th><th>종류</th><th>기간</th><th>상태</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="c" items="${list}">
        <tr>
            <td><strong>${c.contractNo}</strong></td>
            <td>${c.userName}</td>
            <td>${c.deptNm}</td>
            <td>${c.contractTypeCd}</td>
            <td>${c.startDt} ~ ${c.endDt == null ? '∞' : c.endDt}</td>
            <td><span class="badge bg-secondary">${c.statusCd}</span></td>
            <td><a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/contract/admin/preview.do?contractId=${c.contractId}">상세</a></td>
        </tr>
    </c:forEach>
    <c:if test="${empty list}"><tr><td colspan="7" class="text-center text-muted py-4">계약 데이터가 없습니다.</td></tr></c:if>
    </tbody>
</table></div>
