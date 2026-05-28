<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<title>근로계약 작성</title>
<h2 class="mb-3"><i class="bi bi-pencil"></i> 근로계약 신규 작성</h2>
<p class="text-muted small mb-3">
    <i class="bi bi-info-circle"></i>
    연봉을 입력하면 동일 기간의 <strong>연봉(급여)계약</strong> 이 자동으로 생성되어 이 근로계약에 연결되고,
    다음 달부터 월급여가 자동 계산됩니다. (4대보험·소득세 포함)
</p>

<form method="post" action="${pageContext.request.contextPath}/contract/admin/write.do" class="needs-validation" novalidate>
    <sec:csrfInput/>

    <div class="card mb-3">
        <div class="card-header"><i class="bi bi-person-badge"></i> 기본 정보</div>
        <div class="card-body row g-3">
            <div class="col-md-3"><label class="form-label">사원 <span class="text-danger">*</span></label>
                <div class="input-group">
                    <input type="hidden" name="userId" id="contractUserId" value="${userId}" required/>
                    <input type="text" id="contractUserNm" class="form-control" readonly placeholder="사원 선택"/>
                    <button type="button" class="btn btn-outline-primary"
                            onclick="openUserPicker({hidden:'contractUserId', display:'contractUserNm'})">
                        <i class="bi bi-person-search"></i>
                    </button>
                </div>
            </div>
            <div class="col-md-4"><label class="form-label">계약서 양식 <span class="text-danger">*</span></label>
                <select class="form-select" name="templateId" required>
                    <c:forEach var="t" items="${templates}">
                        <option value="${t.templateId}" data-type="${t.contractTypeCd}">${t.templateNm}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2"><label class="form-label">계약 종류 <span class="text-danger">*</span></label>
                <select class="form-select" name="contractTypeCd" required>
                    <option value="REGULAR">정규직</option>
                    <option value="FIXED_TERM">기간제</option>
                    <option value="PART_TIME">단시간</option>
                    <option value="TEMP">시용(수습)</option>
                    <option value="INTERN">인턴</option>
                </select>
            </div>
            <div class="col-md-3"><label class="form-label">시작일 <span class="text-danger">*</span></label>
                <input type="date" class="form-control" name="startDt" required/></div>
            <div class="col-md-3"><label class="form-label">종료일 <span class="text-muted small">(공란=무기한)</span></label>
                <input type="date" class="form-control" name="endDt"/></div>
            <div class="col-md-3"><label class="form-label">수습 기간 (개월)</label>
                <input type="number" class="form-control" name="probationMonths" value="0" min="0" max="12"/></div>
            <div class="col-md-6"><label class="form-label">근무 장소 <span class="text-danger">*</span></label>
                <input class="form-control" name="workplace" required/></div>
            <div class="col-md-6"><label class="form-label">업무 내용 <span class="text-danger">*</span></label>
                <input class="form-control" name="jobDescription" required/></div>
        </div>
    </div>

    <div class="card mb-3">
        <div class="card-header"><i class="bi bi-clock"></i> 근로 조건</div>
        <div class="card-body row g-3">
            <div class="col-md-2"><label class="form-label">주 소정근로시간</label>
                <div class="input-group"><input type="number" step="0.5" class="form-control" name="workHoursPerWeek" value="40"/><span class="input-group-text">h</span></div></div>
            <div class="col-md-2"><label class="form-label">시작 시각</label>
                <input type="time" class="form-control" name="workStartTime" value="09:00"/></div>
            <div class="col-md-2"><label class="form-label">종료 시각</label>
                <input type="time" class="form-control" name="workEndTime" value="18:00"/></div>
            <div class="col-md-2"><label class="form-label">휴게 (분)</label>
                <input type="number" class="form-control" name="breakMinutes" value="60" min="0"/></div>
            <div class="col-md-2"><label class="form-label">주휴일</label>
                <input class="form-control" name="weeklyHoliday" value="일요일"/></div>
            <div class="col-md-2"><label class="form-label">연차 부여 (일)</label>
                <input type="number" class="form-control" name="annualPaidLeaveDays" value="15" min="0"/></div>
        </div>
    </div>

    <div class="card mb-3 border-primary">
        <div class="card-header bg-primary-subtle"><i class="bi bi-cash-coin"></i> 연봉 (급여계약)</div>
        <div class="card-body row g-3">
            <div class="col-md-3"><label class="form-label">연봉 <span class="text-muted small">(원)</span></label>
                <input type="number" class="form-control" name="annualSalary" min="0" step="100000"
                       placeholder="예: 48000000"/>
                <div class="form-text">입력 시 월 기본급 = 연봉/12 로 자동 산정.</div>
            </div>
            <div class="col-md-3"><label class="form-label">월 기본급 <span class="text-muted small">(직접 입력)</span></label>
                <input type="number" class="form-control" name="monthlyBaseSal" min="0" step="10000"
                       placeholder="공란 시 자동"/></div>
            <div class="col-md-2"><label class="form-label">급여 형태</label>
                <select class="form-select" name="divisionTypeCd">
                    <option value="MONTHLY" selected>월급제</option>
                    <option value="HOURLY">시급제</option>
                    <option value="DAILY">일급제</option>
                    <option value="ANNUAL">연봉제</option>
                </select>
            </div>
            <div class="col-md-2"><label class="form-label">지급일</label>
                <div class="input-group"><input type="number" class="form-control" name="paymentDay" value="25" min="1" max="31"/><span class="input-group-text">일</span></div></div>
            <div class="col-md-2"><label class="form-label">&nbsp;</label>
                <div class="form-text">연봉 미입력 시 급여계약은 생성되지 않으며 별도 등록이 필요합니다.</div>
            </div>
        </div>
    </div>

    <div class="card mb-3">
        <div class="card-header"><i class="bi bi-shield-check"></i> 4대보험 적용</div>
        <div class="card-body">
            <div class="row g-2">
                <div class="col-auto form-check form-switch">
                    <input class="form-check-input" type="checkbox" id="insNp" name="applyNp" value="true" checked/>
                    <label class="form-check-label" for="insNp">국민연금 (NP)</label>
                </div>
                <div class="col-auto form-check form-switch ms-3">
                    <input class="form-check-input" type="checkbox" id="insHi" name="applyHi" value="true" checked/>
                    <label class="form-check-label" for="insHi">건강보험 (HI · 장기요양 포함)</label>
                </div>
                <div class="col-auto form-check form-switch ms-3">
                    <input class="form-check-input" type="checkbox" id="insEi" name="applyEi" value="true" checked/>
                    <label class="form-check-label" for="insEi">고용보험 (EI)</label>
                </div>
                <div class="col-auto form-check form-switch ms-3">
                    <input class="form-check-input" type="checkbox" id="insWc" name="applyWc" value="true" checked/>
                    <label class="form-check-label" for="insWc">산재보험 (WC · 회사 부담)</label>
                </div>
            </div>
            <div class="form-text mt-2">체크 항목은 계약서에 기록되며, 실제 공제·부담 금액은 시스템 - <a href="${pageContext.request.contextPath}/payroll/admin/insurance-rate.do">4대보험 요율</a> 에 등록된 비율과 보수월액으로 자동 계산됩니다.</div>
        </div>
    </div>

    <div class="card mb-3">
        <div class="card-header"><i class="bi bi-pen"></i> 특약 / 비고</div>
        <div class="card-body">
            <textarea class="form-control" name="specialTerms" rows="3" placeholder="비밀유지·경업금지·교육비반환 등 별도 약정"></textarea>
        </div>
    </div>

    <div>
        <button class="btn btn-primary"><i class="bi bi-arrow-right"></i> 미리보기로 이동</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/contract/admin/list.do">취소</a>
    </div>
</form>
