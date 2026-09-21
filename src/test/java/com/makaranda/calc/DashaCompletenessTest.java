package com.makaranda.calc;

import com.makaranda.calc.dasha.VimshottariDasha;
import com.makaranda.calc.dasha.VimshottariDasha.Period;
import com.makaranda.calc.dasha.YoginiDasha;
import com.makaranda.dto.BirthRequest;
import com.makaranda.service.JyotishService;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashaCompletenessTest {

    private static final LocalDateTime BIRTH = LocalDateTime.of(1990, 1, 1, 6, 0);

    @Test
    void vimshottariLordsSumTo120() {
        int s = 0;
        for (int y : VedicConstants.VIMSHOTTARI_YEARS) s += y;
        assertEquals(120, s);
        assertEquals(9, VedicConstants.VIMSHOTTARI_LORDS.length);
        assertEquals("Ketu", VedicConstants.VIMSHOTTARI_LORDS[0]);
        assertEquals("Mercury", VedicConstants.VIMSHOTTARI_LORDS[8]);
    }

    @Test
    void ashwiniStartIsFullKetuMaha() {
        List<Period> p = VimshottariDasha.compute(0.0, BIRTH, 3);
        assertEquals(9, p.size());
        assertEquals("Ketu", p.get(0).lord());
        assertEquals(7.0, p.get(0).years(), 1e-9);
        assertEquals(BIRTH, p.get(0).start());
        long span = Duration.between(p.get(0).start(), p.get(8).end()).getSeconds();
        long expect = Math.round(120 * 365.2425 * 24 * 3600);
        assertEquals(expect, span, 2); // rounding on 9 plusYears
        Map<String, Object> cur = VimshottariDasha.currentAt(p, BIRTH);
        assertEquals("Ketu", cur.get("mahadasha"));
        assertEquals("Ketu", cur.get("antardasha"));
        assertEquals("Ketu", cur.get("pratyantardasha"));
    }

    @Test
    void ketuAntarAndPratyantarProportions() {
        List<Period> p = VimshottariDasha.compute(0.0, BIRTH, 3);
        Period ketu = p.get(0);
        assertEquals(9, ketu.children().size());
        Period ketuKetu = ketu.children().get(0);
        assertEquals("Ketu", ketuKetu.lord());
        assertEquals(7.0 * 7.0 / 120.0, ketuKetu.years(), 1e-9);
        Period ketuVenus = ketu.children().get(1);
        assertEquals("Venus", ketuVenus.lord());
        assertEquals(7.0 * 20.0 / 120.0, ketuVenus.years(), 1e-9);
        assertEquals(9, ketuKetu.children().size());
        double pd = 7.0 * 7.0 * 7.0 / (120.0 * 120.0);
        assertEquals(pd, ketuKetu.children().get(0).years(), 1e-9);
        double antarSum = ketu.children().stream().mapToDouble(Period::years).sum();
        assertEquals(7.0, antarSum, 0.002);
    }

    @Test
    void halfAshwiniLeavesHalfKetu() {
        double mid = AstroMath.NAKSHATRA_SPAN / 2.0;
        List<Period> p = VimshottariDasha.compute(mid, BIRTH, 1);
        assertEquals("Ketu", p.get(0).lord());
        Duration used = Duration.between(p.get(0).start(), BIRTH);
        Duration full = Duration.between(p.get(0).start(), p.get(0).end());
        assertEquals(0.5, used.getSeconds() / (double) full.getSeconds(), 0.002);
        Map<String, Object> cur = VimshottariDasha.currentAt(p, BIRTH);
        assertEquals("Ketu", cur.get("mahadasha"));
        assertTrue(VimshottariDasha.contains(p.get(0), BIRTH, false));
        assertFalse(VimshottariDasha.contains(p.get(0), p.get(0).start().minusSeconds(1), false));
    }

    @Test
    void bharaniStartsVenusMaha() {
        // nak 1 = Bharani, lord Venus
        double lon = AstroMath.NAKSHATRA_SPAN + 0.01;
        List<Period> p = VimshottariDasha.compute(lon, BIRTH, 2);
        assertEquals("Venus", p.get(0).lord());
        assertEquals(20.0, p.get(0).years(), 1e-9);
        assertEquals("Sun", p.get(1).lord());
    }

    @Test
    void yoginiCycleIs36AndAshwiniIsMangala() {
        int s = 0;
        for (int y : YoginiDasha.YEARS) s += y;
        assertEquals(36, s);
        assertEquals(8, YoginiDasha.NAMES.length);
        List<Period> y = YoginiDasha.compute(0.0, BIRTH, 2);
        assertEquals(8, y.size());
        assertEquals("Mangala", y.get(0).lord());
        assertEquals("Moon", YoginiDasha.lordOf("Mangala"));
        assertEquals(1.0, y.get(0).years(), 1e-9);
        assertEquals("Sankata", y.get(7).lord());
        assertEquals(8.0, y.get(7).years(), 1e-9);
        long span = Duration.between(y.get(0).start(), y.get(7).end()).getSeconds();
        long expect = Math.round(36 * 365.2425 * 24 * 3600);
        assertEquals(expect, span, 2);
        Map<String, Object> cur = VimshottariDasha.currentAt(y, BIRTH);
        assertEquals("Mangala", cur.get("mahadasha"));
        assertEquals("Mangala", cur.get("antardasha"));
        double firstAntar = 1.0 * 1.0 / 36.0;
        assertEquals(firstAntar, y.get(0).children().get(0).years(), 1e-9);
    }

    @Test
    void yoginiFollowsNakshatraMod8() {
        // Pushya = nak 7 → Sankata
        double pushya = 7 * AstroMath.NAKSHATRA_SPAN + 0.1;
        List<Period> y = YoginiDasha.compute(pushya, BIRTH, 1);
        assertEquals("Sankata", y.get(0).lord());
        assertEquals("Mangala", y.get(1).lord());
        // Magha = nak 9 → index 1 Pingala
        double magha = 9 * AstroMath.NAKSHATRA_SPAN + 0.1;
        assertEquals("Pingala", YoginiDasha.compute(magha, BIRTH, 1).get(0).lord());
    }

    @Test
    void natalPayloadHasCurrentMdAdPdAndYogini() {
        JyotishService js = new JyotishService();
        BirthRequest req = new BirthRequest();
        req.dateTime = LocalDateTime.of(2022, 7, 29, 5, 20);
        req.latitude = 26.5833;
        req.longitude = 85.268;
        Map<String, Object> tree = js.dashaTree(js.chart(req));
        assertEquals("Vimshottari", tree.get("system"));
        assertEquals(120, tree.get("cycleYears"));
        @SuppressWarnings("unchecked")
        Map<String, Object> cur = (Map<String, Object>) tree.get("current");
        assertNotNull(cur.get("mahadasha"));
        assertNotNull(cur.get("antardasha"));
        assertNotNull(cur.get("pratyantardasha"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> periods = (List<Map<String, Object>>) tree.get("periods");
        assertEquals(9, periods.size());
        @SuppressWarnings("unchecked")
        Map<String, Object> first = periods.get(0);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> antars = (List<Map<String, Object>>) first.get("children");
        assertEquals(9, antars.size());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> prat = (List<Map<String, Object>>) antars.get(0).get("children");
        assertEquals(9, prat.size());
        @SuppressWarnings("unchecked")
        Map<String, Object> yog = (Map<String, Object>) tree.get("yogini");
        assertEquals("Yogini", yog.get("system"));
        assertEquals(36, yog.get("cycleYears"));
        @SuppressWarnings("unchecked")
        List<?> yp = (List<?>) yog.get("periods");
        assertEquals(8, yp.size());
        assertNotNull(yog.get("current"));
        assertNotNull(yog.get("birthYogini"));
    }
}
