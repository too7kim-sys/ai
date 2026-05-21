-- =====================================================================
--  V6 : 재무·회계 샘플 데이터 (거래처 + 계약 + 인보이스 + 결제)
-- =====================================================================

-- 거래처 5건
INSERT INTO gw_vendor (vendor_type_cd, biz_no, company_nm, ceo_nm, biz_kind, biz_item,
                       contact_nm, contact_phone, contact_email,
                       bank_cd, bank_account, bank_holder, tax_type_cd, use_yn)
VALUES
  ('CUSTOMER', '123-45-67890', '(주)클라우드테크',  '김클라우드', '서비스업', 'SaaS 솔루션',
   '박매니저', '02-1234-5678', 'park@cloudtech.co.kr', '004', '110-123-456789', '(주)클라우드테크', 'GENERAL', 'Y'),
  ('CUSTOMER', '234-56-78901', '(주)디지털파트너즈', '이디지털', '서비스업', '광고 대행',
   '최담당', '02-2345-6789', 'choi@dp.co.kr',        '088', '110-234-567890', '(주)디지털파트너즈', 'GENERAL', 'Y'),
  ('VENDOR',   '345-67-89012', '오피스월드',         '정오피스', '도소매업', '사무용품',
   '한팀장', '02-3456-7890', 'han@officeworld.kr',   '011', '110-345-678901', '오피스월드', 'GENERAL', 'Y'),
  ('VENDOR',   '456-78-90123', '클라우드인프라(주)', '윤서버',   '서비스업', '서버 호스팅',
   '김기술', '02-4567-8901', 'kim@cloudinfra.kr',    '020', '110-456-789012', '클라우드인프라(주)', 'GENERAL', 'Y'),
  ('BOTH',     '567-89-01234', '에이전시플러스',     '강에이전시', '서비스업', '컨설팅',
   '서대표', '02-5678-9012', 'seo@agencyplus.kr',    '003', '110-567-890123', '에이전시플러스', 'GENERAL', 'Y');

-- 계약 4건
INSERT INTO gw_biz_contract
    (contract_no, vendor_id, contract_type_cd, title, start_dt, end_dt,
     auto_renew_yn, renew_notice_days,
     amount_net, vat_amount, amount_total, vat_included_yn,
     currency_cd, payment_terms_cd, payment_day_of_month,
     owner_user_id, dept_id, status_cd, memo, signed_at, created_by)
VALUES
  ('BIZ-20260101-00001', 1, 'SUBSCRIPTION', '클라우드테크 SaaS 라이선스 (연간)',
   '2026-01-01', '2026-12-31', 'Y', 30,
   12000000, 1200000, 13200000, 'N',
   'KRW', 'MONTHLY', 25,
   1, 4, 'ACTIVE', '월 110만 원 (VAT 별도) 구독', CURRENT_TIMESTAMP - 30, 1),
  ('BIZ-20260201-00002', 2, 'SERVICE', '디지털 파트너즈 광고 대행 (분기)',
   '2026-02-01', '2026-12-31', 'N', 30,
   30000000, 3000000, 33000000, 'N',
   'KRW', 'QUARTERLY', 1,
   1, 4, 'ACTIVE', '월 1천만 원 광고 집행 + 리포팅', CURRENT_TIMESTAMP - 14, 1),
  ('BIZ-20260101-00003', 4, 'MAINTENANCE', '서버 인프라 유지보수',
   '2026-01-01', '2026-06-30', 'N', 30,
   18000000, 1800000, 19800000, 'N',
   'KRW', 'MONTHLY', 5,
   1, 4, 'ACTIVE', '월 330만 원, 24x7 모니터링', CURRENT_TIMESTAMP - 60, 1),
  ('BIZ-20260301-00004', 3, 'GOODS', '사무용품 정기 구매',
   '2026-03-01', '2027-02-28', 'Y', 30,
   6000000, 600000, 6600000, 'N',
   'KRW', 'MONTHLY', 15,
   1, 4, 'DRAFT', '월 55만 원 정기 발주', NULL, 1);

-- 청구 일정 (계약 1번 - 월간, 1~5월) 일부 시드
INSERT INTO gw_billing_schedule
    (biz_contract_id, seq_no, due_dt, amount_net, vat_amount, amount_total, memo, status_cd)
VALUES
  (1, 1, '2026-01-25', 1000000, 100000, 1100000, '1회차 (월간)', 'PAID'),
  (1, 2, '2026-02-25', 1000000, 100000, 1100000, '2회차 (월간)', 'PAID'),
  (1, 3, '2026-03-25', 1000000, 100000, 1100000, '3회차 (월간)', 'PAID'),
  (1, 4, '2026-04-25', 1000000, 100000, 1100000, '4회차 (월간)', 'INVOICED'),
  (1, 5, '2026-05-25', 1000000, 100000, 1100000, '5회차 (월간)', 'PENDING'),
  (1, 6, '2026-06-25', 1000000, 100000, 1100000, '6회차 (월간)', 'PENDING');

-- 매출 인보이스 (계약 1번 4-5회 차)
INSERT INTO gw_invoice
    (invoice_no, direction_cd, vendor_id, biz_contract_id, schedule_id,
     issue_dt, due_dt, amount_net, vat_amount, amount_total,
     paid_amount, remaining_amount, tax_invoice_yn, status_cd,
     owner_user_id, memo, created_by)
VALUES
  ('INV-OUT-20260125-00001', 'OUT', 1, 1, 1, '2026-01-25', '2026-02-24',
   1000000, 100000, 1100000, 1100000, 0, 'Y', 'PAID', 1, '1월분', 1),
  ('INV-OUT-20260225-00002', 'OUT', 1, 1, 2, '2026-02-25', '2026-03-24',
   1000000, 100000, 1100000, 1100000, 0, 'Y', 'PAID', 1, '2월분', 1),
  ('INV-OUT-20260325-00003', 'OUT', 1, 1, 3, '2026-03-25', '2026-04-24',
   1000000, 100000, 1100000, 1100000, 0, 'Y', 'PAID', 1, '3월분', 1),
  ('INV-OUT-20260425-00004', 'OUT', 1, 1, 4, '2026-04-25', '2026-05-24',
   1000000, 100000, 1100000, 0, 1100000, 'Y', 'ISSUED', 1, '4월분', 1);

-- 매출 인보이스 (디지털파트너즈 1분기)
INSERT INTO gw_invoice
    (invoice_no, direction_cd, vendor_id, biz_contract_id,
     issue_dt, due_dt, amount_net, vat_amount, amount_total,
     paid_amount, remaining_amount, tax_invoice_yn, status_cd,
     owner_user_id, memo, created_by)
VALUES
  ('INV-OUT-20260301-00005', 'OUT', 2, 2, '2026-03-01', '2026-03-31',
   10000000, 1000000, 11000000, 5500000, 5500000, 'Y', 'PARTIALLY_PAID', 1, '3월분 (일부 입금)', 1);

-- 매입 인보이스 (서버 인프라)
INSERT INTO gw_invoice
    (invoice_no, direction_cd, vendor_id, biz_contract_id,
     issue_dt, due_dt, amount_net, vat_amount, amount_total,
     paid_amount, remaining_amount, tax_invoice_yn, status_cd,
     owner_user_id, memo, created_by)
VALUES
  ('INV-IN-20260205-00001', 'IN', 4, 3, '2026-02-05', '2026-03-05',
   3000000, 300000, 3300000, 3300000, 0, 'Y', 'PAID', 1, '2월 인프라 유지보수', 1),
  ('INV-IN-20260305-00002', 'IN', 4, 3, '2026-03-05', '2026-04-05',
   3000000, 300000, 3300000, 3300000, 0, 'Y', 'PAID', 1, '3월 인프라 유지보수', 1),
  ('INV-IN-20260405-00003', 'IN', 4, 3, '2026-04-05', '2026-05-05',
   3000000, 300000, 3300000, 0, 3300000, 'Y', 'ISSUED', 1, '4월 인프라 유지보수', 1),
  ('INV-IN-20260415-00004', 'IN', 3, NULL, '2026-04-15', '2026-04-30',
   500000, 50000, 550000, 0, 550000, 'N', 'ISSUED', 1, '사무용품 단발 구매', 1);

-- 결제 이력 (입금)
INSERT INTO gw_payment (invoice_id, pay_type_cd, pay_dt, amount, method_cd,
                        bank_cd, bank_account, counterpart_nm, memo, reg_user_id)
VALUES
  (1, 'INCOMING', '2026-02-10', 1100000, 'BANK_TRANSFER', '004', '110-123-456789', '(주)클라우드테크', '1월분 수금', 1),
  (2, 'INCOMING', '2026-03-08', 1100000, 'BANK_TRANSFER', '004', '110-123-456789', '(주)클라우드테크', '2월분 수금', 1),
  (3, 'INCOMING', '2026-04-12', 1100000, 'BANK_TRANSFER', '004', '110-123-456789', '(주)클라우드테크', '3월분 수금', 1),
  (5, 'INCOMING', '2026-03-25',  5500000, 'BANK_TRANSFER', '088', '110-234-567890', '(주)디지털파트너즈', '3월분 일부 수금', 1);

-- 결제 이력 (출금)
INSERT INTO gw_payment (invoice_id, pay_type_cd, pay_dt, amount, method_cd,
                        bank_cd, bank_account, counterpart_nm, memo, reg_user_id)
VALUES
  (6, 'OUTGOING', '2026-02-25', 3300000, 'BANK_TRANSFER', '020', '110-456-789012', '클라우드인프라(주)', '2월 인프라 지급', 1),
  (7, 'OUTGOING', '2026-03-25', 3300000, 'BANK_TRANSFER', '020', '110-456-789012', '클라우드인프라(주)', '3월 인프라 지급', 1);
