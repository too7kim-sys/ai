-- =============================================================
--  마스터/공통코드/요율/양식/메일 템플릿 시드
--  (사용자/트랜잭션 시드는 DataInitializer에서 bcrypt 해시 생성 후 삽입)
-- =============================================================

-- 테넌트
INSERT INTO GW_TENANT (TENANT_ID, TENANT_NM) VALUES (1, '기본 회사');

-- 역할
INSERT INTO GW_ROLE (ROLE_CD, ROLE_NM, DESCRIPTION, SORT_NO) VALUES
 ('ADMIN',           '시스템 관리자',  '모든 기능 접근', 10),
 ('HR_MANAGER',      '인사 관리자',    '인사·근로계약·급여 관리', 20),
 ('FINANCE_MANAGER', '재무 관리자',    '거래처·사업자계약·인보이스·지출 회계', 30),
 ('MANAGER',         '부서장',         '부서원 결재·근태 승인', 40),
 ('EMPLOYEE',        '일반 직원',      '본인 데이터 + 게시판·캘린더·회의실', 50);

-- 직급
INSERT INTO GW_POSITION (POSITION_ID, POSITION_NM, LEVEL_NO) VALUES
 (1,'사원',1),(2,'주임',2),(3,'대리',3),(4,'과장',4),(5,'차장',5),(6,'부장',6),(7,'대표',9);

-- 부서
INSERT INTO GW_DEPT (DEPT_ID, DEPT_NM, PARENT_ID, SORT_NO) VALUES
 (1,'대표이사',NULL,1),
 (2,'경영지원본부',1,2),
 (3,'인사팀',2,3),
 (4,'재무팀',2,4),
 (5,'개발본부',1,10),
 (6,'개발1팀',5,11),
 (7,'개발2팀',5,12),
 (8,'디자인팀',5,13),
 (9,'마케팅팀',1,20),
 (10,'영업팀',1,21);

-- 회의실
INSERT INTO GW_ROOM (ROOM_ID, ROOM_NM, CAPACITY, LOCATION, EQUIPMENTS) VALUES
 (1,'대회의실A',20,'본사 5층','빔프로젝터, 화상회의'),
 (2,'중회의실B',10,'본사 5층','TV, 화이트보드'),
 (3,'소회의실C',6,'본사 4층','TV'),
 (4,'세미나실',40,'본사 3층','빔프로젝터, 마이크, 카메라'),
 (5,'전화부스',1,'본사 4층','전화기');

-- 메뉴 (계층)
INSERT INTO GW_MENU (MENU_ID, MENU_NM, URL, PARENT_ID, SORT_NO, ICON) VALUES
 (1,'대시보드','/dashboard.do',NULL,1,'bi-speedometer2'),
 (2,'직원 디렉토리','/user/list.do',NULL,2,'bi-people'),
 (3,'공지사항','/notice/list.do',NULL,3,'bi-megaphone'),
 (4,'일정','/calendar/main.do',NULL,4,'bi-calendar3'),
 (5,'전자결재',NULL,NULL,5,'bi-file-earmark-check'),
 (51,'기안하기','/approval/write.do',5,1,NULL),
 (52,'결재 대기함','/approval/pending.do',5,2,NULL),
 (53,'기안함','/approval/draft.do',5,3,NULL),
 (54,'완료함','/approval/completed.do',5,4,NULL),
 (55,'반려함','/approval/rejected.do',5,5,NULL),
 (56,'참조함','/approval/refer.do',5,6,NULL),
 (6,'휴가',NULL,NULL,6,'bi-airplane'),
 (61,'휴가 신청','/leave/write.do',6,1,NULL),
 (62,'내 휴가','/leave/my.do',6,2,NULL),
 (63,'휴가 잔여','/leave/balance.do',6,3,NULL),
 (7,'근태',NULL,NULL,7,'bi-clock'),
 (71,'출퇴근','/attendance/my.do',7,1,NULL),
 (72,'근태 리포트(HR)','/attendance/admin/report.do',7,2,NULL),
 (8,'급여',NULL,NULL,8,'bi-cash'),
 (81,'내 명세서','/payroll/my.do',8,1,NULL),
 (82,'급여 산정(HR)','/payroll/admin/list.do',8,2,NULL),
 (83,'연봉계약(HR)','/payroll/admin/contract.do',8,3,NULL),
 (84,'4대보험 요율(HR)','/payroll/admin/insurance-rate.do',8,4,NULL),
 (9,'인사',NULL,NULL,9,'bi-person-vcard'),
 (91,'조직도','/hr/org.do',9,1,NULL),
 (92,'인사기록','/hr/record.do',9,2,NULL),
 (93,'인사이력','/hr/history.do',9,3,NULL),
 (94,'부양가족','/hr/family.do',9,4,NULL),
 (10,'근로계약',NULL,NULL,10,'bi-file-text'),
 (101,'내 계약서','/contract/my.do',10,1,NULL),
 (102,'계약 관리(HR)','/contract/admin/list.do',10,2,NULL),
 (11,'평가/성과',NULL,NULL,11,'bi-graph-up'),
 (111,'평가표','/evaluation/sheet.do',11,1,NULL),
 (112,'KPI','/performance/my.do',11,2,NULL),
 (12,'회의실 예약','/room/list.do',NULL,12,'bi-door-open'),
 (13,'쪽지','/message/inbox.do',NULL,13,'bi-chat-dots'),
 (14,'거래처','/vendor/list.do',NULL,14,'bi-building'),
 (15,'사업자 계약','/biz-contract/list.do',NULL,15,'bi-briefcase'),
 (16,'인보이스',NULL,NULL,16,'bi-receipt'),
 (161,'매출 인보이스','/invoice/out.do',16,1,NULL),
 (162,'매입 인보이스','/invoice/in.do',16,2,NULL),
 (163,'세금계산서','/invoice/tax.do',16,3,NULL),
 (17,'입출금','/payment/list.do',NULL,17,'bi-bank'),
 (18,'재무 대시보드',NULL,NULL,18,'bi-bar-chart'),
 (181,'미수금(AR)','/finance/ar.do',18,1,NULL),
 (182,'미지급(AP)','/finance/ap.do',18,2,NULL),
 (183,'연체 분석','/finance/aging.do',18,3,NULL),
 (184,'현금흐름','/finance/cashflow.do',18,4,NULL),
 (185,'지출 리포트','/finance/expense-report.do',18,5,NULL),
 (19,'지출결의',NULL,NULL,19,'bi-credit-card'),
 (191,'결의서 작성','/expense/write.do',19,1,NULL),
 (192,'내 결의서','/expense/my.do',19,2,NULL),
 (193,'결의서 관리(재무)','/expense/admin/list.do',19,3,NULL),
 (194,'환급 처리','/expense/admin/reimburse.do',19,4,NULL),
 (20,'시스템 관리',NULL,NULL,90,'bi-gear'),
 (201,'사용자','/sys/user/list.do',20,1,NULL),
 (202,'메뉴/권한','/sys/menu/matrix.do',20,2,NULL),
 (203,'공통코드','/sys/code/list.do',20,3,NULL),
 (204,'계정과목','/sys/account/list.do',20,4,NULL),
 (205,'부서 예산','/sys/budget/list.do',20,5,NULL),
 (206,'감사 로그','/sys/audit/list.do',20,6,NULL),
 (207,'로그인 이력','/sys/loginlog/list.do',20,7,NULL),
 (208,'메일 템플릿','/sys/mail/template.do',20,8,NULL),
 (209,'메일 이력','/sys/mail/log.do',20,9,NULL);

-- 메뉴/권한 매트릭스 (전사 공통 접근은 모두 EMPLOYEE 이상)
INSERT INTO GW_MENU_ROLE (MENU_ID, ROLE_CD)
 SELECT M.MENU_ID, R.ROLE_CD FROM GW_MENU M
   CROSS JOIN GW_ROLE R
   WHERE M.MENU_ID IN (1,2,3,4,5,51,52,53,54,55,56,6,61,62,63,7,71,12,13,8,81,9,91,94,10,101,11,111,112);

-- HR 전용
INSERT INTO GW_MENU_ROLE (MENU_ID, ROLE_CD)
 SELECT M.MENU_ID, 'HR_MANAGER' FROM GW_MENU M
   WHERE M.MENU_ID IN (72,82,83,84,92,93,102);
INSERT INTO GW_MENU_ROLE (MENU_ID, ROLE_CD)
 SELECT M.MENU_ID, 'ADMIN' FROM GW_MENU M
   WHERE M.MENU_ID IN (72,82,83,84,92,93,102);

-- 재무 전용
INSERT INTO GW_MENU_ROLE (MENU_ID, ROLE_CD)
 SELECT M.MENU_ID, 'FINANCE_MANAGER' FROM GW_MENU M
   WHERE M.MENU_ID IN (14,15,16,161,162,163,17,18,181,182,183,184,185,19,191,192,193,194);
INSERT INTO GW_MENU_ROLE (MENU_ID, ROLE_CD)
 SELECT M.MENU_ID, 'ADMIN' FROM GW_MENU M
   WHERE M.MENU_ID IN (14,15,16,161,162,163,17,18,181,182,183,184,185,19,191,192,193,194);
-- 지출결의 작성은 모두 가능
INSERT INTO GW_MENU_ROLE (MENU_ID, ROLE_CD)
 SELECT M.MENU_ID, R.ROLE_CD FROM GW_MENU M CROSS JOIN GW_ROLE R
   WHERE M.MENU_ID IN (19,191,192);

-- 시스템 관리: ADMIN만
INSERT INTO GW_MENU_ROLE (MENU_ID, ROLE_CD)
 SELECT M.MENU_ID, 'ADMIN' FROM GW_MENU M
   WHERE M.MENU_ID IN (20,201,202,203,204,205,206,207,208,209);

-- 공통코드
INSERT INTO GW_CODE_GROUP (GROUP_CD, GROUP_NM, DESCRIPTION) VALUES
 ('LEAVE_TYPE','휴가 유형',NULL),
 ('APPROVAL_STATUS','결재 상태',NULL),
 ('EVAL_GRADE','평가 등급',NULL),
 ('PAY_ITEM_PAYMENT','급여 지급 항목',NULL),
 ('PAY_ITEM_DEDUCTION','급여 공제 항목',NULL),
 ('EXPENSE_CATEGORY','지출 분류',NULL),
 ('PAYMENT_METHOD','결제 수단',NULL),
 ('RECEIPT_TYPE','증빙 유형',NULL),
 ('CONTRACT_TYPE_BIZ','사업자 계약 유형',NULL),
 ('PAYMENT_TERMS','결제 조건',NULL),
 ('BANK','은행',NULL);

INSERT INTO GW_CODE (GROUP_CD, CODE_VAL, CODE_NM, SORT_NO) VALUES
 ('LEAVE_TYPE','ANNUAL','연차',1),
 ('LEAVE_TYPE','HALF','반차',2),
 ('LEAVE_TYPE','SICK','병가',3),
 ('LEAVE_TYPE','FAMILY','경조사',4),
 ('LEAVE_TYPE','OTHER','기타',9),
 ('APPROVAL_STATUS','DRAFT','임시저장',1),
 ('APPROVAL_STATUS','IN_PROGRESS','진행중',2),
 ('APPROVAL_STATUS','APPROVED','완료',3),
 ('APPROVAL_STATUS','REJECTED','반려',4),
 ('APPROVAL_STATUS','CANCELED','회수',5),
 ('EVAL_GRADE','S','S (최우수)',1),
 ('EVAL_GRADE','A','A (우수)',2),
 ('EVAL_GRADE','B','B (보통)',3),
 ('EVAL_GRADE','C','C (개선)',4),
 ('EVAL_GRADE','D','D (저조)',5),
 ('EXPENSE_CATEGORY','ENTERTAIN','접대비',1),
 ('EXPENSE_CATEGORY','MEAL','식대',2),
 ('EXPENSE_CATEGORY','TRANSPORT','교통비',3),
 ('EXPENSE_CATEGORY','TELECOM','통신비',4),
 ('EXPENSE_CATEGORY','SUPPLIES','소모품',5),
 ('EXPENSE_CATEGORY','BOOKS','도서',6),
 ('EXPENSE_CATEGORY','EDU','교육',7),
 ('EXPENSE_CATEGORY','MEETING','회의비',8),
 ('EXPENSE_CATEGORY','TRIP','출장비',9),
 ('EXPENSE_CATEGORY','AD','광고선전',10),
 ('EXPENSE_CATEGORY','RENT','임차료',11),
 ('EXPENSE_CATEGORY','FEE','지급수수료',12),
 ('EXPENSE_CATEGORY','ETC','기타',99),
 ('PAYMENT_METHOD','CORPORATE_CARD','법인카드',1),
 ('PAYMENT_METHOD','PERSONAL_CARD','개인카드',2),
 ('PAYMENT_METHOD','CASH','현금',3),
 ('PAYMENT_METHOD','BANK_TRANSFER','계좌이체',4),
 ('RECEIPT_TYPE','TAX_INVOICE','세금계산서',1),
 ('RECEIPT_TYPE','CASH_RECEIPT','현금영수증',2),
 ('RECEIPT_TYPE','CARD','카드영수증',3),
 ('RECEIPT_TYPE','SIMPLE_RECEIPT','간이영수증',4),
 ('RECEIPT_TYPE','OTHER','기타',9),
 ('CONTRACT_TYPE_BIZ','SALES','매출',1),
 ('CONTRACT_TYPE_BIZ','PURCHASE','매입',2),
 ('CONTRACT_TYPE_BIZ','OUTSOURCE','외주',3),
 ('CONTRACT_TYPE_BIZ','SUBSCRIPTION','구독',4),
 ('CONTRACT_TYPE_BIZ','LICENSE','라이선스',5),
 ('CONTRACT_TYPE_BIZ','LEASE','리스',6),
 ('CONTRACT_TYPE_BIZ','NDA','NDA',7),
 ('CONTRACT_TYPE_BIZ','OTHER','기타',9),
 ('PAYMENT_TERMS','ADVANCE','선급',1),
 ('PAYMENT_TERMS','LUMPSUM','일시불',2),
 ('PAYMENT_TERMS','MONTHLY','월간',3),
 ('PAYMENT_TERMS','QUARTERLY','분기',4),
 ('PAYMENT_TERMS','MILESTONE','마일스톤',5),
 ('PAYMENT_TERMS','NET30','NET30',6),
 ('PAYMENT_TERMS','NET60','NET60',7),
 ('PAYMENT_TERMS','NET90','NET90',8),
 ('BANK','004','국민은행',1),
 ('BANK','088','신한은행',2),
 ('BANK','020','우리은행',3),
 ('BANK','081','하나은행',4),
 ('BANK','003','기업은행',5);

-- 급여 코드: 지급 (TAXABLE_YN 표현은 EXTRA_VAL='Y' = 과세, 'N' = 비과세)
INSERT INTO GW_CODE (GROUP_CD, CODE_VAL, CODE_NM, SORT_NO, EXTRA_VAL) VALUES
 ('PAY_ITEM_PAYMENT','BASE','기본급',1,'Y'),
 ('PAY_ITEM_PAYMENT','POSITION_ALLOW','직책수당',2,'Y'),
 ('PAY_ITEM_PAYMENT','JOB_ALLOW','직무수당',3,'Y'),
 ('PAY_ITEM_PAYMENT','MEAL','식대',10,'N'),
 ('PAY_ITEM_PAYMENT','VEHICLE','자가운전보조금',11,'N'),
 ('PAY_ITEM_PAYMENT','CHILDCARE','출산보육수당',12,'N'),
 ('PAY_ITEM_PAYMENT','RESEARCH','연구활동비',13,'N'),
 ('PAY_ITEM_PAYMENT','OVERTIME','연장근로수당',20,'Y'),
 ('PAY_ITEM_PAYMENT','NIGHT_WORK','야간근로수당',21,'Y'),
 ('PAY_ITEM_PAYMENT','HOLIDAY_WORK','휴일근로수당',22,'Y'),
 ('PAY_ITEM_PAYMENT','WEEKLY_HOLIDAY','주휴수당',23,'Y'),
 ('PAY_ITEM_PAYMENT','FAMILY','가족수당',30,'Y'),
 ('PAY_ITEM_PAYMENT','EDUCATION','자녀학자금',31,'Y'),
 ('PAY_ITEM_PAYMENT','REGULAR_BONUS','정기상여',40,'Y'),
 ('PAY_ITEM_PAYMENT','HOLIDAY_BONUS','명절상여',41,'Y'),
 ('PAY_ITEM_PAYMENT','PERFORMANCE_BONUS','성과급',42,'Y'),
 ('PAY_ITEM_PAYMENT','LONG_SERVICE','장기근속수당',43,'Y'),
 ('PAY_ITEM_PAYMENT','ETC_ALLOW','기타수당',99,'Y');

INSERT INTO GW_CODE (GROUP_CD, CODE_VAL, CODE_NM, SORT_NO) VALUES
 ('PAY_ITEM_DEDUCTION','NP','국민연금',1),
 ('PAY_ITEM_DEDUCTION','HI','건강보험',2),
 ('PAY_ITEM_DEDUCTION','LTC','장기요양보험',3),
 ('PAY_ITEM_DEDUCTION','EI','고용보험',4),
 ('PAY_ITEM_DEDUCTION','INCOME_TAX','소득세',10),
 ('PAY_ITEM_DEDUCTION','LOCAL_INCOME_TAX','지방소득세',11),
 ('PAY_ITEM_DEDUCTION','UNION_DUES','노조비',20),
 ('PAY_ITEM_DEDUCTION','ETC_LOAN','사내대출 상환',21),
 ('PAY_ITEM_DEDUCTION','ETC_DEDUCTION','기타공제',99);

-- 4대보험 요율 (2025년 기준, 근로자 부담 위주)
INSERT INTO GW_INSURANCE_RATE (INSURANCE_CD, EFFECTIVE_FROM, EMPLOYEE_RATE, EMPLOYER_RATE, BASE_MIN, BASE_MAX, NOTE) VALUES
 ('NP', DATE '2025-01-01', 0.04500, 0.04500, 390000, 6170000, '국민연금'),
 ('HI', DATE '2025-01-01', 0.03545, 0.03545, NULL, NULL, '건강보험'),
 ('LTC', DATE '2025-01-01', 0.12950, 0.12950, NULL, NULL, '장기요양 (건강보험료 기준)'),
 ('EI', DATE '2025-01-01', 0.00900, 0.00900, NULL, NULL, '고용보험'),
 ('WC', DATE '2025-01-01', 0.00000, 0.00800, NULL, NULL, '산재보험 (회사 전액 부담, 업종 평균치)');

-- 계정과목
INSERT INTO GW_ACCOUNT_CODE (ACCOUNT_CD, ACCOUNT_NM, TYPE_CD, BUDGET_CONTROL_YN) VALUES
 ('521','복리후생비','EXPENSE','Y'),
 ('513','접대비','EXPENSE','Y'),
 ('522','여비교통비','EXPENSE','Y'),
 ('524','통신비','EXPENSE','Y'),
 ('530','소모품비','EXPENSE','Y'),
 ('531','도서인쇄비','EXPENSE','N'),
 ('532','교육훈련비','EXPENSE','Y'),
 ('533','회의비','EXPENSE','N'),
 ('540','광고선전비','EXPENSE','Y'),
 ('541','임차료','EXPENSE','N'),
 ('542','지급수수료','EXPENSE','N'),
 ('599','기타비용','EXPENSE','N');

-- 결재 양식
INSERT INTO GW_APPROVAL_FORM (FORM_CD, FORM_NM, FIELDS_JSON, USE_YN) VALUES
 ('LEAVE','휴가 신청서','{"fields":[{"k":"leaveType","l":"휴가종류","t":"select"},{"k":"start","l":"시작일","t":"date"},{"k":"end","l":"종료일","t":"date"},{"k":"reason","l":"사유","t":"textarea"}]}','Y'),
 ('GENERAL_PROPOSAL','일반 품의서','{"fields":[{"k":"subject","l":"제목","t":"text"},{"k":"content","l":"내용","t":"textarea"}]}','Y'),
 ('EXPENSE','지출결의서','{"fields":[{"k":"purpose","l":"사용목적","t":"textarea"},{"k":"items","l":"지출 라인","t":"line"}]}','Y'),
 ('BUSINESS_TRIP','출장 신청서','{"fields":[{"k":"dest","l":"출장지","t":"text"},{"k":"start","l":"시작","t":"date"},{"k":"end","l":"종료","t":"date"},{"k":"purpose","l":"목적","t":"textarea"}]}','Y'),
 ('OVERTIME','연장근무 신청서','{"fields":[{"k":"workDt","l":"근무일","t":"date"},{"k":"hours","l":"시간","t":"number"},{"k":"reason","l":"사유","t":"textarea"}]}','Y'),
 ('CONTRACT_PROPOSAL','사업자 계약 품의서','{"fields":[{"k":"vendor","l":"거래처","t":"text"},{"k":"amount","l":"금액","t":"number"},{"k":"period","l":"기간","t":"text"},{"k":"reason","l":"사유","t":"textarea"}]}','Y'),
 ('PAYMENT_REQUEST','대금 지급 품의서','{"fields":[{"k":"vendor","l":"거래처","t":"text"},{"k":"amount","l":"금액","t":"number"},{"k":"invoice","l":"인보이스","t":"text"}]}','Y'),
 ('HR_PROPOSAL','인사 품의서','{"fields":[{"k":"target","l":"대상자","t":"text"},{"k":"action","l":"조치","t":"text"},{"k":"reason","l":"사유","t":"textarea"}]}','Y');

-- 근로계약 양식
INSERT INTO GW_CONTRACT_TEMPLATE (TEMPLATE_NM, CONTRACT_TYPE_CD, BODY_HTML, USE_YN) VALUES
 ('정규직 근로계약서','REGULAR','<h2>표준 근로계약서</h2><p>회사(이하 ''갑'')와 근로자 {{userName}}(이하 ''을'')은 다음과 같이 근로계약을 체결한다.</p><ul><li>근무 장소: {{workplace}}</li><li>업무 내용: {{jobDescription}}</li><li>계약 기간: {{startDt}} ~ {{endDt}}</li><li>소정 근로시간: 주 {{workHoursPerWeek}}시간</li><li>임금: 연 {{annualSalary}}원</li></ul>','Y'),
 ('기간제 근로계약서','FIXED_TERM','<h2>기간제 근로계약서</h2><p>...</p>','Y'),
 ('단시간 근로계약서','PART_TIME','<h2>단시간 근로계약서</h2><p>...</p>','Y'),
 ('시용기간 근로계약서','TEMP','<h2>시용기간 근로계약서</h2><p>...</p>','Y'),
 ('인턴 근로계약서','INTERN','<h2>인턴 근로계약서</h2><p>...</p>','Y');

-- 메일 템플릿
INSERT INTO GW_MAIL_TEMPLATE (TEMPLATE_CD, TEMPLATE_NM, SUBJECT, BODY_HTML, ATTACH_TYPE_CD) VALUES
 ('PAYSLIP_NOTICE','급여명세서 발송','[{{companyName}}] {{payMonth}} 급여명세서','<p>{{userName}}님,</p><p>{{payMonth}} 급여명세서를 첨부와 같이 발송드립니다.</p><p>실수령액: {{netPay}}원</p>','PAYSLIP'),
 ('CONTRACT_SIGN_REQUEST','근로계약 서명 요청','[{{companyName}}] 근로계약서 서명 요청','<p>{{userName}}님, 근로계약서 서명을 요청드립니다.</p><p><a href="{{signUrl}}">서명 페이지 열기</a></p>','CONTRACT'),
 ('INVOICE_ISSUE','인보이스 발행','[{{companyName}}] 인보이스 #{{invoiceNo}}','<p>{{vendorName}}님께,</p><p>인보이스를 발행하였습니다. 금액: {{amount}}원, 지급 만기: {{dueDt}}</p>','NONE'),
 ('INVOICE_REMINDER','대금 미수금 안내','[{{companyName}}] 미수금 안내 #{{invoiceNo}}','<p>{{vendorName}}님, 인보이스 #{{invoiceNo}}의 잔액 {{remaining}}원이 미수 상태입니다.</p>','NONE'),
 ('TAX_INVOICE_ISSUE','세금계산서 발행','[{{companyName}}] 세금계산서 #{{tiNo}}','<p>세금계산서를 발행하였습니다.</p>','NONE'),
 ('PAYMENT_COMPLETED','대금 지급 완료','[{{companyName}}] 대금 지급 완료','<p>{{vendorName}}님, 대금 지급이 완료되었습니다. 금액: {{amount}}원</p>','NONE'),
 ('CONTRACT_EXPIRY_NOTICE','사업자 계약 만료 임박','[{{companyName}}] 계약 만료 {{daysLeft}}일 전','<p>계약 {{contractNo}}이 {{daysLeft}}일 후 만료됩니다.</p>','NONE'),
 ('EXPENSE_APPROVED','지출결의 결재 완료','[{{companyName}}] 지출결의 결재 완료','<p>{{userName}}님의 지출결의 {{reportNo}}이 결재 완료되었습니다.</p>','NONE'),
 ('EXPENSE_REIMBURSED','지출 환급 완료','[{{companyName}}] 지출 환급 완료 안내','<p>{{userName}}님, 지출결의 {{reportNo}}의 환급금 {{amount}}원이 지급되었습니다.</p>','NONE'),
 ('ACCOUNT_UNLOCKED','계정 잠금 해제','[{{companyName}}] 계정 잠금 해제 안내','<p>{{userName}}님의 계정 잠금이 해제되었습니다.</p>','NONE'),
 ('APPROVAL_REQUEST','결재 요청','[{{companyName}}] 결재 요청 - {{docTitle}}','<p>{{drafterName}}님의 결재 요청이 도착했습니다.</p>','NONE');
