<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<title>${r.reportNo}</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-credit-card"></i> ${r.title}</h2>
    <span class="badge bg-secondary fs-6">${r.statusCd}</span>
</div>
<dl class="row mb-3">
    <dt class="col-sm-2">결의번호</dt><dd class="col-sm-4">${r.reportNo}</dd>
    <dt class="col-sm-2">기안자</dt><dd class="col-sm-4">${r.drafterName} (${r.deptNm})</dd>
    <dt class="col-sm-2">총 금액</dt><dd class="col-sm-4"><strong><fmt:formatNumber value="${r.totalAmount}"/></strong> 원
        (공급가 <fmt:formatNumber value="${r.totalNet}"/>, 부가세 <fmt:formatNumber value="${r.totalVat}"/>)
    </dd>
    <dt class="col-sm-2">환급</dt><dd class="col-sm-4">${r.reimburseRequiredYn == 'Y' ? '필요' : '불필요'}
        <c:if test="${r.reimbursedAt != null}"> (${r.reimbursedAt})</c:if></dd>
</dl>
<div class="card mb-3">
    <div class="card-header bg-light">지출 라인</div>
    <table class="table mb-0">
        <thead class="table-light"><tr><th>일자</th><th>분류/계정</th><th>사용처</th>
            <th class="text-end">공급가</th><th class="text-end">부가세</th><th class="text-end">합계</th>
            <th>결제</th><th>증빙</th></tr></thead>
        <tbody>
        <c:forEach var="it" items="${r.items}">
            <tr>
                <td>${it.expenseDt}</td>
                <td>${it.categoryCd} / ${it.accountCd}</td>
                <td>${it.vendorNm}</td>
                <td class="text-end"><fmt:formatNumber value="${it.netAmount}"/></td>
                <td class="text-end"><fmt:formatNumber value="${it.vatAmount}"/></td>
                <td class="text-end"><fmt:formatNumber value="${it.totalAmount}"/></td>
                <td>${it.paymentMethodCd}</td>
                <td>${it.receiptTypeCd}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<c:if test="${r.approvalDocId != null}">
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/approval/detail.do?docId=${r.approvalDocId}">결재 진행상황 보기</a>
</c:if>
<a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/expense/my.do">목록</a>
