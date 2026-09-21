package com.makaranda.calc.interpret;

import com.makaranda.calc.vedic.ChartBuilder.FullChart;
import com.makaranda.calc.yoga.YogaDetector;
import com.makaranda.dto.BirthRequest;
import com.makaranda.service.JyotishService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterpretationP8Test {

    @Test
    void essaysAreFromChartNotCannedAstrotalk() {
        JyotishService js = new JyotishService();
        BirthRequest req = new BirthRequest();
        req.dateTime = LocalDateTime.of(2022, 7, 29, 5, 20);
        req.latitude = 26.5833;
        req.longitude = 85.268;
        req.place = "Darbhanga KSDS";
        FullChart c = js.chart(req);
        Map<String, Object> r = new InterpretationEngine().interpret(c);

        String lagnaHi = String.valueOf(r.get("lagnaEssayHi"));
        assertTrue(lagnaHi.contains("लग्न"), lagnaHi);
        assertTrue(lagnaHi.contains("लग्नेश"), lagnaHi);
        assertFalse(lagnaHi.toLowerCase().contains("astrotalk"));

        String liveHi = String.valueOf(r.get("livelihoodHi"));
        assertTrue(liveHi.contains("दशम"), liveHi);
        assertTrue(liveHi.contains("बुध"), liveHi);

        @SuppressWarnings("unchecked")
        Map<String, Object> mer = (Map<String, Object>) r.get("mercury");
        @SuppressWarnings("unchecked")
        Map<String, Object> sat = (Map<String, Object>) r.get("saturn");
        assertNotNull(mer.get("readingHi"));
        assertTrue(String.valueOf(mer.get("readingHi")).contains("बुध"));
        assertTrue(String.valueOf(sat.get("readingHi")).contains("शनि"));

        @SuppressWarnings("unchecked")
        Map<String, Object> maha = (Map<String, Object>) r.get("mahaByHouse");
        assertTrue(String.valueOf(maha.get("readingHi")).contains("महादशा"));
    }

    @Test
    void visheshaYogasIncludeVesiFamilyWhenPresent() {
        JyotishService js = new JyotishService();
        BirthRequest req = new BirthRequest();
        req.dateTime = LocalDateTime.of(2022, 7, 29, 5, 20);
        req.latitude = 26.5833;
        req.longitude = 85.268;
        Map<String, Object> y = YogaDetector.analyse(js.chart(req));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> yogas = (List<Map<String, Object>>) y.get("yogas");
        boolean any = yogas.stream().anyMatch(m -> {
            String n = String.valueOf(m.get("name"));
            return n.contains("Vesi") || n.contains("Vasi") || n.contains("Ubhayachari")
                    || n.contains("Budhaditya") || n.contains("Gajakesari");
        });
        assertTrue(any, "expected at least one vishesha/classical yoga, got " + yogas);
    }
}
