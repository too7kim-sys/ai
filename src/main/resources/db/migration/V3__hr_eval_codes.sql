-- =============================================================
--  HR / 평가 / 성과 모듈 공통코드 + 평가양식 시드
-- =============================================================

INSERT INTO GW_CODE_GROUP (GROUP_CD, GROUP_NM, DESCRIPTION) VALUES
 ('HR_RECORD_CATEGORY', '인사기록 분류', NULL),
 ('HR_CHANGE_TYPE',     '인사이력 변경유형', NULL),
 ('FAMILY_RELATION',    '가족관계', NULL);

INSERT INTO GW_CODE (GROUP_CD, CODE_VAL, CODE_NM, SORT_NO) VALUES
 ('HR_RECORD_CATEGORY','HIRE','입사',1),
 ('HR_RECORD_CATEGORY','PROMOTION','승진',2),
 ('HR_RECORD_CATEGORY','TRANSFER','이동',3),
 ('HR_RECORD_CATEGORY','EDUCATION','교육이수',4),
 ('HR_RECORD_CATEGORY','AWARD','포상',5),
 ('HR_RECORD_CATEGORY','DISCIPLINE','징계',6),
 ('HR_RECORD_CATEGORY','CERT','자격취득',7),
 ('HR_RECORD_CATEGORY','LEAVE','휴직',8),
 ('HR_RECORD_CATEGORY','TERMINATION','퇴직',9),
 ('HR_RECORD_CATEGORY','ETC','기타',99),

 ('HR_CHANGE_TYPE','HIRE','입사',1),
 ('HR_CHANGE_TYPE','DEPT_CHANGE','부서이동',2),
 ('HR_CHANGE_TYPE','POSITION_CHANGE','직급변경',3),
 ('HR_CHANGE_TYPE','ROLE_CHANGE','역할변경',4),
 ('HR_CHANGE_TYPE','SALARY_CHANGE','연봉변경',5),
 ('HR_CHANGE_TYPE','TERMINATION','퇴직',9),

 ('FAMILY_RELATION','SPOUSE','배우자',1),
 ('FAMILY_RELATION','CHILD','자녀',2),
 ('FAMILY_RELATION','PARENT','부모',3),
 ('FAMILY_RELATION','SIBLING','형제자매',4),
 ('FAMILY_RELATION','OTHER','기타',9);

-- 기본 평가 양식
INSERT INTO GW_EVAL_FORM (FORM_NM, ITEMS_JSON) VALUES
 ('표준 사원 평가표 (5항목)',
  '[{"k":"communication","l":"커뮤니케이션","w":20},'
  '{"k":"ownership","l":"주인의식","w":20},'
  '{"k":"expertise","l":"전문성","w":25},'
  '{"k":"collaboration","l":"협업","w":20},'
  '{"k":"growth","l":"성장 의지","w":15}]'),
 ('관리자 평가표 (6항목)',
  '[{"k":"leadership","l":"리더십","w":25},'
  '{"k":"strategy","l":"전략적 사고","w":15},'
  '{"k":"execution","l":"실행력","w":20},'
  '{"k":"people","l":"인재 육성","w":15},'
  '{"k":"results","l":"성과 창출","w":15},'
  '{"k":"integrity","l":"청렴/윤리","w":10}]');
