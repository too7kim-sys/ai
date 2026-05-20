<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>전자결재 기안</title>
<h2 class="mb-3"><i class="bi bi-pencil"></i> 전자결재 기안</h2>
<div class="alert alert-info small">
    원하는 양식을 선택한 뒤 각 모듈 화면에서 결재 흐름으로 진입하세요.
</div>
<div class="row g-3">
    <c:forEach var="f" items="${forms}">
        <div class="col-md-4">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <h5 class="card-title">${f.formNm}</h5>
                    <p class="text-muted small mb-2"><code>${f.formCd}</code></p>
                    <c:choose>
                        <c:when test="${f.formCd == 'LEAVE'}">
                            <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/leave/write.do">휴가 신청으로 이동 →</a>
                        </c:when>
                        <c:when test="${f.formCd == 'EXPENSE'}">
                            <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/expense/write.do">지출결의 작성으로 이동 →</a>
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted small">다른 모듈에서 자동 상신됩니다.</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:forEach>
</div>
