<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>메일 템플릿</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-file-text"></i> 메일 템플릿</h2>
    <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/mail/log.do">
        <i class="bi bi-arrow-left"></i> 발송 이력
    </a>
</div>

<div class="card">
    <table class="table table-hover mb-0">
        <thead class="table-light">
        <tr><th style="width:160px;">코드</th><th>이름</th><th>제목</th><th>첨부</th><th>사용</th><th></th></tr>
        </thead>
        <tbody>
        <c:forEach var="t" items="${list}">
            <tr>
                <td><code>${t.templateCd}</code></td>
                <td>${t.templateNm}</td>
                <td class="small">${t.subject}</td>
                <td class="small">${t.attachTypeCd}</td>
                <td>
                    <c:choose>
                        <c:when test="${t.useYn eq 'Y'}"><span class="badge bg-success">사용</span></c:when>
                        <c:otherwise><span class="badge bg-secondary">중지</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <a class="btn btn-sm btn-outline-primary"
                       href="${pageContext.request.contextPath}/mail/template/edit.do?templateCd=${t.templateCd}">
                        <i class="bi bi-pencil"></i> 편집
                    </a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="6" class="text-center text-muted py-4">등록된 템플릿이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
