<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<title>내 자산</title>

<h2 class="mb-3"><i class="bi bi-laptop text-primary"></i> 내 자산</h2>

<div class="card">
    <div class="card-header">
        <i class="bi bi-list-ul"></i> 지급 받은 자산
        <small class="text-muted ms-1">총 ${empty list ? 0 : list.size()}건</small>
    </div>
    <div class="table-responsive">
    <table class="table table-hover mb-0 align-middle">
        <thead class="table-light">
            <tr>
                <th style="width:140px">자산번호</th>
                <th>이름</th>
                <th style="width:100px">분류</th>
                <th>모델</th>
                <th>시리얼</th>
                <th style="width:120px">지급일</th>
                <th>위치</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach var="a" items="${list}">
            <tr>
                <td class="small">
                    <a class="text-decoration-none" href="${pageContext.request.contextPath}/asset/detail.do?assetId=${a.assetId}">
                        <code>${a.assetNo}</code>
                    </a>
                </td>
                <td><strong>${a.assetNm}</strong></td>
                <td><span class="badge text-bg-light border">${a.categoryCd}</span></td>
                <td class="small text-muted">${a.brand} ${a.modelNm}</td>
                <td class="small"><code>${a.serialNo}</code></td>
                <td class="small">
                    <c:choose>
                        <c:when test="${not empty a.assignedDt}">${a.assignedDt}</c:when>
                        <c:otherwise><span class="text-muted">—</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="small text-muted">
                    <c:choose>
                        <c:when test="${not empty a.location}">${a.location}</c:when>
                        <c:otherwise>—</c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr>
                <td colspan="7" class="empty-state">
                    <i class="bi bi-laptop empty-state-icon"></i>
                    <div class="empty-state-title">지급받은 자산이 없습니다</div>
                    <div class="empty-state-desc small text-muted">노트북·모니터·휴대폰 등 회사 자산이 지급되면 여기에 표시됩니다.</div>
                </td>
            </tr>
        </c:if>
        </tbody>
    </table>
    </div>
</div>
