<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>급여 산정 (${payMonth})</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-cash-stack"></i> ${payMonth} 급여 산정</h2>
    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/payroll/admin/period.do"><i class="bi bi-plus"></i> 신규 산정</a>
</div>
<form method="get" class="d-flex gap-2 mb-3">
    <input type="month" name="payMonth" value="${payMonth}" class="form-control" style="max-width:200px"/>
    <select name="status" class="form-select" style="max-width:200px">
        <option value="">전체 상태</option>
        <option value="DRAFT" ${status == 'DRAFT' ? 'selected' : ''}>임시</option>
        <option value="CONFIRMED" ${status == 'CONFIRMED' ? 'selected' : ''}>확정</option>
        <option value="PAID" ${status == 'PAID' ? 'selected' : ''}>지급</option>
    </select>
    <button class="btn btn-outline-secondary">검색</button>
</form>
<form method="post" action="${pageContext.request.contextPath}/payroll/admin/send-mail.do">
    <sec:csrfInput/>
    <input type="hidden" name="payMonth" value="${payMonth}"/>
    <div class="card shadow-sm">
        <div class="card-header bg-light d-flex justify-content-between align-items-center">
            <div>대상자 <strong>${fn:length(list)}</strong>명</div>
            <button type="submit" class="btn btn-success btn-sm"><i class="bi bi-envelope"></i> 명세서 메일 발송</button>
        </div>
        <table class="table table-hover mb-0">
            <thead class="table-light">
            <tr>
                <th width="40"><input type="checkbox" onclick="$('input[name=payIds]').prop('checked', this.checked)"/></th>
                <th>부서</th><th>성명</th>
                <th class="text-end">총지급</th>
                <th class="text-end">공제</th>
                <th class="text-end">실수령</th>
                <th>상태</th><th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="p" items="${list}">
                <tr>
                    <td><input type="checkbox" name="payIds" value="${p.payId}" ${p.statusCd == 'DRAFT' ? 'disabled' : ''}/></td>
                    <td>${p.deptNm}</td>
                    <td>${p.userName}</td>
                    <td class="text-end"><fmt:formatNumber value="${p.grossPay}"/></td>
                    <td class="text-end"><fmt:formatNumber value="${p.deductionTotal}"/></td>
                    <td class="text-end fw-bold"><fmt:formatNumber value="${p.netPay}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${p.statusCd == 'PAID'}"><span class="badge bg-success">지급</span></c:when>
                            <c:when test="${p.statusCd == 'CONFIRMED'}"><span class="badge bg-info">확정</span></c:when>
                            <c:otherwise><span class="badge bg-secondary">임시</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td><a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/payroll/admin/edit.do?payId=${p.payId}">편집</a></td>
                </tr>
            </c:forEach>
            <c:if test="${empty list}">
                <tr><td colspan="8" class="text-center text-muted py-4">해당 월의 급여 산정이 없습니다. 상단 [신규 산정]에서 생성하세요.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</form>
<script src="https://code.jquery.com/jquery-3.7.1.slim.min.js"></script>
<%-- jQuery slim에는 jQuery 가 포함되어 있어 위 onclick 동작에 충분 --%>
