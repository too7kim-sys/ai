# 운영 가이드

## 실행

### 개발 (H2)
```bash
mvn tomcat7:run
```
브라우저: <http://localhost:8080/groupware/login.do>

H2 콘솔이 필요하면 `application-dev.properties` 에 활성화.

### 프로덕션 (PostgreSQL + Tomcat 9)

#### Docker Compose
```bash
docker compose up -d
docker compose logs -f app
```

#### 수동 배포
```bash
mvn -DskipTests package
# target/groupware.war 생성

# Tomcat 9+ 의 webapps 에 배치
cp target/groupware.war $CATALINA_HOME/webapps/

# 환경 변수 설정 ($CATALINA_BASE/bin/setenv.sh)
export JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=prod"
export DB_URL=jdbc:postgresql://db:5432/groupware
export DB_USER=groupware
export DB_PWD=...
export MAIL_HOST=smtp.company.com
export MAIL_PORT=587
export MAIL_USERNAME=noreply@company.com
export MAIL_PASSWORD=...
export MAIL_FROM=noreply@company.com
export storage.local.path=/var/lib/groupware/uploads
```

## 헬스체크

| URL | 응답 |
|---|---|
| `GET /health.do` | `{"status":"UP","db":"UP","time":"..."}` |
| `GET /sys/info.do` (ADMIN) | Java/DB/JVM 메모리/테이블 카운트 |

부하 분산기/모니터링에서 `/health.do` 만 사용. 인증 불필요.

## 데이터베이스

### 마이그레이션 적용
Flyway 가 애플리케이션 부팅 시 자동으로 `db/migration/V*.sql` 을 순서대로 실행.

신규 마이그레이션 추가는 다음 번호로 (`V8__*.sql`).

### 백업
```bash
# 전체 덤프
pg_dump -h db -U groupware -F c groupware > backup-$(date +%Y%m%d).dump

# 복구
pg_restore -h db -U groupware -d groupware --clean backup-20260521.dump
```

업로드 파일은 `storage.local.path` 경로(예: `/var/lib/groupware/uploads/`)를 동시에 백업.

### 시드 데이터 재생성

H2(`jdbc:h2:mem:groupware`)는 매 부팅마다 fresh. `DataInitializer`(`bootstrap` 패키지)가 admin 미존재 시 20명 + 시드를 자동 생성.

PostgreSQL은 schema 가 비어있어야 함. 시드를 다시 만들려면 DB drop 후 Flyway 재실행.

## 로그·감사

- **애플리케이션 로그**: SLF4J + Logback (`logback-spring.xml` 추가 가능). 기본은 콘솔.
- **로그인 이력**: `/sys/login-log.do` (ADMIN). 모든 로그인 성공/실패 자동 기록.
- **감사 로그**: `/sys/audit-log.do`. 사용자 잠금 해제, 비번 초기화, 역할 변경, 코드 변경, 메뉴 변경 등 자동 기록.

## 메일 발송

`MAIL_HOST` 미설정 시 시스템은 메일 큐만 쌓고 발송 실패로 기록. SMTP 사용 시 `application-prod.properties` 또는 환경변수로 호스트/계정 지정.

발송 큐 상태는 `/mail/log.do` (HR_MANAGER+) 에서 SENT/QUEUED/FAILED 확인.

## 파일 저장

기본은 로컬 디스크 `storage.local.path`. 디렉토리 구조는 `yyyy/MM/dd/UUID.ext`. 컨테이너 사용 시 볼륨 마운트:

```yaml
volumes:
  - groupware_uploads:/var/lib/groupware/uploads
```

## 권한 (RBAC)

5단계 역할. `/sys/menu.do` (ADMIN) 에서 메뉴별 권한 매트릭스 편집 가능.

| Role | 대표 권한 |
|---|---|
| `ADMIN` | 전 시스템 |
| `HR_MANAGER` | 인사·근태·평가·자산·메일 |
| `FINANCE_MANAGER` | 회계·결제·인보이스·계약 |
| `MANAGER` | 본인 부서 KPI/근태 리포트 + 사업자 계약 조회 |
| `EMPLOYEE` | 본인 데이터·협업 도구 |

비밀번호 정책: 90일 만료 + 5회 실패 시 잠금 (값은 코드에 하드코딩, 추후 properties 화 가능).

## 자주 발생하는 문제

| 증상 | 원인 | 해결 |
|---|---|---|
| 페이지가 navbar 없이 본문만 표시됨 | `WEB-INF/sitemesh3.xml` 누락 | 해당 파일 생성 |
| `/css/app.css` 가 500 (Tomcat 7) | `setContentLengthLong` 미지원 | `mvc:resources` 대신 `mvc:default-servlet-handler` 사용 |
| 한글 GET 쿼리스트링 검색 안됨 | Tomcat URIEncoding 기본 ISO-8859-1 | `tomcat7-maven-plugin uriEncoding=UTF-8` |
| 폼에서 LocalDate 변환 실패 | XML 명시 ConversionService 가 JSR-310 미등록 | `mvc:annotation-driven` 의 `conversion-service` 속성 제거 |
| 로그인 후 NPE `this.user is null` | XML 설정에 `AuthenticationPrincipalArgumentResolver` 미등록 | `mvc:argument-resolvers` 에 추가 |
| 정적 파일 다운로드 시 500 (Tomcat 7) | Spring `ResourceHttpMessageConverter` | `HttpServletResponse` 로 직접 스트림 |

이 모든 픽스는 본 저장소에 이미 적용되어 있음.

## 모니터링 권장

- `/health.do` 30초 폴링
- `/sys/info.do` 일일 점검 (DB 카운트 증가 확인)
- `/sys/login-log.do?successYn=N` 주간 점검 (이상 로그인)
- `/mail/log.do?status=FAILED` 일일 점검

## 보안 체크리스트

- [ ] 시드 계정의 `Demo!2025` 비밀번호를 운영 시 모두 변경
- [ ] `MAIL_PASSWORD`, `DB_PWD` 등 시크릿을 환경 변수로 분리
- [ ] HTTPS 종단 적용 (리버스 프록시)
- [ ] 업로드 디렉토리 권한 600
- [ ] `/sys/*` 접근은 ADMIN 만 허용 (코드에 강제)
- [ ] 정기 백업 (DB + 업로드)
