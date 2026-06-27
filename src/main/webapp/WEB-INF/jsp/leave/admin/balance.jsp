<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>휴가 일수 관리 (HR)</title>

<h2 class="mb-3"><i class="bi bi-calendar-plus"></i> 휴가 일수 관리</h2>

<c:if test="${not empty msg}">
    <div class="alert alert-success py-2">${msg}</div>
</c:if>

<%-- 연도 선택 + 검색 --%>
<form method="get" action="${pageContext.request.contextPath}/leave/admin/balance.do" class="row g-2 align-items-end mb-3">
    <div class="col-auto">
        <label class="form-label small text-muted mb-1">연도</label>
        <select name="year" class="form-select form-select-sm" onchange="this.form.submit()">
            <c:forEach var="y" begin="${year - 2}" end="${year + 1}">
                <option value="${y}" <c:if test="${y == year}">selected</c:if>>${y}</option>
            </c:forEach>
        </select>
    </div>
    <div class="col-md-4">
        <label class="form-label small text-muted mb-1">검색 (이름·이메일·부서)</label>
        <input type="text" name="keyword" value="${keyword}" class="form-control form-control-sm"/>
    </div>
    <div class="col-auto">
        <button type="submit" class="btn btn-sm btn-outline-primary"><i class="bi bi-search"></i> 검색</button>
        <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/leave/admin/balance.do?year=${year}">초기화</a>
    </div>
</form>

<%-- 일괄 부여 액션 --%>
<div class="card mb-3">
    <div class="card-body py-3">
        <div class="row g-3 align-items-end">
            <div class="col-md-5">
                <form method="post" action="${pageContext.request.contextPath}/leave/admin/balance/grant-all.do"
                      class="d-flex gap-2 align-items-end"
                      onsubmit="return confirm('${year}년 재직 중인 직원에게 동일 일수를 부여합니다 (${year}년 시작 전 퇴직자는 제외). 진행할까요?');">
                    <sec:csrfInput/>
                    <input type="hidden" name="year" value="${year}"/>
                    <div class="flex-grow-1">
                        <label class="form-label small text-muted mb-1">전 직원 일괄 부여</label>
                        <div class="input-group input-group-sm">
                            <input type="number" name="days" value="15" step="0.5" min="0" max="50" class="form-control" required/>
                            <span class="input-group-text">일</span>
                            <button class="btn btn-primary" type="submit"><i class="bi bi-people"></i> 부여</button>
                        </div>
                    </div>
                </form>
            </div>
            <div class="col-md-5">
                <form method="post" action="${pageContext.request.contextPath}/leave/admin/balance/grant-by-tenure.do"
                      onsubmit="return confirm('입사일을 기준으로 표준 연차(1년 미만 11일 / 1년 이상 15일 / 3년차부터 2년마다 +1, 최대 25일)를 일괄 부여합니다 (${year}년 시작 전 퇴직자는 제외). 진행할까요?');">
                    <sec:csrfInput/>
                    <input type="hidden" name="year" value="${year}"/>
                    <label class="form-label small text-muted mb-1">입사일 기반 자동 계산</label>
                    <div>
                        <button class="btn btn-sm btn-outline-success" type="submit">
                            <i class="bi bi-magic"></i> 근속연수 기반 일괄 부여
                        </button>
                        <small class="text-muted ms-2">근로기준법 표준 기준</small>
                    </div>
                </form>
            </div>
            <div class="col-md-2 text-end">
                <span class="text-muted small">대상 인원</span>
                <div class="fs-5"><strong>${list.size()}</strong>명</div>
            </div>
        </div>
    </div>
</div>

<%-- 목록 --%>
<div class="card">
    <div class="table-responsive">
        <table class="table table-hover mb-0 align-middle">
            <thead class="table-light">
                <tr>
                    <th style="width:18%">이름</th>
                    <th style="width:18%">부서</th>
                    <th style="width:13%">입사일</th>
                    <th style="width:9%" class="text-end">부여</th>
                    <th style="width:9%" class="text-end">사용</th>
                    <th style="width:9%" class="text-end">잔여</th>
                    <th>부여 일수 수정</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${list}">
                    <tr <c:if test="${r.resignDate != null}">class="table-secondary"</c:if>>
                        <td>
                            <strong><c:out value="${r.name}"/></strong>
                            <c:if test="${r.resignDate != null}">
                                <span class="badge bg-dark ms-1" title="퇴사일: ${r.resignDate}">퇴사</span>
                            </c:if>
                            <div class="text-muted small"><c:out value="${r.email}"/></div>
                        </td>
                        <td><c:out value="${r.deptName}"/></td>
                        <td>${r.hireDate != null ? r.hireDate : '-'}</td>
                        <td class="text-end">${r.annualGiven}</td>
                        <td class="text-end text-warning">${r.annualUsed}</td>
                        <td class="text-end">
                            <c:choose>
                                <c:when test="${r.remaining() lt 0}">
                                    <span class="badge bg-danger">${r.remaining()}일</span>
                                </c:when>
                                <c:otherwise>
                                    <strong class="text-primary">${r.remaining()}일</strong>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <form method="post"
                                  action="${pageContext.request.contextPath}/leave/admin/balance/update.do"
                                  class="d-flex gap-2 align-items-center">
                                <sec:csrfInput/>
                                <input type="hidden" name="userId" value="${r.userId}"/>
                                <input type="hidden" name="year"   value="${year}"/>
                                <input type="hidden" name="keyword" value="${keyword}"/>
                                <div class="input-group input-group-sm" style="max-width:180px">
                                    <input type="number" name="days" value="${r.annualGiven}"
                                           step="0.5" min="0" max="50" class="form-control" required/>
                                    <span class="input-group-text">일</span>
                                    <button class="btn btn-outline-primary" type="submit">저장</button>
                                </div>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty list}">
                    <tr><td colspan="7" class="text-center text-muted py-4">대상 직원이 없습니다.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>
