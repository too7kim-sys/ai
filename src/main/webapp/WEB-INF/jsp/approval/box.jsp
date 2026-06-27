<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>전자결재 - ${box}</title>
<h2 class="mb-3"><i class="bi bi-file-earmark-check"></i> 전자결재
    <span class="badge bg-secondary">${box}</span>
</h2>
<ul class="nav nav-pills mb-3">
    <li class="nav-item"><a class="nav-link ${box=='pending'?'active':''}" href="${pageContext.request.contextPath}/approval/pending.do">대기</a></li>
    <li class="nav-item"><a class="nav-link ${box=='draft'?'active':''}" href="${pageContext.request.contextPath}/approval/draft.do">기안</a></li>
    <li class="nav-item"><a class="nav-link ${box=='completed'?'active':''}" href="${pageContext.request.contextPath}/approval/completed.do">완료</a></li>
    <li class="nav-item"><a class="nav-link ${box=='rejected'?'active':''}" href="${pageContext.request.contextPath}/approval/rejected.do">반려</a></li>
    <li class="nav-item ms-auto"><a class="btn btn-primary" href="${pageContext.request.contextPath}/approval/write.do"><i class="bi bi-pencil"></i> 기안하기</a></li>
</ul>
<div class="card"><table class="table table-hover mb-0">
    <thead class="table-light"><tr><th>문서번호</th><th>양식</th><th>제목</th><th>기안자</th><th>상태</th><th>등록일</th></tr></thead>
    <tbody>
    <c:forEach var="d" items="${list}">
        <tr>
            <td><a href="${pageContext.request.contextPath}/approval/detail.do?docId=${d.docId}">${d.docNo}</a></td>
            <td><span class="badge bg-info">${d.formCd}</span></td>
            <td>${d.title}</td>
            <td>${d.drafterName} (${d.drafterDept})</td>
            <td>
                <c:choose>
                    <c:when test="${d.statusCd == 'APPROVED'}"><span class="badge bg-success">완료</span></c:when>
                    <c:when test="${d.statusCd == 'REJECTED'}"><span class="badge bg-danger">반려</span></c:when>
                    <c:when test="${d.statusCd == 'IN_PROGRESS'}"><span class="badge bg-warning text-dark">진행중</span></c:when>
                    <c:when test="${d.statusCd == 'CANCELED'}"><span class="badge bg-secondary">회수</span></c:when>
                    <c:otherwise><span class="badge bg-light text-dark">${d.statusCd}</span></c:otherwise>
                </c:choose>
            </td>
            <td>${d.createdAt}</td>
        </tr>
    </c:forEach>
    <c:if test="${empty list}"><tr><td colspan="6" class="text-center text-muted py-4">문서가 없습니다.</td></tr></c:if>
    </tbody>
</table></div>
