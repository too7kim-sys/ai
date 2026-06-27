<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>4대보험 요율 관리</title>

<h2 class="mb-3"><i class="bi bi-shield-check"></i> 4대보험 요율 관리</h2>

<c:if test="${not empty flashMsg}">
    <div class="alert alert-${flashType} py-2"><c:out value="${flashMsg}"/></div>
</c:if>

<div class="alert alert-light border small">
    <i class="bi bi-info-circle"></i>
    매년 변경되는 보험 요율을 <strong>적용 시작일</strong> 단위로 누적 등록합니다.
    급여 계산은 <strong>급여월 1일</strong> 시점의 유효 요율을 자동으로 사용하므로,
    예를 들어 2026년 1월부터 변경된 요율을 적용하려면 <code>적용 시작일 = 2026-01-01</code> 로
    새 행을 추가하면 됩니다. 기존 행은 수정하지 말고 그대로 두세요(과거 명세 재계산 시 정확한 요율 보장).
</div>

<%-- 현재(오늘 기준) 활성 요율 요약 --%>
<div class="card mb-3">
    <div class="card-header bg-light"><i class="bi bi-calendar-check"></i> 현재 적용 중인 요율 (오늘 기준)</div>
    <div class="table-responsive">
        <table class="table mb-0">
            <thead class="table-light">
                <tr>
                    <th>보험</th><th>시작일</th><th>종료일</th>
                    <th class="text-end">근로자</th><th class="text-end">회사</th>
                    <th class="text-end">하한</th><th class="text-end">상한</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${activeRates}">
                    <tr>
                        <td><strong>${r.insuranceCd}</strong> <small class="text-muted"><c:out value="${r.note}"/></small></td>
                        <td>${r.effectiveFrom}</td>
                        <td>${r.effectiveTo}</td>
                        <td class="text-end"><fmt:formatNumber value="${r.employeeRate * 100}" maxFractionDigits="3"/>%</td>
                        <td class="text-end"><fmt:formatNumber value="${r.employerRate * 100}" maxFractionDigits="3"/>%</td>
                        <td class="text-end">
                            <c:if test="${r.baseMin != null}"><fmt:formatNumber value="${r.baseMin}"/></c:if>
                        </td>
                        <td class="text-end">
                            <c:if test="${r.baseMax != null}"><fmt:formatNumber value="${r.baseMax}"/></c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty activeRates}">
                    <tr><td colspan="7" class="text-center text-muted py-3">활성 요율이 없습니다.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<%-- 신규 등록 --%>
<div class="card mb-3">
    <div class="card-header bg-light"><i class="bi bi-plus-square"></i> 신규 요율 추가</div>
    <form method="post" action="${pageContext.request.contextPath}/sys/insurance-rate.do" class="card-body">
        <sec:csrfInput/>
        <div class="row g-2">
            <div class="col-md-2">
                <label class="form-label small mb-1">보험 *</label>
                <select name="insuranceCd" class="form-select form-select-sm" required>
                    <option value="NP">국민연금 (NP)</option>
                    <option value="HI">건강보험 (HI)</option>
                    <option value="LTC">장기요양 (LTC)</option>
                    <option value="EI">고용보험 (EI)</option>
                    <option value="WC">산재보험 (WC)</option>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label small mb-1">적용 시작일 *</label>
                <input type="date" name="effectiveFrom" class="form-control form-control-sm" required/>
            </div>
            <div class="col-md-2">
                <label class="form-label small mb-1">적용 종료일</label>
                <input type="date" name="effectiveTo" class="form-control form-control-sm"/>
            </div>
            <div class="col-md-1">
                <label class="form-label small mb-1">근로자 *</label>
                <input type="number" step="0.00001" name="employeeRate" class="form-control form-control-sm" value="0.00000" required/>
            </div>
            <div class="col-md-1">
                <label class="form-label small mb-1">회사 *</label>
                <input type="number" step="0.00001" name="employerRate" class="form-control form-control-sm" value="0.00000" required/>
            </div>
            <div class="col-md-2">
                <label class="form-label small mb-1">기준 하한</label>
                <input type="number" step="1" name="baseMin" class="form-control form-control-sm" placeholder="(국민연금 등)"/>
            </div>
            <div class="col-md-2">
                <label class="form-label small mb-1">기준 상한</label>
                <input type="number" step="1" name="baseMax" class="form-control form-control-sm"/>
            </div>
            <div class="col-md-10">
                <label class="form-label small mb-1">메모</label>
                <input type="text" name="note" class="form-control form-control-sm" placeholder="예) 2026년 국민연금 — 보건복지부 고시 제xxxx호"/>
            </div>
            <div class="col-md-2 d-grid align-self-end">
                <button class="btn btn-sm btn-primary" type="submit">
                    <i class="bi bi-check2"></i> 추가
                </button>
            </div>
        </div>
        <div class="form-text mt-2">
            요율은 소수로 입력합니다 (예: 4.5% → <code>0.04500</code>).
        </div>
    </form>
</div>

<%-- 전체 이력 --%>
<div class="card">
    <div class="card-header bg-light"><i class="bi bi-clock-history"></i> 전체 요율 이력</div>
    <div class="table-responsive">
        <table class="table mb-0">
            <thead class="table-light">
                <tr>
                    <th style="width:90px">보험</th>
                    <th>적용 시작</th>
                    <th>적용 종료</th>
                    <th class="text-end">근로자</th>
                    <th class="text-end">회사</th>
                    <th class="text-end">하한</th>
                    <th class="text-end">상한</th>
                    <th>메모</th>
                    <th style="width:90px">작업</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${rates}">
                    <tr>
                        <td><strong>${r.insuranceCd}</strong></td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/sys/insurance-rate/edit.do" class="row g-1">
                                <sec:csrfInput/>
                                <input type="hidden" name="rateId" value="${r.rateId}"/>
                                <input type="hidden" name="insuranceCd" value="${r.insuranceCd}"/>
                                <input type="date" name="effectiveFrom" class="form-control form-control-sm" value="${r.effectiveFrom}" required form="form-edit-${r.rateId}"/>
                            </form>
                        </td>
                        <td>
                            <input type="date" name="effectiveTo" class="form-control form-control-sm" value="${r.effectiveTo}" form="form-edit-${r.rateId}"/>
                        </td>
                        <td class="text-end">
                            <input type="number" step="0.00001" name="employeeRate" class="form-control form-control-sm text-end" value="${r.employeeRate}" required form="form-edit-${r.rateId}"/>
                        </td>
                        <td class="text-end">
                            <input type="number" step="0.00001" name="employerRate" class="form-control form-control-sm text-end" value="${r.employerRate}" required form="form-edit-${r.rateId}"/>
                        </td>
                        <td class="text-end">
                            <input type="number" step="1" name="baseMin" class="form-control form-control-sm text-end" value="${r.baseMin}" form="form-edit-${r.rateId}"/>
                        </td>
                        <td class="text-end">
                            <input type="number" step="1" name="baseMax" class="form-control form-control-sm text-end" value="${r.baseMax}" form="form-edit-${r.rateId}"/>
                        </td>
                        <td>
                            <input type="text" name="note" class="form-control form-control-sm" value="${r.note}" form="form-edit-${r.rateId}"/>
                        </td>
                        <td>
                            <form id="form-edit-${r.rateId}" method="post" action="${pageContext.request.contextPath}/sys/insurance-rate/edit.do" class="d-inline"></form>
                            <button class="btn btn-sm btn-outline-primary" form="form-edit-${r.rateId}" type="submit" title="저장">
                                <i class="bi bi-save"></i>
                            </button>
                            <form method="post" action="${pageContext.request.contextPath}/sys/insurance-rate/delete.do" class="d-inline"
                                  onsubmit="return confirm('이 요율 행을 삭제할까요? 과거 명세 재계산이 영향받을 수 있습니다.');">
                                <sec:csrfInput/>
                                <input type="hidden" name="rateId" value="${r.rateId}"/>
                                <button class="btn btn-sm btn-outline-danger" type="submit" title="삭제">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty rates}">
                    <tr><td colspan="9" class="text-center text-muted py-4">등록된 요율이 없습니다.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>
