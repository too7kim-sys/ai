<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>시스템 - 공통 코드</title>
<h2 class="mb-3"><i class="bi bi-gear"></i> 시스템 관리</h2>
<c:set var="active" value="code" scope="request"/>
<jsp:include page="_nav.jsp"/>

<div class="row g-3">
    <div class="col-md-4">
        <div class="card mb-3">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span><i class="bi bi-folder"></i> 코드 그룹</span>
                <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#groupModal">
                    <i class="bi bi-plus"></i>
                </button>
            </div>
            <div class="list-group list-group-flush">
                <c:forEach var="g" items="${groups}">
                    <a class="list-group-item list-group-item-action d-flex justify-content-between <c:if test='${groupCd eq g.groupCd}'>active</c:if>"
                       href="${pageContext.request.contextPath}/sys/code.do?groupCd=${g.groupCd}">
                        <span><code>${g.groupCd}</code> · ${g.groupNm}</span>
                        <c:if test="${g.useYn eq 'N'}"><span class="badge bg-secondary">중지</span></c:if>
                    </a>
                </c:forEach>
            </div>
        </div>
    </div>

    <div class="col-md-8">
        <c:if test="${group != null}">
            <div class="card mb-3">
                <div class="card-header d-flex justify-content-between">
                    <strong>${group.groupNm} (${group.groupCd})</strong>
                    <form method="post" action="${pageContext.request.contextPath}/sys/code/group/delete.do" class="d-inline"
                          onsubmit="return confirm('그룹을 삭제할까요?')">
                        <sec:csrfInput/><input type="hidden" name="groupCd" value="${group.groupCd}"/>
                        <button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></button>
                    </form>
                </div>
                <div class="card-body">
                    <p class="text-muted small mb-0">${group.description}</p>
                </div>
            </div>

            <div class="card">
                <div class="card-header"><strong>코드 목록</strong></div>
                <table class="table mb-0 align-middle">
                    <thead class="table-light">
                    <tr><th>코드값</th><th>이름</th><th style="width:80px;">정렬</th><th>추가값</th><th style="width:80px;">사용</th><th></th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="c" items="${codes}">
                        <tr>
                            <td><code>${c.codeVal}</code></td>
                            <td>${c.codeNm}</td>
                            <td>${c.sortNo}</td>
                            <td class="small text-muted">${c.extraVal}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${c.useYn eq 'Y'}"><span class="badge bg-success">활성</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">중지</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/sys/code/delete.do" class="d-inline"
                                      onsubmit="return confirm('삭제하시겠습니까?')">
                                    <sec:csrfInput/>
                                    <input type="hidden" name="codeId" value="${c.codeId}"/>
                                    <input type="hidden" name="groupCd" value="${group.groupCd}"/>
                                    <button class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty codes}">
                        <tr><td colspan="6" class="text-center text-muted py-3">코드가 없습니다.</td></tr>
                    </c:if>
                    </tbody>
                </table>
                <div class="card-footer">
                    <form method="post" action="${pageContext.request.contextPath}/sys/code/save.do" class="row g-2">
                        <sec:csrfInput/>
                        <input type="hidden" name="groupCd" value="${group.groupCd}"/>
                        <div class="col-md-2"><input type="text" name="codeVal" class="form-control form-control-sm" placeholder="코드값" required maxlength="50"/></div>
                        <div class="col-md-3"><input type="text" name="codeNm" class="form-control form-control-sm" placeholder="이름" required maxlength="100"/></div>
                        <div class="col-md-1"><input type="number" name="sortNo" class="form-control form-control-sm" value="0"/></div>
                        <div class="col-md-3"><input type="text" name="extraVal" class="form-control form-control-sm" placeholder="추가값"/></div>
                        <div class="col-md-3 d-grid"><button class="btn btn-sm btn-primary"><i class="bi bi-plus"></i> 코드 추가</button></div>
                    </form>
                </div>
            </div>
        </c:if>
    </div>
</div>

<!-- 그룹 등록 모달 -->
<div class="modal fade" id="groupModal" tabindex="-1">
    <div class="modal-dialog">
        <form method="post" action="${pageContext.request.contextPath}/sys/code/group/save.do" class="modal-content">
            <sec:csrfInput/>
            <input type="hidden" name="isNew" value="true"/>
            <div class="modal-header"><h5 class="modal-title">새 코드 그룹</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
            <div class="modal-body">
                <div class="mb-2"><label class="form-label">그룹 코드</label>
                    <input type="text" name="groupCd" class="form-control" required maxlength="50"/></div>
                <div class="mb-2"><label class="form-label">그룹 이름</label>
                    <input type="text" name="groupNm" class="form-control" required maxlength="100"/></div>
                <div class="mb-2"><label class="form-label">설명</label>
                    <input type="text" name="description" class="form-control"/></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">취소</button>
                <button class="btn btn-primary">등록</button>
            </div>
        </form>
    </div>
</div>
