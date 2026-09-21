package com.makaranda.calc.ephemeris;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.panchang.PanchangCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Chunk 2: superior-planet śīghra epicycle. Sun/Moon bijas and July gold frozen.
 */
class GrahaSighraOuterTest {

    private static final double KSDS_LON = 85.268;

    @Test
    void outerSighraPhalaIsSmallNotTheKendra() {
        // 90° kendra: old formula → ~84° for Saturn; SS epicycle → ~6°.
        double sat = MakarandaSpashta.sighra(0, 90, 9.5);
        double jup = MakarandaSpashta.sighra(0, 90, 5.2);
        double mar = MakarandaSpashta.sighra(0, 90, 1.524);
        assertEquals(6.01, sat, 0.2);
        assertEquals(10.87, jup, 0.3);
        assertEquals(33.25, mar, 0.5);
    }

    @Test
    void inferiorSighraUnchangedAtQuadrature() {
        // k = 0.387, θ = 90° → atan2(0.387, 1) ≈ 21.16°.
        double mer = MakarandaSpashta.sighra(0, 90, 0.387);
        assertEquals(21.16, mer, 0.2);
    }

    @Test
    void saturnDoesNotFollowTheSunWhenElongated() {
        // 20 May 2023 / 4 Jan 2026: Swiss Saturn is ~80° from the Sun.
        // 6 Feb 2023 Saturn was actually near the Sun — do not use that date here.
        assertSaturnOffSun(2023, 5, 20);
        assertSaturnOffSun(2026, 1, 4);
    }

    @Test
    void saturnNearDrikNotWrongRashi() {
        assertSaturnVsDrik(2023, 2, 6, 12);
        assertSaturnVsDrik(2023, 5, 20, 12);
        assertSaturnVsDrik(2026, 1, 4, 12);
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

    private static void assertSaturnOffSun(int y, int m, int d) {
        double jd = AstroMath.julianDayUt(y, m, d, 6, 30, 0, 5.5);
        MakarandaSpashta.Bodies g = MakarandaSpashta.at(jd, KSDS_LON);
        double fromSun = Math.abs(AstroMath.norm180(g.saturn() - g.sun()));
        assertTrue(fromSun > 20,
                y + "-" + m + "-" + d + " saturn " + g.saturn() + " sun " + g.sun() + " Δ=" + fromSun);
    }

    private static void assertSaturnVsDrik(int y, int m, int d, double maxDeg) {
        double jd = AstroMath.julianDayUt(y, m, d, 6, 30, 0, 5.5);
        MakarandaSpashta.Bodies g = MakarandaSpashta.at(jd, KSDS_LON);
        EphemerisEngine eng = new EphemerisEngine();
        double ay = AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA.ayanamsa(jd);
        double drikTrop = eng.compute(jd, PanchangMode.DRIK, KSDS_LON).get("Saturn").lon();
        double drikSid = AstroMath.norm360(drikTrop - ay);
        double delta = Math.abs(AstroMath.norm180(g.saturn() - drikSid));
        assertTrue(delta <= maxDeg,
                y + "-" + m + "-" + d + " SIDD " + g.saturn() + " DRIK " + drikSid + " Δ=" + delta);
    }
}
