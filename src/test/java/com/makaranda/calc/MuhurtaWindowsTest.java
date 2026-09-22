package com.makaranda.calc;

import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.PanchangMode;
import com.makaranda.calc.muhurta.MuhurtaCalculator;
import com.makaranda.calc.panchang.PanchangCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Intra-day clocks on muhurta rows: Rahu / Gulika / Yamaganda are weekday
 * eighths of sunrise–sunset; Abhijit sits on solar noon. 12-hour AM/PM.
 */
class MuhurtaWindowsTest {

    private static final double LAT = PanchangCalculator.KSDS_AKSHANSH_DEG;
    private static final double LON = PanchangCalculator.KSDS_LON;

    @Test
    void fridayRahuIsFourthEighthOfDay() {
        LocalDate d = LocalDate.of(2022, 7, 29); // Friday
        Map<String, Object> p = new PanchangCalculator().compute(
                d, LAT, LON, 5.5,
                AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, PanchangMode.SIDDHANTIC);
        @SuppressWarnings("unchecked")
        Map<String, Object> kala = (Map<String, Object>) p.get("muhurta");
        @SuppressWarnings("unchecked")
        Map<String, String> rahu = (Map<String, String>) kala.get("rahuKalam");
        @SuppressWarnings("unchecked")
        Map<String, String> gulika = (Map<String, String>) kala.get("gulika");
        @SuppressWarnings("unchecked")
        Map<String, String> yama = (Map<String, String>) kala.get("yamaganda");
        @SuppressWarnings("unchecked")
        Map<String, String> abhijit = (Map<String, String>) p.get("abhijit");

        int rise = PanchangCalculator.toMin(String.valueOf(p.get("sunrise")));
        int set = PanchangCalculator.toMin(String.valueOf(p.get("sunset")));
        if (set <= rise) set += 24 * 60;
        double eighth = (set - rise) / 8.0;
        // Friday index 5: Rahu 4th, Yamaganda 7th, Gulika 2nd eighth (1-based)
        assertEquals((int) Math.round(rise + 3 * eighth), PanchangCalculator.toMin(rahu.get("start")));
        assertEquals((int) Math.round(rise + 4 * eighth), PanchangCalculator.toMin(rahu.get("end")));
        assertEquals((int) Math.round(rise + 6 * eighth), PanchangCalculator.toMin(yama.get("start")));
        assertEquals((int) Math.round(rise + 7 * eighth), PanchangCalculator.toMin(yama.get("end")));
        assertEquals((int) Math.round(rise + 1 * eighth), PanchangCalculator.toMin(gulika.get("start")));
        assertEquals((int) Math.round(rise + 2 * eighth), PanchangCalculator.toMin(gulika.get("end")));
        assertTrue(abhijit.get("start").contains("M"));
        assertTrue(abhijit.get("end").contains("M"));
        int mid = (rise + set) / 2;
        int width = Math.max(24, (set - rise) / 15);
        assertEquals(mid - width / 2, PanchangCalculator.toMin(abhijit.get("start")));
        assertEquals(mid + width / 2, PanchangCalculator.toMin(abhijit.get("end")));
    }

    @Test
    void searchRowsExposeTwelveHourClocks() {
        List<Map<String, Object>> rows = new MuhurtaCalculator().search(
                MuhurtaCalculator.Purpose.MARRIAGE,
                LocalDate.of(2022, 7, 29), 40,
                LAT, LON, 5.5,
                AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, PanchangMode.SIDDHANTIC);
        assertFalse(rows.isEmpty());
        for (Map<String, Object> row : rows) {
            assertClock(row.get("rahuClock"));
            assertClock(row.get("gulikaClock"));
            assertClock(row.get("yamaClock"));
            assertClock(row.get("abhijitClock"));
            assertNotNull(row.get("sunrise"));
            assertNotNull(row.get("rahuKalam"));
            assertNotNull(row.get("gulika"));
        }
    }

    private static void assertClock(Object v) {
        String s = String.valueOf(v);
        assertTrue(s.contains("AM") || s.contains("PM"), s);
        assertTrue(s.contains("–"), "missing span dash: " + s);
    }
}
