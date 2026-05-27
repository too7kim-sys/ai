<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>사용자 정보 수정 - ${u.name}</title>
<h2 class="mb-3"><i class="bi bi-pencil-square"></i> 사용자 정보 수정</h2>

<form method="post" action="${pageContext.request.contextPath}/sys/user/edit.do"
      class="card card-body">
    <sec:csrfInput/>
    <input type="hidden" name="userId" value="${u.userId}"/>
    <div class="row g-3">
        <div class="col-md-6">
            <label class="form-label">이메일</label>
            <input type="email" class="form-control" value="${u.email}" disabled/>
            <div class="form-text">이메일(로그인 ID)은 변경할 수 없습니다.</div>
        </div>
        <div class="col-md-6">
            <label class="form-label">이름 <span class="text-danger">*</span></label>
            <input type="text" name="name" class="form-control" value="${u.name}" required/>
        </div>
        <div class="col-md-6">
            <label class="form-label">연락처</label>
            <input type="text" name="phone" class="form-control" value="${u.phone}"/>
        </div>
        <div class="col-md-6">
            <label class="form-label">입사일</label>
            <input type="date" name="hireDate" class="form-control" value="${u.hireDate}"/>
        </div>
        <div class="col-md-6">
            <label class="form-label">부서</label>
            <select name="deptId" class="form-select">
                <option value="">- 선택 -</option>
                <c:forEach var="d" items="${depts}">
                    <option value="${d.deptId}" <c:if test="${d.deptId eq u.deptId}">selected</c:if>>${d.deptNm}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-6">
            <label class="form-label">직급</label>
            <select name="positionId" class="form-select">
                <option value="">- 선택 -</option>
                <c:forEach var="p" items="${positions}">
                    <option value="${p.positionId}" <c:if test="${p.positionId eq u.positionId}">selected</c:if>>${p.positionNm}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-6">
            <label class="form-label">은행 코드</label>
            <input type="text" name="bankCd" class="form-control" value="${u.bankCd}" placeholder="088"/>
        </div>
        <div class="col-md-6">
            <label class="form-label">계좌번호</label>
            <input type="text" name="bankAccount" class="form-control" value="${u.bankAccount}"/>
        </div>
    </div>
    <div class="mt-3 d-flex justify-content-between">
        <div>
            <button class="btn btn-primary"><i class="bi bi-check2"></i> 저장</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/user/profile.do?userId=${u.userId}">취소</a>
        </div>
        <span class="text-muted small">역할 변경은 시스템 - 사용자 관리에서 처리합니다.</span>
    </div>
</form>
