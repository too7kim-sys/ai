<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>내 근태</title>
<h2 class="mb-3"><i class="bi bi-clock"></i> 내 근태</h2>

<div class="row g-3 mb-3">
    <div class="col-md-6">
        <div class="card text-center"><div class="card-body">
            <div class="text-muted small">오늘 출근</div>
            <div class="display-6 text-success">
                <c:choose>
                    <c:when test="${today.checkIn != null}">${fn:substring(today.checkIn, 11, 16)}</c:when>
                    <c:otherwise>—</c:otherwise>
                </c:choose>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/attendance/check-in.do">
                <sec:csrfInput/>
                <button class="btn btn-success" <c:if test="${today.checkIn != null}">disabled</c:if>>
                    <i class="bi bi-box-arrow-in-right"></i> 출근
                </button>
            </form>
        </div></div>
    </div>
    <div class="col-md-6">
        <div class="card text-center"><div class="card-body">
            <div class="text-muted small">오늘 퇴근</div>
            <div class="display-6 text-primary">
                <c:choose>
                    <c:when test="${today.checkOut != null}">${fn:substring(today.checkOut, 11, 16)}</c:when>
                    <c:otherwise>—</c:otherwise>
                </c:choose>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/attendance/check-out.do">
                <sec:csrfInput/>
                <button class="btn btn-outline-primary"
                        <c:if test="${today.checkIn == null || today.checkOut != null}">disabled</c:if>>
                    <i class="bi bi-box-arrow-left"></i> 퇴근
                </button>
            </form>
        </div></div>
    </div>
</div>

<div class="d-flex justify-content-between align-items-center mb-2">
    <h5 class="mb-0">월간 근태 (${month})</h5>
    <form method="get" class="d-flex">
        <input type="month" name="month" value="${month}" class="form-control form-control-sm me-2"/>
        <button class="btn btn-sm btn-outline-secondary">조회</button>
    </form>
</div>

<div class="card">
    <table class="table mb-0">
        <thead class="table-light">
        <tr><th>날짜</th><th>출근</th><th>퇴근</th><th>근로</th><th>연장</th><th>야간</th><th>상태</th></tr>
        </thead>
        <tbody>
        <c:forEach var="a" items="${list}">
            <tr>
                <td>${a.workDt}</td>
                <td>${fn:substring(a.checkIn, 11, 16)}</td>
                <td>${fn:substring(a.checkOut, 11, 16)}</td>
                <td>${a.workMin} 분</td>
                <td>${a.otMin} 분</td>
                <td>${a.nightMin} 분</td>
                <td>
                    <c:choose>
                        <c:when test="${a.statusCd eq 'NORMAL'}"><span class="badge bg-success">정상</span></c:when>
                        <c:when test="${a.statusCd eq 'LATE'}"><span class="badge bg-warning text-dark">지각</span></c:when>
                        <c:when test="${a.statusCd eq 'EARLY_LEAVE'}"><span class="badge bg-info">조기 퇴근</span></c:when>
                        <c:when test="${a.statusCd eq 'HOLIDAY'}"><span class="badge bg-secondary">휴일 근무</span></c:when>
                        <c:otherwise><span class="badge bg-light text-dark">${a.statusCd}</span></c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="7" class="text-center text-muted py-4">기록이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
