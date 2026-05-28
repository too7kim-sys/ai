<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${c.contractNo}</title>
<div class="d-flex justify-content-between mb-3">
    <h2><i class="bi bi-file-text"></i> ${c.contractNo} - ${c.userName}</h2>
    <div>
        <span class="badge bg-secondary">${c.statusCd}</span>
    </div>
</div>
<div class="card mb-3"><div class="card-body">
    ${body}
</div></div>
<div class="d-flex gap-2">
    <c:if test="${c.statusCd == 'DRAFT'}">
        <form method="post" action="${pageContext.request.contextPath}/contract/admin/sign-request.do" class="d-inline">
            <sec:csrfInput/>
            <input type="hidden" name="contractId" value="${c.contractId}"/>
            <button class="btn btn-primary"><i class="bi bi-envelope"></i> 서명 요청 메일 발송</button>
        </form>
    </c:if>
    <c:if test="${c.statusCd == 'SIGNED'}">
        <form method="post" action="${pageContext.request.contextPath}/contract/admin/activate.do" class="d-inline">
            <sec:csrfInput/>
            <input type="hidden" name="contractId" value="${c.contractId}"/>
            <button class="btn btn-success"><i class="bi bi-check2-circle"></i> 활성화 (ACTIVE)</button>
        </form>
    </c:if>
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/contract/my/pdf.do?contractId=${c.contractId}"><i class="bi bi-file-pdf"></i> PDF</a>
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/contract/admin/list.do">목록</a>
</div>
