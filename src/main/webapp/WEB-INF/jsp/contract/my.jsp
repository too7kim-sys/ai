<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>내 근로계약</title>
<h2 class="mb-3"><i class="bi bi-file-text"></i> 내 근로계약</h2>
<div class="card"><table class="table mb-0">
    <thead class="table-light"><tr><th>계약번호</th><th>종류</th><th>기간</th><th>상태</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="c" items="${list}">
        <tr>
            <td><strong>${c.contractNo}</strong></td>
            <td>${c.contractTypeCd}</td>
            <td>${c.startDt} ~ ${c.endDt == null ? '기간없음' : c.endDt}</td>
            <td>
                <c:choose>
                    <c:when test="${c.statusCd == 'ACTIVE'}"><span class="badge bg-success">유효</span></c:when>
                    <c:when test="${c.statusCd == 'SIGNED'}"><span class="badge bg-info">서명완료</span></c:when>
                    <c:when test="${c.statusCd == 'SENT'}"><span class="badge bg-warning">서명요청</span></c:when>
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
    <c:if test="${empty list}"><tr><td colspan="5" class="text-center text-muted py-4">계약 데이터가 없습니다.</td></tr></c:if>
    </tbody>
</table></div>
