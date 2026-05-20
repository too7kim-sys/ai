# 사내 그룹웨어 시스템 (eGovFrame · PostgreSQL)

전자정부 표준프레임워크 기반의 풀스택 사내 그룹웨어 시스템입니다.
인사·급여·근태·전자결재·근로계약·사업자 계약·대금 관리·지출결의 등 한국 기업 업무를 한 솔루션에서 처리합니다.

## 주요 기능

| 영역 | 모듈 |
|---|---|
| 협업 | 공지사항, 일정·캘린더, 쪽지, 인앱 알림, 회의실 예약 |
| 인사 | 직원 디렉토리, 조직도, 인사기록카드, 근로계약(서명·PDF), 평가·성과(KPI) |
| 근태/급여 | 출퇴근, 월 급여 산정(한국 4대보험·소득세·연봉계약), PDF 명세서, **명세서 메일 발송** |
| 결재 | 전자결재(8종 양식, 자유 결재선, 위임), 휴가, 지출결의, 계약·지급 품의 |
| 회계/B2B | 거래처 마스터, 사업자 계약, 청구 스케줄, 인보이스, 세금계산서, 입출금, AR/AP 대시보드, 현금흐름 |
| 지출결의 | 라인 다중 입력, 영수증 첨부, 결재→자동 환급(은행 이체 파일), 부서·계정과목 예산 통제 |
| 시스템 관리 | 사용자/메뉴·권한 매트릭스/공통코드/감사 로그/로그인 이력/메일 템플릿·이력·SMTP |

## 기술 스택

- **Backend**: Java 17, 전자정부 표준프레임워크 4.2, Spring 5.3, Spring Security 5.8, MyBatis 3.5, HikariCP
- **DB**: PostgreSQL 16 (prod), H2 in-memory (dev/test, `MODE=PostgreSQL`)
- **Migration**: Flyway 9
- **View**: JSP + JSTL + SiteMesh 3, Bootstrap 5, FullCalendar, Chart.js, jQuery
- **Mail**: Spring Mail (JavaMailSender) + Jakarta Mail, 비동기 큐
- **Build/Deploy**: Maven, WAR, Docker, GitHub Actions
- **Test**: JUnit 5, Spring Test, MockMvc, Testcontainers PostgreSQL, GreenMail

## 빠른 시작

```bash
# 의존성 다운로드 + 컴파일
mvn -DskipTests package

# 로컬 실행 (H2, dev 프로파일)
mvn tomcat7:run

# 접속
open http://localhost:8080/groupware/login.do
```

### 시드 계정 (개발/데모)
모든 비밀번호: `Demo!2025`

| 이메일 | 역할 |
|---|---|
| `admin@company.com` | ADMIN |
| `hr@company.com` | HR_MANAGER |
| `finance@company.com` | FINANCE_MANAGER |
| `manager.dev1@company.com` | MANAGER (개발1팀장) |
| `emp.dev1.kim@company.com` | EMPLOYEE |

## 운영 배포

`docker-compose up` 또는 WAR를 외부 Tomcat에 배포. 환경변수로 시크릿 분리:

```bash
DB_URL=jdbc:postgresql://db:5432/groupware
DB_USER=groupware
DB_PWD=*****
MAIL_HOST=smtp.company.com
MAIL_PORT=587
MAIL_USERNAME=noreply@company.com
MAIL_PASSWORD=*****
MAIL_FROM=noreply@company.com
```

## 문서

- `docs/architecture.md` — 아키텍처 개요
- `docs/security.md` — 보안 정책 (RBAC, 비밀번호, 감사 로그)
- `docs/operation.md` — 운영 가이드 (로그·헬스체크·백업)
- `docs/demo-scenario.md` — 영업 데모 시나리오

## 라이선스

내부 사용 / 추후 결정.
