<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>일정</title>
<head>
<link href='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.css' rel='stylesheet'/>
<script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js'></script>
</head>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2><i class="bi bi-calendar3"></i> 일정</h2>
    <button class="btn btn-primary" type="button" data-bs-toggle="modal" data-bs-target="#evtModal" onclick="resetForm()">
        <i class="bi bi-plus"></i> 일정 추가
    </button>
</div>

<div class="card"><div class="card-body">
    <div id="calendar"></div>
</div></div>

<!-- 이벤트 입력 모달 -->
<div class="modal fade" id="evtModal" tabindex="-1">
  <div class="modal-dialog">
    <form method="post" action="${pageContext.request.contextPath}/calendar/save.do" class="modal-content">
        <sec:csrfInput/>
        <input type="hidden" name="evtId" id="evtId"/>
        <div class="modal-header"><h5 class="modal-title" id="modalTitle">새 일정</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
        <div class="modal-body">
            <div class="mb-2"><label class="form-label">제목</label>
                <input type="text" name="title" id="title" class="form-control" required/></div>
            <div class="row g-2 mb-2">
                <div class="col-md-6"><label class="form-label">시작</label>
                    <input type="datetime-local" name="startDt" id="startDt" class="form-control" required/></div>
                <div class="col-md-6"><label class="form-label">종료</label>
                    <input type="datetime-local" name="endDt" id="endDt" class="form-control" required/></div>
            </div>
            <div class="row g-2 mb-2">
                <div class="col-md-6"><label class="form-label">범위</label>
                    <select name="scopeCd" id="scopeCd" class="form-select">
                        <option value="PERSONAL">개인</option>
                        <option value="DEPT">부서 공유</option>
                        <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
                            <option value="COMPANY">전사</option>
                        </sec:authorize>
                    </select></div>
                <div class="col-md-6"><label class="form-label">색상</label>
                    <input type="color" name="color" id="color" class="form-control form-control-color" value="#0d6efd"/></div>
            </div>
            <div class="mb-2"><label class="form-label">메모</label>
                <textarea name="memo" id="memo" class="form-control" rows="3"></textarea></div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-outline-danger me-auto" id="deleteBtn" style="display:none;"
                    onclick="deleteEvent()"><i class="bi bi-trash"></i> 삭제</button>
            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">취소</button>
            <button type="submit" class="btn btn-primary">저장</button>
        </div>
    </form>
  </div>
</div>

<form id="deleteForm" method="post" action="${pageContext.request.contextPath}/calendar/delete.do" style="display:none;">
    <sec:csrfInput/>
    <input type="hidden" name="evtId" id="deleteEvtId"/>
</form>

<script>
const ctx = '${pageContext.request.contextPath}';
let modal;
document.addEventListener('DOMContentLoaded', function() {
    modal = new bootstrap.Modal(document.getElementById('evtModal'));
    const cal = new FullCalendar.Calendar(document.getElementById('calendar'), {
        initialView: 'dayGridMonth',
        locale: 'ko',
        headerToolbar: { left: 'prev,next today', center: 'title', right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek' },
        events: function(info, success, fail) {
            fetch(ctx + '/calendar/events.json?start=' + encodeURIComponent(info.startStr)
                + '&end=' + encodeURIComponent(info.endStr))
                .then(r => r.json()).then(success).catch(fail);
        },
        dateClick: function(info) {
            resetForm();
            document.getElementById('startDt').value = info.dateStr + 'T09:00';
            document.getElementById('endDt').value = info.dateStr + 'T10:00';
            modal.show();
        },
        eventClick: function(info) {
            const e = info.event;
            document.getElementById('evtId').value = e.id;
            document.getElementById('title').value = e.title;
            document.getElementById('startDt').value = e.start.toISOString().slice(0, 16);
            document.getElementById('endDt').value = e.end ? e.end.toISOString().slice(0, 16) : e.start.toISOString().slice(0, 16);
            document.getElementById('scopeCd').value = e.extendedProps.scope || 'PERSONAL';
            document.getElementById('color').value = e.backgroundColor || '#0d6efd';
            document.getElementById('memo').value = e.extendedProps.memo || '';
            document.getElementById('modalTitle').textContent = '일정 수정';
            document.getElementById('deleteBtn').style.display = 'inline-block';
            modal.show();
        }
    });
    cal.render();
});
function resetForm() {
    document.getElementById('evtId').value = '';
    document.getElementById('title').value = '';
    document.getElementById('memo').value = '';
    document.getElementById('scopeCd').value = 'PERSONAL';
    document.getElementById('color').value = '#0d6efd';
    document.getElementById('modalTitle').textContent = '새 일정';
    document.getElementById('deleteBtn').style.display = 'none';
}
function deleteEvent() {
    if (!confirm('일정을 삭제하시겠습니까?')) return;
    document.getElementById('deleteEvtId').value = document.getElementById('evtId').value;
    document.getElementById('deleteForm').submit();
}
</script>
