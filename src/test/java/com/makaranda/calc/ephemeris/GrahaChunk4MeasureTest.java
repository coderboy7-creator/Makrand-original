package com.makaranda.calc.ephemeris;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.panchang.PanchangCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Chunk 4: lock that Śani panji−SIDD is <em>not</em> a single constant
 * across 2023 vs 2026 gold rows. No bija in production code.
 */
class GrahaChunk4MeasureTest {

    private static final double KSDS = 85.268;

    @Test
    void saturnResidualDiffersByMoreThanSixTenthsAcrossEpochs() {
        // CSV gold (page_excluded=NO): 2023-05-20 saturn_deg=309.7642; 2026-01-04=329.3931
        double r2023 = panjiMinusSidd(2023, 5, 20, 309.7642);
        double r2026 = panjiMinusSidd(2026, 1, 4, 329.3931);
        assertTrue(r2023 < -7 && r2023 > -10, "2023 panji-SIDD " + r2023);
        assertTrue(r2026 < -9 && r2026 > -12, "2026 panji-SIDD " + r2026);
        assertTrue(Math.abs(r2026 - r2023) > 0.6,
                "epoch gap " + (r2026 - r2023) + " — a constant bija is not justified");
    }

    @Test
    void julySunriseUnchanged() {
        Map<String, Object> p = new PanchangCalculator().compute(
                LocalDate.parse("2022-07-29"),
                PanchangCalculator.KSDS_AKSHANSH_DEG, PanchangCalculator.KSDS_LON, 5.5,
                AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, PanchangMode.SIDDHANTIC);
        int sr = PanchangCalculator.toMin(String.valueOf(p.get("sunrise")));
        int ss = PanchangCalculator.toMin(String.valueOf(p.get("sunset")));
        assertTrue(Math.abs(sr - PanchangCalculator.toMin("5:20 AM")) <= 4, "SR " + p.get("sunrise"));
        assertTrue(Math.abs(ss - PanchangCalculator.toMin("6:50 PM")) <= 4, "SS " + p.get("sunset"));
    }

    private static double panjiMinusSidd(int y, int m, int d, double panji) {
        double jd = AstroMath.julianDayUt(y, m, d, 6, 30, 0, 5.5);
        double sat = MakarandaSpashta.at(jd, KSDS).saturn();
        return AstroMath.norm180(panji - sat);
    }
}
