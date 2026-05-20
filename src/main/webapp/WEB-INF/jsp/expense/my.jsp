<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<title>내 지출결의</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-credit-card"></i> 내 지출결의</h2>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/expense/write.do"><i class="bi bi-plus"></i> 신규 작성</a>
</div>
<div class="card"><table class="table table-hover mb-0">
    <thead class="table-light"><tr><th>결의번호</th><th>제목</th><th class="text-end">금액</th><th>상태</th><th>환급</th><th>작성일</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="r" items="${list}">
        <tr>
            <td>${r.reportNo}</td>
            <td>${r.title}</td>
            <td class="text-end"><fmt:formatNumber value="${r.totalAmount}"/></td>
            <td>
                <c:choose>
                    <c:when test="${r.statusCd == 'REIMBURSED'}"><span class="badge bg-success">환급완료</span></c:when>
                    <c:when test="${r.statusCd == 'APPROVED'}"><span class="badge bg-info">결재완료</span></c:when>
                    <c:when test="${r.statusCd == 'IN_APPROVAL'}"><span class="badge bg-warning text-dark">결재중</span></c:when>
                    <c:when test="${r.statusCd == 'REJECTED'}"><span class="badge bg-danger">반려</span></c:when>
                    <c:otherwise><span class="badge bg-secondary">${r.statusCd}</span></c:otherwise>
                </c:choose>
            </td>
            <td>${r.reimburseRequiredYn == 'Y' ? '필요' : '불필요'}</td>
            <td>${r.createdAt}</td>
            <td><a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/expense/detail.do?reportId=${r.reportId}">상세</a></td>
        </tr>
    </c:forEach>
    <c:if test="${empty list}"><tr><td colspan="7" class="text-center text-muted py-4">작성한 결의서가 없습니다.</td></tr></c:if>
    </tbody>
</table></div>
