<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>지출결의 관리</title>
<h2 class="mb-3"><i class="bi bi-credit-card-2-front"></i> 지출결의 관리 (재무)</h2>
<form method="get" class="d-flex gap-2 mb-3">
    <select class="form-select" name="status" style="max-width:200px">
        <option value="">전체 상태</option>
        <option value="IN_APPROVAL" ${status=='IN_APPROVAL'?'selected':''}>결재중</option>
        <option value="APPROVED" ${status=='APPROVED'?'selected':''}>결재완료 (환급대기)</option>
        <option value="REIMBURSED" ${status=='REIMBURSED'?'selected':''}>환급완료</option>
        <option value="REJECTED" ${status=='REJECTED'?'selected':''}>반려</option>
    </select>
    <button class="btn btn-outline-secondary">검색</button>
</form>
<form method="post" action="${pageContext.request.contextPath}/expense/admin/reimburse.do">
    <sec:csrfInput/>
    <div class="card">
        <div class="card-header bg-light d-flex justify-content-between align-items-center">
            <div>총 <strong>${fn:length(list)}</strong>건</div>
            <button type="submit" class="btn btn-success btn-sm" onclick="return confirm('선택한 결의서를 환급 처리합니다. 계속하시겠습니까?')">
                <i class="bi bi-bank"></i> 일괄 환급 처리 (개인카드/현금)
            </button>
        </div>
        <table class="table table-hover mb-0">
            <thead class="table-light"><tr>
                <th width="40"></th>
                <th>결의번호</th><th>기안자</th><th>부서</th><th>제목</th>
                <th class="text-end">금액</th><th>환급</th><th>상태</th><th></th>
            </tr></thead>
            <tbody>
            <c:forEach var="r" items="${list}">
                <tr>
                    <td><input type="checkbox" name="reportIds" value="${r.reportId}"
                               ${r.statusCd == 'APPROVED' and r.reimburseRequiredYn == 'Y' ? '' : 'disabled'}/></td>
                    <td>${r.reportNo}</td>
                    <td>${r.drafterName}</td>
                    <td>${r.deptNm}</td>
                    <td>${r.title}</td>
                    <td class="text-end"><fmt:formatNumber value="${r.totalAmount}"/></td>
                    <td>${r.reimburseRequiredYn}</td>
                    <td><span class="badge bg-secondary">${r.statusCd}</span></td>
                    <td><a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/expense/detail.do?reportId=${r.reportId}">상세</a></td>
                </tr>
            </c:forEach>
            <c:if test="${empty list}"><tr><td colspan="9" class="text-center text-muted py-4">결의서가 없습니다.</td></tr></c:if>
            </tbody>
        </table>
    </div>
</form>
