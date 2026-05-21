<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<title>자료실 검색</title>
<h2 class="mb-3"><i class="bi bi-search"></i> 자료실 검색</h2>

<form class="row g-2 mb-3" method="get">
    <div class="col-md-4"><input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="제목/파일명 검색"/></div>
    <div class="col-md-2 d-grid"><button class="btn btn-outline-primary">검색</button></div>
    <div class="col-md-6 text-end text-muted small">결과 ${fn:length(results)}건</div>
</form>

<div class="card">
    <table class="table mb-0">
        <thead class="table-light"><tr><th>폴더</th><th>제목</th><th>파일</th><th>업로더</th><th>등록일</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="d" items="${results}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/doc/list.do?folderId=${d.folderId}">${d.folderNm}</a></td>
                <td>${d.title}
                    <c:if test="${not empty d.description}">
                        <div class="small text-muted">${d.description}</div>
                    </c:if>
                </td>
                <td class="small">${d.fileNm}</td>
                <td>${d.ownerName}</td>
                <td class="small text-muted">${fn:substring(d.createdAt, 0, 10)}</td>
                <td>
                    <a class="btn btn-sm btn-outline-success"
                       href="${pageContext.request.contextPath}/doc/download.do?docId=${d.docId}">
                        <i class="bi bi-download"></i>
                    </a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty results}">
            <tr><td colspan="6" class="text-center text-muted py-4">검색 결과가 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
