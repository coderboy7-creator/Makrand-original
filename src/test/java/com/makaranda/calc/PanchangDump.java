package com.makaranda.calc;

import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.PanchangMode;
import com.makaranda.calc.panchang.PanchangCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

class PanchangDump {
    @Test
    void dumpGoldAndEightRow() {
        PanchangCalculator c = new PanchangCalculator();
        double lat = PanchangCalculator.KSDS_AKSHANSH_DEG;
        double lon = PanchangCalculator.KSDS_LON;
        String[] dates = {
                "2016-07-20", "2016-07-21", "2016-07-24", "2016-09-18",
                "2022-07-27", "2022-07-29", "2022-08-12", "2022-11-01",
                "2025-07-11", "2025-10-05", "2026-01-05", "2026-01-10",
                "2026-06-25", "2026-08-01", "2026-08-29"
        };
        for (String ds : dates) {
            Map<String, Object> p = c.compute(LocalDate.parse(ds), lat, lon, 5.5,
                    AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, PanchangMode.SIDDHANTIC);
            System.out.printf("%s SR=%s SS=%s %s %s(#%s) T=%s nak=%s N=%s yoga=%s Y=%s%n",
                    ds, p.get("sunrise"), p.get("sunset"),
                    p.get("paksha"), p.get("tithi"), p.get("tithiNumber"), p.get("tithiEnd"),
                    p.get("nakshatra"), p.get("nakshatraEnd"),
                    p.get("yoga"), p.get("yogaEnd"));
        }
    }
}
