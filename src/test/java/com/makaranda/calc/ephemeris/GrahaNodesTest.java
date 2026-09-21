package com.makaranda.calc.ephemeris;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.panchang.PanchangCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Chunk 3: SIDDHANTIC Rāhu is the ascending node vs DRIK/panji Ketu gold.
 * Phālguna/Chaitra 2023 printed Ketu is garbage — not used here.
 */
class GrahaNodesTest {

    private static final double KSDS_LON = 85.268;

    @Test
    void rahuKetuStayOpposite() {
        double jd = AstroMath.julianDayUt(2026, 1, 4, 6, 30, 0, 5.5);
        assertEquals(180.0, MakarandaSpashta.RAHU_MEAN_OFFSET, 0);
        EphemerisEngine eng = new EphemerisEngine();
        Map<String, EphemerisEngine.GeoPos> s = eng.compute(jd, PanchangMode.SIDDHANTIC, KSDS_LON);
        assertEquals(180.0, Math.abs(AstroMath.norm180(s.get("Ketu").lon() - s.get("Rahu").lon())), 1e-6);
    }

    @Test
    void ketuMatchesDrikOnWinterGold() {
        assertNodeVsDrik(2025, 10, 8, 8);
        assertNodeVsDrik(2025, 12, 5, 8);
        assertNodeVsDrik(2026, 1, 4, 8);
        // Āṣāḍha 2023 Ketu vs Swiss was good even though Guru/Śani pages are excluded.
        assertNodeVsDrik(2023, 6, 5, 8);
    }

    @Test
    void saturnStillKumbhaAfterNodeFix() {
        double jd = AstroMath.julianDayUt(2023, 5, 20, 6, 30, 0, 5.5);
        MakarandaSpashta.Bodies g = MakarandaSpashta.at(jd, KSDS_LON);
        assertTrue(g.saturn() >= 300 && g.saturn() < 330, "saturn " + g.saturn());
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

    private static void assertNodeVsDrik(int y, int m, int d, double maxDeg) {
        double jd = AstroMath.julianDayUt(y, m, d, 6, 30, 0, 5.5);
        EphemerisEngine eng = new EphemerisEngine();
        double ay = AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA.ayanamsa(jd);
        var sidd = eng.compute(jd, PanchangMode.SIDDHANTIC, KSDS_LON);
        var drik = eng.compute(jd, PanchangMode.DRIK, KSDS_LON);
        double sKetu = AstroMath.norm360(sidd.get("Ketu").lon() - ay);
        double dKetu = AstroMath.norm360(drik.get("Ketu").lon() - ay);
        double sRahu = AstroMath.norm360(sidd.get("Rahu").lon() - ay);
        double dRahu = AstroMath.norm360(drik.get("Rahu").lon() - ay);
        double dK = Math.abs(AstroMath.norm180(sKetu - dKetu));
        double dR = Math.abs(AstroMath.norm180(sRahu - dRahu));
        assertTrue(dK <= maxDeg, y + "-" + m + "-" + d + " Ketu SIDD " + sKetu + " DRIK " + dKetu + " Δ=" + dK);
        assertTrue(dR <= maxDeg, y + "-" + m + "-" + d + " Rahu SIDD " + sRahu + " DRIK " + dRahu + " Δ=" + dR);
    }
}
