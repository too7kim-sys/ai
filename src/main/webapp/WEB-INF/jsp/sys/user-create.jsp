<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>시스템 - 사용자 등록</title>
<h2 class="mb-3"><i class="bi bi-gear"></i> 시스템 관리</h2>
<c:set var="active" value="user" scope="request"/>
<jsp:include page="_nav.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h5 class="mb-0"><i class="bi bi-person-plus"></i> 신규 사용자 등록</h5>
    <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/sys/user/list.do">
        <i class="bi bi-arrow-left"></i> 목록
    </a>
</div>

<form method="post" action="${pageContext.request.contextPath}/sys/user/create.do"
      class="card card-body" autocomplete="off">
    <sec:csrfInput/>
    <div class="row g-3">
        <div class="col-md-6">
            <label class="form-label">이메일 <span class="text-danger">*</span></label>
            <input type="email" name="email" class="form-control" required/>
            <div class="form-text">로그인 ID 로 사용됩니다. 중복 시 등록 실패.</div>
        </div>
        <div class="col-md-6">
            <label class="form-label">이름 <span class="text-danger">*</span></label>
            <input type="text" name="name" class="form-control" required/>
        </div>
        <div class="col-md-6">
            <label class="form-label">연락처</label>
            <input type="text" name="phone" class="form-control" placeholder="010-0000-0000"/>
        </div>
        <div class="col-md-6">
            <label class="form-label">입사일</label>
            <input type="date" name="hireDate" class="form-control"/>
        </div>
        <div class="col-md-4">
            <label class="form-label">부서</label>
            <select name="deptId" class="form-select">
                <option value="">- 선택 -</option>
                <c:forEach var="d" items="${depts}">
                    <option value="${d.deptId}">${d.deptNm}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-4">
            <label class="form-label">직급</label>
            <select name="positionId" class="form-select">
                <option value="">- 선택 -</option>
                <c:forEach var="p" items="${positions}">
                    <option value="${p.positionId}">${p.positionNm}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-4">
            <label class="form-label">역할 <span class="text-danger">*</span></label>
            <select name="roleCd" class="form-select" required>
                <option value="EMPLOYEE" selected>EMPLOYEE</option>
                <option value="MANAGER">MANAGER</option>
                <option value="HR_MANAGER">HR_MANAGER</option>
                <option value="FINANCE_MANAGER">FINANCE_MANAGER</option>
                <option value="ADMIN">ADMIN</option>
            </select>
        </div>
        <div class="col-md-6">
            <label class="form-label">초기 비밀번호 <span class="text-danger">*</span></label>
            <input type="text" name="password" class="form-control" required
                   minlength="8" placeholder="대/소문자·숫자·특수문자 포함 8자 이상"/>
            <div class="form-text">첫 로그인 후 사용자가 직접 변경하도록 안내하세요.</div>
        </div>
    </div>
    <div class="mt-3">
        <button class="btn btn-primary"><i class="bi bi-check2"></i> 등록</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/sys/user/list.do">취소</a>
    </div>
</form>
