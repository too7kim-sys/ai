<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${v.vehicleId == null ? '차량 등록' : '차량 수정'}</title>
<h2 class="mb-3"><i class="bi bi-car-front"></i> ${v.vehicleId == null ? '차량 등록' : '차량 수정'}</h2>

<form method="post" action="${pageContext.request.contextPath}/vehicle/edit.do">
    <sec:csrfInput/>
    <c:if test="${v.vehicleId != null}">
        <input type="hidden" name="vehicleId" value="${v.vehicleId}"/>
    </c:if>
    <div class="card">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-4">
                    <label class="form-label">차량번호 *</label>
                    <input type="text" name="plateNo" class="form-control" value="${v.plateNo}" required maxlength="20"/>
                </div>
                <div class="col-md-5">
                    <label class="form-label">모델명 *</label>
                    <input type="text" name="modelNm" class="form-control" value="${v.modelNm}" required maxlength="100"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">연식</label>
                    <input type="number" name="yearModel" class="form-control" value="${v.yearModel}" min="1990" max="2030"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">취득일</label>
                    <input type="date" name="purchaseDt" class="form-control" value="${v.purchaseDt}"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">연료</label>
                    <select name="fuelTypeCd" class="form-select">
                        <option value="GASOLINE" <c:if test="${v.fuelTypeCd eq 'GASOLINE'}">selected</c:if>>가솔린</option>
                        <option value="DIESEL"   <c:if test="${v.fuelTypeCd eq 'DIESEL'}">selected</c:if>>디젤</option>
                        <option value="LPG"      <c:if test="${v.fuelTypeCd eq 'LPG'}">selected</c:if>>LPG</option>
                        <option value="HYBRID"   <c:if test="${v.fuelTypeCd eq 'HYBRID'}">selected</c:if>>하이브리드</option>
                        <option value="EV"       <c:if test="${v.fuelTypeCd eq 'EV'}">selected</c:if>>전기</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label">연비 (km/L)</label>
                    <input type="number" name="fuelEfficiency" class="form-control" value="${v.fuelEfficiency}" step="0.1"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">탑승 인원</label>
                    <input type="number" name="seats" class="form-control" value="${empty v.seats ? 5 : v.seats}" min="2" max="20"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">현재 누적 (km)</label>
                    <input type="number" name="currentMileage" class="form-control" value="${empty v.currentMileage ? 0 : v.currentMileage}" min="0"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">다음 정비일</label>
                    <input type="date" name="nextMaintenanceDt" class="form-control" value="${v.nextMaintenanceDt}"/>
                </div>
                <div class="col-12">
                    <label class="form-label">메모</label>
                    <textarea name="memo" class="form-control" rows="3">${v.memo}</textarea>
                </div>
            </div>
        </div>
        <div class="card-footer">
            <button class="btn btn-primary"><i class="bi bi-save"></i> 저장</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/vehicle/list.do">취소</a>
        </div>
    </div>
</form>
