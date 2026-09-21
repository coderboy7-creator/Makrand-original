package com.makaranda.report;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.dasha.VimshottariDasha;
import com.makaranda.calc.dasha.VimshottariDasha.Period;
import com.makaranda.calc.dasha.YoginiDasha;
import com.makaranda.calc.interpret.InterpretationEngine;
import com.makaranda.calc.vedic.Ashtakavarga;
import com.makaranda.calc.vedic.Shadbala;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;
import com.makaranda.calc.vedic.ChartBuilder.PlanetBody;
import com.makaranda.calc.vedic.SpecialCharts;
import com.makaranda.calc.yoga.DoshaPanel;
import com.makaranda.calc.yoga.YogaDetector;
import com.makaranda.dto.BirthRequest;
import com.makaranda.service.JyotishService;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Makaranda / KSDSU kundali PDF. Hindi is HarfBuzz-shaped (not raw OpenPDF TTF).
 * Not an Astrotalk clone. Does not retune bijas.
 */
@Service
public class PdfReportService {
    private static final Color MAROON = new Color(123, 30, 58);
    private static final Color GOLD = new Color(232, 197, 71);
    private static final Color CREAM = new Color(247, 241, 227);
    private static final Color INK = new Color(28, 22, 18);
    private static final Color NAVY = new Color(18, 14, 28);
    private static final Color ROW_ALT = new Color(252, 246, 232);

    private static final DateTimeFormatter CLOCK12 =
            DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a", Locale.ENGLISH);

    private final JyotishService jyotish;
    private final InterpretationEngine interpreter = new InterpretationEngine();
    private final SpecialCharts special = new SpecialCharts();

    private static BaseFont LATIN;
    private static BaseFont LATIN_B;

    static {
        LATIN = loadFont("/fonts/NotoSansDevanagari-Regular.ttf");
        LATIN_B = loadFont("/fonts/NotoSansDevanagari-Bold.ttf");
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
        double moon = c.planets().get("Moon").siderealLon();
        List<Period> dasha = VimshottariDasha.compute(moon, c.input().localDateTime(), 3);
        Map<String, Object> dashaNow = VimshottariDasha.currentAt(dasha, LocalDateTime.now());
        List<Period> yogini = YoginiDasha.compute(moon, c.input().localDateTime(), 2);
        Map<String, Object> yoginiNow = VimshottariDasha.currentAt(yogini, LocalDateTime.now());
        Map<String, Object> gochar = jyotish.transits(req, LocalDate.now());
        Map<String, Object> dosa = DoshaPanel.from(c, gochar);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 48, 36);
        try {
            PdfWriter w = PdfWriter.getInstance(doc, bos);
            w.setPageEvent(new Chrome());
            doc.addTitle("Makaranda Jyotish — Mithilanchal kundali");
            doc.addAuthor("Makaranda Jyotish");
            doc.addCreator("Makaranda Jyotish");
            doc.addSubject("KSDSU / Surya Siddhanta Makaranda report");
            doc.open();
            cover(doc, req, c);
            chapterBasic(doc, req, c, panch, dosa);
            chapterCharts(doc, c);
            chapterGrahas(doc, c);
            chapterDasha(doc, dasha, dashaNow, yogini, yoginiNow);
            chapterAshtakavarga(doc, Ashtakavarga.fromChart(c));
            chapterShadbala(doc, Shadbala.fromChart(c));
            chapterInterpret(doc, reading, yogas, gems);
            doc.close();
        } catch (Exception e) {
            throw new IllegalStateException("PDF report failed", e);
        }
        return bos.toByteArray();
    }

    private void cover(Document doc, BirthRequest req, FullChart c) throws Exception {
        PdfPTable hero = new PdfPTable(1);
        hero.setWidthPercentage(100);
        PdfPCell band = new PdfPCell();
        band.setBackgroundColor(NAVY);
        band.setBorder(Rectangle.NO_BORDER);
        band.setPadding(18);
        band.setPaddingBottom(20);
        band.addElement(DevanagariPaint.blockOn("मकरन्द ज्योतिष", 22, GOLD, true, 480, NAVY));
        Paragraph en = new Paragraph("Makaranda Jyotish  ·  Mithilanchal", fB(11, GOLD));
        en.setSpacingBefore(6);
        band.addElement(en);
        band.addElement(DevanagariPaint.blockOn("सूर्य सिद्धान्त · मकरन्द पंचांग · कामेश्वर सिंह दरभंगा संस्कृत विश्वविद्यालय", 9, CREAM, false, 480, NAVY));
        hero.addCell(band);
        doc.add(hero);
        gap(doc, 10);
        hi(doc, "जन्म कुंडली प्रतिवेदन", 14, MAROON, true);
        pEn(doc, "Birth Kundali Report  —  not an Astrotalk clone. Parampara math, not medical or legal advice.", 9, INK);
        gap(doc, 6);
        PdfPTable meta = table(2);
        kvCell(meta, "जातक / Native", blank(req.name));
        kvCell(meta, "स्थान / Place", c.input().place());
        kvCell(meta, "जन्म / Birth", CLOCK12.format(c.input().localDateTime()));
        kvCell(meta, "अक्षांश–देशान्तर", String.format(Locale.ENGLISH, "%.4f°N  %.4f°E",
                c.input().latitude(), c.input().longitude()));
        kvCell(meta, "अयनांश / Ayanamsa", c.ayanamsaLabel() + "  =  "
                + String.format(Locale.ENGLISH, "%.4f°", c.ayanamsaDeg()));
        kvCell(meta, "लग्न / Lagna", c.lagna().signHi() + "  " + c.lagna().signDegree());
        doc.add(meta);
    }

    private void chapterBasic(Document doc, BirthRequest req, FullChart c, Map<String, Object> panch,
                              Map<String, Object> dosa) throws Exception {
        h(doc, "१  बुनियादी  /  Basic");
        Map<String, Object> av = c.avakahada();
        kvLine(doc, "लग्नेश", str(av.get("lagneshHi")) + "  (" + str(av.get("lagnesh")) + ")");
        hi(doc, "जन्म पंचांग (अंग समाप्ति = तक)", 11, MAROON, true);
        kvLine(doc, "तिथि", join(panch.get("pakshaHi"), panch.get("tithiHi")) + till(panch.get("tithiEnd")));
        kvLine(doc, "नक्षत्र", str(panch.get("nakshatraHi")) + till(panch.get("nakshatraEnd")));
        kvLine(doc, "योग", str(panch.get("yogaHi")) + till(panch.get("yogaEnd")));
        kvLine(doc, "करण", str(panch.get("karanaHi")) + till(panch.get("karanaEnd")));
        kvLine(doc, "वार", str(panch.get("varaHi")));
        kvLine(doc, "सूर्योदय / सूर्यास्त", str(panch.get("sunrise")) + "  /  " + str(panch.get("sunset")));
        gap(doc, 6);
        hi(doc, "अवकहड़ा (चन्द्र से)", 11, MAROON, true);
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
        gap(doc, 8);
        hi(doc, "दोष पटल — गणित, भय नहीं", 11, MAROON, true);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) dosa.get("items");
        PdfPTable dt = new PdfPTable(new float[]{1.4f, 1.1f, 3.2f});
        dt.setWidthPercentage(100);
        dt.setSpacingBefore(2);
        dt.setSpacingAfter(6);
        header(dt, "दोष", "स्थिति", "टिप्पणी");
        if (items != null) {
            for (Map<String, Object> it : items) {
                cell(dt, str(it.get("nameHi")));
                cell(dt, statusHi(str(it.get("status"))));
                cell(dt, str(it.get("textHi")));
            }
        }
        doc.add(dt);
        hi(doc, str(dosa.get("noteHi")), 8, INK, false);
    }

    private void chapterCharts(Document doc, FullChart c) throws Exception {
        h(doc, "२  कुंडली  /  Charts");
        hi(doc, "D1 लग्न कुंडली — पूर्ण राशि", 10, MAROON, true);
        doc.add(bhavaTable(c, c.lagna().signIndex(), true));
        gap(doc, 6);
        hi(doc, "चन्द्र कुंडली", 10, MAROON, true);
        doc.add(bhavaTable(c, c.planets().get("Moon").signIndex(), false));
        gap(doc, 6);
        hi(doc, "सूर्य कुंडली", 10, MAROON, true);
        doc.add(bhavaTable(c, c.planets().get("Sun").signIndex(), false));
        Map<String, Object> d9 = c.vargas() == null ? null : c.vargas().get(9);
        if (d9 != null) {
            gap(doc, 6);
            hi(doc, "नवमांश D9 — धर्म / दारा", 10, MAROON, true);
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

    private void chapterDasha(Document doc, List<Period> dasha, Map<String, Object> now,
                             List<Period> yogini, Map<String, Object> yoginiNow) throws Exception {
        h(doc, "४  विंशोत्तरी दशा  /  Vimshottari");
        hi(doc, "महादशा · अन्तरदशा · प्रत्यन्तर — परम्परा गणित, भविष्य-वाणी नहीं।", 8, INK, false);
        kvLine(doc, "वर्तमान",
                VedicConstants.planetHi(str(now.get("mahadasha"))) + " → "
                        + VedicConstants.planetHi(str(now.get("antardasha"))) + " → "
                        + VedicConstants.planetHi(str(now.get("pratyantardasha"))));
        PdfPTable t = table(4);
        header(t, "स्वामी", "आरम्भ", "समाप्ति", "वर्ष");
        String curM = str(now.get("mahadasha"));
        int n = 0;
        for (Period maha : dasha) {
            if (n++ >= 5) break;
            String mark = maha.lord().equals(curM) ? " ●" : "";
            cell(t, VedicConstants.planetHi(maha.lord()) + " महादशा" + mark);
            cell(t, CLOCK12.format(maha.start()));
            cell(t, CLOCK12.format(maha.end()));
            cell(t, String.format(Locale.ENGLISH, "%.2f", maha.years()));
            int a = 0;
            String curA = str(now.get("antardasha"));
            for (Period antar : maha.children()) {
                if (a++ >= 9) break;
                if (!maha.lord().equals(curM) && a > 3) break;
                String am = antar.lord().equals(curA) && maha.lord().equals(curM) ? " ●" : "";
                cell(t, "  " + VedicConstants.planetHi(antar.lord()) + am);
                cell(t, CLOCK12.format(antar.start()));
                cell(t, CLOCK12.format(antar.end()));
                cell(t, String.format(Locale.ENGLISH, "%.2f", antar.years()));
            }
        }
        doc.add(t);
        gap(doc, 6);
        hi(doc, "योगिनी दशा (३६ वर्ष) — अश्विनी = मंगला", 11, MAROON, true);
        kvLine(doc, "वर्तमान योगिनी",
                YoginiDasha.nameHi(str(yoginiNow.get("mahadasha"))) + "  ("
                        + VedicConstants.planetHi(YoginiDasha.lordOf(str(yoginiNow.get("mahadasha")))) + ")");
        PdfPTable yt = table(5);
        header(yt, "योगिनी", "स्वामी", "आरम्भ", "समाप्ति", "वर्ष");
        for (Period y : yogini) {
            cell(yt, YoginiDasha.nameHi(y.lord()));
            cell(yt, VedicConstants.planetHi(YoginiDasha.lordOf(y.lord())));
            cell(yt, CLOCK12.format(y.start()));
            cell(yt, CLOCK12.format(y.end()));
            cell(yt, String.format(Locale.ENGLISH, "%.0f", y.years()));
        }
        doc.add(yt);
    }

    @SuppressWarnings("unchecked")
    private void chapterAshtakavarga(Document doc, Map<String, Object> av) throws Exception {
        h(doc, "५  अष्टकवर्ग  /  Ashtakavarga");
        hi(doc, str(av.get("noteHi")), 8, INK, false);
        PdfPTable t = new PdfPTable(14);
        t.setWidthPercentage(100);
        header(t, "ग्रह");
        for (String s : VedicConstants.SIGNS_HI) header(t, s);
        header(t, "योग");
        Map<String, Object> bav = (Map<String, Object>) av.get("bav");
        if (bav != null) {
            for (String g : Ashtakavarga.BAV_GRAHAS) {
                Map<String, Object> row = (Map<String, Object>) bav.get(g);
                cell(t, VedicConstants.planetHi(g));
                List<Integer> bySign = (List<Integer>) row.get("bySign");
                for (int n : bySign) cell(t, String.valueOf(n));
                cell(t, String.valueOf(row.get("total")));
            }
        }
        Map<String, Object> sav = (Map<String, Object>) av.get("sav");
        cell(t, "सर्व");
        if (sav != null) {
            List<Integer> bySign = (List<Integer>) sav.get("bySign");
            for (int n : bySign) cell(t, String.valueOf(n));
            cell(t, String.valueOf(sav.get("total")));
        }
        doc.add(t);
    }

    @SuppressWarnings("unchecked")
    private void chapterShadbala(Document doc, Map<String, Object> sb) throws Exception {
        h(doc, "६  षड्बल / भावबल  /  Shadbala");
        hi(doc, str(sb.get("noteHi")), 8, INK, false);
        PdfPTable t = new PdfPTable(new float[]{1.4f, 1, 1, 1, 1, 1, 1, 1.2f, 0.9f, 1});
        t.setWidthPercentage(100);
        header(t, "ग्रह", "स्थान", "दिक्", "काल", "चेष्टा", "नैसर्गिक", "दृक्", "योग", "रूप", "न्यून");
        Map<String, Object> grahas = (Map<String, Object>) sb.get("grahas");
        if (grahas != null) {
            for (String g : Shadbala.GRAHAS) {
                Map<String, Object> row = (Map<String, Object>) grahas.get(g);
                cell(t, VedicConstants.planetHi(g));
                cell(t, str(row.get("sthana")));
                cell(t, str(row.get("dig")));
                cell(t, str(row.get("kala")));
                cell(t, str(row.get("chesta")));
                cell(t, str(row.get("naisargika")));
                cell(t, str(row.get("drik")));
                cell(t, str(row.get("totalVirupa")));
                cell(t, str(row.get("rupa")));
                cell(t, str(row.get("requiredVirupa")));
            }
        }
        doc.add(t);
        gap(doc, 4);
        hi(doc, "भावबल", 10, MAROON, true);
        PdfPTable bt = table(6);
        header(bt, "भाव", "राशि", "स्वामी", "अधिपति", "दिक्", "योग");
        List<Map<String, Object>> bhavas = (List<Map<String, Object>>) sb.get("bhavas");
        if (bhavas != null) {
            for (Map<String, Object> b : bhavas) {
                cell(bt, str(b.get("house")));
                cell(bt, str(b.get("signHi")));
                cell(bt, str(b.get("lordHi")));
                cell(bt, str(b.get("adhipati")));
                cell(bt, str(b.get("dig")));
                cell(bt, str(b.get("totalVirupa")));
            }
        }
        doc.add(bt);
    }

    @SuppressWarnings("unchecked")
    private void chapterInterpret(Document doc, Map<String, Object> reading, Map<String, Object> yogas,
                                  Map<String, Object> gems) throws Exception {
        h(doc, "७  परम्परा पाठ  /  Reading");
        hi(doc, str(reading.get("summaryHi")), 9, INK, false);
        pEn(doc, str(reading.get("summary")), 8, INK);
        hi(doc, str(reading.get("lagnaEssayHi")), 9, INK, false);
        hi(doc, str(reading.get("livelihoodHi")), 9, INK, false);
        Map<String, Object> mer = (Map<String, Object>) reading.get("mercury");
        Map<String, Object> sat = (Map<String, Object>) reading.get("saturn");
        Map<String, Object> maha = (Map<String, Object>) reading.get("mahaByHouse");
        if (mer != null) hi(doc, str(mer.get("readingHi")), 9, INK, false);
        if (sat != null) hi(doc, str(sat.get("readingHi")), 9, INK, false);
        if (maha != null) hi(doc, str(maha.get("readingHi")), 9, INK, false);
        gap(doc, 6);
        hi(doc, "विशेष योग", 11, MAROON, true);
        List<Map<String, Object>> ys = (List<Map<String, Object>>) yogas.get("yogas");
        if (ys != null) {
            for (Map<String, Object> y : ys) {
                pEn(doc, "• " + y.get("name") + " — " + y.get("text"), 8, INK);
            }
        }
        gap(doc, 6);
        hi(doc, "रत्न (सलाह, विक्रय नहीं)", 11, MAROON, true);
        kvLine(doc, "लग्नेश रत्न", str(gems.get("lifeStone")));
        kvLine(doc, "निर्बल ग्रह", str(gems.get("weakestPlanet")) + "  " + str(gems.get("luckyStone")));
        pEn(doc, str(gems.get("warning")), 8, MAROON);
        gap(doc, 10);
        hi(doc, "टिप्पणी: सिद्धान्तिक अंग मूल KSDSU जुलाई २०१६ / २०२२ स्वर्ण से बँधे हैं। कार्तिक–माघ तिथि ४५–१४० मिनट आगे हो सकती है — बीज न बदलें। दृक् = Swiss Ephemeris।",
                8, INK, false);
    }

    /* ——— chrome & helpers ——— */

    private static class Chrome extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter w, Document doc) {
            PdfContentByte cb = w.getDirectContent();
            float pw = doc.getPageSize().getWidth();
            cb.setColorFill(NAVY);
            cb.rectangle(0, doc.getPageSize().getHeight() - 26, pw, 26);
            cb.fill();
            cb.setColorFill(GOLD);
            cb.rectangle(0, doc.getPageSize().getHeight() - 28, pw, 2.2f);
            cb.fill();
            cb.setColorFill(NAVY);
            cb.rectangle(0, 0, pw, 22);
            cb.fill();
            cb.setColorFill(GOLD);
            cb.setFontAndSize(LATIN, 8);
            cb.beginText();
            cb.showTextAligned(Element.ALIGN_LEFT, "Makaranda Jyotish  ·  Mithilanchal", 40, 8, 0);
            cb.showTextAligned(Element.ALIGN_RIGHT, String.valueOf(w.getPageNumber()), pw - 40, 8, 0);
            cb.endText();
        }
    }

    private static BaseFont loadFont(String cp) {
        try (InputStream in = PdfReportService.class.getResourceAsStream(cp)) {
            if (in == null) throw new IllegalStateException("missing font " + cp);
            byte[] bytes = in.readAllBytes();
            return BaseFont.createFont("font.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, bytes, null);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Font f(float size, Color c) { return new Font(LATIN, size, Font.NORMAL, c); }
    private Font fB(float size, Color c) { return new Font(LATIN_B, size, Font.NORMAL, c); }

    private void h(Document doc, String title) throws Exception {
        gap(doc, 12);
        PdfPTable bar = new PdfPTable(1);
        bar.setWidthPercentage(100);
        PdfPCell c = new PdfPCell();
        c.setBackgroundColor(MAROON);
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(8);
        c.addElement(DevanagariPaint.blockOn(title, 12, GOLD, true, 500, MAROON));
        bar.addCell(c);
        doc.add(bar);
        gap(doc, 6);
    }

    private void hi(Document doc, String text, float pt, Color color, boolean bold) throws Exception {
        if (text == null || text.isBlank()) return;
        Image img = DevanagariPaint.block(text, pt, color, bold, 500);
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingAfter(3);
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(1);
        c.addElement(img);
        t.addCell(c);
        doc.add(t);
    }

    private void pEn(Document doc, String text, float size, Color c) throws Exception {
        Paragraph para = new Paragraph(text == null ? "" : text, f(size, c));
        para.setLeading(size * 1.35f);
        para.setSpacingAfter(3);
        doc.add(para);
    }

    private void kvLine(Document doc, String k, String v) throws Exception {
        hi(doc, k + "  " + (v == null ? "—" : v), 9, INK, false);
    }

    private void kvCell(PdfPTable t, String k, String v) {
        PdfPCell a = paintCell(k, 8, GOLD, true);
        a.setBackgroundColor(NAVY);
        t.addCell(a);
        PdfPCell b = paintCell(v == null ? "—" : v, 8, INK, false);
        b.setBackgroundColor(CREAM);
        t.addCell(b);
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
        t.setSpacingAfter(6);
        t.getDefaultCell().setBorderColor(new Color(232, 197, 71, 80));
        return t;
    }

    private void header(PdfPTable t, String... cols) {
        for (String c : cols) {
            PdfPCell cell = paintCell(c, 8, Color.WHITE, true);
            cell.setBackgroundColor(MAROON);
            t.addCell(cell);
        }
    }

    private void cell(PdfPTable t, String v) {
        PdfPCell cell = paintCell(v == null ? "" : v, 8, INK, false);
        cell.setBackgroundColor((t.getRows().size() % 2 == 0) ? CREAM : ROW_ALT);
        t.addCell(cell);
    }

    private void avCell(PdfPTable t, String k, Object v) {
        PdfPCell a = paintCell(k, 7, GOLD, true);
        a.setBackgroundColor(NAVY);
        t.addCell(a);
        PdfPCell b = paintCell(str(v), 8, INK, false);
        b.setBackgroundColor(CREAM);
        t.addCell(b);
    }

    private PdfPCell paintCell(String v, float pt, Color color, boolean bold) {
        String s = v == null ? "" : v;
        PdfPCell cell;
        if (DevanagariPaint.hasDevanagari(s)) {
            cell = new PdfPCell();
            cell.addElement(DevanagariPaint.block(s, pt, color, bold, 220));
        } else {
            cell = new PdfPCell(new Phrase(s, bold ? fB(pt, color) : f(pt, color)));
        }
        cell.setBorderColor(new Color(201, 162, 39, 60));
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private static String statusHi(String st) {
        return switch (st) {
            case "present" -> "जन्म में है";
            case "active" -> "अभी सक्रिय";
            case "cancelled" -> "निरस्त";
            default -> "नहीं है";
        };
    }

    private static String str(Object o) { return o == null ? "—" : String.valueOf(o); }
    private static String str(Object a, Object b) { return a != null ? String.valueOf(a) : str(b); }
    private static String blank(String s) { return s == null || s.isBlank() ? "—" : s; }
    private static String join(Object a, Object b) { return str(a) + " " + str(b); }
    private static String till(Object end) {
        return end == null ? "" : "  ·  तक  " + end;
    }
}
