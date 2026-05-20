<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>휴가 신청</title>
<h2 class="mb-3"><i class="bi bi-airplane"></i> 휴가 신청</h2>
<div class="alert alert-info small">
    내 연차 잔여: <strong>${balance.remaining()}일</strong> (지급 ${balance.annualGiven} / 사용 ${balance.annualUsed})
</div>
<form method="post" action="${pageContext.request.contextPath}/leave/write.do" class="row g-3">
    <sec:csrfInput/>
    <div class="col-md-3"><label class="form-label">휴가 종류</label>
        <select class="form-select" name="leaveTypeCd" required>
            <option value="ANNUAL">연차</option>
            <option value="HALF">반차</option>
            <option value="SICK">병가</option>
            <option value="FAMILY">경조사</option>
            <option value="OTHER">기타</option>
        </select>
    </div>
    <div class="col-md-3"><label class="form-label">시작일</label><input type="date" class="form-control" name="startDt" required/></div>
    <div class="col-md-3"><label class="form-label">종료일</label><input type="date" class="form-control" name="endDt" required/></div>
    <div class="col-12"><label class="form-label">사유</label><textarea class="form-control" name="reason" rows="3"></textarea></div>
    <div class="col-12">
        <label class="form-label">결재선 (Ctrl+클릭 다중 선택, 미선택 시 관리자에게 자동 지정)</label>
        <select class="form-select" name="approverIds" multiple size="6">
            <c:forEach var="u" items="${approvers}">
                <option value="${u.userId}">${u.name} (${u.roleNm} / ${u.deptNm})</option>
            </c:forEach>
        </select>
    </div>
    <div class="col-12">
        <button class="btn btn-primary"><i class="bi bi-send"></i> 상신</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/leave/my.do">취소</a>
    </div>
</form>
