package egovframework.groupware.cmm.web;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * byte[] 첨부 다운로드 공용 헬퍼.
 *
 * <p>Spring 의 {@code ResponseEntity<byte[]>} 응답 경로는
 * {@code HttpServletResponse#setContentLengthLong(long)} (Servlet 3.1+) 를 호출하는데,
 * Tomcat 7(Servlet 3.0) 에는 이 메서드가 없어 {@code NoSuchMethodError} 가 난다.
 * 따라서 응답을 직접 작성한다.
 */
public final class DownloadSupport {

    private DownloadSupport() {}

    public static void write(HttpServletResponse resp, String filename,
                             String contentType, byte[] body) throws IOException {
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(contentType);
        resp.setContentLength(body.length);
        resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
        resp.getOutputStream().write(body);
        resp.getOutputStream().flush();
    }
}
