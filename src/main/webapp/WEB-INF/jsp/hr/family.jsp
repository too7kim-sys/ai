<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>부양가족</title>
<h2 class="mb-3"><i class="bi bi-people-fill"></i> 부양가족</h2>
<div class="alert alert-secondary small">
    부양가족 정보는 연말정산·자녀학자금·가족수당 등 급여 계산에 활용됩니다.
</div>

<div class="card mb-3">
    <div class="card-header"><i class="bi bi-plus-circle"></i> 가족 추가</div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/hr/family.do" class="row g-2">
            <sec:csrfInput/>
            <div class="col-md-2">
                <label class="form-label small mb-1">관계</label>
                <select name="relationCd" class="form-select form-select-sm" required>
                    <option value="SPOUSE">배우자</option>
                    <option value="CHILD">자녀</option>
                    <option value="PARENT">부모</option>
                    <option value="SIBLING">형제자매</option>
                    <option value="OTHER">기타</option>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label small mb-1">이름</label>
                <input type="text" name="name" class="form-control form-control-sm" required/>
            </div>
            <div class="col-md-2">
                <label class="form-label small mb-1">생년월일</label>
                <input type="date" name="birthDt" class="form-control form-control-sm"/>
            </div>
            <div class="col-md-2 align-self-end">
                <div class="form-check"><input class="form-check-input" type="checkbox" name="dependentYn" value="Y" checked id="dep"/>
                    <label class="form-check-label" for="dep">부양가족</label></div>
            </div>
            <div class="col-md-2 align-self-end">
                <div class="form-check"><input class="form-check-input" type="checkbox" name="elderlyYn" value="Y" id="elderly"/>
                    <label class="form-check-label" for="elderly">경로(70세+)</label></div>
                <div class="form-check"><input class="form-check-input" type="checkbox" name="disabledYn" value="Y" id="dis"/>
                    <label class="form-check-label" for="dis">장애</label></div>
            </div>
            <div class="col-md-2 d-grid align-self-end">
                <button class="btn btn-sm btn-primary">추가</button>
            </div>
        </form>
    </div>
</div>

<div class="card">
    <table class="table mb-0">
        <thead class="table-light">
        <tr><th>관계</th><th>이름</th><th>생년월일</th><th>부양</th><th>경로</th><th>장애</th><th></th></tr>
        </thead>
        <tbody>
        <c:forEach var="f" items="${families}">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${f.relationCd eq 'SPOUSE'}">배우자</c:when>
                        <c:when test="${f.relationCd eq 'CHILD'}">자녀</c:when>
                        <c:when test="${f.relationCd eq 'PARENT'}">부모</c:when>
                        <c:when test="${f.relationCd eq 'SIBLING'}">형제자매</c:when>
                        <c:otherwise>기타</c:otherwise>
                    </c:choose>
                </td>
                <td>${f.name}</td>
                <td>${f.birthDt}</td>
                <td><c:if test="${f.dependentYn eq 'Y'}"><span class="badge bg-success">Y</span></c:if></td>
                <td><c:if test="${f.elderlyYn eq 'Y'}"><span class="badge bg-info">Y</span></c:if></td>
                <td><c:if test="${f.disabledYn eq 'Y'}"><span class="badge bg-warning text-dark">Y</span></c:if></td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/hr/family/delete.do" class="d-inline"
                          onsubmit="return confirm('삭제하시겠습니까?')">
                        <sec:csrfInput/>
                        <input type="hidden" name="famId" value="${f.famId}"/>
                        <button class="btn btn-sm btn-outline-danger">삭제</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty families}">
            <tr><td colspan="7" class="text-center text-muted py-4">등록된 가족이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
