<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>대시보드</title>
<div class="container-fluid">
    <h2 class="mb-4"><i class="bi bi-speedometer2"></i> 대시보드</h2>
    <p class="lead">안녕하세요, <strong>${me.name}</strong>님. 오늘도 좋은 하루 되세요.</p>

    <div class="row g-3">
        <div class="col-md-3">
            <div class="card shadow-sm">
                <div class="card-body">
                    <div class="text-muted small">결재 대기</div>
                    <div class="display-6 text-primary">0</div>
                    <a href="${pageContext.request.contextPath}/approval/pending.do" class="small">바로가기 →</a>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card shadow-sm">
                <div class="card-body">
                    <div class="text-muted small">안 읽은 쪽지</div>
                    <div class="display-6 text-success">0</div>
                    <a href="${pageContext.request.contextPath}/message/inbox.do" class="small">바로가기 →</a>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card shadow-sm">
                <div class="card-body">
                    <div class="text-muted small">내 휴가 잔여</div>
                    <div class="display-6 text-info">15.0일</div>
                    <a href="${pageContext.request.contextPath}/leave/balance.do" class="small">바로가기 →</a>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card shadow-sm">
                <div class="card-body">
                    <div class="text-muted small">오늘 일정</div>
                    <div class="display-6 text-warning">0건</div>
                    <a href="${pageContext.request.contextPath}/calendar/main.do" class="small">바로가기 →</a>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-3 mt-3">
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-header"><i class="bi bi-megaphone"></i> 최근 공지</div>
                <div class="card-body">
                    <p class="text-muted small">초기 빌드 — 공지 데이터는 곧 시드됩니다.</p>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-header"><i class="bi bi-clock"></i> 출퇴근</div>
                <div class="card-body d-flex gap-2">
                    <button class="btn btn-success">출근</button>
                    <button class="btn btn-outline-secondary">퇴근</button>
                </div>
            </div>
        </div>
    </div>
</div>
