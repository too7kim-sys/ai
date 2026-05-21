<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<title>내 자산</title>
<h2 class="mb-3"><i class="bi bi-laptop"></i> 내 자산</h2>

<div class="card">
    <table class="table table-hover mb-0">
        <thead class="table-light">
        <tr><th>자산번호</th><th>이름</th><th>분류</th><th>모델</th><th>시리얼</th><th>지급일</th><th>위치</th></tr>
        </thead>
        <tbody>
        <c:forEach var="a" items="${list}">
            <tr>
                <td class="small"><a href="${pageContext.request.contextPath}/asset/detail.do?assetId=${a.assetId}"><code>${a.assetNo}</code></a></td>
                <td>${a.assetNm}</td>
                <td><span class="badge bg-light text-dark">${a.categoryCd}</span></td>
                <td class="small text-muted">${a.brand} ${a.modelNm}</td>
                <td class="small"><code>${a.serialNo}</code></td>
                <td class="small">${a.assignedDt}</td>
                <td class="small">${a.location}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="7" class="text-center text-muted py-4">지급받은 자산이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
