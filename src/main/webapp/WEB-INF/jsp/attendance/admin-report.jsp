<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<title>근태 리포트</title>
<h2 class="mb-3"><i class="bi bi-clock-history"></i> 근태 리포트 (HR/매니저)</h2>

<form class="row g-2 mb-3" method="get">
    <div class="col-md-3"><input type="month" name="month" value="${month}" class="form-control"/></div>
    <div class="col-md-3 d-grid"><button class="btn btn-outline-primary">조회</button></div>
    <div class="col-md-6 text-end text-muted small">총 ${fn:length(list)}건</div>
</form>

<div class="card">
    <table class="table mb-0">
        <thead class="table-light">
        <tr><th>날짜</th><th>이름</th><th>부서</th><th>출근</th><th>퇴근</th><th>근로</th><th>연장</th><th>야간</th><th>상태</th></tr>
        </thead>
        <tbody>
        <c:forEach var="a" items="${list}">
            <tr>
                <td>${a.workDt}</td>
                <td>${a.userName}</td>
                <td>${a.deptNm}</td>
                <td>${fn:substring(a.checkIn, 11, 16)}</td>
                <td>${fn:substring(a.checkOut, 11, 16)}</td>
                <td>${a.workMin}분</td>
                <td>${a.otMin}분</td>
                <td>${a.nightMin}분</td>
                <td>
                    <c:choose>
                        <c:when test="${a.statusCd eq 'NORMAL'}"><span class="badge bg-success">정상</span></c:when>
                        <c:when test="${a.statusCd eq 'LATE'}"><span class="badge bg-warning text-dark">지각</span></c:when>
                        <c:when test="${a.statusCd eq 'EARLY_LEAVE'}"><span class="badge bg-info">조기 퇴근</span></c:when>
                        <c:when test="${a.statusCd eq 'HOLIDAY'}"><span class="badge bg-secondary">휴일</span></c:when>
                        <c:when test="${a.statusCd eq 'LEAVE'}"><span class="badge text-bg-light border">휴가</span></c:when>
                        <c:when test="${a.statusCd eq 'ABSENT'}"><span class="badge bg-danger">결근</span></c:when>
                        <c:otherwise><span class="badge bg-light text-dark">${a.statusCd}</span></c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="9" class="text-center text-muted py-4">기록이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
