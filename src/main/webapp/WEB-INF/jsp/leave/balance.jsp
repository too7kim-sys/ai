<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<title>연차 잔여</title>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="mb-0"><i class="bi bi-calendar-check text-info"></i> 연차 잔여
        <small class="text-muted ms-2">${balance.year}년</small></h2>
    <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/leave/my.do">
        <i class="bi bi-list-ul"></i> 내 휴가 내역
    </a>
</div>

<div class="row g-3">
    <div class="col-md-4">
        <div class="card h-100 stat-card">
            <div class="card-body d-flex align-items-center justify-content-between">
                <div>
                    <div class="widget-label">올해 부여</div>
                    <div class="widget-value">${balance.annualGiven}<small class="text-muted fs-6 ms-1">일</small></div>
                    <div class="widget-trend text-muted">근속/정책 기준 연간 한도</div>
                </div>
                <span class="widget-icon bg-info-subtle text-info"><i class="bi bi-gift"></i></span>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card h-100 stat-card">
            <div class="card-body d-flex align-items-center justify-content-between">
                <div>
                    <div class="widget-label">올해 사용</div>
                    <div class="widget-value text-warning">${balance.annualUsed}<small class="text-muted fs-6 ms-1">일</small></div>
                    <div class="widget-trend text-muted">결재 완료 차감분</div>
                </div>
                <span class="widget-icon bg-warning-subtle text-warning"><i class="bi bi-clipboard-check"></i></span>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card h-100 stat-card border-primary">
            <div class="card-body d-flex align-items-center justify-content-between">
                <div>
                    <div class="widget-label">잔여</div>
                    <div class="widget-value text-primary">${balance.remaining()}<small class="text-muted fs-6 ms-1">일</small></div>
                    <div class="widget-trend text-muted">신청 가능</div>
                </div>
                <span class="widget-icon bg-primary-subtle text-primary"><i class="bi bi-airplane"></i></span>
            </div>
        </div>
    </div>
</div>

<div class="alert alert-info small mt-3">
    <i class="bi bi-info-circle"></i>
    시간연차는 8 시간 = 1 일로 환산해 차감됩니다. 결재가 반려·취소되면 잔여가 즉시 복구되며,
    근속에 따라 부여 일수는 매년 갱신됩니다.
</div>
