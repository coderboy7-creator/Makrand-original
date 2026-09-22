package com.makaranda.calc.ephemeris;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.panchang.PanchangCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Chunk 5: SS paridhi + mandocca. Sun/Moon and July gold frozen. */
class GrahaSighraFullTest {

    private static final double KSDS = 85.268;

    @Test
    void inferiorMandoccaIsNotSighrocca() {
        assertEquals(220.0 + 28.0 / 60.0, MakarandaSpashta.MER_MANDOCCA, 1e-6);
        assertEquals(79.0 + 50.0 / 60.0, MakarandaSpashta.VEN_MANDOCCA, 1e-6);
        assertEquals(133.0, MakarandaSpashta.MER_SIGHRA_PARIDHI, 0);
        assertEquals(262.0, MakarandaSpashta.VEN_SIGHRA_PARIDHI, 0);
    }

    @Test
    void saturnStillKumbhaOnMay2023() {
        double jd = AstroMath.julianDayUt(2023, 5, 20, 6, 30, 0, 5.5);
        double sat = MakarandaSpashta.at(jd, KSDS).saturn();
        assertTrue(sat >= 300 && sat < 330, "saturn " + sat);
    }

    @Test
    void ketuStillWithDrikNotSwapped() {
        double jd = AstroMath.julianDayUt(2026, 1, 4, 6, 30, 0, 5.5);
        double ay = AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA.ayanamsa(jd);
        EphemerisEngine eng = new EphemerisEngine();
        double s = AstroMath.norm360(eng.compute(jd, PanchangMode.SIDDHANTIC, KSDS).get("Ketu").lon() - ay);
        double d = AstroMath.norm360(eng.compute(jd, PanchangMode.DRIK, KSDS).get("Ketu").lon() - ay);
        assertTrue(Math.abs(AstroMath.norm180(s - d)) <= 8, "Ketu SIDD " + s + " DRIK " + d);
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
}
