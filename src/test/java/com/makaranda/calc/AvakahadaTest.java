package com.makaranda.calc;

import com.makaranda.calc.vedic.Avakahada;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvakahadaTest {

    @Test
    void rohiniPada4MatchesClassicalAvakahada() {
        // 22° Taurus = 52° sidereal = Rohiṇī pāda 4 (PDF native: वर्ण शूद्र, योनि सर्प, लोह, वू)
        Map<String, Object> a = Avakahada.fromMoon(52.0);
        assertEquals("Rohini", a.get("nakshatra"));
        assertEquals(4, a.get("pada"));
        assertEquals("Shudra", a.get("varna"));
        assertEquals("Serpent", a.get("yoni"));
        assertEquals("Manushya", a.get("gana"));
        assertEquals("Antya", a.get("nadi"));
        assertEquals("Taurus", a.get("rashi"));
        assertEquals("Venus", a.get("rashiLord"));
        assertEquals("Moon", a.get("nakLord"));
        assertEquals("Earth", a.get("tattva"));
        assertEquals("वू", a.get("namakshara"));
        assertEquals("Iron", a.get("paya"));
        assertEquals("Quadruped", a.get("vashya"));
    }

    @Test
    void baladiAvasthaOddEven() {
        assertEquals("Bala", Avakahada.avastha(2.0));          // Aries 2° odd
        assertEquals("Mrita", Avakahada.avastha(25.7));        // Aries 25° odd
        assertEquals("Vriddha", Avakahada.avastha(30.0 + 6.1)); // Taurus 6° even → reverse ~24°
        assertEquals("Kumara", Avakahada.avastha(30.0 + 22.3)); // Taurus 22° even → reverse ~8°
        assertEquals("Bala", Avakahada.avastha(240.3));         // Sag 0.3° odd
    }
}
