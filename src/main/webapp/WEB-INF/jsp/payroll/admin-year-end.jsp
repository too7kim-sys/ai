<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>연말정산</title>
<h2 class="mb-3"><i class="bi bi-receipt-cutoff"></i> 연말정산 (${taxYear}년 귀속)</h2>

<div class="card mb-3">
    <div class="card-header"><i class="bi bi-calculator"></i> 연말정산 계산</div>
    <div class="card-body">
        <form method="get" action="${pageContext.request.contextPath}/payroll/admin/year-end/compute.do" class="row g-2">
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
                <label class="form-label small">귀속연도</label>
                <input type="number" name="taxYear" value="${taxYear}" class="form-control" required/>
            </div>
            <div class="col-md-2 align-self-end">
                <button class="btn btn-outline-primary w-100"><i class="bi bi-calculator"></i> 계산</button>
            </div>
        </form>

        <c:if test="${preview != null}">
            <hr/>
            <h6 class="mb-3">${preview.userName} · ${taxYear}년 연말정산 계산 결과</h6>
            <div class="row">
                <div class="col-md-7">
                    <table class="table table-sm">
                        <tr><th class="bg-light">연간 총급여</th><td class="text-end"><fmt:formatNumber value="${preview.grossPay}" type="number"/> 원</td></tr>
                        <tr><th class="bg-light">(-) 소득공제</th><td class="text-end text-muted"><fmt:formatNumber value="${preview.incomeDeduction}" type="number"/> 원</td></tr>
                        <tr><th class="bg-light">결정세액</th><td class="text-end"><fmt:formatNumber value="${preview.determinedTax}" type="number"/> 원</td></tr>
                        <tr><th class="bg-light">(-) 기납부세액</th><td class="text-end text-muted"><fmt:formatNumber value="${preview.paidTax}" type="number"/> 원</td></tr>
                        <tr class="table-active">
                            <th>정산세액</th>
                            <td class="text-end fw-bold">
                                <c:choose>
                                    <c:when test="${preview.settledTax < 0}">
                                        <span class="text-primary"><fmt:formatNumber value="${-preview.settledTax}" type="number"/> 원 환급</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-danger"><fmt:formatNumber value="${preview.settledTax}" type="number"/> 원 추징</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </table>
                    <p class="small text-muted">${preview.memo}</p>
                </div>
                <div class="col-md-5">
                    <form method="post" action="${pageContext.request.contextPath}/payroll/admin/year-end/save.do">
                        <sec:csrfInput/>
                        <input type="hidden" name="userId" value="${preview.userId}"/>
                        <input type="hidden" name="taxYear" value="${taxYear}"/>
                        <button class="btn btn-primary"><i class="bi bi-save"></i> 이 결과 저장</button>
                    </form>
                </div>
            </div>
        </c:if>
    </div>
</div>

<div class="card">
    <div class="card-header">${taxYear}년 연말정산 내역</div>
    <table class="table mb-0">
        <thead><tr><th>부서</th><th>성명</th><th class="text-end">총급여</th><th class="text-end">결정세액</th>
            <th class="text-end">기납부</th><th class="text-end">정산</th><th>상태</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="y" items="${list}">
            <tr>
                <td>${y.deptNm}</td>
                <td>${y.userName}</td>
                <td class="text-end"><fmt:formatNumber value="${y.grossPay}" type="number"/></td>
                <td class="text-end"><fmt:formatNumber value="${y.determinedTax}" type="number"/></td>
                <td class="text-end text-muted"><fmt:formatNumber value="${y.paidTax}" type="number"/></td>
                <td class="text-end fw-bold">
                    <c:choose>
                        <c:when test="${y.settledTax < 0}"><span class="text-primary">▼ <fmt:formatNumber value="${-y.settledTax}" type="number"/></span></c:when>
                        <c:otherwise><span class="text-danger">▲ <fmt:formatNumber value="${y.settledTax}" type="number"/></span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${y.statusCd eq 'CONFIRMED'}"><span class="badge bg-success">확정</span></c:when>
                        <c:otherwise><span class="badge bg-secondary">작성중</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:if test="${y.statusCd ne 'CONFIRMED'}">
                        <form method="post" action="${pageContext.request.contextPath}/payroll/admin/year-end/confirm.do" class="d-inline">
                            <sec:csrfInput/>
                            <input type="hidden" name="userId" value="${y.userId}"/>
                            <input type="hidden" name="taxYear" value="${taxYear}"/>
                            <button class="btn btn-sm btn-outline-success">확정</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="8" class="text-center text-muted py-4">저장된 연말정산 내역이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
<p class="text-muted small mt-2">
    <i class="bi bi-info-circle"></i> 세액은 근로소득공제·본인 인적공제만 반영한 <strong>간이 추정치</strong>입니다.
    실제 신고는 국세청 정산 기준을 따릅니다.
</p>
