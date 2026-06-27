<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>메일 발송 이력</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-envelope"></i> 메일 발송 이력</h2>
    <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/mail/template.do">
        <i class="bi bi-file-text"></i> 템플릿 관리
    </a>
</div>

<form class="row g-2 mb-3" method="get">
    <div class="col-md-3">
        <select name="status" class="form-select">
            <option value="">전체 상태</option>
            <c:forEach var="s" items="${statuses}">
                <option value="${s}" <c:if test="${s eq status}">selected</c:if>>${s}</option>
            </c:forEach>
        </select>
    </div>
    <div class="col-md-4">
        <input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="수신자/제목 검색"/>
    </div>
    <div class="col-md-2"><button class="btn btn-outline-primary"><i class="bi bi-search"></i> 검색</button></div>
    <div class="col-md-3 text-end text-muted small align-self-center">총 ${paging.total}건</div>
</form>

<!-- 테스트 발송 -->
<sec:authorize access="hasRole('ADMIN')">
<div class="card mb-3">
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/mail/test-send.do" class="row g-2">
            <sec:csrfInput/>
            <div class="col-md-4"><input type="email" name="toEmail" class="form-control form-control-sm" placeholder="테스트 수신 이메일" required/></div>
            <div class="col-md-3"><input type="text" name="templateCd" class="form-control form-control-sm" placeholder="템플릿 코드 (선택)"/></div>
            <div class="col-md-3"><button class="btn btn-sm btn-outline-success"><i class="bi bi-send"></i> 테스트 발송</button></div>
        </form>
    </div>
</div>
</sec:authorize>

<div class="card">
    <table class="table mb-0">
        <thead class="table-light">
        <tr><th style="width:80px;">상태</th><th>수신자</th><th>제목</th><th>템플릿</th>
            <th>큐 등록</th><th>발송</th><th>재시도</th></tr>
        </thead>
        <tbody>
        <c:forEach var="m" items="${list}">
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${m.statusCd eq 'SENT'}"><span class="badge bg-success">SENT</span></c:when>
                        <c:when test="${m.statusCd eq 'QUEUED'}"><span class="badge bg-secondary">QUEUED</span></c:when>
                        <c:when test="${m.statusCd eq 'FAILED'}"><span class="badge bg-danger">FAILED</span></c:when>
                        <c:otherwise><span class="badge bg-light text-dark">${m.statusCd}</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="small">${m.toEmail}</td>
                <td>
                    ${m.subject}
                    <c:if test="${not empty m.bodyPreview}">
                        <div class="small text-muted">${m.bodyPreview}</div>
                    </c:if>
                    <c:if test="${m.statusCd eq 'FAILED' and not empty m.errorMessage}">
                        <div class="small text-danger">⚠ ${m.errorMessage}</div>
                    </c:if>
                </td>
                <td class="small text-muted">${m.templateCd}</td>
                <td class="small text-muted">${m.queuedAt}</td>
                <td class="small text-muted">${m.sentAt}</td>
                <td>${m.retryCnt}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty list}">
            <tr><td colspan="7" class="text-center text-muted py-4">발송 이력이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<c:if test="${paging.totalPages > 1}">
<nav class="mt-3"><ul class="pagination justify-content-center">
    <c:forEach var="i" begin="1" end="${paging.totalPages}">
        <li class="page-item <c:if test='${i == paging.page}'>active</c:if>">
            <a class="page-link" href="?page=${i}&status=${status}&keyword=${keyword}">${i}</a>
        </li>
    </c:forEach>
</ul></nav>
</c:if>
