package com.makaranda.calc;

import com.makaranda.calc.vedic.Shadbala;
import com.makaranda.dto.BirthRequest;
import com.makaranda.service.JyotishService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShadbalaTest {

    @Test
    void naisargikaMatchesBphsSevenths() {
        assertEquals(60.00, Shadbala.NAISARGIKA.get("Sun"), 0.01);
        assertEquals(51.43, Shadbala.NAISARGIKA.get("Moon"), 0.01);
        assertEquals(42.86, Shadbala.NAISARGIKA.get("Venus"), 0.01);
        assertEquals(34.29, Shadbala.NAISARGIKA.get("Jupiter"), 0.01);
        assertEquals(25.71, Shadbala.NAISARGIKA.get("Mercury"), 0.01);
        assertEquals(17.14, Shadbala.NAISARGIKA.get("Mars"), 0.01);
        assertEquals(8.57, Shadbala.NAISARGIKA.get("Saturn"), 0.01);
    }

    @Test
    void requiredMinimaAreBphsVirupa() {
        assertEquals(390, Shadbala.REQUIRED.get("Sun"));
        assertEquals(360, Shadbala.REQUIRED.get("Moon"));
        assertEquals(300, Shadbala.REQUIRED.get("Mars"));
        assertEquals(420, Shadbala.REQUIRED.get("Mercury"));
        assertEquals(390, Shadbala.REQUIRED.get("Jupiter"));
        assertEquals(330, Shadbala.REQUIRED.get("Venus"));
        assertEquals(300, Shadbala.REQUIRED.get("Saturn"));
    }

    @Test
    void uchchaIsSixtyAtExaltationZeroAtDebilitation() {
        assertEquals(60.0, Shadbala.uchchaBala("Sun", 10.0), 0.05);
        assertEquals(0.0, Shadbala.uchchaBala("Sun", 190.0), 0.05);
        assertEquals(60.0, Shadbala.uchchaBala("Moon", 33.0), 0.05);
        assertEquals(60.0, Shadbala.uchchaBala("Mars", 9 * 30 + 28), 0.05);
        assertEquals(0.0, Shadbala.uchchaBala("Mars", 3 * 30 + 28), 0.05);
        assertEquals(60.0, Shadbala.uchchaBala("Jupiter", 3 * 30 + 5), 0.05);
        assertEquals(60.0, Shadbala.uchchaBala("Venus", 11 * 30 + 27), 0.05);
        assertEquals(60.0, Shadbala.uchchaBala("Saturn", 6 * 30 + 20), 0.05);
        assertEquals(60.0, Shadbala.uchchaBala("Mercury", 5 * 30 + 15), 0.05);
    }

    @Test
    void digBalaFullInOwnDirectionZeroOpposite() {
        // Sun/Mars west = 7th; Jupiter/Mercury east = 1st; Moon/Venus north = 4th; Saturn south = 10th
        assertEquals(60.0, Shadbala.digBala(0, 7), 0.01);  // Sun in 7
        assertEquals(0.0, Shadbala.digBala(0, 1), 0.01);   // Sun in 1
        assertEquals(60.0, Shadbala.digBala(4, 1), 0.01);  // Jupiter in 1
        assertEquals(0.0, Shadbala.digBala(4, 7), 0.01);
        assertEquals(60.0, Shadbala.digBala(1, 4), 0.01);  // Moon in 4
        assertEquals(60.0, Shadbala.digBala(6, 10), 0.01); // Saturn in 10
        assertEquals(30.0, Shadbala.digBala(0, 4), 0.01);  // Sun 90° from west
    }

    @Test
    void kendradiIsSixtyThirtyFifteen() {
        assertEquals(60, Shadbala.kendradiBala(1));
        assertEquals(30, Shadbala.kendradiBala(2));
        assertEquals(15, Shadbala.kendradiBala(3));
        assertEquals(60, Shadbala.kendradiBala(10));
    }

    @Test
    void sundayFirstHoraIsSun() {
        assertEquals("Sun", Shadbala.horaLord(6.0, 18.0, 6.1, 0));
        assertEquals("Venus", Shadbala.horaLord(6.0, 18.0, 7.1, 0));
    }

    @Test
    void weekdaySun0FridayIsFive() {
        assertEquals(5, Shadbala.weekdaySun0(LocalDate.of(2022, 7, 29)));
        assertEquals(0, Shadbala.weekdaySun0(LocalDate.of(2022, 7, 24)));
    }

    @Test
    void natalChartComponentsSumAndStayInRange() {
        JyotishService js = new JyotishService();
        BirthRequest req = new BirthRequest();
        req.dateTime = LocalDateTime.of(2022, 7, 29, 5, 20);
        req.latitude = 26.5833;
        req.longitude = 85.268;
        req.place = "Darbhanga";
        Map<String, Object> r = Shadbala.fromChart(js.chart(req));
        @SuppressWarnings("unchecked")
        Map<String, Object> grahas = (Map<String, Object>) r.get("grahas");
        for (String g : Shadbala.GRAHAS) {
            @SuppressWarnings("unchecked")
            Map<String, Object> row = (Map<String, Object>) grahas.get(g);
            double sthana = n(row, "sthana");
            double dig = n(row, "dig");
            double kala = n(row, "kala");
            double chesta = n(row, "chesta");
            double nais = n(row, "naisargika");
            double drik = n(row, "drik");
            double total = n(row, "totalVirupa");
            assertEquals(sthana + dig + kala + chesta + nais + drik, total, 0.15, g);
            assertTrue(sthana >= 0 && sthana <= 400, g + " sthana " + sthana);
            assertTrue(dig >= -0.01 && dig <= 60.01, g + " dig " + dig);
            assertTrue(chesta >= -0.01 && chesta <= 60.01, g + " chesta " + chesta);
            assertTrue(total > 50 && total < 800, g + " total " + total);
            assertEquals(nais, Shadbala.NAISARGIKA.get(g), 0.02);
            assertEquals(total / 60.0, n(row, "rupa"), 0.02);
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> bhavas = (List<Map<String, Object>>) r.get("bhavas");
        assertEquals(12, bhavas.size());
        assertEquals(1, bhavas.get(0).get("house"));
        assertTrue(n(bhavas.get(9), "dig") > n(bhavas.get(3), "dig")); // 10th stronger than 4th
    }

    private static double n(Map<String, Object> m, String k) {
        return ((Number) m.get(k)).doubleValue();
    }
}
