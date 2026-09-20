package com.makaranda.calc;

import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.EphemerisEngine;
import com.makaranda.calc.ephemeris.PanchangMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AstroMathTest {
    @Test
    void julianDayJ2000() {
        // 2000-01-01 12:00 UT = 2451545.0
        double jd = AstroMath.julianDayUt(2000, 1, 1, 12, 0, 0, 0);
        assertEquals(2451545.0, jd, 1e-6);
    }

    @Test
    void sunAroundJ2000() {
        EphemerisEngine e = new EphemerisEngine();
        var pos = e.compute(2451545.0, PanchangMode.DRIK).get("Sun");
        // Apparent/geometric Sun at J2000 noon ~ 280.5° tropical
        assertTrue(pos.lon() > 270 && pos.lon() < 290, "Sun lon=" + pos.lon());
    }

    @Test
    void makarandaAyanamsaReasonable() {
        double ay = AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA.ayanamsa(2460676.5); // ~2025
        assertTrue(ay > 20 && ay < 26, "ayanamsa=" + ay);
    }

    @Test
    void norm360() {
        assertEquals(10.0, AstroMath.norm360(370), 1e-9);
        assertEquals(350.0, AstroMath.norm360(-10), 1e-9);
    }
}
