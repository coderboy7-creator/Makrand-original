package com.makaranda.calc;

import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.PanchangMode;
import com.makaranda.calc.panchang.PanchangCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PanchangLimbTest {
    @Test
    void makarandaPanchangHasLimbTimings() {
        Map<String, Object> p = new PanchangCalculator().compute(
                LocalDate.of(2026, 8, 29), 26.1542, 85.8918, 5.5,
                AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, PanchangMode.SIDDHANTIC);
        assertEquals("SIDDHANTIC", p.get("mode"));
        assertTrue(String.valueOf(p.get("school")).contains("Makaranda"));
        assertNotNull(p.get("tithiEnd"));
        assertNotNull(p.get("nakshatraEnd"));
        assertNotNull(p.get("yogaEnd"));
        assertNotNull(p.get("karanaEnd"));
        assertNotNull(p.get("tithiHi"));
        assertNotNull(p.get("nakshatraHi"));
    }
}
