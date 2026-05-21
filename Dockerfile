# =============================================================================
# Stage 1: build WAR
# =============================================================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# 의존성 캐시 (소스 변경 없으면 maven 캐시 재사용)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline || true

# 소스 복사 + 패키징
COPY src ./src
RUN mvn -B -DskipTests package

# =============================================================================
# Stage 2: Tomcat 9 runtime
# =============================================================================
FROM tomcat:9.0-jdk17-temurin
LABEL maintainer="groupware-team"

# Tomcat 기본 webapps 비우기
RUN rm -rf $CATALINA_HOME/webapps/*

# WAR 배포 (컨텍스트 / 로 매핑)
COPY --from=build /workspace/target/groupware.war $CATALINA_HOME/webapps/ROOT.war

# 업로드 디렉토리
RUN mkdir -p /var/lib/groupware/uploads \
    && chown -R 1000:1000 /var/lib/groupware
VOLUME ["/var/lib/groupware/uploads"]

# 환경 변수 기본값 (compose 또는 -e 로 override)
ENV SPRING_PROFILES_ACTIVE=prod \
    DB_URL=jdbc:postgresql://db:5432/groupware \
    DB_USER=groupware \
    DB_PWD=groupware \
    storage.local.path=/var/lib/groupware/uploads \
    CATALINA_OPTS="-Xms256m -Xmx1g -Duser.timezone=Asia/Seoul -Dfile.encoding=UTF-8"

EXPOSE 8080

# 헬스체크 - /health.do 200 OK
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -sf http://localhost:8080/health.do || exit 1

CMD ["catalina.sh", "run"]
