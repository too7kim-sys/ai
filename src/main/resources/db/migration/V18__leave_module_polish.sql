-- 휴가 모듈 보강
-- 1) 반차에 오전/오후(AM/PM) 구분 추가
-- 2) HALF_TYPE 코드 그룹 시드 (멱등)
-- 3) 정책 변경(신청 즉시 잔여 차감) 에 대한 in-flight 보정 — 배포 시점 IN_PROGRESS 휴가에 대해
--    이전 정책에서 누락됐던 차감을 잔여에 일괄 반영

ALTER TABLE GW_LEAVE_REQ ADD COLUMN IF NOT EXISTS HALF_TYPE_CD VARCHAR(2);

-- 코드 그룹/코드는 멱등하게 추가 (이미 있으면 무시)
INSERT INTO GW_CODE_GROUP (GROUP_CD, GROUP_NM, DESCRIPTION)
SELECT 'HALF_TYPE','반차 구분','오전(AM)/오후(PM)'
 WHERE NOT EXISTS (SELECT 1 FROM GW_CODE_GROUP WHERE GROUP_CD = 'HALF_TYPE');

INSERT INTO GW_CODE (GROUP_CD, CODE_VAL, CODE_NM, SORT_NO)
SELECT 'HALF_TYPE','AM','오전 반차',1
 WHERE NOT EXISTS (SELECT 1 FROM GW_CODE WHERE GROUP_CD = 'HALF_TYPE' AND CODE_VAL = 'AM');

INSERT INTO GW_CODE (GROUP_CD, CODE_VAL, CODE_NM, SORT_NO)
SELECT 'HALF_TYPE','PM','오후 반차',2
 WHERE NOT EXISTS (SELECT 1 FROM GW_CODE WHERE GROUP_CD = 'HALF_TYPE' AND CODE_VAL = 'PM');

-- in-flight 보정: 이전 정책(승인 시 차감) 에서 아직 IN_PROGRESS 인 휴가는 잔여에 차감이
-- 안 된 상태인데, 신 정책은 승인 시 추가 차감을 안 한다. 그 차이를 보정해 잔여 정합을 맞춘다.
-- (연차 계열만; 시작 연도 기준으로 보정)
UPDATE GW_LEAVE_BALANCE b
   SET ANNUAL_USED = ANNUAL_USED + COALESCE((
        SELECT SUM(r.DAYS)
          FROM GW_LEAVE_REQ r
         WHERE r.USER_ID = b.USER_ID
           AND r.STATUS_CD = 'IN_PROGRESS'
           AND r.DELETED_AT IS NULL
           AND r.LEAVE_TYPE_CD IN ('ANNUAL','HALF','HOURLY')
           AND EXTRACT(YEAR FROM r.START_DT) = b.BAL_YEAR
   ), 0)
 WHERE EXISTS (
        SELECT 1 FROM GW_LEAVE_REQ r
         WHERE r.USER_ID = b.USER_ID
           AND r.STATUS_CD = 'IN_PROGRESS'
           AND r.DELETED_AT IS NULL
           AND r.LEAVE_TYPE_CD IN ('ANNUAL','HALF','HOURLY')
           AND EXTRACT(YEAR FROM r.START_DT) = b.BAL_YEAR
       );
