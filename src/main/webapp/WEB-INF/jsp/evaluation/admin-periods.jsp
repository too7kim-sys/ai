<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>평가 기간/양식 관리</title>
<h2 class="mb-3"><i class="bi bi-calendar-event"></i> 평가 기간 / 양식 관리</h2>

<div class="row g-3">
    <div class="col-md-7">
        <div class="card mb-3">
            <div class="card-header">평가 기간</div>
            <table class="table mb-0">
                <thead class="table-light"><tr><th>기간명</th><th>시작</th><th>종료</th><th>상태</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="p" items="${periods}">
                    <tr>
                        <td><strong>${p.periodNm}</strong></td>
                        <td>${p.startDt}</td><td>${p.endDt}</td>
                        <td><span class="badge bg-info">${p.statusCd}</span></td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/evaluation/admin/period/status.do" class="d-inline">
                                <sec:csrfInput/>
                                <input type="hidden" name="periodId" value="${p.periodId}"/>
                                <c:if test="${p.statusCd ne 'IN_PROGRESS'}">
                                    <button class="btn btn-sm btn-outline-success" name="statusCd" value="IN_PROGRESS">진행</button>
                                </c:if>
                                <c:if test="${p.statusCd ne 'CLOSED'}">
                                    <button class="btn btn-sm btn-outline-secondary" name="statusCd" value="CLOSED">마감</button>
                                </c:if>
                            </form>
                            <a class="btn btn-sm btn-outline-primary"
                               href="${pageContext.request.contextPath}/evaluation/admin/results.do?periodId=${p.periodId}">결과</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty periods}">
                    <tr><td colspan="5" class="text-center text-muted py-4">등록된 기간이 없습니다.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <div class="card">
            <div class="card-header">새 평가 기간</div>
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/evaluation/admin/period/create.do" class="row g-2">
                    <sec:csrfInput/>
                    <div class="col-md-4"><input type="text" name="periodNm" class="form-control" placeholder="예: 2026년 1분기 평가" required/></div>
                    <div class="col-md-3"><input type="date" name="startDt" class="form-control" required/></div>
                    <div class="col-md-3"><input type="date" name="endDt" class="form-control" required/></div>
                    <div class="col-md-2 d-grid"><button class="btn btn-primary">등록</button></div>
                </form>
            </div>
        </div>
    </div>

    <div class="col-md-5">
        <div class="card mb-3">
            <div class="card-header">평가 양식</div>
            <ul class="list-group list-group-flush">
                <c:forEach var="f" items="${forms}">
                    <li class="list-group-item">
                        <div class="fw-bold">${f.formNm}</div>
                        <div class="small text-muted text-truncate">${f.itemsJson}</div>
                    </li>
                </c:forEach>
                <c:if test="${empty forms}">
                    <li class="list-group-item text-center text-muted">등록된 양식이 없습니다.</li>
                </c:if>
            </ul>
        </div>

        <div class="card">
            <div class="card-header">새 양식</div>
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/evaluation/admin/form/create.do">
                    <sec:csrfInput/>
                    <div class="mb-2"><input type="text" name="formNm" class="form-control" placeholder="양식명" required/></div>
                    <div class="mb-2">
                        <label class="form-label small">항목 JSON
                            <span class="text-muted">예: [{"k":"comm","l":"커뮤니케이션","w":20},{"k":"own","l":"주인의식","w":20}]</span>
                        </label>
                        <textarea name="itemsJson" rows="4" class="form-control font-monospace small"
                                  placeholder='[{"k":"comm","l":"커뮤니케이션","w":20}]'></textarea>
                    </div>
                    <button class="btn btn-outline-primary btn-sm">등록</button>
                </form>
            </div>
        </div>
    </div>
</div>
