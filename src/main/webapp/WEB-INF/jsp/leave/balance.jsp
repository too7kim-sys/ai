<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<title>연차 잔여</title>
<h2 class="mb-3"><i class="bi bi-calendar-check"></i> 연차 잔여 (${balance.year})</h2>
<div class="row g-3">
    <div class="col-md-4"><div class="card"><div class="card-body"><div class="text-muted small">부여</div><div class="display-6">${balance.annualGiven}일</div></div></div></div>
    <div class="col-md-4"><div class="card"><div class="card-body"><div class="text-muted small">사용</div><div class="display-6 text-warning">${balance.annualUsed}일</div></div></div></div>
    <div class="col-md-4"><div class="card bg-primary text-white"><div class="card-body"><div class="small">잔여</div><div class="display-6">${balance.remaining()}일</div></div></div></div>
</div>
