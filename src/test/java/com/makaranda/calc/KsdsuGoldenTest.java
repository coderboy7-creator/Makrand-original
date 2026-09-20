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
 * Original KSDSU विश्वविद्यालय पञ्चांग fixtures. July 2016/2022 must stay tight.
 * Kartika/Magha 2016–17 and 2022–23 photos are gold for identity + residual
 * floors. Do not retune mean bijas here: winter tithi is 45–140 min early while
 * July is 2–20 min; a constant moon bija cannot fit both.
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

    @Test
    void kartika2016ShuklaPratipada() {
        Map<String, Object> p = panji("2016-10-31");
        assertEquals("Shukla", p.get("paksha"));
        assertEquals(1, p.get("tithiNumber"));
        assertEquals("Swati", p.get("nakshatra"));
        // Book रा. 12:01 (1 Nov); app ~10:13 PM 31 Oct.
        assertTrue(!end(p, "tithiLimb").isBefore(LocalDateTime.of(2016, 10, 31, 22, 0)),
                "Kartika 2016 T1 slipped: " + p.get("tithiEnd"));
        assertTrue(absMin(end(p, "nakshatraLimb"), LocalDateTime.of(2016, 10, 31, 11, 45)) <= 45,
                "nak " + p.get("nakshatraEnd"));
        assertSrSs("2016-10-31", "6:28 AM", "5:32 PM", 25);
    }

    @Test
    void magha2017KrishnaPratipada() {
        Map<String, Object> p = panji("2017-01-13");
        assertEquals("Krishna", p.get("paksha"));
        assertEquals(16, p.get("tithiNumber"));
        assertEquals("Pushya", p.get("nakshatra"));
        // Book दि. 4:06; app ~3:04 PM.
        assertTrue(!end(p, "tithiLimb").isBefore(LocalDateTime.of(2017, 1, 13, 14, 50)),
                "Magha 2017 T1 slipped: " + p.get("tithiEnd"));
        assertTrue(absMin(end(p, "nakshatraLimb"), LocalDateTime.of(2017, 1, 14, 1, 45)) <= 40,
                "nak " + p.get("nakshatraEnd"));
        assertSrSs("2017-01-13", "6:45 AM", "5:35 PM", 12);
    }

    @Test
    void kartika2022ShuklaAshtami() {
        Map<String, Object> p = panji("2022-11-01");
        assertEquals("Shukla", p.get("paksha"));
        assertEquals(8, p.get("tithiNumber"));
        assertEquals("Uttara Ashadha", p.get("nakshatra"));
        // Book रा. 1:20 (2 Nov); app ~12:33 AM. Nak book दि. 7:27 is already close.
        assertTrue(!end(p, "tithiLimb").isBefore(LocalDateTime.of(2022, 11, 2, 0, 20)),
                "Kartika 2022 T8 slipped: " + p.get("tithiEnd"));
        assertTrue(absMin(end(p, "nakshatraLimb"), LocalDateTime.of(2022, 11, 1, 7, 27)) <= 15,
                "nak " + p.get("nakshatraEnd"));
        assertSrSs("2022-11-01", "6:29 AM", "5:31 PM", 25);
    }

    @Test
    void pausha2023ShuklaDashami() {
        Map<String, Object> p = panji("2023-01-01");
        assertEquals(10, p.get("tithiNumber"));
        assertEquals("Ashwini", p.get("nakshatra"));
        // Book रा. 10:28; app ~8:52 PM. Nak book दि. 4:38 is essentially exact.
        assertTrue(!end(p, "tithiLimb").isBefore(LocalDateTime.of(2023, 1, 1, 20, 40)),
                "Pausha 2023 T10 slipped: " + p.get("tithiEnd"));
        assertTrue(absMin(end(p, "nakshatraLimb"), LocalDateTime.of(2023, 1, 1, 16, 38)) <= 10,
                "nak " + p.get("nakshatraEnd"));
        assertSrSs("2023-01-01", "6:49 AM", "5:22 PM", 12);
    }

    @Test
    void magha2023KrishnaTritiya() {
        Map<String, Object> p = panji("2023-01-10");
        assertEquals("Krishna", p.get("paksha"));
        assertEquals(18, p.get("tithiNumber"));
        assertEquals("Ashlesha", p.get("nakshatra"));
        // Book दि. 9:48; app ~8:16 AM.
        assertTrue(!end(p, "tithiLimb").isBefore(LocalDateTime.of(2023, 1, 10, 8, 0)),
                "Magha 2023 T3 slipped: " + p.get("tithiEnd"));
        assertTrue(absMin(end(p, "nakshatraLimb"), LocalDateTime.of(2023, 1, 10, 7, 22)) <= 20,
                "nak " + p.get("nakshatraEnd"));
        assertSrSs("2023-01-10", "6:46 AM", "5:36 PM", 12);
    }
}
