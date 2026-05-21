<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${c.title} - 계약 상세</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-briefcase"></i> 계약 상세</h2>
    <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/biz-contract/list.do">
        <i class="bi bi-arrow-left"></i> 목록
    </a>
</div>

<div class="card mb-3">
    <div class="card-header d-flex justify-content-between align-items-center">
        <div>
            <h4 class="d-inline">${c.title}</h4>
            <code class="ms-2">${c.contractNo}</code>
            <span class="ms-2">
                <c:choose>
                    <c:when test="${c.statusCd eq 'DRAFT'}"><span class="badge bg-secondary">작성중</span></c:when>
                    <c:when test="${c.statusCd eq 'ACTIVE'}"><span class="badge bg-success">유효</span></c:when>
                    <c:when test="${c.statusCd eq 'TERMINATED'}"><span class="badge bg-danger">해지</span></c:when>
                    <c:when test="${c.statusCd eq 'EXPIRED'}"><span class="badge bg-warning text-dark">만료</span></c:when>
                </c:choose>
            </span>
        </div>
        <div class="d-flex gap-2">
            <c:if test="${c.statusCd eq 'DRAFT'}">
                <form method="post" action="${pageContext.request.contextPath}/biz-contract/sign.do" class="d-inline"
                      onsubmit="return confirm('계약을 체결할까요? 청구 일정이 자동 생성됩니다.')">
                    <sec:csrfInput/><input type="hidden" name="bizContractId" value="${c.bizContractId}"/>
                    <button class="btn btn-sm btn-success"><i class="bi bi-pen"></i> 체결</button>
                </form>
            </c:if>
            <c:if test="${c.statusCd eq 'ACTIVE'}">
                <form method="post" action="${pageContext.request.contextPath}/biz-contract/terminate.do" class="d-inline"
                      onsubmit="return confirm('계약을 해지할까요?')">
                    <sec:csrfInput/><input type="hidden" name="bizContractId" value="${c.bizContractId}"/>
                    <button class="btn btn-sm btn-outline-danger"><i class="bi bi-x-circle"></i> 해지</button>
                </form>
            </c:if>
            <a class="btn btn-sm btn-outline-primary"
               href="${pageContext.request.contextPath}/biz-contract/edit.do?bizContractId=${c.bizContractId}">
                <i class="bi bi-pencil"></i> 수정
            </a>
        </div>
    </div>
    <div class="card-body">
        <table class="table table-sm mb-0">
            <tr><th class="bg-light" style="width:160px;">거래처</th><td>${c.vendorNm}</td>
                <th class="bg-light" style="width:160px;">담당자</th><td>${c.ownerName} (${c.deptNm})</td></tr>
            <tr><th class="bg-light">유형</th><td>${c.contractTypeCd}</td>
                <th class="bg-light">통화</th><td>${c.currencyCd}</td></tr>
            <tr><th class="bg-light">계약 기간</th><td>${c.startDt} ~ ${empty c.endDt ? '무기한' : c.endDt}</td>
                <th class="bg-light">자동 갱신</th>
                <td>
                    <c:choose>
                        <c:when test="${c.autoRenewYn eq 'Y'}">예 (${c.renewNoticeDays}일 전 알림)</c:when>
                        <c:otherwise>아니오</c:otherwise>
                    </c:choose>
                </td></tr>
            <tr><th class="bg-light">공급가액</th><td class="text-end"><fmt:formatNumber value="${c.amountNet}" type="number"/></td>
                <th class="bg-light">VAT</th><td class="text-end"><fmt:formatNumber value="${c.vatAmount}" type="number"/></td></tr>
            <tr><th class="bg-light">총액</th><td class="text-end fw-bold"><fmt:formatNumber value="${c.amountTotal}" type="number"/></td>
                <th class="bg-light">결제 조건</th><td>${c.paymentTermsCd}
                    <c:if test="${not empty c.paymentDayOfMonth}"> · 매월 ${c.paymentDayOfMonth}일</c:if></td></tr>
            <c:if test="${not empty c.memo}">
                <tr><th class="bg-light">메모</th><td colspan="3" style="white-space: pre-wrap;">${c.memo}</td></tr>
            </c:if>
        </table>
    </div>
</div>

<div class="card">
    <div class="card-header d-flex justify-content-between">
        <span><i class="bi bi-calendar-check"></i> 청구 일정</span>
        <form method="post" action="${pageContext.request.contextPath}/biz-contract/schedule/regenerate.do" class="d-inline"
              onsubmit="return confirm('기존 스케줄을 삭제하고 재생성합니다. 계속할까요?')">
            <sec:csrfInput/><input type="hidden" name="bizContractId" value="${c.bizContractId}"/>
            <button class="btn btn-sm btn-outline-secondary"><i class="bi bi-arrow-clockwise"></i> 재생성</button>
        </form>
    </div>
    <table class="table mb-0">
        <thead class="table-light">
        <tr><th>회차</th><th>예정일</th><th class="text-end">공급가액</th><th class="text-end">VAT</th><th class="text-end">총액</th><th>메모</th><th>상태</th></tr>
        </thead>
        <tbody>
        <c:forEach var="s" items="${schedules}">
            <tr>
                <td>${s.seqNo}</td>
                <td>${s.dueDt}</td>
                <td class="text-end"><fmt:formatNumber value="${s.amountNet}" type="number"/></td>
                <td class="text-end"><fmt:formatNumber value="${s.vatAmount}" type="number"/></td>
                <td class="text-end fw-bold"><fmt:formatNumber value="${s.amountTotal}" type="number"/></td>
                <td class="small text-muted">${s.memo}</td>
                <td>
                    <c:choose>
                        <c:when test="${s.statusCd eq 'PENDING'}"><span class="badge bg-secondary">대기</span></c:when>
                        <c:when test="${s.statusCd eq 'INVOICED'}"><span class="badge bg-info text-dark">발행</span></c:when>
                        <c:when test="${s.statusCd eq 'PAID'}"><span class="badge bg-success">완료</span></c:when>
                        <c:otherwise><span class="badge bg-light text-dark">${s.statusCd}</span></c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty schedules}">
            <tr><td colspan="7" class="text-center text-muted py-3">청구 일정이 없습니다. 체결 시 자동 생성됩니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
