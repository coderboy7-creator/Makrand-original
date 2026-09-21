package com.makaranda.calc;

import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.PanchangMode;
import com.makaranda.calc.panchang.PanchangCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PanchangMonthTest {

    @Test
    void july2022Has31DaysWithTillTimesNotStartAsEnd() {
        PanchangCalculator p = new PanchangCalculator();
        List<Map<String, Object>> days = p.month(
                LocalDate.of(2022, 7, 1), 26.5833, 85.268, 5.5,
                AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, PanchangMode.SIDDHANTIC);
        assertEquals(31, days.size());
        assertEquals("2022-07-01", days.get(0).get("date"));
        assertEquals("2022-07-31", days.get(30).get("date"));
        Map<String, Object> d29 = days.stream()
                .filter(m -> "2022-07-29".equals(m.get("date")))
                .findFirst().orElseThrow();
        assertNotNull(d29.get("tithiEnd"));
        assertNotNull(d29.get("nakshatraEnd"));
        assertNotEquals(d29.get("tithiStart"), d29.get("tithiEnd"));
        assertEquals("5:23 AM", d29.get("sunrise"));
        assertEquals("6:50 PM", d29.get("sunset"));
        assertTrue(String.valueOf(d29.get("tithiHi")).length() > 0);
    }
}
