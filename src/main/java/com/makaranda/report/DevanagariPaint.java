package com.makaranda.report;

import com.lowagie.text.Image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * OpenPDF/iText does not run GSUB/GPOS, so raw TTF Devanagari prints broken matras.
 * JDK TextLayout uses HarfBuzz — we rasterise Hindi at 2× and embed PNG.
 */
public final class DevanagariPaint {
    private static final Font BASE;
    private static final Font BASE_B;
    private static final float DPI = 192f;

    static {
        BASE = load("/fonts/NotoSansDevanagari-Regular.ttf");
        BASE_B = load("/fonts/NotoSansDevanagari-Bold.ttf");
    }

    private DevanagariPaint() {}

    public static Font awtFont(boolean bold) {
        return bold ? BASE_B : BASE;
    }

    public static boolean hasDevanagari(String s) {
        if (s == null) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0x0900 && c <= 0x097F) return true;
        }
        return false;
    }

    public static Image line(String text, float pt, Color color, boolean bold) {
        return wrap(render(text == null ? "" : text, pt, color, bold, 900, null), pt);
    }

    public static Image block(String text, float pt, Color color, boolean bold, float maxPtWidth) {
        return wrap(render(text == null ? "" : text, pt, color, bold, maxPtWidth, null), pt);
    }

    public static Image blockOn(String text, float pt, Color color, boolean bold, float maxPtWidth, Color bg) {
        return wrap(render(text == null ? "" : text, pt, color, bold, maxPtWidth, bg), pt);
    }

    private static Image wrap(BufferedImage bi, float pt) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", bos);
            Image img = Image.getInstance(bos.toByteArray());
            img.scalePercent(72f / DPI * 100f);
            img.setAlignment(Image.ALIGN_LEFT);
            img.setSpacingAfter(0);
            return img;
        } catch (Exception e) {
            throw new IllegalStateException("Hindi paint failed", e);
        }
    }

    private static BufferedImage render(String text, float pt, Color color, boolean bold,
                                        float maxPtWidth, Color bg) {
        Font font = (bold ? BASE_B : BASE).deriveFont(pt * (DPI / 72f));
        float maxPx = Math.max(24, maxPtWidth * (DPI / 72f));
        FontRenderContext frc = new FontRenderContext(null, true, true);
        AttributedString as = new AttributedString(text.isEmpty() ? " " : text);
        as.addAttribute(TextAttribute.FONT, font);
        as.addAttribute(TextAttribute.FOREGROUND, color);
        AttributedCharacterIterator it = as.getIterator();
        LineBreakMeasurer measurer = new LineBreakMeasurer(it, frc);
        List<TextLayout> lines = new ArrayList<>();
        float width = 1, height = 4;
        int limit = it.getEndIndex();
        while (measurer.getPosition() < limit) {
            TextLayout layout = measurer.nextLayout(maxPx);
            lines.add(layout);
            width = Math.max(width, layout.getAdvance());
            height += layout.getAscent() + layout.getDescent() + layout.getLeading() + 2;
        }
        int w = Math.max(2, (int) Math.ceil(width + 8));
        int h = Math.max(2, (int) Math.ceil(height + 4));
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        if (bg != null) {
            g.setColor(bg);
            g.fillRect(0, 0, w, h);
        }
        float y = 2;
        for (TextLayout layout : lines) {
            y += layout.getAscent();
            layout.draw(g, 4, y);
            y += layout.getDescent() + layout.getLeading() + 2;
        }
        g.dispose();
        return bi;
    }

    private static Font load(String cp) {
        try (InputStream in = DevanagariPaint.class.getResourceAsStream(cp)) {
            if (in == null) throw new IllegalStateException("missing " + cp);
            return Font.createFont(Font.TRUETYPE_FONT, in);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
