<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>상여 · 성과급 관리</title>
<h2 class="mb-3"><i class="bi bi-gift"></i> 상여 · 성과급 관리</h2>

<div class="card mb-3">
    <div class="card-header"><i class="bi bi-plus-circle"></i> 상여 일괄 등록</div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/payroll/admin/bonus/create.do" class="row g-2">
            <sec:csrfInput/>
            <div class="col-md-2">
                <label class="form-label small">급여월</label>
                <input type="month" name="payMonth" value="${payMonth}" class="form-control" required/>
            </div>
            <div class="col-md-2">
                <label class="form-label small">상여 유형</label>
                <select name="bonusTypeCd" class="form-select" required>
                    <option value="REGULAR">정기상여</option>
                    <option value="HOLIDAY">명절상여</option>
                    <option value="PERFORMANCE">성과급</option>
                    <option value="SPECIAL">특별상여</option>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label small">1인 금액</label>
                <input type="number" name="amount" class="form-control text-end" required min="1" step="1000"/>
            </div>
            <div class="col-md-2">
                <label class="form-label small">과세</label>
                <select name="taxableYn" class="form-select">
                    <option value="Y">과세</option>
                    <option value="N">비과세</option>
                </select>
            </div>
            <div class="col-md-4">
                <label class="form-label small">메모</label>
                <input type="text" name="memo" class="form-control" placeholder="예: 2026년 설 명절 상여"/>
            </div>
            <div class="col-12">
                <label class="form-label small">대상 직원
                    <a href="#" id="selectAll" class="ms-2">전체 선택</a> /
                    <a href="#" id="clearAll">해제</a>
                </label>
                <div class="border rounded p-2" style="max-height:200px;overflow-y:auto;">
                    <c:forEach var="u" items="${users}">
                        <div class="form-check form-check-inline">
                            <input type="checkbox" name="userIds" value="${u.userId}" class="form-check-input emp-cb" id="u${u.userId}"/>
                            <label class="form-check-label small" for="u${u.userId}">${u.deptNm} ${u.name}</label>
                        </div>
                    </c:forEach>
                </div>
            </div>
            <div class="col-12">
                <button class="btn btn-primary"><i class="bi bi-check2"></i> 일괄 등록</button>
            </div>
        </form>
    </div>
</div>

<form class="row g-2 mb-3" method="get">
    <div class="col-md-3"><input type="month" name="payMonth" value="${payMonth}" class="form-control"/></div>
    <div class="col-md-3">
        <select name="bonusTypeCd" class="form-select">
            <option value="">전체 유형</option>
            <option value="REGULAR"     <c:if test="${bonusTypeCd eq 'REGULAR'}">selected</c:if>>정기상여</option>
            <option value="HOLIDAY"     <c:if test="${bonusTypeCd eq 'HOLIDAY'}">selected</c:if>>명절상여</option>
            <option value="PERFORMANCE" <c:if test="${bonusTypeCd eq 'PERFORMANCE'}">selected</c:if>>성과급</option>
            <option value="SPECIAL"     <c:if test="${bonusTypeCd eq 'SPECIAL'}">selected</c:if>>특별상여</option>
        </select>
    </div>
    <div class="col-md-2"><button class="btn btn-outline-primary w-100">조회</button></div>
</form>

<div class="card">
    <div class="card-header">${payMonth} 상여 내역</div>
    <table class="table mb-0">
        <thead><tr><th>유형</th><th>부서</th><th>성명</th><th class="text-end">금액</th><th>과세</th><th>메모</th><th>상태</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="b" items="${list}">
            <tr>
                <td><span class="badge bg-soft-primary">${b.bonusTypeNm}</span></td>
                <td>${b.deptNm}</td>
                <td>${b.userName}</td>
                <td class="text-end fw-bold"><fmt:formatNumber value="${b.amount}" type="number"/></td>
                <td>${b.taxableYn eq 'Y' ? '과세' : '비과세'}</td>
                <td class="small text-muted">${b.memo}</td>
                <td>
                    <c:choose>
                        <c:when test="${b.statusCd eq 'PLANNED'}"><span class="badge bg-secondary">예정</span></c:when>
                        <c:when test="${b.statusCd eq 'APPLIED'}"><span class="badge bg-success">반영완료</span></c:when>
                        <c:otherwise><span class="badge bg-light text-dark">취소</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:if test="${b.statusCd eq 'PLANNED'}">
                        <form method="post" action="${pageContext.request.contextPath}/payroll/admin/bonus/delete.do" class="d-inline"
                              onsubmit="return confirm('삭제하시겠습니까?')">
                            <sec:csrfInput/>
                            <input type="hidden" name="bonusId" value="${b.bonusId}"/>
                            <input type="hidden" name="payMonth" value="${payMonth}"/>
                            <button class="btn btn-sm btn-link text-danger p-0">삭제</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="8" class="text-center text-muted py-4">상여 내역이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
<p class="text-muted small mt-2">
    <i class="bi bi-info-circle"></i> PLANNED 상여는 해당 급여월 산정 시 자동으로 지급 항목에 포함되고,
    급여 확정 시 '반영완료'로 바뀝니다.
</p>

<script>
document.getElementById('selectAll').addEventListener('click', function(e){
    e.preventDefault();
    document.querySelectorAll('.emp-cb').forEach(function(cb){ cb.checked = true; });
});
document.getElementById('clearAll').addEventListener('click', function(e){
    e.preventDefault();
    document.querySelectorAll('.emp-cb').forEach(function(cb){ cb.checked = false; });
});
</script>
