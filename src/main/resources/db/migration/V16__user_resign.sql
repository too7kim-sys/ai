-- 퇴사일 컬럼 추가
-- 사용자가 퇴사한 시점을 기록한다. NULL 이면 재직 중.
-- 급여 생성(generateForMonth) 은 다음 정책으로 이 컬럼을 활용한다.
--   · resign_date < 급여월 첫날      → 이미 퇴사, 급여 생성 안 함
--   · 급여월 첫날 ≤ resign_date ≤ 마지막날
--                                     → 일할 계산 (재직일수 / 월일수)
--   · resign_date NULL 또는 > 마지막날 → 정상 1 개월 급여
ALTER TABLE GW_USER ADD COLUMN IF NOT EXISTS RESIGN_DATE   DATE;
ALTER TABLE GW_USER ADD COLUMN IF NOT EXISTS RESIGN_REASON VARCHAR(500);
