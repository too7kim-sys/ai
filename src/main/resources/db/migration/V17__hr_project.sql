-- 개인 프로젝트 수행 경력 + KOSA(한국SW산업협회) 증빙 첨부
-- 직원 본인이 자기 프로젝트 이력을 직접 등재하고, KOSA 경력증명서 등
-- 증빙 자료를 첨부 그룹에 업로드한다. GW_ATTACH_GROUP / GW_ATTACH 를
-- attach_group_id 로 연결해 다른 모듈과 동일한 첨부 파이프라인을 사용.

CREATE TABLE GW_HR_PROJECT (
    PROJECT_ID         BIGSERIAL PRIMARY KEY,
    USER_ID            BIGINT NOT NULL,
    PROJECT_NM         VARCHAR(200) NOT NULL,
    CLIENT_NM          VARCHAR(200),         -- 발주처 / 고객사
    CONTRACTOR_NM      VARCHAR(200),         -- 수행사 (소속 회사 / 외주처)
    ROLE_NM            VARCHAR(100),         -- 역할 (PL / PM / 분석가 / 개발자 등)
    START_DT           DATE,
    END_DT             DATE,
    TECH_STACK         VARCHAR(1000),        -- 사용 기술 (콤마/세미콜론 구분)
    DESCRIPTION        VARCHAR(2000),        -- 담당 업무 상세
    KOSA_GRADE_CD      VARCHAR(20),          -- KOSA_GRADE 코드 (초/중/고/특급)
    KOSA_CONFIRMED_YN  CHAR(1) NOT NULL DEFAULT 'N',  -- KOSA 신고 완료 여부
    ATTACH_GROUP_ID    BIGINT,               -- 코사증빙 등 첨부
    CREATED_AT         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATED_BY         BIGINT,
    UPDATED_AT         TIMESTAMP,
    DELETED_AT         TIMESTAMP
);
CREATE INDEX IX_GW_HR_PROJECT_USER ON GW_HR_PROJECT(USER_ID, END_DT DESC);

ALTER TABLE GW_HR_PROJECT ADD CONSTRAINT FK_HR_PROJECT_USER
    FOREIGN KEY (USER_ID) REFERENCES GW_USER(USER_ID) ON DELETE CASCADE;

-- 코드 그룹
INSERT INTO GW_CODE_GROUP (GROUP_CD, GROUP_NM, DESCRIPTION) VALUES
 ('KOSA_GRADE','KOSA 기술자 등급','한국SW산업협회 SW 기술자 등급');

INSERT INTO GW_CODE (GROUP_CD, CODE_VAL, CODE_NM, SORT_NO) VALUES
 ('KOSA_GRADE','BEGINNER','초급',1),
 ('KOSA_GRADE','INTERMEDIATE','중급',2),
 ('KOSA_GRADE','ADVANCED','고급',3),
 ('KOSA_GRADE','SPECIAL','특급',4);
