package egovframework.groupware.contract.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import egovframework.groupware.contract.service.EmploymentContractVO;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

@Component
public class ContractPdfWriter {

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
            HTMLWorker w = new HTMLWorker(doc);
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
        String[] candidates = {
            "/usr/share/fonts/truetype/nanum/NanumGothic.ttf",
            "/usr/share/fonts/nanum/NanumGothic.ttf",
            "/Library/Fonts/AppleGothic.ttf",
            "C:/Windows/Fonts/malgun.ttf"
        };
        for (String p : candidates) {
            try { return BaseFont.createFont(p, BaseFont.IDENTITY_H, BaseFont.EMBEDDED); } catch (Exception ignored) {}
        }
        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
    }
}
