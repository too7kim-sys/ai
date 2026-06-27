# 보안 정책

## 1. 인증

- Spring Security 5.8 폼 로그인 (`/auth/login`)
- 이메일 + 비밀번호
- 비밀번호 해시: **BCrypt** (`BCryptPasswordEncoder`)
- CSRF 토큰 자동 검증 (모든 POST/PUT/DELETE)
- 세션 고정 보호: `newSession` (Tomcat 7 호환)
- 동시 세션: 사용자 당 1세션 (`<concurrency-control max-sessions="1"/>`)

## 2. 비밀번호 정책

| 항목 | 값 |
|---|---:|
| 만료 | 90일 |
| 실패 잠금 | 5회 |
| 최소 길이 | (정책 권장 8자 — 검증 코드는 추가 권장) |
| 시드 비밀번호 | `Demo!2025` (운영 전 반드시 변경) |

잠금된 계정은 ADMIN 이 `/sys/user/list.do` 에서 잠금 해제.

## 3. 권한 (RBAC)

5단계 역할. URL 기준 `<intercept-url>` + 메서드 기준 `@PreAuthorize` 이중 보호.

| Role | 대표 권한 |
|---|---|
| `ADMIN` | 모든 시스템 권한 |
| `HR_MANAGER` | 인사기록, 인사발령, 평가관리, 자산 관리, 근태 리포트, 메일 |
| `FINANCE_MANAGER` | 거래처, 인보이스, 입출금, 재무 대시보드 |
| `MANAGER` | 본인 부서 KPI·근태 리포트, 사업자 계약 조회 |
| `EMPLOYEE` | 본인 데이터, 협업 도구 |

`/sys/menu.do` 에서 메뉴별 권한 매트릭스 편집 가능.

### 핵심 URL 권한

```
/login.do                     → permitAll
/health.do                    → permitAll
/sys/**                       → ADMIN
/hr/admin/**, /hr/record.do   → ADMIN, HR_MANAGER
/payroll/admin/**             → ADMIN, HR_MANAGER
/finance/**, /vendor/**       → ADMIN, FINANCE_MANAGER
/biz-contract/**              → ADMIN, FINANCE_MANAGER, MANAGER
/invoice/**, /payment/**      → ADMIN, FINANCE_MANAGER
/mail/**                      → ADMIN, HR_MANAGER
/attendance/admin/**          → ADMIN, HR_MANAGER, MANAGER
/notice/write.do              → ADMIN, HR_MANAGER, MANAGER
나머지 /**                    → 인증된 사용자
```

## 4. CSRF

Spring Security 가 모든 상태 변경 요청에 CSRF 토큰을 강제.

JSP 폼에는 `<sec:csrfInput/>` 또는 hidden field `_csrf` 자동 포함:

```jsp
<form method="post" action="...">
    <sec:csrfInput/>
    ...
</form>
```

## 5. SQL Injection

MyBatis 의 `#{param}` 바인딩만 사용 — 자동 PreparedStatement 처리.

`${param}` 은 검색 정렬 등 enum-like 안전 입력에만 제한적으로 사용.

## 6. XSS

- JSP EL `${...}` 은 기본 escape 안 함 — 모든 출력에 escape 권장 (`<c:out>` 또는 JSTL).
- 게시판/공지 본문은 사용자 입력 그대로 렌더링되므로 운영에서는 HTML 새니타이저 (예: OWASP HTML Sanitizer) 적용 권장.

현재 코드는 `style="white-space: pre-wrap;"` 으로 본문을 텍스트 형태로 보존하는 패턴이라 기본 보호는 됨.

## 7. 파일 업로드

- 멀티파트 제한: 최대 50MB / 요청 총합 100MB (`web.xml multipart-config`)
- 저장 경로: `storage.local.path` (외부 디렉토리)
- 저장 파일명: `UUID.확장자` — 원본 파일명은 DB 에 기록, 디스크는 안전한 이름
- 다운로드 시 `Content-Disposition: attachment; filename*=UTF-8''<encoded>` 강제 — XSS 방지

## 8. 세션 / 쿠키

- `JSESSIONID` HttpOnly
- 세션 timeout: Tomcat 기본 (web.xml 에서 조정)
- 로그인 시 세션 갱신 (`newSession`)

운영 권장: HTTPS 종단 + `Secure` 쿠키 (`web.xml session-config / cookie-config / secure`).

## 9. 감사 로그

`/sys/audit-log.do` 에서 조회 가능. 자동 기록되는 액션:

- `USER_UNLOCK`, `USER_RESET_PWD`, `USER_TOGGLE`, `USER_ROLE`
- `CODE_GROUP_SAVE`, `CODE_GROUP_DELETE`, `CODE_SAVE`, `CODE_DELETE`
- `MENU_SAVE`, `MENU_DELETE`

추가 액션은 `AuditLogService.log(...)` 호출로 확장 가능.

## 10. 로그인 이력

모든 로그인 시도(성공/실패) 가 `gw_login_log` 에 기록되며 `/sys/login-log.do` 에서 검색 가능.

## 11. 운영 권장 사항

- [ ] 시드 비밀번호 `Demo!2025` 전수 변경
- [ ] HTTPS + HSTS
- [ ] SMTP/DB 시크릿을 환경 변수 또는 vault 에서 주입
- [ ] 업로드 디렉토리 권한 `0700`
- [ ] DB 사용자 권한 최소화 (DDL 제외)
- [ ] `application-prod.properties` 의 H2 콘솔 비활성화
- [ ] 정기 보안 패치 (Spring/PostgreSQL/Tomcat)
- [ ] 감사 로그·로그인 이력 정기 점검
