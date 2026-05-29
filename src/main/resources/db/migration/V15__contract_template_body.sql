-- 근로계약 템플릿 본문 보강
-- V2 시드의 기간제/단시간/시용/인턴 템플릿은 본문이 "...]" 정도로만 채워져
-- 있어 미리보기/PDF 에 입력한 항목이 거의 노출되지 않았다. 5 종 모두 입력
-- 폼이 받는 모든 필드(연봉·근무시간 시작/종료·휴게·주휴일·연차·수습·
-- 사회보험·특약 등) 를 한 페이지 안에서 표시하도록 본문을 교체한다.
--
-- 변수 키는 ContractServiceImpl#vars() 와 매핑된다. 새 변수가 추가됐을 때
-- 이 본문도 함께 갱신해야 한다.

-- 공통 본문 — 종별 첫 줄만 다르게 두고 나머지는 동일
UPDATE GW_CONTRACT_TEMPLATE
   SET BODY_HTML = '<h2 style="text-align:center;margin-bottom:1.2rem">근로계약서 ({{contractTypeNm}})</h2>'
                 || '<p>주식회사 {{companyName}}(이하 ''갑'')과 근로자 <strong>{{userName}}</strong>(이하 ''을'')은 다음과 같이 근로계약을 체결한다.</p>'
                 || '<table style="width:100%;border-collapse:collapse;margin:.8rem 0" border="1" cellpadding="6">'
                 || '<tr><th style="width:25%;background:#f5f7fa">계약번호</th><td>{{contractNo}}</td></tr>'
                 || '<tr><th style="background:#f5f7fa">계약 종류</th><td>{{contractTypeNm}}</td></tr>'
                 || '<tr><th style="background:#f5f7fa">소속 / 사원</th><td>{{deptNm}} / {{userName}} ({{email}})</td></tr>'
                 || '<tr><th style="background:#f5f7fa">계약 기간</th><td>{{startDt}} ~ {{endDt}}</td></tr>'
                 || '<tr><th style="background:#f5f7fa">수습 기간</th><td>{{probationMonths}} 개월</td></tr>'
                 || '<tr><th style="background:#f5f7fa">근무 장소</th><td>{{workplace}}</td></tr>'
                 || '<tr><th style="background:#f5f7fa">업무 내용</th><td>{{jobDescription}}</td></tr>'
                 || '</table>'
                 || '<h4 style="margin-top:1.2rem">제1조 (근로시간 및 휴게)</h4>'
                 || '<ul>'
                 || '<li>주 소정근로시간: {{workHoursPerWeek}} 시간</li>'
                 || '<li>근무 시각: {{workStartTime}} ~ {{workEndTime}}</li>'
                 || '<li>휴게: {{breakMinutes}} 분</li>'
                 || '<li>주휴일: {{weeklyHoliday}}</li>'
                 || '<li>연차 유급휴가: 연 {{annualPaidLeaveDays}} 일 (근기법 제60조)</li>'
                 || '</ul>'
                 || '<h4 style="margin-top:1.2rem">제2조 (임금)</h4>'
                 || '<ul>'
                 || '<li>연봉: {{annualSalary}} 원</li>'
                 || '<li>월 기본급: {{monthlyBaseSal}} 원</li>'
                 || '<li>지급일: 매월 {{paymentDay}} 일 (휴일인 경우 직전 영업일)</li>'
                 || '<li>지급 방법: 본인 명의 계좌 입금</li>'
                 || '</ul>'
                 || '<h4 style="margin-top:1.2rem">제3조 (사회보험)</h4>'
                 || '<p>적용 보험: {{insuranceApplied}}</p>'
                 || '<h4 style="margin-top:1.2rem">제4조 (특약 사항)</h4>'
                 || '<p style="white-space:pre-wrap">{{specialTerms}}</p>'
                 || '<h4 style="margin-top:1.2rem">제5조 (기타)</h4>'
                 || '<p>본 계약서에 명시되지 않은 사항은 근로기준법 및 회사 취업규칙에 따른다.</p>'
                 || '<p style="margin-top:2.5rem;text-align:right">계약 체결일: {{startDt}}</p>'
                 || '<table style="width:100%;margin-top:1.2rem" cellpadding="6">'
                 || '<tr><td style="width:50%">갑: 주식회사 {{companyName}} (인)</td>'
                 || '<td style="width:50%">을: {{userName}} (서명)</td></tr></table>'
 WHERE CONTRACT_TYPE_CD IN ('REGULAR', 'FIXED_TERM', 'PART_TIME', 'TEMP', 'INTERN');
