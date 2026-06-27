<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${c.bizContractId == null ? '계약 등록' : '계약 수정'}</title>
<h2 class="mb-3"><i class="bi bi-briefcase"></i> ${c.bizContractId == null ? '새 계약' : '계약 수정'}</h2>

<form method="post" action="${pageContext.request.contextPath}/biz-contract/edit.do">
    <sec:csrfInput/>
    <c:if test="${c.bizContractId != null}">
        <input type="hidden" name="bizContractId" value="${c.bizContractId}"/>
    </c:if>
    <div class="card">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label">계약명 *</label>
                    <input type="text" name="title" class="form-control" value="${c.title}" required maxlength="200"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">계약번호</label>
                    <input type="text" name="contractNo" class="form-control" value="${c.contractNo}" maxlength="50" placeholder="자동 생성"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">유형 *</label>
                    <select name="contractTypeCd" class="form-select" required>
                        <option value="SERVICE"     <c:if test="${c.contractTypeCd eq 'SERVICE'}">selected</c:if>>용역</option>
                        <option value="GOODS"       <c:if test="${c.contractTypeCd eq 'GOODS'}">selected</c:if>>물품</option>
                        <option value="MAINTENANCE" <c:if test="${c.contractTypeCd eq 'MAINTENANCE'}">selected</c:if>>유지보수</option>
                        <option value="SUBSCRIPTION"<c:if test="${c.contractTypeCd eq 'SUBSCRIPTION'}">selected</c:if>>구독</option>
                        <option value="OTHER"       <c:if test="${c.contractTypeCd eq 'OTHER'}">selected</c:if>>기타</option>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label">거래처 *</label>
                    <select name="vendorId" class="form-select" required>
                        <option value="">선택...</option>
                        <c:forEach var="v" items="${vendors}">
                            <option value="${v.vendorId}" <c:if test="${c.vendorId == v.vendorId}">selected</c:if>>${v.companyNm} (${v.bizNo})</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label">시작일 *</label>
                    <input type="date" name="startDt" class="form-control" value="${c.startDt}" required/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">종료일</label>
                    <input type="date" name="endDt" class="form-control" value="${c.endDt}"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">공급가액</label>
                    <input type="number" name="amountNet" class="form-control text-end" value="${c.amountNet}" step="1" min="0"/>
                </div>
                <div class="col-md-2">
                    <label class="form-label">통화</label>
                    <select name="currencyCd" class="form-select">
                        <option value="KRW" <c:if test="${empty c.currencyCd || c.currencyCd eq 'KRW'}">selected</c:if>>KRW</option>
                        <option value="USD" <c:if test="${c.currencyCd eq 'USD'}">selected</c:if>>USD</option>
                        <option value="JPY" <c:if test="${c.currencyCd eq 'JPY'}">selected</c:if>>JPY</option>
                        <option value="EUR" <c:if test="${c.currencyCd eq 'EUR'}">selected</c:if>>EUR</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label">결제 조건</label>
                    <select name="paymentTermsCd" class="form-select">
                        <option value="LUMPSUM"    <c:if test="${empty c.paymentTermsCd || c.paymentTermsCd eq 'LUMPSUM'}">selected</c:if>>일시불</option>
                        <option value="MONTHLY"    <c:if test="${c.paymentTermsCd eq 'MONTHLY'}">selected</c:if>>매월</option>
                        <option value="QUARTERLY"  <c:if test="${c.paymentTermsCd eq 'QUARTERLY'}">selected</c:if>>분기</option>
                        <option value="YEARLY"     <c:if test="${c.paymentTermsCd eq 'YEARLY'}">selected</c:if>>연간</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">결제일</label>
                    <input type="number" name="paymentDayOfMonth" class="form-control" value="${c.paymentDayOfMonth}" min="1" max="31"/>
                </div>
                <div class="col-md-2">
                    <label class="form-label">VAT 포함</label>
                    <select name="vatIncludedYn" class="form-select">
                        <option value="N" <c:if test="${empty c.vatIncludedYn || c.vatIncludedYn eq 'N'}">selected</c:if>>아니오 (별도 10%)</option>
                        <option value="Y" <c:if test="${c.vatIncludedYn eq 'Y'}">selected</c:if>>예 (총액 입력)</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">자동 갱신</label>
                    <select name="autoRenewYn" class="form-select">
                        <option value="N" <c:if test="${empty c.autoRenewYn || c.autoRenewYn eq 'N'}">selected</c:if>>아니오</option>
                        <option value="Y" <c:if test="${c.autoRenewYn eq 'Y'}">selected</c:if>>예</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">갱신 알림 (일)</label>
                    <input type="number" name="renewNoticeDays" class="form-control" value="${empty c.renewNoticeDays ? 30 : c.renewNoticeDays}" min="1"/>
                </div>
                <div class="col-12">
                    <label class="form-label">메모</label>
                    <textarea name="memo" class="form-control" rows="4">${c.memo}</textarea>
                </div>
            </div>
        </div>
        <div class="card-footer">
            <button class="btn btn-primary"><i class="bi bi-save"></i> 저장</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/biz-contract/list.do">취소</a>
        </div>
    </div>
</form>
