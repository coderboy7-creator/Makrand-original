package com.makaranda.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LocationServiceTest {
    @Test
    void delhiResolvesInIndiaNotOntario() {
        LocationService s = new LocationService();
        s.nominatim = "http://127.0.0.1:9";
        List<Map<String, Object>> r = s.search("delhi");
        String name = String.valueOf(r.get(0).get("displayName")).toLowerCase();
        assertTrue(name.contains("delhi") && name.contains("india"), name);
        double lat = (Double) r.get(0).get("lat");
        assertTrue(lat > 27 && lat < 30, "lat=" + lat);
        double lon = (Double) r.get(0).get("lon");
        assertTrue(lon > 76 && lon < 78, "lon=" + lon);
    }

    @Test
    void darbhangaDefaultAlias() {
        LocationService s = new LocationService();
        s.nominatim = "http://127.0.0.1:9";
        List<Map<String, Object>> r = s.search("दरभंगा");
        assertTrue(String.valueOf(r.get(0).get("displayName")).contains("Darbhanga"));
    }
}
