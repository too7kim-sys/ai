package egovframework.groupware.contract.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import egovframework.groupware.contract.service.EmploymentContractVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ContractPdfWriter {

    private static final Logger log = LoggerFactory.getLogger(ContractPdfWriter.class);
    /** OpenPDF HTMLWorker 가 인식하는 폰트 alias. body 의 face 속성과 일치해야 한다. */
    private static final String FONT_ALIAS = "NanumGothic";

    /** BaseFont 직접 생성용 (Paragraph title/footer 등에 사용). */
    private byte[] koreanFontBytes;
    /** HTMLWorker 가 사용할 등록된 폰트의 파일 경로 (FontFactory.register 결과). */
    private String registeredFontFile;

    /**
     * Bean 생성 시 PDF 한글 폰트 등록.
     * <ol>
     *   <li>클래스패스 {@code /fonts/NanumGothic.ttf} 가 있으면 임시 파일로 풀어 FontFactory 에 등록</li>
     *   <li>없으면 시스템에 설치된 흔한 한글 폰트 경로 시도 (Linux nanum / macOS AppleGothic / Windows Malgun)</li>
     *   <li>모두 실패하면 폴백으로 Helvetica 가 사용되어 한글이 깨진다.</li>
     * </ol>
     */
    public ContractPdfWriter() {
        registerKoreanFont();
    }

    private void registerKoreanFont() {
        try (InputStream in = getClass().getResourceAsStream("/fonts/NanumGothic.ttf")) {
            if (in != null) {
                koreanFontBytes = in.readAllBytes();
                Path tmp = Files.createTempFile("nanumgothic", ".ttf");
                Files.write(tmp, koreanFontBytes);
                tmp.toFile().deleteOnExit();
                FontFactory.register(tmp.toString(), FONT_ALIAS);
                registeredFontFile = tmp.toString();
                log.info("Registered bundled Korean font for PDF: {}", tmp);
                return;
            }
        } catch (Exception ex) {
            log.warn("Bundled Korean font failed to load", ex);
        }
        for (String p : new String[]{
                "/usr/share/fonts/truetype/nanum/NanumGothic.ttf",
                "/usr/share/fonts/nanum/NanumGothic.ttf",
                "/Library/Fonts/AppleGothic.ttf",
                "C:/Windows/Fonts/malgun.ttf"}) {
            if (Files.exists(Paths.get(p))) {
                try {
                    koreanFontBytes = Files.readAllBytes(Paths.get(p));
                    FontFactory.register(p, FONT_ALIAS);
                    registeredFontFile = p;
                    log.info("Registered system Korean font for PDF: {}", p);
                    return;
                } catch (Exception ignored) { /* try next */ }
            }
        }
        log.warn("No Korean font available — PDF 한글 출력이 깨질 수 있습니다.");
    }

    public byte[] write(EmploymentContractVO c, String renderedHtml) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 본문에 큰 표가 들어가므로 좌우/하단 마진을 넉넉히 잡아 자르기를 방지.
        Document doc = new Document(PageSize.A4, 48, 48, 56, 56);
        PdfWriter.getInstance(doc, baos);

        Font smallFont;
        try {
            BaseFont bf = loadKoreanBaseFont();
            smallFont = new Font(bf, 9);
        } catch (Exception ex) {
            smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        }

        doc.open();
        // 타이틀·갑/을 서명란은 V15 템플릿 본문이 직접 그리므로 PdfWriter 가 별도로
        // 그리지 않는다(중복·겹침 방지). renderedHtml 이 비어 있는 경우에만 폴백 출력.
        try {
            StyleSheet css = new StyleSheet();
            css.loadTagStyle("body", "face", FONT_ALIAS);
            css.loadTagStyle("body", "encoding", BaseFont.IDENTITY_H);
            HTMLWorker w = new HTMLWorker(doc);
            w.setStyleSheet(css);
            w.parse(new StringReader(renderedHtml == null ? "" : renderedHtml));
        } catch (Exception ex) {
            doc.add(new Paragraph(renderedHtml == null ? "" : renderedHtml, smallFont));
        }
        // 서명 일시는 본문에 동적 변수가 없으므로 후처리로 표시.
        if (c.getEmployeeSignedAt() != null) {
            doc.add(new Paragraph(" ", smallFont));
            doc.add(new Paragraph("근로자 서명 일시: " + c.getEmployeeSignedAt(), smallFont));
        }
        doc.close();
        return baos.toByteArray();
    }

    private BaseFont loadKoreanBaseFont() throws Exception {
        if (koreanFontBytes != null) {
            return BaseFont.createFont("NanumGothic.ttf", BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED, BaseFont.CACHED, koreanFontBytes, null);
        }
        if (registeredFontFile != null) {
            return BaseFont.createFont(registeredFontFile, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        }
        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
    }
}
