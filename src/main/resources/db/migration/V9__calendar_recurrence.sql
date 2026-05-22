-- =====================================================================
--  V9 : 캘린더 반복 일정
--  반복 일정은 생성 시점에 개별 행으로 펼쳐 저장하고, 같은 묶음을
--  repeat_group_id 로 식별한다 (반복 전체 삭제용).
-- =====================================================================

ALTER TABLE gw_cal_event ADD COLUMN repeat_group_id BIGINT;

CREATE INDEX ix_gw_cal_repeat ON gw_cal_event(repeat_group_id);
