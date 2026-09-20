package com.makaranda.calc;

import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.PanchangMode;
import com.makaranda.calc.panchang.PanchangCalculator;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Original KSDSU fixtures must never regress. Winter/Oct printed rows are
 * recorded as residual floors (do not get earlier) until an original
 * Magha/Pausha KSDSU page is supplied — those clocks may be Mithila, not KSDSU.
 */
class KsdsuGoldenTest {
    private static final double LAT = PanchangCalculator.KSDS_AKSHANSH_DEG;
    private static final double LON = PanchangCalculator.KSDS_LON;

    private Map<String, Object> panji(String date) {
        return new PanchangCalculator().compute(
                LocalDate.parse(date), LAT, LON, 5.5,
                AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, PanchangMode.SIDDHANTIC);
    }

    private static LocalDateTime end(Map<String, Object> p, String limbKey) {
        return LocalDateTime.parse(String.valueOf(((Map<?, ?>) p.get(limbKey)).get("endIso")));
    }

    private static long absMin(LocalDateTime a, LocalDateTime b) {
        return Math.abs(Duration.between(a, b).toMinutes());
    }

    private static int clockMin(String ampm) {
        return PanchangCalculator.toMin(ampm);
    }

    private void assertSrSs(String date, String rise, String set, int tolMin) {
        Map<String, Object> p = panji(date);
        int dr = Math.abs(clockMin(String.valueOf(p.get("sunrise"))) - clockMin(rise));
        int ds = Math.abs(clockMin(String.valueOf(p.get("sunset"))) - clockMin(set));
        assertTrue(dr <= tolMin, date + " SR " + p.get("sunrise") + " want " + rise);
        assertTrue(ds <= tolMin, date + " SS " + p.get("sunset") + " want " + set);
    }

    @Test
    void july2016KrishnaPratipada() {
        Map<String, Object> p = panji("2016-07-20");
        assertEquals("Krishna", p.get("paksha"));
        assertEquals(16, p.get("tithiNumber"));
        assertEquals("Uttara Ashadha", p.get("nakshatra"));
        assertEquals("Vishkambha", p.get("yoga"));
        assertTrue(absMin(end(p, "tithiLimb"), LocalDateTime.of(2016, 7, 21, 4, 3)) <= 12,
                "tithi " + p.get("tithiEnd"));
        assertSrSs("2016-07-20", "5:16 AM", "6:44 PM", 12);
    }

    @Test
    void july2016KrishnaPanchami() {
        Map<String, Object> p = panji("2016-07-24");
        assertEquals(20, p.get("tithiNumber"));
        assertEquals("Purva Bhadrapada", p.get("nakshatra"));
        assertTrue(absMin(end(p, "tithiLimb"), LocalDateTime.of(2016, 7, 24, 22, 16)) <= 8,
                "tithi " + p.get("tithiEnd"));
        assertTrue(absMin(end(p, "nakshatraLimb"), LocalDateTime.of(2016, 7, 24, 15, 4)) <= 15,
                "nak " + p.get("nakshatraEnd"));
        assertSrSs("2016-07-24", "5:18 AM", "6:42 PM", 12);
    }

    @Test
    void july2022ShuklaPratipada() {
        Map<String, Object> p = panji("2022-07-29");
        assertEquals("Shukla", p.get("paksha"));
        assertEquals(1, p.get("tithiNumber"));
        assertEquals("Pushya", p.get("nakshatra"));
        assertTrue(absMin(end(p, "tithiLimb"), LocalDateTime.of(2022, 7, 30, 0, 4)) <= 15,
                "tithi " + p.get("tithiEnd"));
        assertTrue(absMin(end(p, "nakshatraLimb"), LocalDateTime.of(2022, 7, 29, 9, 53)) <= 8,
                "nak " + p.get("nakshatraEnd"));
        assertSrSs("2022-07-29", "5:20 AM", "6:50 PM", 4);
    }

    @Test
    void august2022PurnimaMustNotSlip() {
        Map<String, Object> p = panji("2022-08-12");
        assertEquals(15, p.get("tithiNumber"));
        // Book दि. 7:27; app ~7:47. Constant bijas cannot close this without moving 29 Jul T1.
        assertTrue(absMin(end(p, "tithiLimb"), LocalDateTime.of(2022, 8, 12, 7, 27)) <= 22,
                "purnima " + p.get("tithiEnd"));
        assertSrSs("2022-08-12", "5:28 AM", "6:42 PM", 4);
    }

    @Test
    void july2025PratipadaStaysNearBook() {
        Map<String, Object> p = panji("2025-07-11");
        assertEquals(16, p.get("tithiNumber"));
        assertEquals("Purva Ashadha", p.get("nakshatra"));
        assertTrue(absMin(end(p, "tithiLimb"), LocalDateTime.of(2025, 7, 12, 2, 8)) <= 25,
                "tithi " + p.get("tithiEnd"));
        assertSrSs("2025-07-11", "5:14 AM", "6:46 PM", 12);
    }

    /**
     * Winter/Oct printed times (owner 8-row). App is early. Lock a floor so a
     * future fit cannot silently make them worse; do not require book match yet.
     */
    @Test
    void winterResidualMustNotGetEarlier() {
        Map<String, Object> oct = panji("2025-10-05");
        assertEquals(13, oct.get("tithiNumber"));
        assertEquals("Shatabhisha", oct.get("nakshatra"));
        assertTrue(!end(oct, "tithiLimb").isBefore(LocalDateTime.of(2025, 10, 5, 11, 40)),
                "Oct tithi slipped earlier: " + oct.get("tithiEnd"));

        Map<String, Object> jan = panji("2026-01-10");
        assertEquals(22, jan.get("tithiNumber"));
        assertEquals("Hasta", jan.get("nakshatra"));
        assertTrue(!end(jan, "tithiLimb").isBefore(LocalDateTime.of(2026, 1, 10, 8, 50)),
                "Jan tithi slipped earlier: " + jan.get("tithiEnd"));
    }
}
