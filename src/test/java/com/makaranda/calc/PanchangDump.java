package com.makaranda.calc;
import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.PanchangMode;
import com.makaranda.calc.panchang.PanchangCalculator;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Map;
class PanchangDump {
    @Test
    void dump() {
        PanchangCalculator c = new PanchangCalculator();
        for (String ds : new String[]{"2016-07-19","2016-07-20","2016-07-21","2016-07-22","2016-08-18","2016-08-19"}) {
            LocalDate d = LocalDate.parse(ds);
            for (PanchangMode m : new PanchangMode[]{PanchangMode.SIDDHANTIC, PanchangMode.DRIK}) {
                Map<String,Object> p = c.compute(d, 26.1542, 85.8918, 5.5,
                    AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, m);
                System.out.printf("%s %s SR=%s SS=%s %s %s(#%s) end=%s nak=%s end=%s yoga=%s end=%s%n",
                    ds, m, p.get("sunrise"), p.get("sunset"),
                    p.get("paksha"), p.get("tithi"), p.get("tithiNumber"), p.get("tithiEnd"),
                    p.get("nakshatra"), p.get("nakshatraEnd"),
                    p.get("yoga"), p.get("yogaEnd"));
            }
        }
    }
}
