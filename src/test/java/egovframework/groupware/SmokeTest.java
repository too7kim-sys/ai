package egovframework.groupware;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 빌드 파이프라인 정상 동작 검증용 스모크 테스트.
 * 필수 라이브러리(Spring Security BCrypt 등)가 클래스패스에 있고
 * JUnit 5 + AssertJ 가 작동하는지를 확인한다.
 */
class SmokeTest {

    @Test
    void bcryptEncoderRoundTrip() {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        String hash = enc.encode("Demo!2025");
        assertThat(hash).startsWith("$2a$");
        assertThat(enc.matches("Demo!2025", hash)).isTrue();
        assertThat(enc.matches("Wrong!", hash)).isFalse();
    }

    @Test
    void domainVoConstruction() {
        // 핵심 VO 가 컴파일/생성 가능한지
        var noti = new egovframework.groupware.notification.service.NotificationVO();
        noti.setTitle("ping");
        noti.setTypeCd("SYSTEM");
        assertThat(noti.getTitle()).isEqualTo("ping");
        assertThat(noti.getTypeCd()).isEqualTo("SYSTEM");
    }
}
