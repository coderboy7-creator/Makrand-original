package com.makaranda.calc;

import com.makaranda.calc.vedic.Rudraksha;
import com.makaranda.dto.BirthRequest;
import com.makaranda.service.JyotishService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RudrakshaTest {

    @Test
    void mukhiFollowsNakshatraLord() {
        assertEquals(1, Rudraksha.mukhiForLord("Sun"));
        assertEquals(2, Rudraksha.mukhiForLord("Moon"));
        assertEquals(7, Rudraksha.mukhiForLord("Saturn"));
        assertEquals(9, Rudraksha.mukhiForLord("Ketu"));
    }

    @Test
    void pushyaBirthGetsSevenMukhiAndDoesNotSell() {
        JyotishService js = new JyotishService();
        BirthRequest req = new BirthRequest();
        req.dateTime = LocalDateTime.of(2022, 7, 29, 5, 20);
        req.latitude = 26.5833;
        req.longitude = 85.268;
        Map<String, Object> gems = js.chartPayload(req);
        @SuppressWarnings("unchecked")
        Map<String, Object> g = (Map<String, Object>) gems.get("gemstones");
        assertNotNull(g.get("lifeStone"));
        String warn = String.valueOf(g.get("warning"));
        assertTrue(warn.toLowerCase().contains("do not sell") || warn.contains("नहीं बेचते"), warn);

        @SuppressWarnings("unchecked")
        Map<String, Object> r = (Map<String, Object>) g.get("rudraksha");
        assertEquals("Pushya", r.get("nakshatra"));
        assertEquals("Saturn", r.get("lord"));
        assertEquals(7, r.get("mukhi"));
        assertEquals(Boolean.FALSE, r.get("sell"));
        assertFalse(String.valueOf(r.get("note")).toLowerCase().contains("buy"));
        assertFalse(String.valueOf(r.get("note")).toLowerCase().contains("cart"));
        assertTrue(String.valueOf(r.get("noteHi")).contains("नहीं बेचते"));
    }
}
