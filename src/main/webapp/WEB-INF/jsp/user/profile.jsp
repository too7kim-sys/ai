<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${user.name} 프로필</title>
<div class="d-flex justify-content-between align-items-center mb-3">
    <h2><i class="bi bi-person-vcard"></i> ${user.name} 프로필</h2>
    <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/user/list.do">
        <i class="bi bi-arrow-left"></i> 목록
    </a>
</div>

<div class="row g-3">
    <div class="col-md-4">
        <div class="card">
            <div class="card-body text-center">
                <div class="display-3 text-primary"><i class="bi bi-person-circle"></i></div>
                <h4 class="mt-2 mb-0">${user.name}</h4>
                <div class="text-muted">${user.positionNm} · ${user.deptNm}</div>
                <span class="badge bg-secondary mt-2">${user.roleNm}</span>
            </div>
            <ul class="list-group list-group-flush small">
                <li class="list-group-item d-flex justify-content-between">
                    <span class="text-muted">이메일</span><span>${user.email}</span></li>
                <li class="list-group-item d-flex justify-content-between">
                    <span class="text-muted">연락처</span><span>${user.phone}</span></li>
                <li class="list-group-item d-flex justify-content-between">
                    <span class="text-muted">입사일</span><span>${user.hireDate}</span></li>
                <li class="list-group-item d-flex justify-content-between">
                    <span class="text-muted">은행</span><span>${user.bankCd} ${user.bankAccount}</span></li>
                <li class="list-group-item d-flex justify-content-between">
                    <span class="text-muted">최근 로그인</span><span>${user.lastLoginAt}</span></li>
            </ul>
        </div>
    </div>

    <div class="col-md-8">
        <div class="card mb-3">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-clock-history"></i> 인사이력</span>
                <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
                    <a class="btn btn-sm btn-outline-primary"
                       href="${pageContext.request.contextPath}/hr/history.do?userId=${user.userId}">전체 보기</a>
                </sec:authorize>
            </div>
            <table class="table table-sm mb-0">
                <thead class="table-light">
                <tr><th>발효일</th><th>유형</th><th>변경 내역</th></tr>
                </thead>
                <tbody>
                <c:forEach var="h" items="${histories}" end="9">
                    <tr>
                        <td>${h.effectiveDt}</td>
                        <td><span class="badge bg-info">${h.changeTypeNm != null ? h.changeTypeNm : h.changeTypeCd}</span></td>
                        <td class="small text-muted">${h.beforeJson} → ${h.afterJson}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty histories}">
                    <tr><td colspan="3" class="text-center text-muted py-3">이력 없음</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <div class="card mb-3">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-journal-text"></i> 인사기록</span>
                <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
                    <a class="btn btn-sm btn-outline-primary"
                       href="${pageContext.request.contextPath}/hr/record.do?userId=${user.userId}">관리</a>
                </sec:authorize>
            </div>
            <table class="table table-sm mb-0">
                <thead class="table-light"><tr><th>일자</th><th>분류</th><th>제목</th></tr></thead>
                <tbody>
                <c:forEach var="r" items="${records}" end="9">
                    <tr>
                        <td>${r.eventDt}</td>
                        <td><span class="badge bg-light text-dark">${r.categoryNm != null ? r.categoryNm : r.categoryCd}</span></td>
                        <td>${r.title}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty records}">
                    <tr><td colspan="3" class="text-center text-muted py-3">기록 없음</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <div class="card">
            <div class="card-header"><i class="bi bi-people-fill"></i> 부양가족</div>
            <table class="table table-sm mb-0">
                <thead class="table-light"><tr><th>관계</th><th>이름</th><th>생년월일</th><th>부양</th><th>경로/장애</th></tr></thead>
                <tbody>
                <c:forEach var="f" items="${families}">
                    <tr>
                        <td>${f.relationCd}</td>
                        <td>${f.name}</td>
                        <td>${f.birthDt}</td>
                        <td><c:choose><c:when test="${f.dependentYn eq 'Y'}"><span class="badge bg-success">Y</span></c:when><c:otherwise>N</c:otherwise></c:choose></td>
                        <td>
                            <c:if test="${f.elderlyYn eq 'Y'}"><span class="badge bg-info">경로</span></c:if>
                            <c:if test="${f.disabledYn eq 'Y'}"><span class="badge bg-warning text-dark">장애</span></c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty families}">
                    <tr><td colspan="5" class="text-center text-muted py-3">등록 없음</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
