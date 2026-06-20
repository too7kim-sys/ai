<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<title>내 급여명세서</title>

<h2 class="mb-3"><i class="bi bi-cash text-success"></i> 내 급여명세서</h2>

<%-- list 가 createdAt DESC 정렬이므로 첫 행이 가장 최근. 빠른 접근용 강조 카드. --%>
<c:if test="${not empty list}">
    <c:set var="latest" value="${list[0]}"/>
    <div class="card border-primary mb-3 stat-card">
        <div class="card-body d-flex flex-wrap align-items-center justify-content-between gap-3">
            <div>
                <div class="widget-label">최근 발행 — ${latest.payMonth}</div>
                <div class="d-flex align-items-baseline gap-2">
                    <span class="widget-value text-primary"><fmt:formatNumber value="${latest.netPay}"/></span>
                    <small class="text-muted">원 실수령</small>
                </div>
                <div class="widget-trend text-muted">
                    총 지급 <fmt:formatNumber value="${latest.grossPay}"/> ·
                    공제 <span class="text-warning"><fmt:formatNumber value="${latest.deductionTotal}"/></span>
                    <c:choose>
                        <c:when test="${latest.statusCd == 'PAID'}"> · <span class="badge bg-success">지급완료</span></c:when>
                        <c:when test="${latest.statusCd == 'CONFIRMED'}"> · <span class="badge bg-info">확정</span></c:when>
                        <c:otherwise> · <span class="badge text-bg-light border">${latest.statusCd}</span></c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="d-flex gap-2">
                <a class="btn btn-primary btn-sm"
                   href="${pageContext.request.contextPath}/payroll/my/detail.do?payMonth=${latest.payMonth}">
                    <i class="bi bi-search"></i> 상세 보기
                </a>
                <a class="btn btn-outline-primary btn-sm"
                   href="${pageContext.request.contextPath}/payroll/my/pdf.do?payMonth=${latest.payMonth}">
                    <i class="bi bi-file-earmark-pdf"></i> PDF
                </a>
            </div>
        </div>
    </div>
</c:if>

<div class="card">
    <div class="card-header d-flex justify-content-between align-items-center">
        <span><i class="bi bi-list-ul"></i> 발행 내역
            <small class="text-muted ms-1">총 ${empty list ? 0 : list.size()}건</small></span>
        <small class="text-muted d-none d-md-inline">금액 단위: 원</small>
    </div>
    <div class="table-responsive">
    <table class="table table-hover mb-0 align-middle">
        <thead class="table-light">
            <tr>
                <th style="width:110px">지급월</th>
                <th class="text-end">총 지급액</th>
                <th class="text-end">공제</th>
                <th class="text-end">실 수령</th>
                <th style="width:100px">상태</th>
                <th style="width:120px">지급일</th>
                <th style="width:140px"></th>
            </tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${list}">
            <tr>
                <td><strong>${p.payMonth}</strong></td>
                <td class="text-end"><fmt:formatNumber value="${p.grossPay}"/></td>
                <td class="text-end text-warning"><fmt:formatNumber value="${p.deductionTotal}"/></td>
                <td class="text-end fw-bold text-primary"><fmt:formatNumber value="${p.netPay}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${p.statusCd == 'PAID'}"><span class="badge bg-success">지급완료</span></c:when>
                        <c:when test="${p.statusCd == 'CONFIRMED'}"><span class="badge bg-info">확정</span></c:when>
                        <c:when test="${p.statusCd == 'DRAFT'}"><span class="badge text-bg-light border">작성중</span></c:when>
                        <c:otherwise><span class="badge bg-secondary">${p.statusCd}</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="small">
                    <c:choose>
                        <c:when test="${not empty p.paidDt}">${p.paidDt}</c:when>
                        <c:otherwise><span class="text-muted">—</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <a class="btn btn-sm btn-outline-primary"
                       href="${pageContext.request.contextPath}/payroll/my/detail.do?payMonth=${p.payMonth}"
                       title="상세">
                        <i class="bi bi-search"></i> 상세
                    </a>
                    <a class="btn btn-sm btn-outline-secondary"
                       href="${pageContext.request.contextPath}/payroll/my/pdf.do?payMonth=${p.payMonth}"
                       title="PDF 다운로드">
                        <i class="bi bi-file-earmark-pdf"></i>
                    </a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr>
                <td colspan="7" class="empty-state">
                    <i class="bi bi-cash empty-state-icon"></i>
                    <div class="empty-state-title">발행된 급여명세서가 없습니다</div>
                    <div class="empty-state-desc small text-muted">매월 급여가 확정되면 여기에 자동으로 표시됩니다.</div>
                </td>
            </tr>
        </c:if>
        </tbody>
    </table>
    </div>
</div>
