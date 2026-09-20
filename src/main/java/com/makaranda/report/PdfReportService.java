package com.makaranda.report;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;
import com.makaranda.calc.vedic.ChartBuilder.PlanetBody;
import com.makaranda.calc.yoga.YogaDetector;
import com.makaranda.service.JyotishService;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Map;

@Service
public class PdfReportService {
    private static final Color MAROON = new Color(123, 30, 58);
    private static final Color GOLD = new Color(201, 162, 39);
    private static final Color CREAM = new Color(247, 241, 227);

    private final JyotishService jyotish;

    public PdfReportService(JyotishService jyotish) {
        this.jyotish = jyotish;
    }

    public byte[] kundaliReport(com.makaranda.dto.BirthRequest req) {
        FullChart c = jyotish.chart(req);
        Map<String, Object> yogas = YogaDetector.analyse(c);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36, 36, 48, 36);
        PdfWriter.getInstance(doc, bos);
        doc.open();
        Font title = FontFactory.getFont(FontFactory.TIMES_BOLD, 20, MAROON);
        Font sub = FontFactory.getFont(FontFactory.TIMES_ITALIC, 11, GOLD);
        Font h = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, MAROON);
        Font body = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);

        Paragraph t = new Paragraph("MAKARANDA JYOTISH", title);
        t.setAlignment(Element.ALIGN_CENTER);
        doc.add(t);
        Paragraph s = new Paragraph("Surya Siddhanta • Makaranda Panchang • Mithilanchal", sub);
        s.setAlignment(Element.ALIGN_CENTER);
        doc.add(s);
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Birth Kundali Report — " + LocalDate.now(), h));
        doc.add(new Paragraph("Native: " + (req.name == null ? "—" : req.name)
                + "    Place: " + c.input().place(), body));
        doc.add(new Paragraph("Date/Time: " + c.input().localDateTime()
                + "  (" + c.input().timeZone() + ")", body));
        doc.add(new Paragraph("Ayanamsa: " + c.ayanamsaLabel() + "  =  "
                + String.format("%.4f°", c.ayanamsaDeg())
                + "    Mode: " + c.panchangMode(), body));
        doc.add(new Paragraph("Lagna: " + c.lagna().signSa() + "  " + c.lagna().signDegree()
                + "    JD(UT): " + String.format("%.5f", c.julianDayUt()), body));
        doc.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(new float[]{2, 2, 3, 1, 2, 2});
        table.setWidthPercentage(100);
        header(table, "Graha", "Rashi", "Degree", "Bhava", "Nakshatra", "Dignity");
        for (PlanetBody p : c.planets().values()) {
            cell(table, p.name());
            cell(table, p.signSa());
            cell(table, p.signDegree());
            cell(table, String.valueOf(p.house()));
            cell(table, p.nakshatra() + " " + p.pada());
            cell(table, p.dignity() + (p.retrograde() ? " R" : ""));
        }
        doc.add(table);
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Vimshopaka (Shodashavarga)", h));
        doc.add(new Paragraph(c.vimshopaka().toString(), body));
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Yogas detected: " + yogas.get("yogaCount")
                + "    Doshas: " + yogas.get("doshaCount"), h));
        doc.add(new Paragraph("This report uses the Makaranda / Surya Siddhanta defaults of Mithilanchal unless another ayanamsa was selected. Planetary longitudes in Drik mode follow JPL Keplerian + Meeus lunar theory; Siddhantic mode follows Surya Siddhanta mean motions with manda/sighra. For ritual muhurta, confirm with a living Jyotishi.", body));
        doc.close();
        return bos.toByteArray();
    }

    private void header(PdfPTable t, String... cols) {
        for (String c : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(c, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
            cell.setBackgroundColor(MAROON);
            cell.setPadding(5);
            t.addCell(cell);
        }
    }

    private void cell(PdfPTable t, String v) {
        PdfPCell cell = new PdfPCell(new Phrase(v == null ? "" : v, FontFactory.getFont(FontFactory.HELVETICA, 8)));
        cell.setBackgroundColor(CREAM);
        cell.setPadding(4);
        t.addCell(cell);
    }
}
