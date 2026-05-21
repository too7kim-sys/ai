<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>입출금 등록</title>
<h2 class="mb-3"><i class="bi bi-bank"></i> 입출금 등록</h2>

<c:if test="${invoice != null}">
    <div class="alert alert-info">
        <i class="bi bi-receipt"></i> 인보이스 <code>${invoice.invoiceNo}</code> · ${invoice.vendorNm}
        · 총액 <fmt:formatNumber value="${invoice.amountTotal}" type="number"/>원
        · 잔액 <strong class="text-danger"><fmt:formatNumber value="${invoice.remainingAmount}" type="number"/></strong>원
    </div>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/payment/edit.do">
    <sec:csrfInput/>
    <c:if test="${vo.invoiceId != null}">
        <input type="hidden" name="invoiceId" value="${vo.invoiceId}"/>
    </c:if>
    <div class="card">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-3">
                    <label class="form-label">유형 *</label>
                    <select name="payTypeCd" class="form-select" required>
                        <option value="INCOMING" <c:if test="${vo.payTypeCd eq 'INCOMING'}">selected</c:if>>입금 (받음)</option>
                        <option value="OUTGOING" <c:if test="${vo.payTypeCd eq 'OUTGOING'}">selected</c:if>>출금 (보냄)</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label">일자</label>
                    <input type="date" name="payDt" class="form-control" value="${vo.payDt}"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">금액 *</label>
                    <input type="number" name="amount" class="form-control text-end" value="${vo.amount}" required step="1" min="1"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">결제 수단</label>
                    <select name="methodCd" class="form-select">
                        <option value="BANK_TRANSFER" <c:if test="${empty vo.methodCd || vo.methodCd eq 'BANK_TRANSFER'}">selected</c:if>>계좌이체</option>
                        <option value="CARD"          <c:if test="${vo.methodCd eq 'CARD'}">selected</c:if>>카드</option>
                        <option value="CASH"          <c:if test="${vo.methodCd eq 'CASH'}">selected</c:if>>현금</option>
                        <option value="CHECK"         <c:if test="${vo.methodCd eq 'CHECK'}">selected</c:if>>수표</option>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label">거래 상대</label>
                    <input type="text" name="counterpartNm" class="form-control" value="${vo.counterpartNm}" maxlength="150"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">은행</label>
                    <input type="text" name="bankCd" class="form-control" value="${vo.bankCd}" maxlength="10"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">계좌</label>
                    <input type="text" name="bankAccount" class="form-control" value="${vo.bankAccount}" maxlength="50"/>
                </div>
                <div class="col-12">
                    <label class="form-label">메모</label>
                    <textarea name="memo" class="form-control" rows="3">${vo.memo}</textarea>
                </div>
            </div>
        </div>
        <div class="card-footer">
            <button class="btn btn-primary"><i class="bi bi-save"></i> 등록</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/payment/list.do">취소</a>
        </div>
    </div>
</form>
