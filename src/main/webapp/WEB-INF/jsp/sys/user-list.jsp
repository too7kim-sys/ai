<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>시스템 - 사용자 관리</title>
<h2 class="mb-3"><i class="bi bi-gear"></i> 시스템 관리</h2>
<c:set var="active" value="user" scope="request"/>
<jsp:include page="_nav.jsp"/>

<c:if test="${not empty tempPassword}">
    <div class="alert alert-success alert-dismissible">
        <i class="bi bi-key"></i>
        사용자 #${resetUserId} 의 비밀번호가 초기화되었습니다. 임시 비밀번호:
        <strong class="user-select-all">${tempPassword}</strong>
        <div class="small text-muted mt-1">이 값은 다시 표시되지 않습니다. 해당 사용자에게 안전하게 전달하세요.</div>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<form class="row g-2 mb-3" method="get">
    <div class="col-md-4"><input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="이름/이메일 검색"/></div>
    <div class="col-md-2"><button class="btn btn-outline-primary"><i class="bi bi-search"></i> 검색</button></div>
    <div class="col-md-6 d-flex justify-content-end align-items-center gap-3">
        <span class="text-muted small">총 ${paging.total}건</span>
        <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/sys/user/create.do">
                <i class="bi bi-person-plus"></i> 신규 사용자 등록
            </a>
        </sec:authorize>
    </div>
</form>

<div class="card">
    <table class="table table-hover mb-0 align-middle">
        <thead class="table-light">
        <tr>
            <th>ID</th><th>이메일</th><th>이름</th><th>부서</th><th>직급</th>
            <th>역할</th><th>잠금</th><th>사용</th><th>최근 로그인</th><th style="width:240px;">관리</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${list}">
            <tr>
                <td>${u.userId}</td>
                <td class="small">${u.email}</td>
                <td>${u.name}</td>
                <td class="small">${u.deptNm}</td>
                <td class="small">${u.positionNm}</td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/sys/user/role.do" class="d-flex">
                        <sec:csrfInput/>
                        <input type="hidden" name="userId" value="${u.userId}"/>
                        <select name="roleCd" class="form-select form-select-sm me-1" onchange="this.form.submit()">
                            <option value="EMPLOYEE"        <c:if test="${u.roleCd eq 'EMPLOYEE'}">selected</c:if>>EMPLOYEE</option>
                            <option value="MANAGER"         <c:if test="${u.roleCd eq 'MANAGER'}">selected</c:if>>MANAGER</option>
                            <option value="HR_MANAGER"      <c:if test="${u.roleCd eq 'HR_MANAGER'}">selected</c:if>>HR_MANAGER</option>
                            <option value="FINANCE_MANAGER" <c:if test="${u.roleCd eq 'FINANCE_MANAGER'}">selected</c:if>>FINANCE_MANAGER</option>
                            <option value="ADMIN"           <c:if test="${u.roleCd eq 'ADMIN'}">selected</c:if>>ADMIN</option>
                        </select>
                    </form>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${u.lockedYn eq 'Y'}"><span class="badge bg-danger">잠금</span></c:when>
                        <c:otherwise><span class="badge bg-light text-dark">정상</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${u.useYn eq 'N'}"><span class="badge bg-secondary">중지</span></c:when>
                        <c:otherwise><span class="badge bg-success">활성</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="small text-muted">
                    <c:if test="${not empty u.lastLoginAt}">
                        <div>${fn:substring(u.lastLoginAt, 0, 16)}</div>
                        <div>${u.lastLoginIp}</div>
                    </c:if>
                </td>
                <td>
                    <c:if test="${u.lockedYn eq 'Y'}">
                        <form method="post" action="${pageContext.request.contextPath}/sys/user/unlock.do" class="d-inline">
                            <sec:csrfInput/><input type="hidden" name="userId" value="${u.userId}"/>
                            <button class="btn btn-sm btn-warning">잠금 해제</button>
                        </form>
                    </c:if>
                    <form method="post" action="${pageContext.request.contextPath}/sys/user/reset-password.do" class="d-inline"
                          onsubmit="return confirm('임시 비밀번호를 발급해 초기화할까요?')">
                        <sec:csrfInput/><input type="hidden" name="userId" value="${u.userId}"/>
                        <button class="btn btn-sm btn-outline-secondary">PW 초기화</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/sys/user/toggle.do" class="d-inline">
                        <sec:csrfInput/>
                        <input type="hidden" name="userId" value="${u.userId}"/>
                        <input type="hidden" name="useYn" value="${u.useYn eq 'Y' ? 'N' : 'Y'}"/>
                        <button class="btn btn-sm btn-outline-${u.useYn eq 'Y' ? 'danger' : 'success'}">
                            <c:choose>
                                <c:when test="${u.useYn eq 'Y'}">비활성</c:when>
                                <c:otherwise>활성</c:otherwise>
                            </c:choose>
                        </button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<c:if test="${paging.totalPages > 1}">
<nav class="mt-3"><ul class="pagination justify-content-center">
    <c:forEach var="i" begin="1" end="${paging.totalPages}">
        <li class="page-item <c:if test='${i == paging.page}'>active</c:if>">
            <a class="page-link" href="?page=${i}&keyword=${keyword}">${i}</a>
        </li>
    </c:forEach>
</ul></nav>
</c:if>
