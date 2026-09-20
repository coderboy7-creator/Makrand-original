package com.makaranda.calc;

import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.PanchangMode;
import com.makaranda.calc.panchang.PanchangCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SunriseMithilaTest {
    private static final double LAT = PanchangCalculator.KSDS_AKSHANSH_DEG;
    private static final double LON = PanchangCalculator.KSDS_LON;

    private static int minutes(String clock) {
        return PanchangCalculator.toMin(clock);
    }

    private Map<String, Object> panji(String date) {
        return new PanchangCalculator().compute(
                LocalDate.parse(date), LAT, LON, 5.5,
                AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, PanchangMode.SIDDHANTIC);
    }

    private void assertNear(String date, String expRise, String expSet) {
        Map<String, Object> p = panji(date);
        int dr = Math.abs(minutes(String.valueOf(p.get("sunrise"))) - minutes(expRise));
        int ds = Math.abs(minutes(String.valueOf(p.get("sunset"))) - minutes(expSet));
        assertTrue(dr <= 4, date + " sunrise got " + p.get("sunrise") + " want " + expRise);
        assertTrue(ds <= 4, date + " sunset got " + p.get("sunset") + " want " + expSet);
    }

    @Test
    void ksdsu2022Sunrise() {
        // Original KSDSU 29-07-2022 to 12-08-2022, सू.उ. / सू.अ.
        assertNear("2022-07-29", "5:20 AM", "6:50 PM");
        assertNear("2022-08-12", "5:28 AM", "6:42 PM");
    }

    @Test
    void ksdsu2022ShuklaPakshaLimbs() {
        Map<String, Object> p = panji("2022-07-29");
        assertEquals("Shukla", p.get("paksha"));
        assertEquals(1, p.get("tithiNumber"));
        assertEquals("Pushya", p.get("nakshatra"));
        String tithiIso = String.valueOf(((Map<?, ?>) p.get("tithiLimb")).get("endIso"));
        LocalDateTime tEnd = LocalDateTime.parse(tithiIso);
        // Book: शुक्ल प्रतिपदा रा. १२।०४ = 30 Jul 2022 12:04 AM
        LocalDateTime wantT = LocalDateTime.of(2022, 7, 30, 0, 4);
        long tMin = Math.abs(java.time.Duration.between(tEnd, wantT).toMinutes());
        assertTrue(tMin <= 20, "tithi end " + tithiIso + " want 2022-07-30T00:04, Δ=" + tMin + " min");

        String nakIso = String.valueOf(((Map<?, ?>) p.get("nakshatraLimb")).get("endIso"));
        LocalDateTime nEnd = LocalDateTime.parse(nakIso);
        // Book: पुष्य दि. ०९।५३
        LocalDateTime wantN = LocalDateTime.of(2022, 7, 29, 9, 53);
        long nMin = Math.abs(java.time.Duration.between(nEnd, wantN).toMinutes());
        assertTrue(nMin <= 20, "nakshatra end " + nakIso + " want 2022-07-29T09:53, Δ=" + nMin + " min");
    }

    @Test
    void ksdsu2022Purnima() {
        Map<String, Object> p = panji("2022-08-12");
        assertEquals(15, p.get("tithiNumber"));
        String iso = String.valueOf(((Map<?, ?>) p.get("tithiLimb")).get("endIso"));
        LocalDateTime end = LocalDateTime.parse(iso);
        LocalDateTime want = LocalDateTime.of(2022, 8, 12, 7, 27);
        long min = Math.abs(java.time.Duration.between(end, want).toMinutes());
        assertTrue(min <= 25, "purnima end " + iso + " want 07:27, Δ=" + min + " min");
    }

    @Test
    void shravanaKrishna1_2016_tithiMatchesPanji() {
        Map<String, Object> p = panji("2016-07-20");
        assertEquals("Krishna", p.get("paksha"));
        assertEquals(16, p.get("tithiNumber"));
        String iso = String.valueOf(((Map<?, ?>) p.get("tithiLimb")).get("endIso"));
        assertTrue(iso.startsWith("2016-07-21T03:")
                        || iso.startsWith("2016-07-21T04:"),
                "expected 21 Jul ~04:03 AM, limb iso=" + iso + " display=" + p.get("tithiEnd"));
        assertEquals("Uttara Ashadha", p.get("nakshatra"));
        assertEquals("Vishkambha", p.get("yoga"));
    }
}
