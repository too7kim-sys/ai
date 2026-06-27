<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${inv.invoiceId == null ? '인보이스 등록' : '인보이스 수정'}</title>
<h2 class="mb-3"><i class="bi bi-receipt"></i> ${inv.invoiceId == null ? '새 인보이스' : '인보이스 수정'}</h2>

<form method="post" action="${pageContext.request.contextPath}/invoice/edit.do">
    <sec:csrfInput/>
    <c:if test="${inv.invoiceId != null}">
        <input type="hidden" name="invoiceId" value="${inv.invoiceId}"/>
    </c:if>
    <div class="card">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-3">
                    <label class="form-label">방향 *</label>
                    <select name="directionCd" class="form-select" required>
                        <option value="OUT" <c:if test="${inv.directionCd eq 'OUT'}">selected</c:if>>매출 (발행)</option>
                        <option value="IN"  <c:if test="${inv.directionCd eq 'IN'}">selected</c:if>>매입 (수령)</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label class="form-label">인보이스 번호</label>
                    <input type="text" name="invoiceNo" class="form-control" value="${inv.invoiceNo}" placeholder="자동 생성"/>
                </div>
                <div class="col-md-5">
                    <label class="form-label">거래처 *</label>
                    <select name="vendorId" class="form-select" required>
                        <option value="">선택...</option>
                        <c:forEach var="v" items="${vendors}">
                            <option value="${v.vendorId}" <c:if test="${inv.vendorId == v.vendorId}">selected</c:if>>${v.companyNm}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label">연결 계약 (선택)</label>
                    <select name="bizContractId" class="form-select">
                        <option value="">없음</option>
                        <c:forEach var="cc" items="${contracts}">
                            <option value="${cc.bizContractId}" <c:if test="${inv.bizContractId == cc.bizContractId}">selected</c:if>>
                                ${cc.contractNo} - ${cc.title}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label">발행일</label>
                    <input type="date" name="issueDt" class="form-control" value="${inv.issueDt}"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">만기일</label>
                    <input type="date" name="dueDt" class="form-control" value="${inv.dueDt}"/>
                </div>
                <div class="col-md-4">
                    <label class="form-label">공급가액 *</label>
                    <input type="number" name="amountNet" class="form-control text-end" value="${inv.amountNet}" required step="1" min="0"/>
                </div>
                <div class="col-md-4">
                    <label class="form-label">세금계산서 발행</label>
                    <select name="taxInvoiceYn" class="form-select">
                        <option value="N" <c:if test="${empty inv.taxInvoiceYn || inv.taxInvoiceYn eq 'N'}">selected</c:if>>아니오</option>
                        <option value="Y" <c:if test="${inv.taxInvoiceYn eq 'Y'}">selected</c:if>>예</option>
                    </select>
                </div>
                <div class="col-12">
                    <label class="form-label">메모</label>
                    <textarea name="memo" class="form-control" rows="3">${inv.memo}</textarea>
                </div>
            </div>
        </div>
        <div class="card-footer">
            <button class="btn btn-primary"><i class="bi bi-save"></i> 저장</button>
            <a class="btn btn-outline-secondary"
               href="${pageContext.request.contextPath}/invoice/${inv.directionCd eq 'OUT' ? 'out' : 'in'}.do">취소</a>
        </div>
    </div>
</form>
