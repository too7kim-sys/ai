<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<title>내 근로계약</title>
<h2 class="mb-3"><i class="bi bi-file-text"></i> 내 근로계약</h2>
<div class="card"><div class="table-responsive"><table class="table table-hover mb-0 align-middle">
    <thead class="table-light">
    <tr>
        <th>계약번호</th><th>종류</th><th>기간</th>
        <th class="text-end">연봉</th><th class="text-end">월 기본급</th><th>지급일</th>
        <th>상태</th><th></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="c" items="${list}">
        <tr>
            <td><strong>${c.contractNo}</strong></td>
            <td>
                <c:choose>
                    <c:when test="${c.contractTypeCd == 'REGULAR'}">정규직</c:when>
                    <c:when test="${c.contractTypeCd == 'FIXED_TERM'}">기간제</c:when>
                    <c:when test="${c.contractTypeCd == 'PART_TIME'}">단시간</c:when>
                    <c:when test="${c.contractTypeCd == 'TEMP'}">시용기간</c:when>
                    <c:when test="${c.contractTypeCd == 'INTERN'}">인턴</c:when>
                    <c:otherwise>${c.contractTypeCd}</c:otherwise>
                </c:choose>
            </td>
            <td class="small">${c.startDt} ~ ${c.endDt == null ? '기간없음' : c.endDt}</td>
            <td class="text-end">
                <c:if test="${not empty c.annualSalary}"><fmt:formatNumber value="${c.annualSalary}" type="number"/> 원</c:if>
            </td>
            <td class="text-end">
                <c:if test="${not empty c.monthlyBaseSal}"><fmt:formatNumber value="${c.monthlyBaseSal}" type="number"/> 원</c:if>
            </td>
            <td class="small text-muted">
                <c:if test="${not empty c.paymentDay}">매월 ${c.paymentDay}일</c:if>
            </td>
            <td>
                <c:choose>
                    <c:when test="${c.statusCd == 'ACTIVE'}"><span class="badge bg-success">유효</span></c:when>
                    <c:when test="${c.statusCd == 'SIGNED'}"><span class="badge bg-info">서명완료</span></c:when>
                    <c:when test="${c.statusCd == 'SENT'}"><span class="badge bg-warning text-dark">서명요청</span></c:when>
                    <c:otherwise><span class="badge bg-secondary">${c.statusCd}</span></c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:if test="${c.statusCd == 'SENT' || c.statusCd == 'DRAFT'}">
                    <a class="btn btn-sm btn-primary" href="${pageContext.request.contextPath}/contract/my/sign.do?contractId=${c.contractId}">서명하기</a>
                </c:if>
                <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/contract/my/pdf.do?contractId=${c.contractId}">PDF</a>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty list}"><tr><td colspan="8" class="text-center text-muted py-4">계약 데이터가 없습니다.</td></tr></c:if>
    </tbody>
</table></div></div>

<div class="alert alert-info small mt-3">
    <i class="bi bi-info-circle"></i>
    매월 급여 명세서는 <a href="${pageContext.request.contextPath}/payroll/my.do">급여 메뉴</a> 에서 확인할 수 있습니다.
    공제(국민연금·건강보험·장기요양·고용보험·소득세·지방소득세) 는 위 연봉 기준으로 자동 산정됩니다.
</div>
