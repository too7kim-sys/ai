<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${a.assetNo} ${a.assetNm}</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-laptop"></i> 자산 상세</h2>
    <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/asset/list.do">
        <i class="bi bi-arrow-left"></i> 목록
    </a>
</div>

<div class="card mb-3">
    <div class="card-header d-flex justify-content-between">
        <div>
            <code>${a.assetNo}</code>
            <strong class="ms-2">${a.assetNm}</strong>
            <span class="badge bg-light text-dark ms-2">${a.categoryCd}</span>
            <c:choose>
                <c:when test="${a.statusCd eq 'IN_USE'}"><span class="badge bg-success ms-1">사용 중</span></c:when>
                <c:when test="${a.statusCd eq 'IN_STOCK'}"><span class="badge bg-secondary ms-1">재고</span></c:when>
                <c:when test="${a.statusCd eq 'REPAIRING'}"><span class="badge bg-warning text-dark ms-1">수리 중</span></c:when>
                <c:when test="${a.statusCd eq 'DISPOSED'}"><span class="badge bg-dark ms-1">폐기</span></c:when>
            </c:choose>
        </div>
        <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
            <div class="d-flex gap-2">
                <a class="btn btn-sm btn-outline-primary"
                   href="${pageContext.request.contextPath}/asset/edit.do?assetId=${a.assetId}">
                    <i class="bi bi-pencil"></i> 수정
                </a>
            </div>
        </sec:authorize>
    </div>
    <div class="card-body">
        <table class="table table-sm mb-0">
            <tr><th class="bg-light" style="width:140px;">브랜드/모델</th><td>${a.brand} ${a.modelNm}</td>
                <th class="bg-light" style="width:140px;">시리얼</th><td><code>${a.serialNo}</code></td></tr>
            <tr><th class="bg-light">취득일</th><td>${a.purchaseDt}</td>
                <th class="bg-light">취득가</th><td><fmt:formatNumber value="${a.purchaseAmount}" type="number"/> 원</td></tr>
            <tr><th class="bg-light">감가상각 기간</th><td>${a.depreciationMonths}개월</td>
                <th class="bg-light">위치</th><td>${a.location}</td></tr>
            <tr><th class="bg-light">사용자</th>
                <td>${a.assignedUserName}<small class="text-muted ms-1">${a.assignedUserDept}</small></td>
                <th class="bg-light">지급일</th><td>${a.assignedDt}</td></tr>
            <c:if test="${not empty a.memo}">
                <tr><th class="bg-light">메모</th><td colspan="3" style="white-space: pre-wrap;">${a.memo}</td></tr>
            </c:if>
        </table>
    </div>
</div>

<!-- 액션 -->
<sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
<div class="card mb-3">
    <div class="card-header"><i class="bi bi-arrow-left-right"></i> 자산 액션</div>
    <div class="card-body">
        <div class="row g-3">
            <c:if test="${a.statusCd ne 'DISPOSED'}">
                <c:if test="${a.statusCd eq 'IN_STOCK'}">
                    <div class="col-md-6">
                        <form method="post" action="${pageContext.request.contextPath}/asset/assign.do" class="row g-2">
                            <sec:csrfInput/>
                            <input type="hidden" name="assetId" value="${a.assetId}"/>
                            <div class="col-md-5">
                                <select name="userId" class="form-select" required>
                                    <option value="">사용자 선택...</option>
                                    <c:forEach var="u" items="${users}">
                                        <option value="${u.userId}">${u.name} (${u.deptNm})</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-5"><input type="text" name="memo" class="form-control" placeholder="메모"/></div>
                            <div class="col-md-2 d-grid"><button class="btn btn-success">지급</button></div>
                        </form>
                    </div>
                </c:if>
                <c:if test="${a.statusCd eq 'IN_USE'}">
                    <div class="col-md-3">
                        <form method="post" action="${pageContext.request.contextPath}/asset/return.do" onsubmit="return confirm('자산을 회수할까요?')">
                            <sec:csrfInput/><input type="hidden" name="assetId" value="${a.assetId}"/>
                            <input type="text" name="memo" class="form-control form-control-sm mb-1" placeholder="회수 메모"/>
                            <button class="btn btn-outline-secondary btn-sm w-100">회수 (Return)</button>
                        </form>
                    </div>
                </c:if>
                <div class="col-md-3">
                    <form method="post" action="${pageContext.request.contextPath}/asset/repair.do">
                        <sec:csrfInput/><input type="hidden" name="assetId" value="${a.assetId}"/>
                        <input type="text" name="memo" class="form-control form-control-sm mb-1" placeholder="수리 메모"/>
                        <button class="btn btn-outline-warning btn-sm w-100">수리 의뢰</button>
                    </form>
                </div>
                <div class="col-md-3">
                    <form method="post" action="${pageContext.request.contextPath}/asset/dispose.do" onsubmit="return confirm('자산을 폐기 처리할까요?')">
                        <sec:csrfInput/><input type="hidden" name="assetId" value="${a.assetId}"/>
                        <input type="text" name="memo" class="form-control form-control-sm mb-1" placeholder="폐기 사유"/>
                        <button class="btn btn-outline-danger btn-sm w-100">폐기</button>
                    </form>
                </div>
            </c:if>
        </div>
    </div>
</div>
</sec:authorize>

<div class="card">
    <div class="card-header"><i class="bi bi-clock-history"></i> 변경 이력</div>
    <table class="table mb-0">
        <thead class="table-light"><tr><th>일시</th><th>액션</th><th>상태 변화</th><th>대상</th><th>담당</th><th>메모</th></tr></thead>
        <tbody>
        <c:forEach var="h" items="${history}">
            <tr>
                <td class="small text-muted">${h.actionDt}</td>
                <td><span class="badge bg-info text-dark">${h.actionCd}</span></td>
                <td class="small">${h.beforeStatus} → <strong>${h.afterStatus}</strong></td>
                <td class="small">${h.targetUserName}</td>
                <td class="small">${h.actorName}</td>
                <td class="small text-muted">${h.memo}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty history}">
            <tr><td colspan="6" class="text-center text-muted py-3">이력이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
