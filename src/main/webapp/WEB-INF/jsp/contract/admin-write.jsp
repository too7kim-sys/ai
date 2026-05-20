<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>근로계약 작성</title>
<h2 class="mb-3"><i class="bi bi-pencil"></i> 근로계약 신규 작성</h2>
<form method="post" action="${pageContext.request.contextPath}/contract/admin/write.do" class="row g-3">
    <sec:csrfInput/>
    <div class="col-md-3"><label class="form-label">사원 ID</label><input class="form-control" name="userId" value="${userId}" required/></div>
    <div class="col-md-4"><label class="form-label">계약서 양식</label>
        <select class="form-select" name="templateId" required>
            <c:forEach var="t" items="${templates}">
                <option value="${t.templateId}" data-type="${t.contractTypeCd}">${t.templateNm}</option>
            </c:forEach>
        </select>
    </div>
    <div class="col-md-2"><label class="form-label">계약 종류</label>
        <select class="form-select" name="contractTypeCd" required>
            <option value="REGULAR">정규직</option>
            <option value="FIXED_TERM">기간제</option>
            <option value="PART_TIME">단시간</option>
            <option value="TEMP">시용</option>
            <option value="INTERN">인턴</option>
        </select>
    </div>
    <div class="col-md-3"><label class="form-label">연봉계약 ID (선택)</label><input class="form-control" name="salaryContractId"/></div>
    <div class="col-md-3"><label class="form-label">시작일</label><input type="date" class="form-control" name="startDt" required/></div>
    <div class="col-md-3"><label class="form-label">종료일</label><input type="date" class="form-control" name="endDt"/></div>
    <div class="col-md-3"><label class="form-label">주 소정근로시간</label><input class="form-control" name="workHoursPerWeek" value="40"/></div>
    <div class="col-md-3"></div>
    <div class="col-md-6"><label class="form-label">근무 장소</label><input class="form-control" name="workplace" required/></div>
    <div class="col-md-6"><label class="form-label">업무 내용</label><input class="form-control" name="jobDescription" required/></div>
    <div class="col-12"><label class="form-label">특약 / 비고</label><textarea class="form-control" name="specialTerms" rows="3"></textarea></div>
    <div class="col-12">
        <button class="btn btn-primary"><i class="bi bi-arrow-right"></i> 미리보기로 이동</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/contract/admin/list.do">취소</a>
    </div>
</form>
