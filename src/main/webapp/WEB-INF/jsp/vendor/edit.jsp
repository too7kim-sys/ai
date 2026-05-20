<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>거래처 ${vo.vendorId == null ? '등록' : '수정'}</title>
<h2 class="mb-3"><i class="bi bi-building-add"></i> 거래처 ${vo.vendorId == null ? '등록' : '수정'}</h2>
<form method="post" action="${pageContext.request.contextPath}/vendor/edit.do" class="row g-3">
    <sec:csrfInput/>
    <input type="hidden" name="vendorId" value="${vo.vendorId}"/>
    <div class="col-md-3"><label class="form-label">구분</label>
        <select class="form-select" name="vendorTypeCd" required>
            <option value="CUSTOMER" ${vo.vendorTypeCd=='CUSTOMER'?'selected':''}>고객</option>
            <option value="SUPPLIER" ${vo.vendorTypeCd=='SUPPLIER'?'selected':''}>공급사</option>
            <option value="BOTH" ${vo.vendorTypeCd=='BOTH'?'selected':''}>고객+공급사</option>
        </select>
    </div>
    <div class="col-md-3"><label class="form-label">사업자번호</label><input class="form-control" name="bizNo" value="${vo.bizNo}" required ${vo.vendorId != null ? 'readonly' : ''}/></div>
    <div class="col-md-3"><label class="form-label">과세 유형</label>
        <select class="form-select" name="taxTypeCd">
            <option value="GENERAL" ${vo.taxTypeCd=='GENERAL'?'selected':''}>일반</option>
            <option value="SIMPLE" ${vo.taxTypeCd=='SIMPLE'?'selected':''}>간이</option>
            <option value="EXEMPT" ${vo.taxTypeCd=='EXEMPT'?'selected':''}>면세</option>
        </select>
    </div>
    <div class="col-md-3"><label class="form-label">사용여부</label>
        <select class="form-select" name="useYn"><option value="Y" ${vo.useYn!='N'?'selected':''}>Y</option><option value="N" ${vo.useYn=='N'?'selected':''}>N</option></select>
    </div>
    <div class="col-md-6"><label class="form-label">상호</label><input class="form-control" name="companyNm" value="${vo.companyNm}" required/></div>
    <div class="col-md-3"><label class="form-label">대표자</label><input class="form-control" name="ceoNm" value="${vo.ceoNm}"/></div>
    <div class="col-md-3"><label class="form-label">업태</label><input class="form-control" name="bizKind" value="${vo.bizKind}"/></div>
    <div class="col-md-9"><label class="form-label">종목</label><input class="form-control" name="bizItem" value="${vo.bizItem}"/></div>
    <div class="col-md-3"><label class="form-label">우편번호</label><input class="form-control" name="zipcode" value="${vo.zipcode}"/></div>
    <div class="col-12"><label class="form-label">주소</label><input class="form-control" name="address" value="${vo.address}"/></div>
    <div class="col-md-4"><label class="form-label">담당자</label><input class="form-control" name="contactNm" value="${vo.contactNm}"/></div>
    <div class="col-md-4"><label class="form-label">담당자 전화</label><input class="form-control" name="contactPhone" value="${vo.contactPhone}"/></div>
    <div class="col-md-4"><label class="form-label">담당자 메일</label><input class="form-control" name="contactEmail" value="${vo.contactEmail}"/></div>
    <div class="col-md-3"><label class="form-label">은행</label><input class="form-control" name="bankCd" value="${vo.bankCd}"/></div>
    <div class="col-md-5"><label class="form-label">계좌</label><input class="form-control" name="bankAccount" value="${vo.bankAccount}"/></div>
    <div class="col-md-4"><label class="form-label">예금주</label><input class="form-control" name="bankHolder" value="${vo.bankHolder}"/></div>
    <div class="col-12"><label class="form-label">메모</label><textarea class="form-control" name="memo" rows="2">${vo.memo}</textarea></div>
    <div class="col-12">
        <button class="btn btn-primary">저장</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/vendor/list.do">취소</a>
    </div>
</form>
