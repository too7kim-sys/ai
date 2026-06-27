# 사내 그룹웨어 시스템

전자정부 표준프레임워크(eGovFrame) 기반의 통합 사내 그룹웨어. 인사·급여·근태·결재·협업·재무·자산을 한 시스템에서 처리합니다.

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring](https://img.shields.io/badge/Spring-5.3-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![License](https://img.shields.io/badge/License-Internal-lightgrey)

---

## 1. 핵심 기능

28개 도메인 모듈 / 54개 테이블 / 87개 화면.

### 협업
- **공지사항** — 고정/검색/페이징, 댓글, 작성 시 전사 인앱 알림
- **일정·캘린더** — FullCalendar 통합, 개인/부서/전사 스코프
- **쪽지** — 받은·보낸함, 답장, 수신자 자동 알림
- **인앱 알림** — 도메인별 typeCd, 상단 navbar 벨 뱃지, 읽음 처리
- **회의실 예약** — 시간 겹침 자동 검증
- **게시판** — 자유 / Q&A(자동 답변완료) / 익명 건의함
- **자료실** — 폴더 트리(PRIVATE/DEPT/COMPANY 3단계 권한) + 파일 업로드

### 인사
- **직원 디렉토리·조직도** — 부서/직급 트리, 프로필 카드
- **인사기록·인사발령** — 입사·승진·이동·퇴사 이력
- **부양가족** — 가족관계, 연말정산용
- **근로계약** — 서명/PDF 다운로드, 임금·근무지 변경 이력
- **평가** — 분기 평가기간, 동료/매니저 평가, 결과 집계
- **성과·KPI** — 개인/팀 KPI, 목표 대비 달성률

### 근태·휴가·급여
- **근태** — 1-클릭 출퇴근, 지각·조기퇴근·야간(22-06) 자동 산정, 월간 리포트
- **휴가** — 연차 잔여 관리, 신청·승인, 5종(연차/반차/병가/경조사/기타)
- **급여** — 한국 4대보험·소득세 자동 계산, PDF 명세서, **명세서 메일 발송**

### 전자결재
- **8종 양식** — 휴가/지출/계약/지급/일반 등
- **자유 결재선** — 위임, 회수, 반려 후 재상신
- **휴가·지출과 자동 연결** — 결재 완료 시 휴가 차감/지출 환급 트리거

### 재무·회계
- **거래처 마스터** — 사업자번호/세무 유형/은행 계좌
- **사업자 계약** — 청구 스케줄 자동 생성(월/분기/연간), 만료 알림
- **인보이스** — 매출(발행)/매입(수령), 세금계산서, 잔액 자동 갱신
- **입출금** — 인보이스 잔액 자동 차감/복구
- **재무 대시보드** — AR/AP 미수금·미지급, 거래처 TOP 5, 월별 매출·매입

### 지출결의
- **라인 다중 입력**, 영수증 첨부, 결재 완료 시 **자동 환급** (지급 거래 자동 생성)
- 부서·계정과목 예산 통제

### 자산·차량
- **자산** — 분류 8종(노트북/모니터/휴대폰 등), 지급·회수·수리·폐기 라이프사이클 + 변경 이력
- **차량** — 예약 → 출발 → 도착(누적 km 갱신), 시간 겹침 차단

### 시스템 관리 (ADMIN 전용)
- **사용자** — 잠금 해제, 비밀번호 초기화, 역할 변경, 사용여부 토글
- **로그인 이력 / 감사 로그** — 액션·엔티티·기간 필터
- **공통 코드** — 코드 그룹/코드 CRUD
- **메뉴·권한 매트릭스** — 메뉴별 ROLE 매핑
- **메일** — 발송 이력·템플릿 편집·테스트 발송
- **시스템 정보** — Java/DB/메모리/테이블 통계

---

## 2. 기술 스택

| 영역 | 기술 |
|---|---|
| Backend | Java 17, 전자정부 표준프레임워크 4.2, Spring 5.3, Spring Security 5.8, MyBatis 3.5, HikariCP |
| DB | PostgreSQL 16 (prod), H2 in-memory `MODE=PostgreSQL` (dev) |
| Migration | Flyway 9 |
| View | JSP + JSTL + SiteMesh 3, Bootstrap 5.3, Bootstrap Icons, FullCalendar 6 |
| Mail | Spring Mail + Jakarta Mail, 비동기 큐 (`MAIL_*` env) |
| Build | Maven, WAR |
| 보안 | RBAC (ADMIN/HR_MANAGER/FINANCE_MANAGER/MANAGER/EMPLOYEE), CSRF, 세션 고정 보호, 비밀번호 BCrypt |

---

## 3. 빠른 시작 (개발)

```bash
# 컴파일
mvn -DskipTests compile

# 로컬 실행 (H2, dev 프로파일)
mvn tomcat7:run

# 접속
open http://localhost:8080/groupware/login.do
```

### 시드 계정

모든 비밀번호: **`Demo!2025`**

| 이메일 | 역할 | 비고 |
|---|---|---|
| `admin@company.com` | ADMIN | 시스템 관리 + 전 권한 |
| `hr@company.com` | HR_MANAGER | 인사·평가·근태 관리 |
| `finance@company.com` | FINANCE_MANAGER | 재무·회계 |
| `manager.dev1@company.com` | MANAGER | 개발1팀장 (팀 KPI 등) |
| `emp.dev1.kim@company.com` | EMPLOYEE | 일반 직원 |

총 20명 시드 (admin 1 + HR 1 + FIN 1 + 매니저 5 + 직원 12).

### 시드 데이터 한 줄 요약

공지 5건 · 일정 4건 · 게시판 글 6건 · 자료실 폴더 4건+파일 4건 · 거래처 5건 · 계약 4건 · 인보이스 9건 · 결제 6건 · 자산 12건 · 차량 4대.

---

## 4. 운영 배포

### Docker (권장)

```bash
docker compose up -d
```

`docker-compose.yml` 은 PostgreSQL 16 + Tomcat 9 (groupware.war) 두 서비스.

### 환경 변수 (`application.properties` 가 읽음)

```properties
spring.profiles.active=prod
DB_URL=jdbc:postgresql://db:5432/groupware
DB_USER=groupware
DB_PWD=*****
MAIL_HOST=smtp.company.com
MAIL_PORT=587
MAIL_USERNAME=noreply@company.com
MAIL_PASSWORD=*****
MAIL_FROM=noreply@company.com
storage.local.path=/var/lib/groupware/uploads
```

### 외부 Tomcat에 WAR 배포

```bash
mvn -DskipTests package
cp target/groupware.war $CATALINA_HOME/webapps/
```

대상 Tomcat은 **9 이상** 권장 (Tomcat 7 호환 코드도 포함되어 있지만 정적 리소스/세션 픽서 일부는 Servlet 3.1+ 메서드 사용).

---

## 5. 아키텍처

```
[Browser]
   │ HTTPS + Bootstrap UI
   ▼
[Tomcat 9 + Spring 5.3 MVC]
   │ Spring Security RBAC · CSRF · SiteMesh 데코레이터
   ├─ Web Controllers  (87 JSP 뷰)
   ├─ Service          (트랜잭션 경계)
   └─ MyBatis Mapper   (29 XML)
   ▼
[PostgreSQL 16]      [Local FS]      [SMTP]
  54 tables           uploads/        비동기 큐
  Flyway 7 마이그레이션
```

### 보안 모델 (RBAC)

| Role | 권한 |
|---|---|
| `ADMIN` | 시스템 관리 + 전체 |
| `HR_MANAGER` | 인사·근태·평가·자산 관리 |
| `FINANCE_MANAGER` | 회계·결제·인보이스 |
| `MANAGER` | 본인 부서 KPI/근태 리포트 + 사업자 계약 조회 |
| `EMPLOYEE` | 본인 데이터·협업 도구 |

### 데이터 모델 하이라이트

- **소프트 삭제** (`deleted_at`) — 게시글·인보이스·계약 등 비즈니스 엔티티
- **감사 로그** (`gw_audit_log`) — 사용자 액션 자동 기록
- **첨부파일** (`gw_attach_group` + `gw_attach`) — 게시판·자료실 공통 인프라

---

## 6. 디렉토리 구조

```
src/main/java/egovframework/groupware/
├── approval/        결재
├── asset/           자산
├── attach/          파일 저장 공통
├── attendance/      근태
├── auth/            인증·로그인 이력
├── bizcontract/     사업자 계약
├── board/           게시판
├── calendar/        일정
├── cmm/             공통 (BaseVO, GlobalExceptionHandler, Paging)
├── contract/        근로계약
├── doc/             자료실
├── evaluation/      평가
├── expense/         지출결의
├── finance/         재무 대시보드
├── hr/              인사기록·발령·조직도
├── invoice/         인보이스
├── leave/           휴가
├── mail/            메일
├── message/         쪽지
├── notice/          공지사항
├── notification/    인앱 알림
├── payment/         입출금
├── payroll/         급여
├── performance/     KPI
├── room/            회의실
├── sys/             시스템 관리 (사용자/코드/메뉴/감사)
├── user/            사용자
├── vehicle/         차량
└── vendor/          거래처

src/main/resources/
├── application.properties
├── application-dev.properties     (H2)
├── application-prod.properties    (PostgreSQL)
├── db/migration/V1..V7__*.sql     (Flyway)
└── egovframework/
    ├── spring/                    (Spring XML 설정)
    └── mapper/groupware/<도메인>/  (MyBatis SQL XML)

src/main/webapp/
├── WEB-INF/
│   ├── jsp/<도메인>/*.jsp          (87 화면)
│   ├── jsp/layout/decorator.jsp   (SiteMesh 데코레이터)
│   ├── sitemesh3.xml
│   └── web.xml
├── css/app.css
└── js/app.js
```

---

## 7. 통계

| 항목 | 수 |
|---|---:|
| Java 클래스 | 200 |
| MyBatis Mapper XML | 29 |
| JSP 화면 | 87 |
| DB 테이블 | 54 |
| Flyway 마이그레이션 | 7 |
| 총 LoC | ~22,500 |

---

## 8. 문서

- [`docs/operation.md`](docs/operation.md) — 운영 가이드 (실행·헬스체크·백업·로그)
- [`docs/security.md`](docs/security.md) — 보안 정책

(`docs/` 폴더는 다음 라운드에 추가)

---

## 9. 라이선스

내부 사용 / 추후 결정.
