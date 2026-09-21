package com.makaranda.report;

import com.lowagie.text.Image;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DevanagariPaintTest {

    @Test
    void shapesHindiIntoPng() {
        assertTrue(DevanagariPaint.hasDevanagari("लग्न कुंडली"));
        Image img = DevanagariPaint.block("तिथि पूर्णिमा तक", 12, Color.BLACK, true, 400);
        assertTrue(img.getPlainWidth() > 10, "width " + img.getPlainWidth());
        assertTrue(img.getPlainHeight() > 8, "height " + img.getPlainHeight());
    }
}
