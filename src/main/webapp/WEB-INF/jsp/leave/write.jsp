<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>휴가 신청</title>
<h2 class="mb-3"><i class="bi bi-airplane"></i> 휴가 신청</h2>
<div class="alert alert-info small">
    내 연차 잔여: <strong>${balance.remaining()}일</strong> (지급 ${balance.annualGiven} / 사용 ${balance.annualUsed})
    <span class="ms-3 text-muted">시간연차 환산: 1일 = 8시간</span>
</div>
<form method="post" action="${pageContext.request.contextPath}/leave/write.do" class="row g-3" id="leaveForm">
    <sec:csrfInput/>
    <div class="col-md-3"><label class="form-label">휴가 종류</label>
        <select class="form-select" name="leaveTypeCd" id="leaveTypeCd" required>
            <option value="ANNUAL">연차</option>
            <option value="HALF">반차</option>
            <option value="HOURLY">시간연차</option>
            <option value="SICK">병가</option>
            <option value="FAMILY">경조사</option>
            <option value="OTHER">기타</option>
        </select>
    </div>
    <div class="col-md-3" id="startDtWrap">
        <label class="form-label">시작일</label>
        <input type="date" class="form-control" name="startDt" id="startDt" required/>
    </div>
    <div class="col-md-3" id="endDtWrap">
        <label class="form-label">종료일</label>
        <input type="date" class="form-control" name="endDt" id="endDt"/>
    </div>

    <%-- 시간연차 전용 입력 --%>
    <div class="col-md-3" id="startTimeWrap" style="display:none">
        <label class="form-label">시작 시각</label>
        <input type="time" class="form-control" name="startTime" id="startTime"
               step="3600" min="09:00" max="17:00" value="09:00"/>
    </div>
    <div class="col-md-3" id="endTimeWrap" style="display:none">
        <label class="form-label">종료 시각</label>
        <input type="time" class="form-control" name="endTime" id="endTime"
               step="3600" min="10:00" max="18:00" value="10:00"/>
    </div>
    <div class="col-12" id="hourlyHint" style="display:none">
        <div class="small text-muted">
            <i class="bi bi-info-circle"></i>
            시간연차는 업무 시간(09:00~18:00) 내에서 1시간 단위로 신청합니다. 점심 시간(12:00~13:00)은 제외됩니다.
            예상 차감: <strong id="hourlyEst">0.125</strong>일
        </div>
    </div>

    <%-- 반차 전용 입력 — 비-HALF 모드에서는 disabled 로 폼 전송에서 제외. --%>
    <div class="col-md-3" id="halfTypeWrap" style="display:none">
        <label class="form-label">반차 구분</label>
        <div class="btn-group w-100" role="group">
            <input type="radio" class="btn-check" name="halfTypeCd" id="halfAm" value="AM" checked disabled>
            <label class="btn btn-outline-primary" for="halfAm">오전 (09~13)</label>
            <input type="radio" class="btn-check" name="halfTypeCd" id="halfPm" value="PM" disabled>
            <label class="btn btn-outline-primary" for="halfPm">오후 (13~18)</label>
        </div>
    </div>

    <div class="col-12"><label class="form-label">사유</label>
        <textarea class="form-control" name="reason" rows="3"></textarea></div>
    <div class="col-12">
        <label class="form-label">결재선 <small class="text-muted">(미선택 시 관리자에게 자동 지정)</small></label>
        <div>
            <button type="button" class="btn btn-outline-primary btn-sm"
                    onclick="openUserPicker({multi:true, multiSelect:'leaveApprovers', chipsContainer:'leaveApproverChips'})">
                <i class="bi bi-person-plus"></i> 결재선 사원 선택
            </button>
            <div id="leaveApproverChips" class="mt-2"></div>
            <select id="leaveApprovers" name="approverIds" multiple class="d-none"></select>
        </div>
    </div>
    <div class="col-12">
        <button class="btn btn-primary"><i class="bi bi-send"></i> 상신</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/leave/my.do">취소</a>
    </div>
</form>

<script>
(function () {
    var sel     = document.getElementById('leaveTypeCd');
    var endWrap = document.getElementById('endDtWrap');
    var endIn   = document.getElementById('endDt');
    var sTWrap  = document.getElementById('startTimeWrap');
    var eTWrap  = document.getElementById('endTimeWrap');
    var hint    = document.getElementById('hourlyHint');
    var est     = document.getElementById('hourlyEst');
    var halfWrap= document.getElementById('halfTypeWrap');
    var halfAm  = document.getElementById('halfAm');
    var halfPm  = document.getElementById('halfPm');
    var startDt = document.getElementById('startDt');
    var sTime   = document.getElementById('startTime');
    var eTime   = document.getElementById('endTime');

    function toMin(t) { var p = (t || '00:00').split(':'); return (+p[0]) * 60 + (+p[1]); }
    function updateEst() {
        var mins = toMin(eTime.value) - toMin(sTime.value);
        if (mins <= 0) { est.textContent = '0'; return; }
        est.textContent = (mins / 60 / 8).toFixed(3);
    }

    function syncMode() {
        var hourly = sel.value === 'HOURLY';
        var half   = sel.value === 'HALF';
        endWrap.style.display = hourly ? 'none' : '';
        endIn.required        = !hourly;
        sTWrap.style.display  = hourly ? '' : 'none';
        eTWrap.style.display  = hourly ? '' : 'none';
        hint.style.display    = hourly ? '' : 'none';
        sTime.required = hourly;
        eTime.required = hourly;
        halfWrap.style.display = half ? '' : 'none';
        halfAm.disabled = !half;
        halfPm.disabled = !half;
        // 반차는 단일 일자 — 종료일을 시작일로 자동 동기화
        if (half && startDt.value) endIn.value = startDt.value;
        if (hourly) updateEst();
    }

    sel.addEventListener('change', syncMode);
    sTime.addEventListener('change', updateEst);
    eTime.addEventListener('change', updateEst);
    startDt.addEventListener('change', function () {
        if (sel.value === 'HALF') endIn.value = startDt.value;
    });
    syncMode();
})();
</script>
