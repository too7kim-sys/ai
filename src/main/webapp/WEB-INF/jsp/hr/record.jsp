<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>인사기록</title>
<h2 class="mb-3"><i class="bi bi-journal-text"></i> 인사기록 카드</h2>

<form class="row g-2 mb-3" method="get" action="${pageContext.request.contextPath}/hr/record.do">
    <div class="col-md-4">
        <select name="userId" class="form-select" onchange="this.form.submit()">
            <option value="">대상자 선택...</option>
            <c:forEach var="u" items="${users}">
                <option value="${u.userId}" <c:if test="${userId == u.userId}">selected</c:if>>
                    ${u.deptNm} - ${u.name} (${u.positionNm})
                </option>
            </c:forEach>
        </select>
    </div>
</form>

<c:if test="${not empty user}">
<div class="card mb-3">
    <div class="card-header d-flex justify-content-between align-items-center">
        <span><i class="bi bi-person"></i> ${user.name} (${user.deptNm} / ${user.positionNm})</span>
        <a class="btn btn-sm btn-outline-secondary"
           href="${pageContext.request.contextPath}/user/profile.do?userId=${user.userId}">프로필 보기</a>
    </div>
</div>

<div class="card mb-3">
    <div class="card-header"><i class="bi bi-plus-circle"></i> 새 기록 추가</div>
    <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/hr/record.do" class="row g-2">
            <sec:csrfInput/>
            <input type="hidden" name="userId" value="${user.userId}"/>
            <div class="col-md-2">
                <select name="categoryCd" class="form-select" required>
                    <option value="HIRE">입사</option>
                    <option value="PROMOTION">승진</option>
                    <option value="TRANSFER">이동</option>
                    <option value="EDUCATION">교육</option>
                    <option value="AWARD">포상</option>
                    <option value="DISCIPLINE">징계</option>
                    <option value="CERT">자격</option>
                    <option value="LEAVE">휴직</option>
                    <option value="TERMINATION">퇴직</option>
                    <option value="ETC">기타</option>
                </select>
            </div>
            <div class="col-md-2"><input type="date" name="eventDt" class="form-control"/></div>
            <div class="col-md-3"><input type="text" name="title" class="form-control" placeholder="제목" required/></div>
            <div class="col-md-4"><input type="text" name="content" class="form-control" placeholder="내용"/></div>
            <div class="col-md-1 d-grid"><button class="btn btn-primary">추가</button></div>
        </form>
    </div>
</div>

<div class="card">
    <table class="table mb-0">
        <thead class="table-light"><tr><th>일자</th><th>분류</th><th>제목</th><th>내용</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="r" items="${records}">
            <tr>
                <td>${r.eventDt}</td>
                <td><span class="badge bg-info">${r.categoryNm != null ? r.categoryNm : r.categoryCd}</span></td>
                <td>${r.title}</td>
                <td class="small text-muted">${r.content}</td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/hr/record/delete.do" class="d-inline"
                          onsubmit="return confirm('삭제하시겠습니까?')">
                        <sec:csrfInput/>
                        <input type="hidden" name="recId" value="${r.recId}"/>
                        <input type="hidden" name="userId" value="${user.userId}"/>
                        <button class="btn btn-sm btn-outline-danger">삭제</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty records}">
            <tr><td colspan="5" class="text-center text-muted py-4">등록된 기록이 없습니다.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
</c:if>

<c:if test="${empty user}">
    <div class="card">
        <div class="card-header"><i class="bi bi-clock-history"></i> 최근 등록된 인사기록</div>
        <table class="table mb-0">
            <thead class="table-light"><tr><th>등록일시</th><th>대상자</th><th>분류</th><th>제목</th></tr></thead>
            <tbody>
            <c:forEach var="r" items="${recent}">
                <tr>
                    <td class="small text-muted">${r.createdAt}</td>
                    <td><a href="?userId=${r.userId}">${r.userName}</a></td>
                    <td><span class="badge bg-info">${r.categoryNm != null ? r.categoryNm : r.categoryCd}</span></td>
                    <td>${r.title}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty recent}">
                <tr><td colspan="4" class="text-center text-muted py-4">최근 기록이 없습니다.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</c:if>
