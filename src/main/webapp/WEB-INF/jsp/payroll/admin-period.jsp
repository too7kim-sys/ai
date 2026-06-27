<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>급여 산정 생성</title>
<h2 class="mb-4"><i class="bi bi-calculator"></i> 급여 산정 시작</h2>
<div class="card shadow-sm" style="max-width:520px;">
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/payroll/admin/generate.do">
            <sec:csrfInput/>
            <div class="mb-3">
                <label class="form-label">대상 월</label>
                <input type="month" name="payMonth" class="form-control" required/>
                <div class="form-text">선택한 달의 재직자 전체를 일괄 생성합니다 (이미 있으면 건너뜁니다).</div>
            </div>
            <button class="btn btn-primary"><i class="bi bi-play-fill"></i> 산정 생성</button>
        </form>
    </div>
</div>
