package egovframework.groupware.contract.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
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
        Document doc = new Document(PageSize.A4, 40, 40, 50, 40);
        PdfWriter.getInstance(doc, baos);

        Font titleFont;
        Font smallFont;
        try {
            BaseFont bf = loadKoreanBaseFont();
            titleFont = new Font(bf, 18, Font.BOLD);
            smallFont = new Font(bf, 9);
        } catch (Exception ex) {
            titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        }

        doc.open();
        Paragraph title = new Paragraph("근로계약서", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(new Paragraph(" ", smallFont));
        try {
            // HTMLWorker 에 한글 폰트를 명시적으로 적용.
            // body 의 face 를 등록한 NanumGothic 으로, encoding 을 IDENTITY_H 로 두지 않으면
            // HTMLWorker 가 기본 Helvetica 를 사용해 한글 문자가 모두 빈 자리로 떨어진다.
            StyleSheet css = new StyleSheet();
            css.loadTagStyle("body", "face", FONT_ALIAS);
            css.loadTagStyle("body", "encoding", BaseFont.IDENTITY_H);
            HTMLWorker w = new HTMLWorker(doc);
            w.setStyleSheet(css);
            w.parse(new StringReader(renderedHtml == null ? "" : renderedHtml));
        } catch (Exception ex) {
            doc.add(new Paragraph(renderedHtml == null ? "" : renderedHtml, smallFont));
        }
        doc.add(new Paragraph(" ", smallFont));
        doc.add(new Paragraph("계약번호: " + c.getContractNo(), smallFont));
        doc.add(new Paragraph("상태: " + c.getStatusCd(), smallFont));
        doc.add(new Paragraph("\n\n갑: 회사 _____________________ (인)", smallFont));
        doc.add(new Paragraph("을: " + (c.getUserName() == null ? "" : c.getUserName()) + " _____________________ (인)", smallFont));
        if (c.getEmployeeSignedAt() != null) {
            doc.add(new Paragraph("\n근로자 서명 일시: " + c.getEmployeeSignedAt(), smallFont));
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
