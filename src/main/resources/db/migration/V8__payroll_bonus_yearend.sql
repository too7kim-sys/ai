-- =====================================================================
--  V8 : 급여 확장 — 상여 / 연말정산 / 퇴직정산
-- =====================================================================

-- ============== 상여 / 성과급 ==============
CREATE TABLE gw_payroll_bonus (
    bonus_id      BIGSERIAL PRIMARY KEY,
    pay_month     VARCHAR(7) NOT NULL,           -- 반영 급여월 (yyyy-MM)
    /* REGULAR 정기상여 | HOLIDAY 명절상여 | PERFORMANCE 성과급 | SPECIAL 특별상여 */
    bonus_type_cd VARCHAR(20) NOT NULL,
    user_id       BIGINT NOT NULL,
    amount        NUMERIC(14,0) NOT NULL DEFAULT 0,
    taxable_yn    CHAR(1) NOT NULL DEFAULT 'Y',
    memo          VARCHAR(300),
    /* PLANNED 예정 | APPLIED 급여반영완료 | CANCELED */
    status_cd     VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT
);
CREATE INDEX ix_gw_bonus_month ON gw_payroll_bonus(pay_month, status_cd);
CREATE INDEX ix_gw_bonus_user ON gw_payroll_bonus(user_id);

-- ============== 연말정산 ==============
CREATE TABLE gw_year_end_tax (
    yet_id            BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL,
    tax_year          INT NOT NULL,
    gross_pay         NUMERIC(14,0) NOT NULL DEFAULT 0,  -- 연간 총급여
    taxable_pay       NUMERIC(14,0) NOT NULL DEFAULT 0,  -- 연간 과세대상
    paid_tax          NUMERIC(14,0) NOT NULL DEFAULT 0,  -- 기납부 소득세 합계
    income_deduction  NUMERIC(14,0) NOT NULL DEFAULT 0,  -- 소득공제 합계
    tax_credit        NUMERIC(14,0) NOT NULL DEFAULT 0,  -- 세액공제 합계
    determined_tax    NUMERIC(14,0) NOT NULL DEFAULT 0,  -- 결정세액
    settled_tax       NUMERIC(14,0) NOT NULL DEFAULT 0,  -- (+)추징 / (-)환급
    /* DRAFT 작성중 | CONFIRMED 확정 */
    status_cd         VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    memo              VARCHAR(500),
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP
);
CREATE UNIQUE INDEX ux_gw_yet_user_year ON gw_year_end_tax(user_id, tax_year);

-- ============== 퇴직정산 ==============
CREATE TABLE gw_severance (
    sev_id          BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    hire_date       DATE,
    leave_date      DATE NOT NULL,
    service_days    INT NOT NULL DEFAULT 0,            -- 근속일수
    avg_monthly_wage NUMERIC(14,0) NOT NULL DEFAULT 0, -- 평균임금(최근 3개월 월평균)
    avg_daily_wage  NUMERIC(14,0) NOT NULL DEFAULT 0,  -- 1일 평균임금
    severance_pay   NUMERIC(14,0) NOT NULL DEFAULT 0,  -- 퇴직금 (세전)
    severance_tax   NUMERIC(14,0) NOT NULL DEFAULT 0,  -- 퇴직소득세 (간이)
    net_pay         NUMERIC(14,0) NOT NULL DEFAULT 0,  -- 실지급액
    /* DRAFT | CONFIRMED | PAID */
    status_cd       VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    paid_dt         DATE,
    memo            VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT
);
CREATE INDEX ix_gw_severance_user ON gw_severance(user_id);

-- 상여 시드 데이터는 DataInitializer 에서 생성한다 (Flyway 실행 시점엔 gw_user 가 비어 있음).
