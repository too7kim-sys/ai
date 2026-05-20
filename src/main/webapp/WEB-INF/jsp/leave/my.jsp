<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>내 휴가</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-airplane"></i> 내 휴가</h2>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/leave/write.do"><i class="bi bi-plus"></i> 신청</a>
</div>
<div class="alert alert-secondary small">연차 잔여: <strong>${balance.remaining()}일</strong></div>
<div class="card"><table class="table mb-0">
    <thead class="table-light"><tr><th>유형</th><th>시작</th><th>종료</th><th>일수</th><th>사유</th><th>상태</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="r" items="${list}">
        <tr>
            <td>${r.leaveTypeCd}</td>
            <td>${r.startDt}</td><td>${r.endDt}</td>
            <td>${r.days}</td>
            <td>${r.reason}</td>
            <td><span class="badge bg-secondary">${r.statusCd}</span></td>
            <td><c:if test="${r.approvalDocId != null}"><a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/approval/detail.do?docId=${r.approvalDocId}">결재 보기</a></c:if></td>
        </tr>
    </c:forEach>
    <c:if test="${empty list}"><tr><td colspan="7" class="text-center text-muted py-4">신청 내역이 없습니다.</td></tr></c:if>
    </tbody>
</table></div>
