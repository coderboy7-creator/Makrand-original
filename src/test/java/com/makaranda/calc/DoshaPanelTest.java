package com.makaranda.calc;

import com.makaranda.calc.yoga.DoshaPanel;
import com.makaranda.dto.BirthRequest;
import com.makaranda.service.JyotishService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoshaPanelTest {

    private final JyotishService js = new JyotishService();

    private BirthRequest ksdsu() {
        BirthRequest r = new BirthRequest();
        r.dateTime = LocalDateTime.of(2022, 7, 29, 5, 20);
        r.latitude = 26.5833;
        r.longitude = 85.268;
        r.place = "Darbhanga";
        r.tzOffsetHours = 5.5;
        return r;
    }

    @Test
    void panelAlwaysHasThreeItemsAndHonestCopy() {
        Map<String, Object> chart = js.chartPayload(ksdsu());
        @SuppressWarnings("unchecked")
        Map<String, Object> pan = (Map<String, Object>) chart.get("doshaPanel");
        assertNotNull(pan);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) pan.get("items");
        assertEquals(3, items.size());
        assertEquals("mangal", items.get(0).get("id"));
        assertEquals("kaalsarpa", items.get(1).get("id"));
        assertEquals("sadesati", items.get(2).get("id"));
        String blob = (pan.get("note") + " " + pan.get("noteHi")
                + items.get(0).get("text") + items.get(0).get("textHi")
                + items.get(1).get("text") + items.get(1).get("textHi")
                + items.get(2).get("text") + items.get(2).get("textHi")).toLowerCase();
        assertFalse(blob.contains("deadly"));
        assertFalse(blob.contains("ruin"));
        assertFalse(blob.contains("you will suffer"));
        assertTrue(blob.contains("not a curse") || blob.contains("शाप नहीं"));
        assertTrue(String.valueOf(pan.get("noteHi")).contains("भय-प्रचार"));
    }

    @Test
    void mangalFlagMatchesMarsHouse() {
        var c = js.chart(ksdsu());
        Map<String, Object> m = DoshaPanel.mangal(c);
        int h = c.planets().get("Mars").house();
        boolean inHouse = h == 1 || h == 2 || h == 4 || h == 7 || h == 8 || h == 12;
        assertEquals(h, m.get("house"));
        if (!inHouse) {
            assertEquals("absent", m.get("status"));
            assertFalse((Boolean) m.get("present"));
        } else {
            assertTrue("present".equals(m.get("status")) || "cancelled".equals(m.get("status")));
        }
    }

    @Test
    void kaalsarpaAgreesWithYogaDetector() {
        var c = js.chart(ksdsu());
        Map<String, Object> k = DoshaPanel.kaalsarpa(c);
        String type = com.makaranda.calc.yoga.YogaDetector.kaalSarp(c);
        assertEquals(type != null, k.get("present"));
        assertEquals(type, k.get("type"));
        if (type != null) assertNotNull(k.get("typeHi"));
    }

    @Test
    void sadeSatiUsesGocharSaturnFromMoon() {
        BirthRequest r = ksdsu();
        Map<String, Object> go = js.transits(r, java.time.LocalDate.of(2022, 7, 29));
        Map<String, Object> s = DoshaPanel.sadeSati(go);
        @SuppressWarnings("unchecked")
        Map<String, Object> raw = (Map<String, Object>) go.get("sadeSati");
        assertEquals(raw.get("active"), s.get("active"));
        assertEquals(raw.get("houseFromMoon"), s.get("houseFromMoon"));
        assertNotNull(s.get("phaseHi"));
        assertTrue(s.get("status").equals("active") || s.get("status").equals("absent"));
    }
}
