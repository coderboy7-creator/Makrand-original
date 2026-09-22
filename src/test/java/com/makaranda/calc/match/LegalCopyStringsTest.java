package com.makaranda.calc.match;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Copy lock: gems/milan must not read as a clinic, shop, or marriage licence. */
class LegalCopyStringsTest {

    @Test
    void matcherDisclaimerIsInSource() throws Exception {
        String java = Files.readString(Path.of("src/main/java/com/makaranda/calc/match/AshtakootaMatcher.java"));
        assertTrue(java.contains("not a marriage licence"));
        assertTrue(java.contains("not a medical or genetic test"));
    }

    @Test
    void gemDisclaimerIsInSource() throws Exception {
        String java = Files.readString(Path.of("src/main/java/com/makaranda/calc/vedic/SpecialCharts.java"));
        assertTrue(java.contains("We do not sell gems or rudraksha"));
        assertTrue(java.contains("Not a medical prescription"));
    }

    @Test
    void i18nHasLegalKeys() throws Exception {
        String ts = Files.readString(Path.of("frontend/src/i18n.tsx"));
        assertTrue(ts.contains("milan_legal"));
        assertTrue(ts.contains("gems_legal"));
        assertTrue(ts.contains("not a marriage licence"));
        assertTrue(ts.contains("We do not sell gems or rudraksha"));
    }
}
