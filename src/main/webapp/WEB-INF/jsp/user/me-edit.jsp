<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>내 정보 수정</title>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="mb-0"><i class="bi bi-person-gear"></i> 내 정보 수정</h2>
    <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/user/profile.do?userId=${u.userId}">
        <i class="bi bi-arrow-left"></i> 내 프로필
    </a>
</div>

<div class="alert alert-info small">
    <i class="bi bi-info-circle"></i>
    이메일·연락처·은행/계좌만 본인이 수정할 수 있습니다.
    이름·부서·직급·역할·입사일 등은 인사 담당자(HR) 에게 변경 요청하세요.
    경력·학력·교육이수 는 <a href="${pageContext.request.contextPath}/user/profile.do?userId=${u.userId}">내 프로필</a> 화면에서 직접 입력 가능합니다.
</div>

<form method="post" action="${pageContext.request.contextPath}/user/me.do" class="card card-body" autocomplete="off">
    <sec:csrfInput/>
    <div class="row g-3">
        <div class="col-md-6">
            <label class="form-label">이메일 <span class="text-danger">*</span></label>
            <input type="email" name="email" class="form-control" value="${u.email}" required/>
            <div class="form-text">로그인 ID 입니다. 변경하면 다음 로그인부터 새 이메일을 사용합니다.</div>
        </div>
        <div class="col-md-6">
            <label class="form-label">연락처</label>
            <input type="text" name="phone" class="form-control" value="${u.phone}" placeholder="010-0000-0000"/>
        </div>
        <div class="col-md-6">
            <label class="form-label">은행 코드</label>
            <input type="text" name="bankCd" class="form-control" value="${u.bankCd}" placeholder="088 (신한)"/>
        </div>
        <div class="col-md-6">
            <label class="form-label">계좌번호 <span class="text-muted small">(급여 입금)</span></label>
            <input type="text" name="bankAccount" class="form-control" value="${u.bankAccount}" placeholder="000-000-000000"/>
        </div>
    </div>
    <div class="mt-3">
        <button class="btn btn-primary"><i class="bi bi-check2"></i> 저장</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/user/profile.do?userId=${u.userId}">취소</a>
    </div>
</form>
