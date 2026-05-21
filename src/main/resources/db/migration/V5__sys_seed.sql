-- =====================================================================
--  V5 : 시스템 관리 추가 시드 (V2에 없는 코드 그룹만 보충)
-- =====================================================================

-- ROLE_CD 그룹은 V2에 없으므로 추가
INSERT INTO gw_code_group (group_cd, group_nm, description, use_yn) VALUES
  ('ROLE_CD',   '시스템 역할', '사용자 역할 코드 (ADMIN/HR_MANAGER/...)', 'Y'),
  ('NOTI_TYPE', '알림 유형',  '인앱 알림 type_cd',                         'Y');

INSERT INTO gw_code (group_cd, code_val, code_nm, sort_no, use_yn) VALUES
  ('ROLE_CD', 'ADMIN',           '시스템 관리자',  1, 'Y'),
  ('ROLE_CD', 'HR_MANAGER',      '인사 관리자',    2, 'Y'),
  ('ROLE_CD', 'FINANCE_MANAGER', '재무 관리자',    3, 'Y'),
  ('ROLE_CD', 'MANAGER',         '부서 매니저',    4, 'Y'),
  ('ROLE_CD', 'EMPLOYEE',        '일반 직원',      5, 'Y');

INSERT INTO gw_code (group_cd, code_val, code_nm, sort_no, use_yn) VALUES
  ('NOTI_TYPE', 'NOTICE',   '공지',         1, 'Y'),
  ('NOTI_TYPE', 'APPROVAL', '결재',         2, 'Y'),
  ('NOTI_TYPE', 'LEAVE',    '휴가',         3, 'Y'),
  ('NOTI_TYPE', 'MESSAGE',  '쪽지',         4, 'Y'),
  ('NOTI_TYPE', 'ROOM',     '회의실',       5, 'Y'),
  ('NOTI_TYPE', 'CALENDAR', '캘린더',       6, 'Y'),
  ('NOTI_TYPE', 'EXPENSE',  '지출결의',     7, 'Y'),
  ('NOTI_TYPE', 'BOARD',    '게시판',       8, 'Y'),
  ('NOTI_TYPE', 'SYSTEM',   '시스템',       9, 'Y');
