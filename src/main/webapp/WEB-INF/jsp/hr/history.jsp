<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>인사이력</title>
<h2 class="mb-3"><i class="bi bi-clock-history"></i> 인사이력</h2>

<form class="row g-2 mb-3" method="get" action="${pageContext.request.contextPath}/hr/history.do">
    <div class="col-md-4">
        <select name="userId" class="form-select" onchange="this.form.submit()">
            <option value="">전체 직원</option>
            <c:forEach var="u" items="${users}">
                <option value="${u.userId}" <c:if test="${userId == u.userId}">selected</c:if>>${u.deptNm} - ${u.name}</option>
            </c:forEach>
        </select>
    </div>
    <div class="col-md-3">
        <select name="changeTypeCd" class="form-select" onchange="this.form.submit()">
            <option value="">전체 유형</option>
            <option value="HIRE" <c:if test="${changeTypeCd eq 'HIRE'}">selected</c:if>>입사</option>
            <option value="DEPT_CHANGE" <c:if test="${changeTypeCd eq 'DEPT_CHANGE'}">selected</c:if>>부서이동</option>
            <option value="POSITION_CHANGE" <c:if test="${changeTypeCd eq 'POSITION_CHANGE'}">selected</c:if>>직급변경</option>
            <option value="ROLE_CHANGE" <c:if test="${changeTypeCd eq 'ROLE_CHANGE'}">selected</c:if>>역할변경</option>
            <option value="SALARY_CHANGE" <c:if test="${changeTypeCd eq 'SALARY_CHANGE'}">selected</c:if>>연봉변경</option>
            <option value="TERMINATION" <c:if test="${changeTypeCd eq 'TERMINATION'}">selected</c:if>>퇴직</option>
        </select>
    </div>
</form>

<c:if test="${not empty user}">
<div class="card mb-3">
    <div class="card-header bg-light">
        <strong>${user.name}</strong> · ${user.deptNm} / ${user.positionNm} / ${user.roleNm}
    </div>
    <div class="card-body">
        <h6 class="mb-2"><i class="bi bi-arrow-left-right"></i> 인사발령</h6>
        <form method="post" action="${pageContext.request.contextPath}/hr/admin/transfer.do" class="row g-2">
            <sec:csrfInput/>
            <input type="hidden" name="userId" value="${user.userId}"/>
            <div class="col-md-3">
                <label class="form-label small mb-1">부서</label>
                <select name="deptId" class="form-select form-select-sm">
                    <option value="">변경 안함</option>
                    <c:forEach var="d" items="${depts}">
                        <option value="${d.deptId}">${d.deptNm}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label small mb-1">직급</label>
                <select name="positionId" class="form-select form-select-sm">
                    <option value="">변경 안함</option>
                    <option value="1">사원</option><option value="2">주임</option>
                    <option value="3">대리</option><option value="4">과장</option>
                    <option value="5">차장</option><option value="6">부장</option>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label small mb-1">역할</label>
                <select name="roleCd" class="form-select form-select-sm">
                    <option value="">변경 안함</option>
                    <option value="EMPLOYEE">EMPLOYEE</option>
                    <option value="MANAGER">MANAGER</option>
                    <option value="HR_MANAGER">HR_MANAGER</option>
                    <option value="FINANCE_MANAGER">FINANCE_MANAGER</option>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label small mb-1">발효일</label>
                <input type="date" name="effectiveDt" class="form-control form-control-sm"/>
            </div>
            <div class="col-md-2 d-grid align-self-end">
                <button class="btn btn-sm btn-primary">발령 등록</button>
            </div>
        </form>
    </div>
</div>
</c:if>

<div class="card">
    <table class="table mb-0">
        <thead class="table-light">
            <tr><th>발효일</th><c:if test="${empty user}"><th>대상자</th></c:if><th>유형</th><th>변경 전</th><th>변경 후</th></tr>
        </thead>
        <tbody>
        <c:forEach var="h" items="${histories}">
            <tr>
                <td>${h.effectiveDt}</td>
                <c:if test="${empty user}">
                    <td><a href="?userId=${h.userId}">${h.userName}</a></td>
                </c:if>
                <td><span class="badge bg-info">${h.changeTypeNm != null ? h.changeTypeNm : h.changeTypeCd}</span></td>
                <td class="small text-muted">${h.beforeText}</td>
                <td class="small text-success">${h.afterText}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty histories}">
            <tr><td colspan="5" class="text-center text-muted py-4">이력이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
