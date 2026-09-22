package com.makaranda.calc.ephemeris;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.panchang.PanchangCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SS four-step spaṣṭa. Sun/Moon and July gold frozen. No bija.
 * 6 Feb 2023 Mangal must share DRIK rāśi (Chunk 2 leftover).
 */
class GrahaSsFourStepTest {

    private static final double KSDS = 85.268;

    @Test
    void marsFeb2023SameRashiAsDrik() {
        assertMarsVsDrik(2023, 2, 6, 5);
        assertMarsVsDrik(2023, 5, 20, 5);
    }

    @Test
    void saturnStillNearDrik() {
        assertSatVsDrik(2023, 2, 6, 12);
        assertSatVsDrik(2023, 5, 20, 12);
        assertSatVsDrik(2026, 1, 4, 12);
    }

    @Test
    void july2022SunriseStillWithinFourMinutes() {
        Map<String, Object> p = new PanchangCalculator().compute(
                LocalDate.parse("2022-07-29"),
                PanchangCalculator.KSDS_AKSHANSH_DEG, PanchangCalculator.KSDS_LON, 5.5,
                AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, PanchangMode.SIDDHANTIC);
        int sr = PanchangCalculator.toMin(String.valueOf(p.get("sunrise")));
        int ss = PanchangCalculator.toMin(String.valueOf(p.get("sunset")));
        assertTrue(Math.abs(sr - PanchangCalculator.toMin("5:20 AM")) <= 4, "SR " + p.get("sunrise"));
        assertTrue(Math.abs(ss - PanchangCalculator.toMin("6:50 PM")) <= 4, "SS " + p.get("sunset"));
    }

    private static void assertMarsVsDrik(int y, int m, int d, double maxDeg) {
        assertVsDrik("Mars", y, m, d, maxDeg, MakarandaSpashta.at(
                AstroMath.julianDayUt(y, m, d, 6, 30, 0, 5.5), KSDS).mars());
    }

    private static void assertSatVsDrik(int y, int m, int d, double maxDeg) {
        assertVsDrik("Saturn", y, m, d, maxDeg, MakarandaSpashta.at(
                AstroMath.julianDayUt(y, m, d, 6, 30, 0, 5.5), KSDS).saturn());
    }

    private static void assertVsDrik(String graha, int y, int m, int d, double maxDeg, double sidd) {
        double jd = AstroMath.julianDayUt(y, m, d, 6, 30, 0, 5.5);
        double ay = AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA.ayanamsa(jd);
        double drik = AstroMath.norm360(new EphemerisEngine()
                .compute(jd, PanchangMode.DRIK, KSDS).get(graha).lon() - ay);
        double delta = Math.abs(AstroMath.norm180(sidd - drik));
        assertTrue(delta <= maxDeg,
                y + "-" + m + "-" + d + " " + graha + " SIDD " + sidd + " DRIK " + drik + " Δ=" + delta);
    }
}
