<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>지출결의 작성</title>
<h2 class="mb-3"><i class="bi bi-credit-card"></i> 지출결의서 작성</h2>
<form method="post" action="${pageContext.request.contextPath}/expense/write.do">
    <sec:csrfInput/>
    <div class="row g-3 mb-3">
        <div class="col-md-6"><label class="form-label">제목</label><input class="form-control" name="title" required/></div>
        <div class="col-12"><label class="form-label">사용 목적</label><textarea class="form-control" name="purpose" rows="2"></textarea></div>
    </div>
    <h5 class="mt-4">지출 라인 <small class="text-muted">- 행 추가/삭제 가능</small></h5>
    <table class="table table-bordered" id="itemTable">
        <thead class="table-light"><tr>
            <th>사용일</th><th>분류</th><th>계정</th><th>사용처</th>
            <th>공급가</th><th>부가세</th><th>결제수단</th><th>증빙</th><th></th>
        </tr></thead>
        <tbody>
            <tr class="item-row">
                <td><input type="date" class="form-control form-control-sm" name="expenseDt" required/></td>
                <td><select class="form-select form-select-sm" name="categoryCd">
                    <option value="MEAL">식대</option><option value="TRANSPORT">교통비</option>
                    <option value="ENTERTAIN">접대비</option><option value="SUPPLIES">소모품</option>
                    <option value="EDU">교육</option><option value="MEETING">회의비</option>
                    <option value="TRIP">출장비</option><option value="ETC">기타</option>
                </select></td>
                <td><select class="form-select form-select-sm" name="accountCd">
                    <option value="521">521 복리후생</option><option value="513">513 접대비</option>
                    <option value="522">522 여비교통</option><option value="530">530 소모품</option>
                    <option value="532">532 교육훈련</option><option value="533">533 회의비</option>
                </select></td>
                <td><input class="form-control form-control-sm" name="vendorNm"/></td>
                <td><input type="number" class="form-control form-control-sm" name="netAmount" value="0"/></td>
                <td><input type="number" class="form-control form-control-sm" name="vatAmount" value="0"/></td>
                <td><select class="form-select form-select-sm" name="paymentMethodCd">
                    <option value="CORPORATE_CARD">법인카드</option>
                    <option value="PERSONAL_CARD">개인카드</option>
                    <option value="CASH">현금</option>
                    <option value="BANK_TRANSFER">이체</option>
                </select></td>
                <td><select class="form-select form-select-sm" name="receiptTypeCd">
                    <option value="CARD">카드영수증</option>
                    <option value="TAX_INVOICE">세금계산서</option>
                    <option value="CASH_RECEIPT">현금영수증</option>
                    <option value="SIMPLE_RECEIPT">간이영수증</option>
                </select></td>
                <td><button type="button" class="btn btn-sm btn-outline-danger" onclick="rmRow(this)"><i class="bi bi-x"></i></button></td>
            </tr>
        </tbody>
    </table>
    <button type="button" class="btn btn-outline-secondary btn-sm mb-3" onclick="addRow()"><i class="bi bi-plus"></i> 라인 추가</button>
    <div class="mb-3">
        <label class="form-label">결재선 (Ctrl+클릭 다중 선택, 미선택 시 재무팀 자동)</label>
        <select class="form-select" name="approverIds" multiple size="5">
            <c:forEach var="u" items="${approvers}">
                <option value="${u.userId}">${u.name} (${u.roleNm} / ${u.deptNm})</option>
            </c:forEach>
        </select>
    </div>
    <div>
        <button class="btn btn-primary"><i class="bi bi-send"></i> 상신</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/expense/my.do">취소</a>
    </div>
</form>
<script>
function addRow(){
    var tbody = document.querySelector('#itemTable tbody');
    var row = tbody.querySelector('.item-row').cloneNode(true);
    row.querySelectorAll('input, select').forEach(function(el){
        if (el.type === 'date') el.value = '';
        else if (el.type === 'number') el.value = '0';
        else if (el.tagName !== 'SELECT') el.value = '';
    });
    tbody.appendChild(row);
}
function rmRow(btn){
    var rows = document.querySelectorAll('#itemTable .item-row');
    if (rows.length <= 1) return;
    btn.closest('tr').remove();
}
</script>
