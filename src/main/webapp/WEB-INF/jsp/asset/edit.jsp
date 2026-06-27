<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${a.assetId == null ? '자산 등록' : '자산 수정'}</title>
<h2 class="mb-3"><i class="bi bi-laptop"></i> ${a.assetId == null ? '자산 등록' : '자산 수정'}</h2>

<form method="post" action="${pageContext.request.contextPath}/asset/edit.do">
    <sec:csrfInput/>
    <c:if test="${a.assetId != null}">
        <input type="hidden" name="assetId" value="${a.assetId}"/>
    </c:if>
    <div class="card">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-3">
                    <label class="form-label">자산번호</label>
                    <input type="text" name="assetNo" class="form-control" value="${a.assetNo}" placeholder="자동 생성"/>
                </div>
                <div class="col-md-6">
                    <label class="form-label">자산명 *</label>
                    <input type="text" name="assetNm" class="form-control" value="${a.assetNm}" required maxlength="150"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">분류 *</label>
                    <select name="categoryCd" class="form-select" required>
                        <option value="LAPTOP"    <c:if test="${a.categoryCd eq 'LAPTOP'}">selected</c:if>>노트북</option>
                        <option value="DESKTOP"   <c:if test="${a.categoryCd eq 'DESKTOP'}">selected</c:if>>데스크탑</option>
                        <option value="MONITOR"   <c:if test="${a.categoryCd eq 'MONITOR'}">selected</c:if>>모니터</option>
                        <option value="PHONE"     <c:if test="${a.categoryCd eq 'PHONE'}">selected</c:if>>휴대폰</option>
                        <option value="PRINTER"   <c:if test="${a.categoryCd eq 'PRINTER'}">selected</c:if>>프린터</option>
                        <option value="HEADSET"   <c:if test="${a.categoryCd eq 'HEADSET'}">selected</c:if>>헤드셋</option>
                        <option value="FURNITURE" <c:if test="${a.categoryCd eq 'FURNITURE'}">selected</c:if>>가구</option>
                        <option value="OTHER"     <c:if test="${a.categoryCd eq 'OTHER'}">selected</c:if>>기타</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label class="form-label">브랜드</label>
                    <input type="text" name="brand" class="form-control" value="${a.brand}" maxlength="100"/>
                </div>
                <div class="col-md-4">
                    <label class="form-label">모델명</label>
                    <input type="text" name="modelNm" class="form-control" value="${a.modelNm}" maxlength="150"/>
                </div>
                <div class="col-md-4">
                    <label class="form-label">시리얼 번호</label>
                    <input type="text" name="serialNo" class="form-control" value="${a.serialNo}" maxlength="100"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">취득일</label>
                    <input type="date" name="purchaseDt" class="form-control" value="${a.purchaseDt}"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">취득가</label>
                    <input type="number" name="purchaseAmount" class="form-control text-end" value="${a.purchaseAmount}" step="1" min="0"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">감가상각 (개월)</label>
                    <input type="number" name="depreciationMonths" class="form-control" value="${empty a.depreciationMonths ? 36 : a.depreciationMonths}" min="1" max="120"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">초기 상태</label>
                    <select name="statusCd" class="form-select" <c:if test="${a.assetId != null}">disabled</c:if>>
                        <option value="IN_STOCK">재고</option>
                        <option value="IN_USE">사용 중</option>
                    </select>
                </div>
                <div class="col-12">
                    <label class="form-label">위치</label>
                    <input type="text" name="location" class="form-control" value="${a.location}" maxlength="200"/>
                </div>
                <div class="col-12">
                    <label class="form-label">메모</label>
                    <textarea name="memo" class="form-control" rows="3">${a.memo}</textarea>
                </div>
            </div>
        </div>
        <div class="card-footer">
            <button class="btn btn-primary"><i class="bi bi-save"></i> 저장</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/asset/list.do">취소</a>
        </div>
    </div>
</form>
