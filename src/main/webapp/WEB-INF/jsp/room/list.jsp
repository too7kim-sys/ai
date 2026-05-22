<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>회의실 예약</title>
<h2 class="mb-3"><i class="bi bi-door-open"></i> 회의실 예약</h2>
<sec:authentication property="principal.userId" var="myUid" scope="page"/>

<div class="row g-3">
    <div class="col-md-4">
        <div class="card mb-3">
            <div class="card-header">회의실</div>
            <div class="list-group list-group-flush">
                <c:forEach var="r" items="${rooms}">
                    <a class="list-group-item list-group-item-action <c:if test='${roomId == r.roomId}'>active</c:if>"
                       href="${pageContext.request.contextPath}/room/list.do?roomId=${r.roomId}&day=${day}">
                        <strong>${r.roomNm}</strong>
                        <small class="d-block">${r.location} · 정원 ${r.capacity}명</small>
                        <small class="d-block text-muted">${r.equipments}</small>
                    </a>
                </c:forEach>
                <c:if test="${empty rooms}">
                    <div class="list-group-item text-muted text-center">등록된 회의실이 없습니다.</div>
                </c:if>
            </div>
        </div>

        <div class="card">
            <div class="card-header"><i class="bi bi-calendar-check"></i> 내 예약</div>
            <ul class="list-group list-group-flush">
                <c:forEach var="mr" items="${myReservations}">
                    <li class="list-group-item d-flex justify-content-between">
                        <div>
                            <strong>${mr.roomNm}</strong>
                            <small class="d-block text-muted">${mr.startDt} ~ ${fn:substring(mr.endDt, 11, 16)}</small>
                            <small class="text-muted">${mr.purpose}</small>
                        </div>
                        <form method="post" action="${pageContext.request.contextPath}/room/cancel.do" class="d-inline"
                              onsubmit="return confirm('취소하시겠습니까?')">
                            <sec:csrfInput/>
                            <input type="hidden" name="resId" value="${mr.resId}"/>
                            <input type="hidden" name="roomId" value="${roomId}"/>
                            <button class="btn btn-sm btn-outline-danger">취소</button>
                        </form>
                    </li>
                </c:forEach>
                <c:if test="${empty myReservations}">
                    <li class="list-group-item text-muted text-center">예약 없음</li>
                </c:if>
            </ul>
        </div>
    </div>

    <div class="col-md-8">
        <c:if test="${room == null}">
            <div class="alert alert-info">왼쪽에서 회의실을 선택하세요.</div>
        </c:if>
        <c:if test="${room != null}">
            <div class="card mb-3">
                <div class="card-header d-flex justify-content-between">
                    <span><strong>${room.roomNm}</strong> · ${room.location} · 정원 ${room.capacity}명</span>
                    <form method="get" class="d-flex">
                        <input type="hidden" name="roomId" value="${room.roomId}"/>
                        <input type="date" name="day" value="${day}" class="form-control form-control-sm me-2"/>
                        <button class="btn btn-sm btn-outline-secondary">조회</button>
                    </form>
                </div>
                <div class="card-body">
                    <h6 class="mb-3">📅 ${day} 예약 현황</h6>
                    <c:if test="${empty reservations}">
                        <div class="text-center text-muted py-3">예약 없음 — 자유롭게 예약 가능합니다.</div>
                    </c:if>
                    <c:forEach var="r" items="${reservations}">
                        <div class="border rounded p-2 mb-2">
                            <div class="d-flex justify-content-between">
                                <div>
                                    <i class="bi bi-clock"></i>
                                    <strong>${fn:substring(r.startDt, 11, 16)} ~ ${fn:substring(r.endDt, 11, 16)}</strong>
                                    <span class="badge bg-secondary ms-2">${r.userName}</span>
                                </div>
                                <c:set var="canCancel" value="${r.userId == myUid}"/>
                                <sec:authorize access="hasRole('ADMIN')">
                                    <c:set var="canCancel" value="${true}"/>
                                </sec:authorize>
                                <c:if test="${canCancel}">
                                    <form method="post" action="${pageContext.request.contextPath}/room/cancel.do"
                                          onsubmit="return confirm('이 예약을 취소하시겠습니까?')">
                                        <sec:csrfInput/>
                                        <input type="hidden" name="resId" value="${r.resId}"/>
                                        <input type="hidden" name="roomId" value="${room.roomId}"/>
                                        <button class="btn btn-sm btn-outline-danger py-0">취소</button>
                                    </form>
                                </c:if>
                            </div>
                            <c:if test="${not empty r.purpose}">
                                <div class="small text-muted mt-1">${r.purpose}</div>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div class="card">
                <div class="card-header">예약 추가</div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/room/reserve.do" class="row g-2">
                        <sec:csrfInput/>
                        <input type="hidden" name="roomId" value="${room.roomId}"/>
                        <div class="col-md-5">
                            <label class="form-label small">시작</label>
                            <input type="datetime-local" name="startDt" class="form-control" required value="${day}T09:00"/>
                        </div>
                        <div class="col-md-5">
                            <label class="form-label small">종료</label>
                            <input type="datetime-local" name="endDt" class="form-control" required value="${day}T10:00"/>
                        </div>
                        <div class="col-md-2 align-self-end d-grid">
                            <button class="btn btn-primary"><i class="bi bi-plus"></i> 예약</button>
                        </div>
                        <div class="col-12">
                            <label class="form-label small">목적</label>
                            <input type="text" name="purpose" class="form-control" placeholder="예: 주간 회의" maxlength="255"/>
                        </div>
                    </form>
                </div>
            </div>
        </c:if>
    </div>
</div>
