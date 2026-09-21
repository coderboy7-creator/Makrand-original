package com.makaranda.calc;

import com.makaranda.calc.vedic.Ashtakavarga;
import com.makaranda.dto.BirthRequest;
import com.makaranda.service.JyotishService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AshtakavargaTest {

    @Test
    void classicalTotalsAreBphs337() {
        Map<String, Integer> allAries = new LinkedHashMap<>();
        for (String k : Ashtakavarga.KARAKAS) allAries.put(k, 0);
        Map<String, Object> r = Ashtakavarga.fromSigns(allAries);
        @SuppressWarnings("unchecked")
        Map<String, Integer> totals = (Map<String, Integer>) r.get("totals");
        assertEquals(Ashtakavarga.CLASSICAL_TOTALS, totals);
        assertEquals(337, r.get("savTotal"));
    }

    @Test
    void eachSignIsZeroToEight() {
        Map<String, Integer> signs = new LinkedHashMap<>();
        signs.put("Lagna", 8);
        signs.put("Sun", 4);
        signs.put("Moon", 1);
        signs.put("Mars", 0);
        signs.put("Mercury", 5);
        signs.put("Jupiter", 8);
        signs.put("Venus", 6);
        signs.put("Saturn", 9);
        Map<String, Object> r = Ashtakavarga.fromSigns(signs);
        @SuppressWarnings("unchecked")
        Map<String, Object> bav = (Map<String, Object>) r.get("bav");
        for (String g : Ashtakavarga.BAV_GRAHAS) {
            @SuppressWarnings("unchecked")
            Map<String, Object> row = (Map<String, Object>) bav.get(g);
            @SuppressWarnings("unchecked")
            List<Integer> bySign = (List<Integer>) row.get("bySign");
            assertEquals(12, bySign.size());
            int sum = 0;
            for (int n : bySign) {
                assertTrue(n >= 0 && n <= 8, g + " bindu " + n);
                sum += n;
            }
            assertEquals(Ashtakavarga.CLASSICAL_TOTALS.get(g), sum);
        }
    }

    @Test
    void allAriesSunBavInMeshaIsThree() {
        // From Aries, house 1: Sun, Mars, Saturn donate to Sūrya BAV; others do not.
        Map<String, Integer> allAries = new LinkedHashMap<>();
        for (String k : Ashtakavarga.KARAKAS) allAries.put(k, 0);
        Map<String, Object> r = Ashtakavarga.fromSigns(allAries);
        @SuppressWarnings("unchecked")
        Map<String, Object> bav = (Map<String, Object>) r.get("bav");
        @SuppressWarnings("unchecked")
        Map<String, Object> sun = (Map<String, Object>) bav.get("Sun");
        @SuppressWarnings("unchecked")
        List<Integer> bySign = (List<Integer>) sun.get("bySign");
        assertEquals(3, bySign.get(0));
        @SuppressWarnings("unchecked")
        List<List<String>> who = (List<List<String>>) sun.get("contributorsBySign");
        assertEquals(List.of("Sun", "Mars", "Saturn"), who.get(0));
    }

    @Test
    void natalChartSavStill337() {
        JyotishService js = new JyotishService();
        BirthRequest req = new BirthRequest();
        req.dateTime = LocalDateTime.of(2022, 7, 29, 5, 20);
        req.latitude = 26.5833;
        req.longitude = 85.268;
        req.place = "Darbhanga";
        Map<String, Object> r = Ashtakavarga.fromChart(js.chart(req));
        assertEquals(337, r.get("savTotal"));
        @SuppressWarnings("unchecked")
        Map<String, Object> sav = (Map<String, Object>) r.get("sav");
        @SuppressWarnings("unchecked")
        List<Integer> bySign = (List<Integer>) sav.get("bySign");
        int sum = 0;
        for (int n : bySign) {
            assertTrue(n >= 0 && n <= 56, "SAV cell " + n);
            sum += n;
        }
        assertEquals(337, sum);
    }
}
