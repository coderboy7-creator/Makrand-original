package com.makaranda.calc;

import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.EphemerisEngine;
import com.makaranda.calc.vedic.KpSystem;
import com.makaranda.calc.vedic.KpSystem.Division;
import com.makaranda.dto.BirthRequest;
import com.makaranda.service.JyotishService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KpSystemTest {

    @Test
    void ashwiniStartsKetuKetu() {
        Division d = KpSystem.of(0.0);
        assertEquals("Ashwini", d.nakshatra());
        assertEquals("Ketu", d.starLord());
        assertEquals("Ketu", d.subLord());
        assertEquals("Mars", d.signLord());
    }

    @Test
    void ketuSubIsFirst7Of120ThenVenus() {
        assertEquals("Ketu", KpSystem.subLord(0.5, "Ketu"));
        assertEquals("Venus", KpSystem.subLord(0.9, "Ketu"));
    }

    @Test
    void bharaniIsVenusStar() {
        Division d = KpSystem.of(13.3334);
        assertEquals("Bharani", d.nakshatra());
        assertEquals("Venus", d.starLord());
    }

    @Test
    void kpAliasDoesNotStealDefaultAyanamsa() {
        assertEquals(AyanamsaSystem.KRISHNAMURTI, AyanamsaSystem.from("KP"));
        assertEquals(AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, AyanamsaSystem.from(null));
        assertEquals(AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, AyanamsaSystem.from(""));
    }

    @Test
    void placidusCuspsOpposeAndMatchAxes() {
        EphemerisEngine e = new EphemerisEngine();
        double jd = AstroMath.julianDayUt(2022, 7, 29, 5, 20, 0, 5.5);
        double lat = 26.5833, lon = 85.268;
        double[] c = e.houseCuspsPlacidus(jd, lat, lon);
        assertEquals(e.tropicalAscendant(jd, lat, lon), c[1], 1e-6);
        assertEquals(e.tropicalMc(jd, lon), c[10], 1e-6);
        for (int h = 1; h <= 6; h++) {
            assertEquals(180.0, Math.abs(AstroMath.norm180(c[h + 6] - c[h])), 1e-4,
                    "cusp " + h + " vs " + (h + 6));
        }
        int hMc = AstroMath.houseFromCusps(c[10] + 0.01, c);
        assertEquals(10, hMc);
    }

    @Test
    void defaultChartStaysWholeSignMakaranda() {
        JyotishService js = new JyotishService();
        BirthRequest req = sample();
        var c = js.chart(req);
        assertEquals("WHOLE_SIGN", c.input().houseSystem());
        assertEquals(AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, c.input().ayanamsa());
        int moonSignHouse = AstroMath.houseFromLagna(
                c.lagna().signIndex(), c.planets().get("Moon").signIndex());
        assertEquals(moonSignHouse, c.planets().get("Moon").house());
    }

    @Test
    void kpEndpointHasCuspsStarAndSub() {
        JyotishService js = new JyotishService();
        BirthRequest req = sample();
        req.ayanamsa = "KRISHNAMURTI";
        req.houseSystem = "KP";
        req.panchangMode = "DRIK";
        Map<String, Object> kp = js.kp(req);
        assertEquals("KP", kp.get("system"));
        assertEquals(Boolean.FALSE, kp.get("defaultPaddhati"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cusps = (List<Map<String, Object>>) kp.get("cusps");
        assertEquals(12, cusps.size());
        assertNotNull(cusps.get(0).get("starLord"));
        assertNotNull(cusps.get(0).get("subLord"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> bodies = (List<Map<String, Object>>) kp.get("bodies");
        assertTrue(bodies.size() >= 10);
        assertFalse(String.valueOf(kp.get("note")).toLowerCase().contains("astrotalk"));
    }

    @Test
    void payloadIncludesKpWithoutChangingDefaultHouse() {
        JyotishService js = new JyotishService();
        Map<String, Object> p = js.chartPayload(sample());
        assertNotNull(p.get("kp"));
        assertEquals("WHOLE_SIGN", sample().houseSystem);
    }

    @Test
    void kpHousesCanDifferFromWholeSign() {
        JyotishService js = new JyotishService();
        BirthRequest whole = sample();
        BirthRequest kp = sample();
        kp.houseSystem = "PLACIDUS";
        var a = js.chart(whole);
        var b = js.chart(kp);
        assertEquals(a.planets().get("Sun").signIndex(), b.planets().get("Sun").signIndex());
        assertEquals("PLACIDUS", b.input().houseSystem());
    }

    private static BirthRequest sample() {
        BirthRequest req = new BirthRequest();
        req.dateTime = LocalDateTime.of(2022, 7, 29, 5, 20);
        req.latitude = 26.5833;
        req.longitude = 85.268;
        req.place = "Darbhanga KSDS";
        return req;
    }
}
