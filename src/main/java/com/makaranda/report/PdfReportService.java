package com.makaranda.report;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.dasha.VimshottariDasha;
import com.makaranda.calc.dasha.VimshottariDasha.Period;
import com.makaranda.calc.interpret.InterpretationEngine;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;
import com.makaranda.calc.vedic.ChartBuilder.PlanetBody;
import com.makaranda.calc.vedic.SpecialCharts;
import com.makaranda.calc.yoga.YogaDetector;
import com.makaranda.dto.BirthRequest;
import com.makaranda.service.JyotishService;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Multi-chapter Makaranda / KSDSU kundali PDF. Hindi+English. Not an Astrotalk clone.
 * Does not retune bijas; prints whatever the engine already computed.
 */
@Service
public class PdfReportService {
    private static final Color MAROON = new Color(123, 30, 58);
    private static final Color GOLD = new Color(201, 162, 39);
    private static final Color CREAM = new Color(247, 241, 227);
    private static final Color INK = new Color(32, 28, 24);

    private static final DateTimeFormatter CLOCK12 =
            DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a", Locale.ENGLISH);

    private final JyotishService jyotish;
    private final InterpretationEngine interpreter = new InterpretationEngine();
    private final SpecialCharts special = new SpecialCharts();

    private static BaseFont DEV;
    private static BaseFont DEV_B;

    static {
        DEV = loadFont("/fonts/NotoSansDevanagari-Regular.ttf");
        DEV_B = loadFont("/fonts/NotoSansDevanagari-Bold.ttf");
    }

    public PdfReportService(JyotishService jyotish) {
        this.jyotish = jyotish;
    }

    public byte[] kundaliReport(BirthRequest req) {
        FullChart c = jyotish.chart(req);
        Map<String, Object> panch = jyotish.panchang(
                c.input().localDateTime().toLocalDate(),
                c.input().latitude(), c.input().longitude(), c.input().tzOffsetHours(),
                c.input().ayanamsa() == null ? null : c.input().ayanamsa().name(),
                c.panchangMode());
        Map<String, Object> yogas = YogaDetector.analyse(c);
        Map<String, Object> reading = interpreter.interpret(c);
        Map<String, Object> gems = special.gemstones(c);
        List<Period> dasha = VimshottariDasha.compute(
                c.planets().get("Moon").siderealLon(), c.input().localDateTime(), 2);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36, 36, 42, 40);
        try {
            PdfWriter.getInstance(doc, bos);
            doc.open();
            cover(doc, req, c);
            chapterBasic(doc, req, c, panch);
            chapterCharts(doc, c);
            chapterGrahas(doc, c);
            chapterDasha(doc, dasha);
            chapterExtras(doc, c, yogas, reading, gems);
            footerNote(doc);
            doc.close();
        } catch (Exception e) {
            throw new IllegalStateException("PDF report failed", e);
        }
        return bos.toByteArray();
    }

    private void cover(Document doc, BirthRequest req, FullChart c) throws Exception {
        p(doc, "मकरन्द ज्योतिष", fB(20, MAROON), Element.ALIGN_CENTER);
        p(doc, "Makaranda Jyotish", fB(12, GOLD), Element.ALIGN_CENTER);
        p(doc, "सूर्य सिद्धान्त · मकरन्द पंचांग · मिथिलांचल", f(10, GOLD), Element.ALIGN_CENTER);
        p(doc, "कामेश्वर सिंह दरभंगा संस्कृत विश्वविद्यालय पद्धति", f(9, INK), Element.ALIGN_CENTER);
        gap(doc, 8);
        p(doc, "जन्म कुंडली प्रतिवेदन  /  Birth Kundali Report", fB(13, MAROON), Element.ALIGN_CENTER);
        gap(doc, 6);
        kv(doc, "जातक / Native", blank(req.name));
        kv(doc, "स्थान / Place", c.input().place());
        kv(doc, "जन्म / Birth", CLOCK12.format(c.input().localDateTime()) + "  (" + c.input().timeZone() + ")");
        kv(doc, "अक्षांश–देशान्तर", String.format(Locale.ENGLISH, "%.4f°N  %.4f°E",
                c.input().latitude(), c.input().longitude()));
        kv(doc, "अयनांश / Ayanamsa", c.ayanamsaLabel() + "  =  "
                + String.format(Locale.ENGLISH, "%.4f°", c.ayanamsaDeg()));
        kv(doc, "गणिता / Mode", c.panchangMode());
        kv(doc, "लग्न / Lagna", c.lagna().signHi() + "  " + c.lagna().signSa() + "  " + c.lagna().signDegree());
        gap(doc, 4);
        p(doc, "यह प्रतिवेदन मकरन्द / सूर्य सिद्धान्त स्पष्ट पर है (सिद्धान्तिक) जब तक दृक् न चुना हो। "
                + "Astrotalk की नकल नहीं; अध्याय-क्रम एक सामान्य कुंडली-रिपोर्ट का है। परम्परा पाठ — चिकित्सा या कानूनी सलाह नहीं।",
                f(8, INK), Element.ALIGN_JUSTIFIED);
    }

    private void chapterBasic(Document doc, BirthRequest req, FullChart c, Map<String, Object> panch) throws Exception {
        h(doc, "१  बुनियादी  /  Basic");
        Map<String, Object> av = c.avakahada();
        kv(doc, "लग्नेश / Lagnesha", str(av.get("lagneshHi")) + "  (" + str(av.get("lagnesh")) + ")");
        kv(doc, "लिंग / Gender", blank(req.gender));
        gap(doc, 4);
        p(doc, "जन्म पंचांग (अंग समाप्ति = तक)", fB(11, MAROON), Element.ALIGN_LEFT);
        kv(doc, "तिथि", join(panch.get("pakshaHi"), panch.get("tithiHi")) + till(panch.get("tithiEnd")));
        kv(doc, "नक्षत्र", str(panch.get("nakshatraHi")) + till(panch.get("nakshatraEnd")));
        kv(doc, "योग", str(panch.get("yogaHi")) + till(panch.get("yogaEnd")));
        kv(doc, "करण", str(panch.get("karanaHi")) + till(panch.get("karanaEnd")));
        kv(doc, "वार", str(panch.get("varaHi")));
        kv(doc, "सूर्योदय / सूर्यास्त", str(panch.get("sunrise")) + "  /  " + str(panch.get("sunset")));
        gap(doc, 4);
        p(doc, "अवकहड़ा (चन्द्र से)", fB(11, MAROON), Element.ALIGN_LEFT);
        PdfPTable t = table(4);
        avCell(t, "वर्ण", av.get("varnaHi"));
        avCell(t, "वश्य", av.get("vashyaHi"));
        avCell(t, "योनि", av.get("yoniHi"));
        avCell(t, "गण", av.get("ganaHi"));
        avCell(t, "नाड़ी", av.get("nadiHi"));
        avCell(t, "राशि", av.get("rashiHi"));
        avCell(t, "राशि स्वामी", av.get("rashiLordHi"));
        avCell(t, "नक्षत्र", av.get("nakshatraHi"));
        avCell(t, "नक्षत्र स्वामी", av.get("nakLordHi"));
        avCell(t, "चरण", av.get("pada"));
        avCell(t, "तत्त्व", av.get("tattvaHi"));
        avCell(t, "नाम अक्षर", av.get("namakshara"));
        avCell(t, "पाया", av.get("payaHi"));
        avCell(t, "लग्न राशि", av.get("lagnaRashiHi"));
        doc.add(t);
    }

    private void chapterCharts(Document doc, FullChart c) throws Exception {
        h(doc, "२  कुंडली  /  Charts");
        p(doc, "D1 लग्न कुंडली — पूर्ण राशि (भाव संख्या = लग्न से)", fB(10, MAROON), Element.ALIGN_LEFT);
        doc.add(bhavaTable(c, c.lagna().signIndex(), true));
        gap(doc, 6);
        p(doc, "चन्द्र कुंडली — भाव चन्द्र राशि से", fB(10, MAROON), Element.ALIGN_LEFT);
        doc.add(bhavaTable(c, c.planets().get("Moon").signIndex(), false));
        gap(doc, 6);
        p(doc, "सूर्य कुंडली — भाव सूर्य राशि से", fB(10, MAROON), Element.ALIGN_LEFT);
        doc.add(bhavaTable(c, c.planets().get("Sun").signIndex(), false));
        Map<String, Object> d9 = c.vargas() == null ? null : c.vargas().get(9);
        if (d9 != null) {
            gap(doc, 6);
            p(doc, "नवमांश D9 — धर्म / दारा", fB(10, MAROON), Element.ALIGN_LEFT);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> bodies = (List<Map<String, Object>>) d9.get("bodies");
            PdfPTable t = table(3);
            header(t, "ग्रह", "राशि", "भाव");
            if (bodies != null) {
                for (Map<String, Object> b : bodies) {
                    cell(t, str(b.get("nameHi"), b.get("name")));
                    cell(t, str(b.get("signHi"), b.get("signSa")));
                    cell(t, str(b.get("house")));
                }
            }
            doc.add(t);
        }
        p(doc, "भाव चलित (श्रीपति) कुंडली पृष्ठ पर अलग अनुरोध से दिखती है; यह प्रतिवेदन जन्म की राशि-पद्धति नहीं बदलता।",
                f(8, INK), Element.ALIGN_LEFT);
    }

    private PdfPTable bhavaTable(FullChart c, int originSign, boolean markLagnaHouse1) {
        List<List<String>> occ = new ArrayList<>();
        for (int i = 0; i < 12; i++) occ.add(new ArrayList<>());
        if (markLagnaHouse1) occ.get(0).add("लग्न");
        else {
            int lh = ((c.lagna().signIndex() - originSign + 12) % 12);
            occ.get(lh).add("लग्न");
        }
        for (PlanetBody p : c.planets().values()) {
            int h = ((p.signIndex() - originSign + 12) % 12);
            occ.get(h).add(p.nameHi() + (p.retrograde() ? " वक्र" : ""));
        }
        PdfPTable t = table(3);
        header(t, "भाव", "राशि", "ग्रह");
        for (int i = 0; i < 12; i++) {
            int sign = (originSign + i) % 12;
            cell(t, String.valueOf(i + 1));
            cell(t, VedicConstants.SIGNS_HI[sign] + "  " + VedicConstants.SIGNS_SA[sign]);
            cell(t, occ.get(i).isEmpty() ? "—" : String.join("  ", occ.get(i)));
        }
        return t;
    }

    private void chapterGrahas(Document doc, FullChart c) throws Exception {
        h(doc, "३  ग्रह  /  Grahas");
        PdfPTable t = new PdfPTable(new float[]{1.6f, 1.4f, 1.3f, 0.7f, 1.8f, 1.2f, 1.6f, 1.3f, 1.1f});
        t.setWidthPercentage(100);
        header(t, "ग्रह", "राशि", "स्वामी", "भाव", "नक्षत्र", "न.स्वामी", "अंश", "गरिमा", "बालादि");
        rowGraha(t, c.lagna());
        for (PlanetBody p : c.planets().values()) rowGraha(t, p);
        doc.add(t);
    }

    private void rowGraha(PdfPTable t, PlanetBody p) {
        cell(t, p.nameHi() + (p.retrograde() ? " वक्र" : ""));
        cell(t, p.signHi());
        cell(t, p.rashiLordHi());
        cell(t, String.valueOf(p.house()));
        cell(t, p.nakshatraHi() + " " + p.pada());
        cell(t, p.nakLordHi());
        cell(t, p.signDegree() == null ? p.dms() : p.signDegree());
        cell(t, p.dignityHi());
        cell(t, p.avasthaHi() == null ? p.avastha() : p.avasthaHi());
    }

    private void chapterDasha(Document doc, List<Period> dasha) throws Exception {
        h(doc, "४  विंशोत्तरी दशा  /  Vimshottari");
        p(doc, "महादशा · अन्तरदशा जन्म-चन्द्र नक्षत्र से। यह परम्परा गणित है, भविष्य-वाणी नहीं।",
                f(8, INK), Element.ALIGN_LEFT);
        PdfPTable t = table(4);
        header(t, "स्वामी", "आरम्भ", "समाप्ति", "वर्ष");
        int n = 0;
        for (Period maha : dasha) {
            if (n++ >= 5) break;
            cell(t, VedicConstants.planetHi(maha.lord()) + " महादशा");
            cell(t, CLOCK12.format(maha.start()));
            cell(t, CLOCK12.format(maha.end()));
            cell(t, String.format(Locale.ENGLISH, "%.2f", maha.years()));
            int a = 0;
            for (Period antar : maha.children()) {
                if (a++ >= 4) break;
                cell(t, "  " + VedicConstants.planetHi(antar.lord()));
                cell(t, CLOCK12.format(antar.start()));
                cell(t, CLOCK12.format(antar.end()));
                cell(t, String.format(Locale.ENGLISH, "%.2f", antar.years()));
            }
        }
        doc.add(t);
    }

    @SuppressWarnings("unchecked")
    private void chapterExtras(Document doc, FullChart c, Map<String, Object> yogas,
                               Map<String, Object> reading, Map<String, Object> gems) throws Exception {
        h(doc, "५  विशेष  /  Extras");
        p(doc, "विंशोपक (षोडशवर्ग)", fB(11, MAROON), Element.ALIGN_LEFT);
        PdfPTable vt = table(3);
        header(vt, "ग्रह", "अंक / 20", "");
        c.vimshopaka().forEach((k, v) -> {
            cell(vt, VedicConstants.planetHi(k));
            cell(vt, String.valueOf(v));
            cell(vt, "");
        });
        doc.add(vt);
        gap(doc, 6);
        p(doc, "योग व दोष (पैटर्न-सूची — भय-प्रचार नहीं)", fB(11, MAROON), Element.ALIGN_LEFT);
        List<Map<String, Object>> ys = (List<Map<String, Object>>) yogas.get("yogas");
        List<Map<String, Object>> ds = (List<Map<String, Object>>) yogas.get("doshas");
        if (ys != null) {
            for (Map<String, Object> y : ys) {
                p(doc, "• " + y.get("name") + " — " + y.get("text"), f(8, INK), Element.ALIGN_LEFT);
            }
        }
        if (ds != null) {
            for (Map<String, Object> y : ds) {
                p(doc, "• " + y.get("name") + " — " + y.get("text"), f(8, INK), Element.ALIGN_LEFT);
            }
        }
        gap(doc, 6);
        p(doc, "परम्परा पाठ", fB(11, MAROON), Element.ALIGN_LEFT);
        p(doc, str(reading.get("summary")), f(9, INK), Element.ALIGN_JUSTIFIED);
        p(doc, str(reading.get("personality")), f(9, INK), Element.ALIGN_JUSTIFIED);
        Map<String, Object> career = (Map<String, Object>) reading.get("career");
        Map<String, Object> marriage = (Map<String, Object>) reading.get("marriage");
        if (career != null) p(doc, "कर्म: " + career.get("reading"), f(9, INK), Element.ALIGN_JUSTIFIED);
        if (marriage != null) p(doc, "विवाह: " + marriage.get("reading"), f(9, INK), Element.ALIGN_JUSTIFIED);
        gap(doc, 6);
        p(doc, "रत्न (सलाह, विक्रय नहीं)", fB(11, MAROON), Element.ALIGN_LEFT);
        kv(doc, "लग्नेश रत्न", str(gems.get("lifeStone")));
        kv(doc, "निर्बल ग्रह", str(gems.get("weakestPlanet")) + "  " + str(gems.get("luckyStone")));
        p(doc, str(gems.get("warning")), f(8, MAROON), Element.ALIGN_LEFT);
    }

    private void footerNote(Document doc) throws Exception {
        gap(doc, 10);
        p(doc, "टिप्पणी: सिद्धान्तिक अंग मूल KSDSU जुलाई २०१६ / २०२२ स्वर्ण से बँधे हैं। "
                + "कार्तिक–माघ तिथि अभी ४५–१४० मिनट आगे हो सकती है — बीज न बदलें। "
                + "दृक् = Swiss Ephemeris। मुहूर्त के लिए जीवित ज्योतिषी से पुष्टि करें। "
                + "मकरन्द ज्योतिष · अक्षांश २६।३५ देशान्तर ०१।३५ पल्लभा ६।",
                f(8, INK), Element.ALIGN_JUSTIFIED);
    }

    /* ——— layout helpers ——— */

    private static BaseFont loadFont(String cp) {
        try (InputStream in = PdfReportService.class.getResourceAsStream(cp)) {
            if (in == null) throw new IllegalStateException("missing font " + cp);
            byte[] bytes = in.readAllBytes();
            return BaseFont.createFont("font.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, bytes, null);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Font f(float size, Color c) { return new Font(DEV, size, Font.NORMAL, c); }
    private Font fB(float size, Color c) { return new Font(DEV_B, size, Font.NORMAL, c); }

    private void h(Document doc, String title) throws Exception {
        gap(doc, 10);
        Paragraph p = new Paragraph(title, fB(13, MAROON));
        p.setSpacingAfter(6);
        doc.add(p);
    }

    private void p(Document doc, String text, Font font, int align) throws Exception {
        Paragraph para = new Paragraph(text == null ? "" : text, font);
        para.setAlignment(align);
        para.setLeading(font.getSize() * 1.35f);
        para.setSpacingAfter(3);
        doc.add(para);
    }

    private void kv(Document doc, String k, String v) throws Exception {
        Paragraph para = new Paragraph();
        para.add(new Phrase(k + "  ", fB(9, MAROON)));
        para.add(new Phrase(v == null ? "—" : v, f(9, INK)));
        para.setSpacingAfter(2);
        doc.add(para);
    }

    private void gap(Document doc, float n) throws Exception {
        Paragraph p = new Paragraph(" ");
        p.setSpacingAfter(n);
        doc.add(p);
    }

    private PdfPTable table(int cols) {
        PdfPTable t = new PdfPTable(cols);
        t.setWidthPercentage(100);
        t.setSpacingBefore(2);
        t.setSpacingAfter(4);
        return t;
    }

    private void header(PdfPTable t, String... cols) {
        for (String c : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(c, fB(8, Color.WHITE)));
            cell.setBackgroundColor(MAROON);
            cell.setPadding(4);
            t.addCell(cell);
        }
    }

    private void cell(PdfPTable t, String v) {
        PdfPCell cell = new PdfPCell(new Phrase(v == null ? "" : v, f(8, INK)));
        cell.setBackgroundColor(CREAM);
        cell.setPadding(3);
        t.addCell(cell);
    }

    private void avCell(PdfPTable t, String k, Object v) {
        PdfPCell a = new PdfPCell(new Phrase(k, f(7, GOLD)));
        a.setBackgroundColor(new Color(20, 24, 48));
        a.setPadding(3);
        t.addCell(a);
        PdfPCell b = new PdfPCell(new Phrase(str(v), f(8, INK)));
        b.setBackgroundColor(CREAM);
        b.setPadding(3);
        t.addCell(b);
    }

    private static String str(Object o) { return o == null ? "—" : String.valueOf(o); }
    private static String str(Object a, Object b) { return a != null ? String.valueOf(a) : str(b); }
    private static String blank(String s) { return s == null || s.isBlank() ? "—" : s; }
    private static String join(Object a, Object b) { return str(a) + " " + str(b); }
    private static String till(Object end) {
        return end == null ? "" : "  ·  तक  " + end;
    }
}
