<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>차량 예약</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-car-front"></i> 차량 예약</h2>
    <sec:authorize access="hasAnyRole('ADMIN','HR_MANAGER')">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/vehicle/edit.do">
            <i class="bi bi-plus"></i> 차량 등록
        </a>
    </sec:authorize>
</div>

<div class="row g-3">
    <!-- 차량 목록 -->
    <div class="col-md-4">
        <div class="card mb-3">
            <div class="card-header">차량 ${fn:length(vehicles)}대</div>
            <div class="list-group list-group-flush">
                <c:forEach var="v" items="${vehicles}">
                    <a class="list-group-item list-group-item-action d-flex justify-content-between
                              <c:if test='${vehicleId == v.vehicleId}'>active</c:if>"
                       href="${pageContext.request.contextPath}/vehicle/list.do?vehicleId=${v.vehicleId}&day=${day}">
                        <div>
                            <strong>${v.plateNo}</strong>
                            <div class="small">${v.modelNm}
                                <c:if test="${not empty v.yearModel}">(${v.yearModel})</c:if>
                            </div>
                            <small class="text-muted">${v.fuelTypeCd} · ${v.seats}인승 · ${v.currentMileage}km</small>
                        </div>
                        <span>
                            <c:choose>
                                <c:when test="${v.statusCd eq 'AVAILABLE'}"><span class="badge bg-success">가능</span></c:when>
                                <c:when test="${v.statusCd eq 'IN_USE'}"><span class="badge bg-warning text-dark">운행</span></c:when>
                                <c:when test="${v.statusCd eq 'MAINTENANCE'}"><span class="badge bg-info text-dark">점검</span></c:when>
                                <c:when test="${v.statusCd eq 'RETIRED'}"><span class="badge bg-secondary">폐차</span></c:when>
                            </c:choose>
                        </span>
                    </a>
                </c:forEach>
            </div>
        </div>

        <!-- 내 예약 -->
        <div class="card">
            <div class="card-header"><i class="bi bi-calendar-check"></i> 내 예약</div>
            <ul class="list-group list-group-flush">
                <c:forEach var="r" items="${myReservations}">
                    <li class="list-group-item d-flex justify-content-between align-items-start">
                        <div>
                            <strong>${r.plateNo}</strong> ${r.modelNm}
                            <small class="d-block text-muted">${r.startDt} ~ ${fn:substring(r.endDt, 11, 16)}</small>
                            <small class="text-muted">${r.destination}</small>
                            <c:choose>
                                <c:when test="${r.statusCd eq 'APPROVED'}"><span class="badge bg-info text-dark">예약</span></c:when>
                                <c:when test="${r.statusCd eq 'IN_USE'}"><span class="badge bg-warning text-dark">운행 중</span></c:when>
                                <c:when test="${r.statusCd eq 'COMPLETED'}"><span class="badge bg-success">완료</span></c:when>
                                <c:when test="${r.statusCd eq 'CANCELED'}"><span class="badge bg-secondary">취소</span></c:when>
                            </c:choose>
                        </div>
                        <c:if test="${r.statusCd ne 'COMPLETED' and r.statusCd ne 'CANCELED'}">
                            <form method="post" action="${pageContext.request.contextPath}/vehicle/cancel.do" class="d-inline"
                                  onsubmit="return confirm('예약을 취소할까요?')">
                                <sec:csrfInput/><input type="hidden" name="reservationId" value="${r.reservationId}"/>
                                <input type="hidden" name="vehicleId" value="${vehicleId}"/>
                                <button class="btn btn-sm btn-outline-danger">취소</button>
                            </form>
                        </c:if>
                    </li>
                </c:forEach>
                <c:if test="${empty myReservations}">
                    <li class="list-group-item text-center text-muted">예약 없음</li>
                </c:if>
            </ul>
        </div>
    </div>

    <div class="col-md-8">
        <c:if test="${vehicle == null}">
            <div class="alert alert-info">왼쪽에서 차량을 선택하세요.</div>
        </c:if>
        <c:if test="${vehicle != null}">
            <div class="card mb-3">
                <div class="card-header d-flex justify-content-between">
                    <div>
                        <strong>${vehicle.plateNo}</strong> ${vehicle.modelNm}
                        <small class="text-muted ms-2">${vehicle.fuelTypeCd} · ${vehicle.seats}인승 · 누적 ${vehicle.currentMileage}km</small>
                    </div>
                    <form method="get" class="d-flex">
                        <input type="hidden" name="vehicleId" value="${vehicle.vehicleId}"/>
                        <input type="date" name="day" value="${day}" class="form-control form-control-sm me-2"/>
                        <button class="btn btn-sm btn-outline-secondary">조회</button>
                    </form>
                </div>
                <div class="card-body">
                    <h6 class="mb-3">📅 ${day} 예약 현황</h6>
                    <c:if test="${empty reservations}">
                        <div class="text-center text-muted py-3">예약 없음</div>
                    </c:if>
                    <c:forEach var="r" items="${reservations}">
                        <div class="border rounded p-2 mb-2">
                            <div class="d-flex justify-content-between">
                                <div>
                                    <i class="bi bi-clock"></i>
                                    <strong>${fn:substring(r.startDt, 11, 16)} ~ ${fn:substring(r.endDt, 11, 16)}</strong>
                                    <span class="badge bg-secondary ms-2">${r.userName}</span>
                                    <c:choose>
                                        <c:when test="${r.statusCd eq 'APPROVED'}"><span class="badge bg-info text-dark ms-1">예약</span></c:when>
                                        <c:when test="${r.statusCd eq 'IN_USE'}"><span class="badge bg-warning text-dark ms-1">운행 중</span></c:when>
                                        <c:when test="${r.statusCd eq 'COMPLETED'}"><span class="badge bg-success ms-1">완료</span></c:when>
                                    </c:choose>
                                </div>
                                <div>
                                    <c:if test="${r.statusCd eq 'APPROVED'}">
                                        <form method="post" action="${pageContext.request.contextPath}/vehicle/start.do" class="d-inline">
                                            <sec:csrfInput/><input type="hidden" name="reservationId" value="${r.reservationId}"/>
                                            <input type="hidden" name="vehicleId" value="${vehicle.vehicleId}"/>
                                            <button class="btn btn-sm btn-warning"><i class="bi bi-play"></i> 출발</button>
                                        </form>
                                    </c:if>
                                    <c:if test="${r.statusCd eq 'IN_USE'}">
                                        <form method="post" action="${pageContext.request.contextPath}/vehicle/complete.do" class="d-inline">
                                            <sec:csrfInput/><input type="hidden" name="reservationId" value="${r.reservationId}"/>
                                            <input type="hidden" name="vehicleId" value="${vehicle.vehicleId}"/>
                                            <input type="number" name="mileageEnd" class="form-control form-control-sm d-inline-block" style="width:90px;" placeholder="km" required min="${vehicle.currentMileage}"/>
                                            <button class="btn btn-sm btn-success"><i class="bi bi-stop"></i> 도착</button>
                                        </form>
                                    </c:if>
                                </div>
                            </div>
                            <small class="text-muted d-block mt-1">
                                ${r.purpose}
                                <c:if test="${not empty r.destination}"> · 목적지: ${r.destination}</c:if>
                                · ${r.passengerCnt}명
                                <c:if test="${not empty r.mileageEnd}"> · 운행 ${r.mileageEnd - r.mileageStart}km</c:if>
                            </small>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <c:if test="${vehicle.statusCd ne 'RETIRED' and vehicle.statusCd ne 'MAINTENANCE'}">
            <div class="card">
                <div class="card-header">예약 추가</div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/vehicle/reserve.do" class="row g-2">
                        <sec:csrfInput/>
                        <input type="hidden" name="vehicleId" value="${vehicle.vehicleId}"/>
                        <div class="col-md-4">
                            <label class="form-label small">시작</label>
                            <input type="datetime-local" name="startDt" class="form-control" required value="${day}T09:00"/>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label small">종료</label>
                            <input type="datetime-local" name="endDt" class="form-control" required value="${day}T18:00"/>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label small">탑승 인원</label>
                            <input type="number" name="passengerCnt" class="form-control" value="1" min="1" max="${vehicle.seats}"/>
                        </div>
                        <div class="col-md-2 align-self-end d-grid">
                            <button class="btn btn-primary"><i class="bi bi-plus"></i> 예약</button>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small">목적</label>
                            <input type="text" name="purpose" class="form-control" placeholder="예: 고객 미팅" maxlength="500"/>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small">목적지</label>
                            <input type="text" name="destination" class="form-control" placeholder="예: 강남" maxlength="200"/>
                        </div>
                    </form>
                </div>
            </div>
            </c:if>
        </c:if>
    </div>
</div>
