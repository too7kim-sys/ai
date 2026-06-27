--
-- 소득세 간이세액표 (월 과세급여 기준 구간별 누진).
--
-- 기존엔 PayrollCalculator.simplifiedIncomeTax 안에 하드코딩되어 있어 매년 국세청
-- 개정 시 코드 수정이 필요했다. 이 테이블을 도입해 EFFECTIVE_FROM/TO 로 연도별
-- 요율을 누적 보관하고, 급여 계산 시 해당 시점에 유효한 구간만 조회해 적용한다.
--
-- 한 행 = 한 구간. DEPENDENT_DEDUCTION / CHILD_DEDUCTION 은 같은 연도의 모든 행에
-- 동일하게 들어간다(denormalized). 매년 새 세트를 INSERT 만 하면 자동으로 다음 해
-- 급여부터 새 표가 적용된다.
--
CREATE TABLE GW_INCOME_TAX_BRACKET (
    BRACKET_ID          BIGSERIAL PRIMARY KEY,
    EFFECTIVE_FROM      DATE NOT NULL,
    EFFECTIVE_TO        DATE,
    MIN_TAXABLE         NUMERIC(14,0) NOT NULL,
    -- 최상위 구간은 NULL = 상한 없음.
    MAX_TAXABLE         NUMERIC(14,0),
    -- 누진 시작점 (해당 구간 진입 시 기본 누적세액).
    BASE_TAX            NUMERIC(14,0) NOT NULL DEFAULT 0,
    -- 구간 내 한계세율.
    PROGRESSIVE_RATE    NUMERIC(7,5) NOT NULL,
    -- 부양가족(본인 포함) 1인당 정액 공제. 모든 구간에 동일 값.
    DEPENDENT_DEDUCTION NUMERIC(10,0) NOT NULL DEFAULT 0,
    -- 20세 이하 자녀 1인당 추가 정액 공제. 모든 구간에 동일 값.
    CHILD_DEDUCTION     NUMERIC(10,0) NOT NULL DEFAULT 0,
    NOTE                VARCHAR(255)
);
CREATE INDEX IX_INCOME_TAX_BRACKET_FROM
    ON GW_INCOME_TAX_BRACKET(EFFECTIVE_FROM DESC, MIN_TAXABLE);

-- 2025년 간이세액표 시드 — 기존 코드(PayrollCalculator.simplifiedIncomeTax) 와 동일한
-- 구간을 그대로 이관해, 마이그레이션 직후 계산 결과가 변하지 않도록 한다.
INSERT INTO GW_INCOME_TAX_BRACKET
  (EFFECTIVE_FROM, MIN_TAXABLE, MAX_TAXABLE, BASE_TAX, PROGRESSIVE_RATE,
   DEPENDENT_DEDUCTION, CHILD_DEDUCTION, NOTE) VALUES
 (DATE '2025-01-01',        0,  2000000,        0, 0.00000, 8000, 10000, '면세'),
 (DATE '2025-01-01',  2000000,  3000000,        0, 0.06000, 8000, 10000, '6%'),
 (DATE '2025-01-01',  3000000,  5000000,    60000, 0.15000, 8000, 10000, '15%'),
 (DATE '2025-01-01',  5000000,  8000000,   360000, 0.24000, 8000, 10000, '24%'),
 (DATE '2025-01-01',  8000000, 15000000,  1080000, 0.35000, 8000, 10000, '35%'),
 (DATE '2025-01-01', 15000000,     NULL,  3530000, 0.38000, 8000, 10000, '38%');
