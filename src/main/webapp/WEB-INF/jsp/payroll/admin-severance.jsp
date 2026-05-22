<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>퇴직정산</title>
<h2 class="mb-3"><i class="bi bi-box-arrow-right"></i> 퇴직금 정산</h2>

<div class="card mb-3">
    <div class="card-header"><i class="bi bi-calculator"></i> 퇴직금 계산</div>
    <div class="card-body">
        <form method="get" action="${pageContext.request.contextPath}/payroll/admin/severance/compute.do" class="row g-2">
            <div class="col-md-3">
                <label class="form-label small">직원</label>
                <select name="userId" class="form-select" required>
                    <option value="">선택...</option>
                    <c:forEach var="u" items="${users}">
                        <option value="${u.userId}" <c:if test="${preview.userId == u.userId}">selected</c:if>>${u.deptNm} ${u.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label small">퇴직일</label>
                <input type="date" name="leaveDate" value="${preview.leaveDate}" class="form-control" required/>
            </div>
            <div class="col-md-2 align-self-end">
                <button class="btn btn-outline-primary w-100"><i class="bi bi-calculator"></i> 계산</button>
            </div>
        </form>

        <c:if test="${preview != null}">
            <hr/>
            <h6 class="mb-3">${preview.userName} 퇴직금 계산 결과</h6>
            <div class="row">
                <div class="col-md-7">
                    <table class="table table-sm">
                        <tr><th class="bg-light">입사일 / 퇴직일</th><td>${preview.hireDate} ~ ${preview.leaveDate}</td></tr>
                        <tr><th class="bg-light">근속일수</th><td>${preview.serviceDays}일 (약 ${preview.serviceYears}년)</td></tr>
                        <tr><th class="bg-light">평균임금 (월)</th><td class="text-end"><fmt:formatNumber value="${preview.avgMonthlyWage}" type="number"/> 원</td></tr>
                        <tr><th class="bg-light">1일 평균임금</th><td class="text-end"><fmt:formatNumber value="${preview.avgDailyWage}" type="number"/> 원</td></tr>
                        <tr><th class="bg-light">퇴직금 (세전)</th><td class="text-end"><fmt:formatNumber value="${preview.severancePay}" type="number"/> 원</td></tr>
                        <tr><th class="bg-light">(-) 퇴직소득세</th><td class="text-end text-muted"><fmt:formatNumber value="${preview.severanceTax}" type="number"/> 원</td></tr>
                        <tr class="table-active"><th>실지급액</th>
                            <td class="text-end fw-bold text-primary"><fmt:formatNumber value="${preview.netPay}" type="number"/> 원</td></tr>
                    </table>
                    <p class="small text-muted">${preview.memo}</p>
                </div>
                <div class="col-md-5">
                    <form method="post" action="${pageContext.request.contextPath}/payroll/admin/severance/save.do">
                        <sec:csrfInput/>
                        <input type="hidden" name="userId" value="${preview.userId}"/>
                        <input type="hidden" name="leaveDate" value="${preview.leaveDate}"/>
                        <button class="btn btn-primary"><i class="bi bi-save"></i> 이 결과 저장</button>
                    </form>
                </div>
            </div>
        </c:if>
    </div>
</div>

<div class="card">
    <div class="card-header">퇴직정산 내역</div>
    <table class="table mb-0">
        <thead><tr><th>부서</th><th>성명</th><th>퇴직일</th><th class="text-end">근속</th>
            <th class="text-end">퇴직금</th><th class="text-end">실지급</th><th>상태</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="s" items="${list}">
            <tr>
                <td>${s.deptNm}</td>
                <td>${s.userName}</td>
                <td>${s.leaveDate}</td>
                <td class="text-end">${s.serviceYears}년</td>
                <td class="text-end"><fmt:formatNumber value="${s.severancePay}" type="number"/></td>
                <td class="text-end fw-bold"><fmt:formatNumber value="${s.netPay}" type="number"/></td>
                <td>
                    <c:choose>
                        <c:when test="${s.statusCd eq 'PAID'}"><span class="badge bg-success">지급완료</span></c:when>
                        <c:when test="${s.statusCd eq 'CONFIRMED'}"><span class="badge bg-info text-dark">확정</span></c:when>
                        <c:otherwise><span class="badge bg-secondary">작성중</span></c:otherwise>
                    </c:choose>
                    <c:if test="${s.statusCd eq 'PAID' and not empty s.paidDt}">
                        <small class="text-muted d-block">${s.paidDt}</small>
                    </c:if>
                </td>
                <td>
                    <c:if test="${s.statusCd ne 'PAID'}">
                        <form method="post" action="${pageContext.request.contextPath}/payroll/admin/severance/pay.do" class="d-inline"
                              onsubmit="return confirm('퇴직금 지급 완료 처리할까요?')">
                            <sec:csrfInput/>
                            <input type="hidden" name="sevId" value="${s.sevId}"/>
                            <button class="btn btn-sm btn-outline-success">지급 완료</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="8" class="text-center text-muted py-4">퇴직정산 내역이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
<p class="text-muted small mt-2">
    <i class="bi bi-info-circle"></i> 평균임금은 최근 3개월 급여총액 기준,
    퇴직소득세는 5% 간이 추정입니다.
</p>
