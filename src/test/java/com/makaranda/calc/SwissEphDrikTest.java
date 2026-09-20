package com.makaranda.calc;

import com.makaranda.calc.ephemeris.EphemerisEngine;
import com.makaranda.calc.ephemeris.PanchangMode;
import com.makaranda.calc.ephemeris.SwissEphAdapter;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Drik-only Swiss Eph. SIDDHANTIC must not change. */
class SwissEphDrikTest {

    @Test
    void swissEphFilesLoadForDrik() {
        assertTrue(SwissEphAdapter.available(), "sepl_18.se1 + semo_18.se1 should load");
        assertEquals("swiss-ephemeris", SwissEphAdapter.backend());
    }

    @Test
    void j2000SunIsNearJpl() {
        // JD 2451545.0 = 2000-01-01 12:00 UT. Apparent tropical Sun ≈ 280.37°.
        double[] sm = SwissEphAdapter.tropicalSunMoon(2451545.0);
        assertTrue(Math.abs(AstroMath.norm180(sm[0] - 280.37)) < 0.02,
                "Sun " + sm[0]);
        assertTrue(sm[1] >= 0 && sm[1] < 360);
        assertTrue(Math.abs(AstroMath.norm180(sm[0] - sm[1])) > 1);
    }

    @Test
    void drikUsesSwissEphSidhanticDoesNot() {
        EphemerisEngine eng = new EphemerisEngine();
        double jd = AstroMath.julianDayUt(2022, 7, 29, 0, 0, 0, 5.5);
        Map<String, EphemerisEngine.GeoPos> drik = eng.compute(jd, PanchangMode.DRIK);
        Map<String, EphemerisEngine.GeoPos> ss = eng.compute(jd, PanchangMode.SIDDHANTIC, 85.268);
        assertEquals("swiss-ephemeris", eng.drikBackend());
        double seSun = SwissEphAdapter.tropicalSunMoon(jd)[0];
        assertTrue(Math.abs(AstroMath.norm180(drik.get("Sun").lon() - seSun)) < 1e-6);
        assertTrue(Math.abs(AstroMath.norm180(ss.get("Sun").lon() - seSun)) > 0.2,
                "SIDDHANTIC sun must not be Swiss Eph");
        assertTrue(drik.containsKey("Rahu") && drik.containsKey("Ketu"));
        assertTrue(Math.abs(AstroMath.norm180(drik.get("Ketu").lon() - drik.get("Rahu").lon() - 180)) < 1e-6);
    }
}
