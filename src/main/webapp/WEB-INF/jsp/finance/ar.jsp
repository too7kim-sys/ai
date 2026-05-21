<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<title>재무 대시보드</title>
<h2 class="mb-3"><i class="bi bi-bar-chart"></i> 재무 대시보드</h2>

<!-- 핵심 지표 -->
<div class="row g-3 mb-4">
    <div class="col-md-3">
        <div class="card border-success shadow-sm h-100"><div class="card-body">
            <div class="text-muted small">매출 (발행 인보이스)</div>
            <div class="display-6 text-success"><fmt:formatNumber value="${salesSummary.total_amount}" type="number"/></div>
            <small class="text-muted">${salesSummary.cnt} 건</small>
        </div></div>
    </div>
    <div class="col-md-3">
        <div class="card border-danger shadow-sm h-100"><div class="card-body">
            <div class="text-muted small">미수금 (AR)</div>
            <div class="display-6 text-danger"><fmt:formatNumber value="${salesOutstanding.remaining_amount}" type="number"/></div>
            <small class="text-muted">${salesOutstanding.cnt} 건</small>
        </div></div>
    </div>
    <div class="col-md-3">
        <div class="card border-primary shadow-sm h-100"><div class="card-body">
            <div class="text-muted small">매입 (수령 인보이스)</div>
            <div class="display-6 text-primary"><fmt:formatNumber value="${purchaseSummary.total_amount}" type="number"/></div>
            <small class="text-muted">${purchaseSummary.cnt} 건</small>
        </div></div>
    </div>
    <div class="col-md-3">
        <div class="card border-warning shadow-sm h-100"><div class="card-body">
            <div class="text-muted small">미지급 (AP)</div>
            <div class="display-6 text-warning"><fmt:formatNumber value="${purchaseOutstanding.remaining_amount}" type="number"/></div>
            <small class="text-muted">${purchaseOutstanding.cnt} 건</small>
        </div></div>
    </div>
</div>

<!-- 현금 흐름 -->
<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="card text-center"><div class="card-body">
            <i class="bi bi-arrow-down-circle text-success" style="font-size:1.5rem;"></i>
            <div class="text-muted small">총 입금</div>
            <div class="h4"><fmt:formatNumber value="${sumIncoming.total_amount}" type="number"/></div>
            <small>${sumIncoming.cnt} 건</small>
        </div></div>
    </div>
    <div class="col-md-4">
        <div class="card text-center"><div class="card-body">
            <i class="bi bi-arrow-up-circle text-danger" style="font-size:1.5rem;"></i>
            <div class="text-muted small">총 출금</div>
            <div class="h4"><fmt:formatNumber value="${sumOutgoing.total_amount}" type="number"/></div>
            <small>${sumOutgoing.cnt} 건</small>
        </div></div>
    </div>
    <div class="col-md-4">
        <div class="card text-center"><div class="card-body">
            <i class="bi bi-cash-stack" style="font-size:1.5rem;"></i>
            <div class="text-muted small">순현금 흐름</div>
            <div class="h4"><fmt:formatNumber value="${sumIncoming.total_amount - sumOutgoing.total_amount}" type="number"/></div>
        </div></div>
    </div>
</div>

<!-- 거래처 TOP -->
<div class="row g-3 mb-4">
    <div class="col-md-6">
        <div class="card"><div class="card-header"><i class="bi bi-trophy"></i> 매출 TOP 거래처 (5)</div>
            <table class="table mb-0">
                <thead class="table-light"><tr><th>거래처</th><th class="text-end">건수</th><th class="text-end">매출액</th><th class="text-end">미수금</th></tr></thead>
                <tbody>
                <c:forEach var="t" items="${salesTopVendors}">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/invoice/out.do?vendorId=${t.vendor_id}">${t.vendor_nm}</a></td>
                        <td class="text-end">${t.cnt}</td>
                        <td class="text-end fw-bold"><fmt:formatNumber value="${t.total_amount}" type="number"/></td>
                        <td class="text-end text-danger"><fmt:formatNumber value="${t.remaining_amount}" type="number"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty salesTopVendors}"><tr><td colspan="4" class="text-center text-muted py-3">데이터 없음</td></tr></c:if>
                </tbody>
            </table>
        </div>
    </div>
    <div class="col-md-6">
        <div class="card"><div class="card-header"><i class="bi bi-trophy"></i> 매입 TOP 거래처 (5)</div>
            <table class="table mb-0">
                <thead class="table-light"><tr><th>거래처</th><th class="text-end">건수</th><th class="text-end">매입액</th><th class="text-end">미지급</th></tr></thead>
                <tbody>
                <c:forEach var="t" items="${purchaseTopVendors}">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/invoice/in.do?vendorId=${t.vendor_id}">${t.vendor_nm}</a></td>
                        <td class="text-end">${t.cnt}</td>
                        <td class="text-end fw-bold"><fmt:formatNumber value="${t.total_amount}" type="number"/></td>
                        <td class="text-end text-warning"><fmt:formatNumber value="${t.remaining_amount}" type="number"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty purchaseTopVendors}"><tr><td colspan="4" class="text-center text-muted py-3">데이터 없음</td></tr></c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- 월별 매출 추이 -->
<div class="row g-3 mb-4">
    <div class="col-md-6">
        <div class="card"><div class="card-header">월별 매출 (최근 12개월)</div>
            <table class="table mb-0">
                <thead class="table-light"><tr><th>월</th><th class="text-end">매출</th><th class="text-end">수금</th></tr></thead>
                <tbody>
                <c:forEach var="m" items="${salesMonthly}">
                    <tr>
                        <td>${m.ym}</td>
                        <td class="text-end"><fmt:formatNumber value="${m.total_amount}" type="number"/></td>
                        <td class="text-end text-success"><fmt:formatNumber value="${m.paid_amount}" type="number"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty salesMonthly}"><tr><td colspan="3" class="text-center text-muted py-3">데이터 없음</td></tr></c:if>
                </tbody>
            </table>
        </div>
    </div>
    <div class="col-md-6">
        <div class="card"><div class="card-header">월별 매입 (최근 12개월)</div>
            <table class="table mb-0">
                <thead class="table-light"><tr><th>월</th><th class="text-end">매입</th><th class="text-end">지급</th></tr></thead>
                <tbody>
                <c:forEach var="m" items="${purchaseMonthly}">
                    <tr>
                        <td>${m.ym}</td>
                        <td class="text-end"><fmt:formatNumber value="${m.total_amount}" type="number"/></td>
                        <td class="text-end text-success"><fmt:formatNumber value="${m.paid_amount}" type="number"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty purchaseMonthly}"><tr><td colspan="3" class="text-center text-muted py-3">데이터 없음</td></tr></c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- 청구 예정 및 만료 예정 -->
<div class="row g-3 mb-4">
    <div class="col-md-6">
        <div class="card border-info"><div class="card-header bg-info bg-opacity-25"><i class="bi bi-calendar-event"></i> 30일 내 청구 예정 (${fn:length(upcomingSchedules)}건)</div>
            <table class="table mb-0">
                <thead class="table-light"><tr><th>예정일</th><th>회차</th><th class="text-end">금액</th></tr></thead>
                <tbody>
                <c:forEach var="s" items="${upcomingSchedules}">
                    <tr>
                        <td>${s.dueDt}</td>
                        <td class="small">${s.seqNo}회차 · 계약#${s.bizContractId}</td>
                        <td class="text-end"><fmt:formatNumber value="${s.amountTotal}" type="number"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty upcomingSchedules}"><tr><td colspan="3" class="text-center text-muted py-3">예정된 청구 없음</td></tr></c:if>
                </tbody>
            </table>
        </div>
    </div>
    <div class="col-md-6">
        <div class="card border-warning"><div class="card-header bg-warning bg-opacity-25"><i class="bi bi-exclamation-triangle"></i> 60일 내 만료 예정 계약 (${fn:length(expiringContracts)}건)</div>
            <table class="table mb-0">
                <thead class="table-light"><tr><th>만료일</th><th>계약</th><th class="text-end">총액</th></tr></thead>
                <tbody>
                <c:forEach var="c" items="${expiringContracts}">
                    <tr>
                        <td>${c.endDt}</td>
                        <td class="small"><a href="${pageContext.request.contextPath}/biz-contract/detail.do?bizContractId=${c.bizContractId}">${c.title}</a><br><small class="text-muted">${c.vendorNm}</small></td>
                        <td class="text-end"><fmt:formatNumber value="${c.amountTotal}" type="number"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty expiringContracts}"><tr><td colspan="3" class="text-center text-muted py-3">만료 예정 계약 없음</td></tr></c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
