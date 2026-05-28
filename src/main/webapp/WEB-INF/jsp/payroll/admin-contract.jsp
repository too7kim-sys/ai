<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>연봉계약</title>
<h2 class="mb-3"><i class="bi bi-file-earmark-text"></i> 연봉계약 관리</h2>
<div class="card mb-3"><div class="card-header bg-light">신규 계약 등록</div><div class="card-body">
    <form method="post" action="${pageContext.request.contextPath}/payroll/admin/contract.do" class="row g-2 align-items-end">
        <sec:csrfInput/>
        <div class="col-md-3"><label class="form-label small">사원</label>
            <div class="input-group input-group-sm">
                <input type="hidden" id="salaryContractUserId" name="userId" required/>
                <input type="text" id="salaryContractUserNm" class="form-control" readonly placeholder="사원 선택"/>
                <button type="button" class="btn btn-outline-primary"
                        onclick="openUserPicker({hidden:'salaryContractUserId', display:'salaryContractUserNm'})">
                    <i class="bi bi-person-search"></i>
                </button>
            </div>
        </div>
        <div class="col-md-2"><label class="form-label small">시작일</label><input class="form-control" type="date" name="startDt" required/></div>
        <div class="col-md-3"><label class="form-label small">연봉 (원)</label><input class="form-control" type="number" name="annualSalary" required placeholder="48000000"/></div>
        <div class="col-md-2"><label class="form-label small">분할</label>
            <select name="divisionTypeCd" class="form-select"><option value="12">12분할</option><option value="13">13분할</option><option value="14">14분할</option></select>
        </div>
        <div class="col-md-2"><button class="btn btn-primary w-100">등록</button></div>
    </form>
</div></div>
<div class="card"><div class="card-header bg-light">연봉계약 이력</div>
<table class="table mb-0">
    <thead><tr><th>계약ID</th><th>사용자</th><th>시작</th><th>종료</th><th class="text-end">연봉</th><th class="text-end">월 기본급</th><th>분할</th></tr></thead>
    <tbody>
    <c:forEach var="c" items="${list}">
        <tr><td>${c.contractId}</td><td>${c.userName} (#${c.userId})</td><td>${c.startDt}</td><td>${c.endDt}</td>
            <td class="text-end"><fmt:formatNumber value="${c.annualSalary}"/></td>
            <td class="text-end"><fmt:formatNumber value="${c.monthlyBaseSal}"/></td>
            <td>${c.divisionTypeCd}분할</td></tr>
    </c:forEach>
    <c:if test="${empty list}"><tr><td colspan="7" class="text-center text-muted py-3">계약 데이터가 없습니다.</td></tr></c:if>
    </tbody>
</table></div>
