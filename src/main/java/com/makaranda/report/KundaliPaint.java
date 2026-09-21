package com.makaranda.report;

import com.lowagie.text.Image;
import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;
import com.makaranda.calc.vedic.ChartBuilder.PlanetBody;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * North-Indian diamond kuṇḍalī as a HarfBuzz-shaped PNG.
 * Same geometry as the web chart; OpenPDF cannot stroke this as live text.
 */
public final class KundaliPaint {
    private static final Color NAVY = new Color(8, 6, 14);
    private static final Color GOLD = new Color(232, 197, 71);
    private static final Color CREAM = new Color(247, 241, 227);
    private static final Color RASHI = new Color(224, 122, 47);
    private static final float DPI = 160f;

    private static final String[] GRAHA_SHORT = {
            "सू", "चं", "मं", "बु", "गु", "शु", "शनि", "रा", "के"
    };
    private static final String[] RASHI_SHORT = {
            "मेष", "वृष", "मिथु", "कर्क", "सिंह", "कन्या",
            "तुला", "वृश्च", "धनु", "मकर", "कुम्भ", "मीन"
    };

    private KundaliPaint() {}

    public static Image d1(FullChart c, float pt) {
        return north(occupantsFromSign(c, c.lagna().signIndex(), true), pt);
    }

    public static Image fromOrigin(FullChart c, String graha, float pt) {
        PlanetBody o = c.planets().get(graha);
        return north(occupantsFromSign(c, o.signIndex(), false), pt);
    }

    public static Image chalit(FullChart sripati, float pt) {
        @SuppressWarnings("unchecked")
        List<String>[] houses = empty();
        houses[1].add("ल");
        for (PlanetBody p : sripati.planets().values()) {
            int h = p.house();
            if (h < 1 || h > 12) h = 1;
            houses[h].add(shortGraha(p.name()) + (p.retrograde() ? "व" : ""));
        }
        return north(new Occupancy(sripati.lagna().signIndex(), houses), pt);
    }

    @SuppressWarnings("unchecked")
    public static Image d9(FullChart c, float pt) {
        Map<String, Object> v = c.vargas() == null ? null : c.vargas().get(9);
        List<String>[] houses = empty();
        int origin = c.lagna().signIndex();
        if (v != null) {
            List<Map<String, Object>> bodies = (List<Map<String, Object>>) v.get("bodies");
            if (bodies != null) {
                for (Map<String, Object> b : bodies) {
                    if ("Lagna".equals(String.valueOf(b.get("name")))) {
                        origin = ((Number) b.get("signIndex")).intValue();
                        houses[1].add("ल");
                    }
                }
                for (Map<String, Object> b : bodies) {
                    String name = String.valueOf(b.get("name"));
                    if ("Lagna".equals(name)) continue;
                    int h = b.get("house") instanceof Number n ? n.intValue() : 1;
                    if (h < 1 || h > 12) h = 1;
                    houses[h].add(shortGraha(name));
                }
            }
        }
        return north(new Occupancy(origin, houses), pt);
    }

    @SuppressWarnings("unchecked")
    public static Image gochar(FullChart natal, Map<String, Object> gochar, float pt) {
        List<String>[] houses = empty();
        houses[1].add("ल");
        List<Map<String, Object>> planets = gochar == null ? null
                : (List<Map<String, Object>>) gochar.get("planets");
        if (planets != null) {
            for (Map<String, Object> p : planets) {
                int h = p.get("houseFromLagna") instanceof Number n ? n.intValue() : 1;
                if (h < 1 || h > 12) h = 1;
                boolean retro = Boolean.TRUE.equals(p.get("retrograde"));
                houses[h].add(shortGraha(String.valueOf(p.get("planet"))) + (retro ? "व" : ""));
            }
        }
        return north(new Occupancy(natal.lagna().signIndex(), houses), pt);
    }

    private record Occupancy(int originSign, List<String>[] houses) {}

    private static List<String>[] empty() {
        @SuppressWarnings("unchecked")
        List<String>[] houses = new List[13];
        for (int i = 1; i <= 12; i++) houses[i] = new ArrayList<>();
        return houses;
    }

    private static Occupancy occupantsFromSign(FullChart c, int originSign, boolean lagnaInHouse1) {
        List<String>[] houses = empty();
        if (lagnaInHouse1) houses[1].add("ल");
        else {
            int h = ((c.lagna().signIndex() - originSign + 12) % 12) + 1;
            houses[h].add("ल");
        }
        for (PlanetBody p : c.planets().values()) {
            int h = ((p.signIndex() - originSign + 12) % 12) + 1;
            houses[h].add(shortGraha(p.name()) + (p.retrograde() ? "व" : ""));
        }
        return new Occupancy(originSign, houses);
    }

    private static String shortGraha(String name) {
        for (int i = 0; i < VedicConstants.PLANETS.length; i++) {
            if (VedicConstants.PLANETS[i].equalsIgnoreCase(name)) return GRAHA_SHORT[i];
        }
        if ("Lagna".equalsIgnoreCase(name)) return "ल";
        return VedicConstants.planetHi(name);
    }

    private static Image north(Occupancy occ, float pt) {
        float scale = DPI / 72f;
        int s = Math.max(200, Math.round(pt * scale));
        BufferedImage bi = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        float m = s * 0.045f;
        g.setColor(NAVY);
        g.fill(new RoundRectangle2D.Float(m / 2, m / 2, s - m, s - m, s * 0.04f, s * 0.04f));
        g.setStroke(new BasicStroke(Math.max(1.6f, s * 0.007f)));
        g.setColor(GOLD);
        g.draw(new RoundRectangle2D.Float(m, m, s - 2 * m, s - 2 * m, s * 0.03f, s * 0.03f));
        g.setStroke(new BasicStroke(Math.max(1.1f, s * 0.0045f)));
        g.drawLine(Math.round(m), Math.round(m), Math.round(s - m), Math.round(s - m));
        g.drawLine(Math.round(s - m), Math.round(m), Math.round(m), Math.round(s - m));
        g.drawLine(s / 2, Math.round(m), Math.round(m), s / 2);
        g.drawLine(s / 2, Math.round(m), Math.round(s - m), s / 2);
        g.drawLine(s / 2, Math.round(s - m), Math.round(m), s / 2);
        g.drawLine(s / 2, Math.round(s - m), Math.round(s - m), s / 2);

        float[][] pos = {
                {0, 0},
                {s / 2f, s * 0.22f},
                {s * 0.28f, s * 0.12f},
                {s * 0.12f, s * 0.28f},
                {s * 0.22f, s / 2f},
                {s * 0.12f, s * 0.72f},
                {s * 0.28f, s * 0.88f},
                {s / 2f, s * 0.78f},
                {s * 0.72f, s * 0.88f},
                {s * 0.88f, s * 0.72f},
                {s * 0.78f, s / 2f},
                {s * 0.88f, s * 0.28f},
                {s * 0.72f, s * 0.12f},
        };
        float rashiPx = s * 0.032f;
        float grahaPx = s * 0.036f;
        Font rashiFont = DevanagariPaint.awtFont(true).deriveFont(rashiPx);
        Font grahaFont = DevanagariPaint.awtFont(true).deriveFont(grahaPx);
        for (int h = 1; h <= 12; h++) {
            int sign = (occ.originSign() + h - 1) % 12;
            float x = pos[h][0];
            float y = pos[h][1];
            g.setFont(rashiFont);
            g.setColor(RASHI);
            drawCentered(g, RASHI_SHORT[sign], x, y - s * 0.032f);
            String grahas = String.join(" ", occ.houses()[h]);
            if (!grahas.isBlank()) {
                g.setFont(grahaFont);
                g.setColor(CREAM);
                drawCentered(g, grahas, x, y + s * 0.012f);
            }
        }
        g.dispose();
        return toPdf(bi);
    }

    private static void drawCentered(Graphics2D g, String text, float x, float y) {
        var frc = g.getFontRenderContext();
        var layout = new java.awt.font.TextLayout(text, g.getFont(), frc);
        layout.draw(g, x - layout.getAdvance() / 2f, y + layout.getAscent() / 2f);
    }

    private static Image toPdf(BufferedImage bi) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", bos);
            Image img = Image.getInstance(bos.toByteArray());
            img.scalePercent(72f / DPI * 100f);
            img.setAlignment(Image.ALIGN_CENTER);
            return img;
        } catch (Exception e) {
            throw new IllegalStateException("kundali paint failed", e);
        }
    }
}
