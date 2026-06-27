<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<title>조직도</title>
<h2 class="mb-3"><i class="bi bi-diagram-3"></i> 조직도</h2>

<div class="row g-3">
    <div class="col-md-5">
        <div class="card">
            <div class="card-header">전체 조직</div>
            <table class="table table-sm table-hover mb-0">
                <tbody>
                <c:forEach var="d" items="${flat}">
                    <tr class="<c:if test='${dept == d.deptId}'>table-active</c:if>">
                        <td>
                            <c:forEach begin="0" end="${d.depth}" var="i">&nbsp;&nbsp;</c:forEach>
                            <c:if test="${d.depth > 0}"><i class="bi bi-arrow-return-right text-muted"></i> </c:if>
                            <a href="${pageContext.request.contextPath}/hr/org.do?deptId=${d.deptId}"
                               class="text-decoration-none <c:if test='${dept == d.deptId}'>fw-bold text-primary</c:if>">
                                ${d.deptNm}
                            </a>
                            <span class="badge bg-light text-muted ms-1">${d.headcount}</span>
                            <c:if test="${not empty d.managerName}">
                                <small class="text-muted ms-2">팀장 ${d.managerName}</small>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="col-md-7">
        <c:if test="${not empty members}">
            <div class="card">
                <div class="card-header"><i class="bi bi-people"></i> 부서 구성원 (${fn:length(members)}명)</div>
                <table class="table mb-0">
                    <thead class="table-light"><tr><th>이름</th><th>직급</th><th>역할</th><th>이메일</th><th>연락처</th></tr></thead>
                    <tbody>
                    <c:forEach var="u" items="${members}">
                        <tr>
                            <td><a href="${pageContext.request.contextPath}/user/profile.do?userId=${u.userId}">${u.name}</a></td>
                            <td>${u.positionNm}</td>
                            <td><span class="badge bg-secondary">${u.roleNm}</span></td>
                            <td>${u.email}</td>
                            <td>${u.phone}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
        <c:if test="${empty members}">
            <div class="alert alert-info"><i class="bi bi-info-circle"></i> 왼쪽에서 부서를 선택하면 구성원이 표시됩니다.</div>
        </c:if>
    </div>
</div>
