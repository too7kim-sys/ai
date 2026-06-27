-- =====================================================================
--  V7 : 자산 / 차량 관리 (Asset / Vehicle)
-- =====================================================================

-- ============== 자산 마스터 ==============
CREATE TABLE gw_asset (
    asset_id          BIGSERIAL PRIMARY KEY,
    asset_no          VARCHAR(50) NOT NULL,
    asset_nm          VARCHAR(150) NOT NULL,
    /* LAPTOP | DESKTOP | MONITOR | PHONE | PRINTER | HEADSET | FURNITURE | OTHER */
    category_cd       VARCHAR(30) NOT NULL,
    brand             VARCHAR(100),
    model_nm          VARCHAR(150),
    serial_no         VARCHAR(100),
    purchase_dt       DATE,
    purchase_amount   NUMERIC(14,0) NOT NULL DEFAULT 0,
    depreciation_months INT NOT NULL DEFAULT 36,
    /* IN_STOCK | IN_USE | REPAIRING | DISPOSED */
    status_cd         VARCHAR(20) NOT NULL DEFAULT 'IN_STOCK',
    assigned_user_id  BIGINT,
    assigned_dt       DATE,
    location          VARCHAR(200),
    memo              VARCHAR(1000),
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by        BIGINT,
    updated_at        TIMESTAMP,
    deleted_at        TIMESTAMP
);
CREATE UNIQUE INDEX ux_gw_asset_no ON gw_asset(asset_no);
CREATE INDEX ix_gw_asset_status ON gw_asset(status_cd);
CREATE INDEX ix_gw_asset_user ON gw_asset(assigned_user_id);

-- ============== 자산 이력 ==============
CREATE TABLE gw_asset_history (
    history_id   BIGSERIAL PRIMARY KEY,
    asset_id     BIGINT NOT NULL,
    /* ASSIGN | RETURN | REPAIR | DISPOSE | RELOCATE */
    action_cd    VARCHAR(20) NOT NULL,
    actor_id     BIGINT,
    target_user_id BIGINT,
    action_dt    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    before_status VARCHAR(20),
    after_status  VARCHAR(20),
    memo         VARCHAR(500)
);
CREATE INDEX ix_gw_asset_history_asset ON gw_asset_history(asset_id, action_dt DESC);

-- ============== 차량 마스터 ==============
CREATE TABLE gw_vehicle (
    vehicle_id        BIGSERIAL PRIMARY KEY,
    plate_no          VARCHAR(20) NOT NULL,
    model_nm          VARCHAR(100) NOT NULL,
    year_model        INT,
    purchase_dt       DATE,
    /* GASOLINE | DIESEL | LPG | HYBRID | EV */
    fuel_type_cd      VARCHAR(20) NOT NULL DEFAULT 'GASOLINE',
    fuel_efficiency   NUMERIC(5,2),
    seats             INT NOT NULL DEFAULT 5,
    owner_dept_id     BIGINT,
    current_mileage   INT NOT NULL DEFAULT 0,
    /* AVAILABLE | IN_USE | MAINTENANCE | RETIRED */
    status_cd         VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    next_maintenance_dt DATE,
    memo              VARCHAR(1000),
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at        TIMESTAMP
);
CREATE UNIQUE INDEX ux_gw_vehicle_plate ON gw_vehicle(plate_no);

-- ============== 차량 예약 ==============
CREATE TABLE gw_vehicle_reservation (
    reservation_id  BIGSERIAL PRIMARY KEY,
    vehicle_id      BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    start_dt        TIMESTAMP NOT NULL,
    end_dt          TIMESTAMP NOT NULL,
    purpose         VARCHAR(500),
    destination     VARCHAR(200),
    passenger_cnt   INT NOT NULL DEFAULT 1,
    mileage_start   INT,
    mileage_end     INT,
    /* PENDING | APPROVED | IN_USE | COMPLETED | CANCELED */
    status_cd       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP
);
CREATE INDEX ix_gw_vehicle_res_vehicle ON gw_vehicle_reservation(vehicle_id, start_dt);
CREATE INDEX ix_gw_vehicle_res_user ON gw_vehicle_reservation(user_id, start_dt DESC);

-- ============== 시드 ==============

-- 자산 12건
INSERT INTO gw_asset
  (asset_no, asset_nm, category_cd, brand, model_nm, serial_no,
   purchase_dt, purchase_amount, depreciation_months, status_cd,
   assigned_user_id, assigned_dt, location, memo, created_by)
VALUES
  ('ASSET-0001', 'MacBook Pro 16"',       'LAPTOP',  'Apple',   'MBP 16 M3 Pro',  'C02XX1A1J1JG', '2024-03-15', 4200000, 36, 'IN_USE',      4, '2024-03-20', '본사 5층 개발1팀', '김개발 사용', 1),
  ('ASSET-0002', 'MacBook Pro 16"',       'LAPTOP',  'Apple',   'MBP 16 M3 Pro',  'C02XX1A1J1JH', '2024-03-15', 4200000, 36, 'IN_USE',      5, '2024-03-20', '본사 5층 개발1팀', '이대리 사용', 1),
  ('ASSET-0003', 'ThinkPad X1 Carbon',    'LAPTOP',  'Lenovo',  'X1 Carbon Gen11','PF3X8C71',     '2023-08-01', 2800000, 36, 'IN_USE',      6, '2023-08-15', '본사 4층 개발1팀', '한개발 사용', 1),
  ('ASSET-0004', 'LG UltraFine 27"',      'MONITOR', 'LG',      '27UP850N',       '203NTAJAA293', '2024-02-10',  650000, 60, 'IN_USE',      4, '2024-03-20', '본사 5층 개발1팀', NULL,            1),
  ('ASSET-0005', 'LG UltraFine 27"',      'MONITOR', 'LG',      '27UP850N',       '203NTAJAA294', '2024-02-10',  650000, 60, 'IN_USE',      5, '2024-03-20', '본사 5층 개발1팀', NULL,            1),
  ('ASSET-0006', 'Dell U2723QE 27"',      'MONITOR', 'Dell',    'U2723QE',        'CN0YJWGD',     '2023-08-01',  720000, 60, 'IN_USE',      6, '2023-08-15', '본사 4층 개발1팀', NULL,            1),
  ('ASSET-0007', 'iPhone 15 Pro',         'PHONE',   'Apple',   'iPhone 15 Pro',  'F2L4K7Z3Q1M',  '2024-05-01', 1700000, 24, 'IN_USE',      1, '2024-05-10', '본사 5층 임원실', NULL,            1),
  ('ASSET-0008', 'iPhone 15',             'PHONE',   'Apple',   'iPhone 15',      'F2L4K7Z3Q1N',  '2024-05-01', 1200000, 24, 'IN_USE',      2, '2024-05-10', '본사 3층 인사팀', NULL,            1),
  ('ASSET-0009', 'HP LaserJet Pro',       'PRINTER', 'HP',      'M404dn',         'CNB1234567',   '2022-11-20',  450000, 60, 'IN_USE',      NULL, NULL, '본사 5층 공용 프린터실', '5층 공용', 1),
  ('ASSET-0010', 'Sony WH-1000XM5',       'HEADSET', 'Sony',    'WH-1000XM5',     '7340051234',   '2024-06-15',  450000, 36, 'IN_STOCK',    NULL, NULL, '본사 1층 IT 창고', '신규 입사자용 재고', 1),
  ('ASSET-0011', 'Sony WH-1000XM5',       'HEADSET', 'Sony',    'WH-1000XM5',     '7340051235',   '2024-06-15',  450000, 36, 'IN_STOCK',    NULL, NULL, '본사 1층 IT 창고', '신규 입사자용 재고', 1),
  ('ASSET-0012', 'MacBook Air 13"',       'LAPTOP',  'Apple',   'MBA 13 M2',      'C02XX1A1KLM5', '2023-02-01', 1900000, 36, 'REPAIRING',   NULL, NULL, '서비스센터 입고', '키보드 수리 의뢰', 1);

-- 자산 이력 시드
INSERT INTO gw_asset_history (asset_id, action_cd, actor_id, target_user_id, before_status, after_status, memo)
VALUES
  (1,  'ASSIGN', 1, 4, 'IN_STOCK', 'IN_USE', '신규 입사자 지급'),
  (2,  'ASSIGN', 1, 5, 'IN_STOCK', 'IN_USE', '신규 입사자 지급'),
  (3,  'ASSIGN', 1, 6, 'IN_STOCK', 'IN_USE', '교체 지급'),
  (7,  'ASSIGN', 1, 1, 'IN_STOCK', 'IN_USE', '대표 업무용'),
  (12, 'REPAIR', 1, NULL, 'IN_USE', 'REPAIRING', '키보드 일부 키 불량 - 서비스센터 입고');

-- 차량 4건
INSERT INTO gw_vehicle
  (plate_no, model_nm, year_model, purchase_dt, fuel_type_cd, fuel_efficiency,
   seats, owner_dept_id, current_mileage, status_cd, next_maintenance_dt, memo)
VALUES
  ('12가3456', '카니발 9인승', 2023, '2023-03-15', 'DIESEL',  10.50, 9, 4, 24500, 'AVAILABLE', '2026-08-15', '대표 행사용/단체 이동'),
  ('34나5678', '소나타',       2024, '2024-01-10', 'GASOLINE',13.20, 5, 4,  8200, 'AVAILABLE', '2026-07-10', '영업/외근 공용'),
  ('56다7890', '아이오닉 5',   2024, '2024-02-20', 'EV',      NULL,  5, 4,  6100, 'AVAILABLE', '2026-08-20', '전기차 (충전 사옥 지하 B1)'),
  ('78라9012', '봉고3 트럭',   2022, '2022-06-01', 'DIESEL',   9.80, 3, 4, 41200, 'MAINTENANCE', '2026-05-30', '엔진 점검중');

-- 차량 예약 3건 (오늘 기준 진행/예정)
INSERT INTO gw_vehicle_reservation
  (vehicle_id, user_id, start_dt, end_dt, purpose, destination, passenger_cnt,
   mileage_start, mileage_end, status_cd)
VALUES
  (2, 10, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('HOUR', 3, DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
   '고객 미팅 (강남)', '강남 클라우드테크 본사', 2, 8100, 8200, 'COMPLETED'),
  (3,  6, CURRENT_TIMESTAMP, DATEADD('HOUR', 6, CURRENT_TIMESTAMP),
   '판교 협력사 방문', '판교 IT 클러스터', 1, 6100, NULL, 'IN_USE'),
  (1,  4, DATEADD('DAY', 7, CURRENT_TIMESTAMP), DATEADD('HOUR', 8, DATEADD('DAY', 7, CURRENT_TIMESTAMP)),
   '전사 워크샵 이동', '양평 워크샵장', 9, NULL, NULL, 'APPROVED');
