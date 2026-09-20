package com.makaranda.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocationServiceTest {
    private LocationService svc() {
        LocationService s = new LocationService();
        s.nominatim = "http://127.0.0.1:9";
        return s;
    }

    @Test
    void delhiResolvesInIndiaNotOntario() {
        List<Map<String, Object>> r = svc().search("delhi");
        String name = String.valueOf(r.get(0).get("displayName")).toLowerCase();
        assertTrue(name.contains("delhi") && name.contains("india"), name);
        double lat = (Double) r.get(0).get("lat");
        assertTrue(lat > 27 && lat < 30, "lat=" + lat);
        double lon = (Double) r.get(0).get("lon");
        assertTrue(lon > 76 && lon < 78, "lon=" + lon);
    }

    @Test
    void darbhangaDefaultAliasUsesKsds() {
        List<Map<String, Object>> r = svc().search("दरभंगा");
        assertTrue(String.valueOf(r.get(0).get("displayName")).contains("Darbhanga"));
        assertEquals(26.5833, (Double) r.get(0).get("lat"), 0.0001);
        assertEquals(85.268, (Double) r.get(0).get("lon"), 0.0001);
    }

    @Test
    void unknownCityDoesNotFallBackToDarbhanga() {
        List<Map<String, Object>> r = svc().search("Thane");
        assertTrue(r.stream().anyMatch(m -> String.valueOf(m.get("displayName")).contains("Thane")), r.toString());
        List<Map<String, Object>> miss = svc().search("Zzqxnotacity");
        assertTrue(miss.isEmpty(), "unexpected " + miss);
    }

    @Test
    void indiaSubstringDoesNotListEveryCity() {
        List<Map<String, Object>> r = svc().search("india");
        assertTrue(r.size() < 8, "too many: " + r.size());
    }
}
