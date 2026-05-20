package egovframework.groupware.payroll.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import egovframework.groupware.payroll.service.PayrollItemVO;
import egovframework.groupware.payroll.service.PayrollVO;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * 한국 표준 양식 급여명세서 PDF.
 * 한글 표시를 위해 시스템 폰트(예: NanumGothic, Malgun Gothic)를 찾아 사용.
 * 없으면 OpenPDF 기본 폰트로 폴백 — 본 PR에서는 운영 환경에서 별도 폰트 설치를 권장.
 */
@Component
public class PayslipPdfWriter {

    private static final NumberFormat WON = NumberFormat.getNumberInstance(Locale.KOREA);

    private final Font titleFont;
    private final Font headerFont;
    private final Font bodyFont;
    private final Font smallFont;

    public PayslipPdfWriter() {
        Font t, h, b, s;
        try {
            BaseFont bf = loadKoreanBaseFont();
            t = new Font(bf, 18, Font.BOLD);
            h = new Font(bf, 11, Font.BOLD);
            b = new Font(bf, 10);
            s = new Font(bf, 9);
        } catch (Exception ex) {
            t = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            h = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            b = FontFactory.getFont(FontFactory.HELVETICA, 10);
            s = FontFactory.getFont(FontFactory.HELVETICA, 9);
        }
        this.titleFont = t; this.headerFont = h; this.bodyFont = b; this.smallFont = s;
    }

    public byte[] write(PayrollVO p) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(doc, baos);
        doc.open();
        doc.add(new Paragraph("급여명세서", titleFont));
        doc.add(new Paragraph("지급월: " + p.getPayMonth(), bodyFont));
        doc.add(new Paragraph("성명: " + nz(p.getUserName()) + "  / 부서: " + nz(p.getDeptNm()) + "  / 사번: " + p.getUserId(), bodyFont));
        doc.add(new Paragraph(" ", smallFont));

        PdfPTable summary = new PdfPTable(4);
        summary.setWidthPercentage(100);
        summary.addCell(headerCell("총 지급액"));
        summary.addCell(bodyCell(won(p.getGrossPay()) + " 원"));
        summary.addCell(headerCell("실 수령액"));
        summary.addCell(bodyCell(won(p.getNetPay()) + " 원"));
        doc.add(summary);
        doc.add(new Paragraph(" ", smallFont));

        // 지급
        doc.add(new Paragraph("[지급내역]", headerFont));
        PdfPTable pay = new PdfPTable(new float[]{2, 2, 1, 1});
        pay.setWidthPercentage(100);
        pay.addCell(headerCell("항목"));
        pay.addCell(headerCell("금액(원)"));
        pay.addCell(headerCell("과세/비과세"));
        pay.addCell(headerCell("산출"));
        for (PayrollItemVO it : p.getItems()) {
            if (!"PAYMENT".equals(it.getKindCd())) continue;
            pay.addCell(bodyCell(it.getItemNm()));
            pay.addCell(rightCell(won(it.getAmount())));
            pay.addCell(bodyCell("Y".equals(it.getTaxableYn()) ? "과세" : "비과세"));
            pay.addCell(bodyCell("Y".equals(it.getAutoYn()) ? "자동" : "수동"));
        }
        doc.add(pay);
        doc.add(new Paragraph(" ", smallFont));

        // 공제
        doc.add(new Paragraph("[공제내역]", headerFont));
        PdfPTable ded = new PdfPTable(new float[]{2, 2, 2});
        ded.setWidthPercentage(100);
        ded.addCell(headerCell("항목"));
        ded.addCell(headerCell("금액(원)"));
        ded.addCell(headerCell("비고"));
        for (PayrollItemVO it : p.getItems()) {
            if (!"DEDUCTION".equals(it.getKindCd())) continue;
            ded.addCell(bodyCell(it.getItemNm()));
            ded.addCell(rightCell(won(it.getAmount())));
            ded.addCell(bodyCell(nz(it.getMemo())));
        }
        doc.add(ded);
        doc.add(new Paragraph(" ", smallFont));

        if (!p.getEmployerCosts().isEmpty()) {
            doc.add(new Paragraph("[회사 부담분 (참고)]", smallFont));
            PdfPTable emp = new PdfPTable(2);
            emp.setWidthPercentage(60);
            for (var c : p.getEmployerCosts()) {
                emp.addCell(bodyCell(c.getInsuranceCd()));
                emp.addCell(rightCell(won(c.getAmount())));
            }
            doc.add(emp);
        }

        doc.add(new Paragraph("\n본 명세서는 시스템에서 자동 생성되었습니다.", smallFont));
        doc.close();
        return baos.toByteArray();
    }

    private PdfPCell headerCell(String s) {
        PdfPCell c = new PdfPCell(new Phrase(s, headerFont));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setBackgroundColor(new java.awt.Color(230, 235, 245));
        c.setPadding(5);
        return c;
    }
    private PdfPCell bodyCell(String s) {
        PdfPCell c = new PdfPCell(new Phrase(s, bodyFont));
        c.setPadding(4);
        return c;
    }
    private PdfPCell rightCell(String s) {
        PdfPCell c = new PdfPCell(new Phrase(s, bodyFont));
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c.setPadding(4);
        return c;
    }
    private String won(BigDecimal v) {
        return v == null ? "0" : WON.format(v);
    }
    private String nz(String s) { return s == null ? "" : s; }

    private BaseFont loadKoreanBaseFont() throws Exception {
        String[] candidates = {
            "/usr/share/fonts/truetype/nanum/NanumGothic.ttf",
            "/usr/share/fonts/nanum/NanumGothic.ttf",
            "/Library/Fonts/AppleGothic.ttf",
            "C:/Windows/Fonts/malgun.ttf"
        };
        for (String p : candidates) {
            try {
                return BaseFont.createFont(p, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception ignored) { }
        }
        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
    }
}
