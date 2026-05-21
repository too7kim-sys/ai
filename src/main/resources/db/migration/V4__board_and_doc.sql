-- =====================================================================
--  V4 : Board (게시판) + Document Library (자료실)
-- =====================================================================

-- ============== 게시판 마스터 ==============
CREATE TABLE gw_board (
    board_id     BIGSERIAL PRIMARY KEY,
    board_cd     VARCHAR(50) NOT NULL,
    board_nm     VARCHAR(100) NOT NULL,
    board_type   VARCHAR(20) NOT NULL DEFAULT 'NORMAL',  -- NORMAL | QNA | ANONYMOUS
    description  VARCHAR(500),
    use_yn       CHAR(1) NOT NULL DEFAULT 'Y',
    sort_no      INT NOT NULL DEFAULT 0,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX ux_gw_board_cd ON gw_board(board_cd);

-- ============== 게시글 ==============
CREATE TABLE gw_board_post (
    post_id          BIGSERIAL PRIMARY KEY,
    board_id         BIGINT NOT NULL,
    title            VARCHAR(200) NOT NULL,
    content          TEXT NOT NULL,
    author_id        BIGINT NOT NULL,
    pinned_yn        CHAR(1) NOT NULL DEFAULT 'N',
    anonymous_yn     CHAR(1) NOT NULL DEFAULT 'N',
    view_cnt         INT NOT NULL DEFAULT 0,
    answered_yn      CHAR(1) NOT NULL DEFAULT 'N',  -- QNA 답변완료 표시
    attach_group_id  BIGINT,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP,
    created_by       BIGINT,
    updated_by       BIGINT,
    deleted_at       TIMESTAMP
);
CREATE INDEX ix_gw_post_board ON gw_board_post(board_id, pinned_yn DESC, created_at DESC);
CREATE INDEX ix_gw_post_author ON gw_board_post(author_id);

-- ============== 댓글 ==============
CREATE TABLE gw_board_comment (
    cmt_id        BIGSERIAL PRIMARY KEY,
    post_id       BIGINT NOT NULL,
    parent_cmt_id BIGINT,
    author_id     BIGINT NOT NULL,
    content       VARCHAR(1000) NOT NULL,
    anonymous_yn  CHAR(1) NOT NULL DEFAULT 'N',
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP
);
CREATE INDEX ix_gw_post_cmt ON gw_board_comment(post_id, created_at);

-- ============== 자료실 폴더 ==============
CREATE TABLE gw_doc_folder (
    folder_id    BIGSERIAL PRIMARY KEY,
    parent_id    BIGINT,
    folder_nm    VARCHAR(200) NOT NULL,
    folder_path  VARCHAR(1000),
    /* PRIVATE: 본인만, DEPT: 부서원, COMPANY: 전사 */
    access_scope VARCHAR(20) NOT NULL DEFAULT 'COMPANY',
    dept_id      BIGINT,
    owner_id     BIGINT NOT NULL,
    description  VARCHAR(500),
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at   TIMESTAMP
);
CREATE INDEX ix_gw_doc_folder_parent ON gw_doc_folder(parent_id);

-- ============== 자료실 문서(파일) ==============
CREATE TABLE gw_doc_file (
    doc_id       BIGSERIAL PRIMARY KEY,
    folder_id    BIGINT NOT NULL,
    attach_id    BIGINT NOT NULL,
    title        VARCHAR(255) NOT NULL,
    description  VARCHAR(1000),
    owner_id     BIGINT NOT NULL,
    download_cnt INT NOT NULL DEFAULT 0,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at   TIMESTAMP
);
CREATE INDEX ix_gw_doc_file_folder ON gw_doc_file(folder_id, created_at DESC);

-- ============== 시드 데이터 : 게시판 ==============
INSERT INTO gw_board (board_cd, board_nm, board_type, description, sort_no) VALUES
  ('FREE',  '자유게시판',  'NORMAL',    '임직원 자유 의견 게시판',    1),
  ('QNA',   'Q&A',         'QNA',       '업무/시스템 관련 질문/답변', 2),
  ('VOICE', '익명 건의함', 'ANONYMOUS', '익명으로 회사에 의견 제출',  3);

-- ============== 시드 데이터 : 자료실 폴더 ==============
INSERT INTO gw_doc_folder (folder_nm, folder_path, access_scope, owner_id, description) VALUES
  ('전사 공유',  '/전사 공유',  'COMPANY', 1, '임직원 모두가 접근 가능한 폴더'),
  ('인사 자료',  '/인사 자료',  'COMPANY', 1, '인사팀이 공유하는 양식/규정'),
  ('회계 자료',  '/회계 자료',  'COMPANY', 1, '재무팀이 공유하는 양식/보고서'),
  ('IT 자료',    '/IT 자료',    'COMPANY', 1, 'IT팀 매뉴얼/가이드');
