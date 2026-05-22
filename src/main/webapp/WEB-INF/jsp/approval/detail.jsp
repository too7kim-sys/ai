<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>${d.docNo}</title>
<h2 class="mb-3"><i class="bi bi-file-earmark-text"></i> ${d.title}</h2>
<div class="card mb-3"><div class="card-body">
    <dl class="row mb-0">
        <dt class="col-sm-2">문서번호</dt><dd class="col-sm-4">${d.docNo}</dd>
        <dt class="col-sm-2">양식</dt><dd class="col-sm-4">${d.formNm} (${d.formCd})</dd>
        <dt class="col-sm-2">기안자</dt><dd class="col-sm-4">${d.drafterName} (${d.drafterDept})</dd>
        <dt class="col-sm-2">상태</dt><dd class="col-sm-4">
            <c:choose>
                <c:when test="${d.statusCd == 'DRAFT'}"><span class="badge bg-secondary">기안</span></c:when>
                <c:when test="${d.statusCd == 'IN_PROGRESS'}"><span class="badge bg-primary">진행중</span></c:when>
                <c:when test="${d.statusCd == 'APPROVED'}"><span class="badge bg-success">승인완료</span></c:when>
                <c:when test="${d.statusCd == 'REJECTED'}"><span class="badge bg-danger">반려</span></c:when>
                <c:when test="${d.statusCd == 'CANCELED'}"><span class="badge bg-dark">회수</span></c:when>
                <c:otherwise><span class="badge bg-secondary">${d.statusCd}</span></c:otherwise>
            </c:choose>
        </dd>
        <dt class="col-sm-2">상신일</dt><dd class="col-sm-4">${d.submittedAt}</dd>
        <dt class="col-sm-2">완료일</dt><dd class="col-sm-4">${d.completedAt}</dd>
    </dl>
</div></div>

<div class="card mb-3">
    <div class="card-header bg-light"><i class="bi bi-card-text"></i> 본문</div>
    <c:choose>
        <c:when test="${contentFields != null and not empty contentFields}">
            <table class="table mb-0">
                <tbody>
                <c:forEach var="f" items="${contentFields}">
                    <tr>
                        <th class="bg-light" style="width:160px;"><c:out value="${f.key}"/></th>
                        <td style="white-space:pre-wrap; word-break:break-all;"><c:out value="${f.value}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <pre class="card-body mb-0" style="white-space:pre-wrap; word-break:break-all;"><c:out value="${d.contentJson}"/></pre>
        </c:otherwise>
    </c:choose>
</div>

<div class="card mb-3">
    <div class="card-header bg-light"><i class="bi bi-list-check"></i> 결재선</div>
    <table class="table mb-0">
        <thead class="table-light"><tr><th width="60">순서</th><th>결재자</th><th>유형</th><th>상태</th><th>의견</th><th>일시</th></tr></thead>
        <tbody>
        <c:forEach var="l" items="${d.lines}">
            <tr>
                <td>${l.stepNo}</td>
                <td>${l.approverName}</td>
                <td>
                    <c:choose>
                        <c:when test="${l.lineTypeCd == 'APPROVE'}">결재</c:when>
                        <c:when test="${l.lineTypeCd == 'AGREE'}">합의</c:when>
                        <c:when test="${l.lineTypeCd == 'REFER'}">참조</c:when>
                        <c:otherwise>${l.lineTypeCd}</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${l.statusCd == 'APPROVED'}"><span class="badge bg-success">승인</span></c:when>
                        <c:when test="${l.statusCd == 'REJECTED'}"><span class="badge bg-danger">반려</span></c:when>
                        <c:otherwise><span class="badge bg-warning text-dark">대기</span></c:otherwise>
                    </c:choose>
                </td>
                <td>${l.comment}</td>
                <td>${l.actedAt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<sec:authentication property="principal" var="me"/>
<%-- 현재 대기 단계의 결재자만 액션 노출 --%>
<c:forEach var="l" items="${d.lines}">
    <c:if test="${l.statusCd == 'PENDING' and l.approverId == me.userId and d.statusCd == 'IN_PROGRESS'}">
        <c:set var="canAct" value="true"/>
    </c:if>
    <c:if test="${l.statusCd == 'PENDING' and !empty canActPrev}"><c:remove var="canAct"/></c:if>
</c:forEach>

<c:if test="${canAct}">
<form method="post" action="${pageContext.request.contextPath}/approval/act.do" class="card p-3">
    <sec:csrfInput/>
    <input type="hidden" name="docId" value="${d.docId}"/>
    <div class="mb-2"><label class="form-label">의견</label><textarea class="form-control" name="comment" rows="2"></textarea></div>
    <div>
        <button name="approve" value="true" class="btn btn-success"
                onclick="return confirm('이 문서를 승인 처리하시겠습니까?')"><i class="bi bi-check2"></i> 승인</button>
        <button name="approve" value="false" class="btn btn-danger"
                onclick="return confirm('이 문서를 반려 처리하시겠습니까?')"><i class="bi bi-x"></i> 반려</button>
    </div>
</form>
</c:if>

<c:if test="${d.drafterId == me.userId and (d.statusCd == 'DRAFT' or d.statusCd == 'IN_PROGRESS')}">
<form method="post" action="${pageContext.request.contextPath}/approval/cancel.do" class="mt-2 d-inline">
    <sec:csrfInput/>
    <input type="hidden" name="docId" value="${d.docId}"/>
    <button class="btn btn-outline-secondary"><i class="bi bi-arrow-counterclockwise"></i> 회수</button>
</form>
</c:if>
