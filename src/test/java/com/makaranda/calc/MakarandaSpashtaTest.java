package com.makaranda.calc;

import com.makaranda.calc.ephemeris.MakarandaSpashta;
import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.PanchangMode;
import com.makaranda.calc.panchang.PanchangCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** P13: frozen bijas + sphuṭa paridhi + bhujāntara. July gold is the gate. */
class MakarandaSpashtaTest {

    @Test
    void bijasStayFrozen() {
        assertEquals(-0.19, MakarandaSpashta.SUN_MEAN_BIJA);
        assertEquals(2.31, MakarandaSpashta.MOON_MEAN_BIJA);
        assertEquals(-90.0, MakarandaSpashta.MOON_APSIS_OFFSET);
        assertEquals(174.3, MakarandaSpashta.SUN_APOGEE_OFFSET);
        assertEquals(14.0, MakarandaSpashta.SUN_MANDA_ODD);
        assertEquals(32.0, MakarandaSpashta.MOON_MANDA_ODD);
    }

    @Test
    void sphutaParidhiIsEvenSmallerNotANewBija() {
        double odd = MakarandaSpashta.sphutaParidhi(90, 14.0);
        double even = MakarandaSpashta.sphutaParidhi(0, 14.0);
        assertEquals(14.0, odd, 0.02);
        assertEquals(14.0 - 20.0 / 60.0, even, 0.02);
        assertTrue(odd > even);
    }

    @Test
    void jyaSinMatchesUnityAtNinety() {
        assertEquals(0.0, MakarandaSpashta.jyaSin(0), 1e-4);
        assertEquals(1.0, MakarandaSpashta.jyaSin(90), 0.003);
        assertEquals(-1.0, MakarandaSpashta.jyaSin(270), 0.003);
    }

    @Test
    void july2022SunriseStillWithinFourMinutes() {
        Map<String, Object> p = new PanchangCalculator().compute(
                LocalDate.parse("2022-07-29"),
                PanchangCalculator.KSDS_AKSHANSH_DEG, PanchangCalculator.KSDS_LON, 5.5,
                AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA, PanchangMode.SIDDHANTIC);
        int sr = PanchangCalculator.toMin(String.valueOf(p.get("sunrise")));
        int ss = PanchangCalculator.toMin(String.valueOf(p.get("sunset")));
        assertTrue(Math.abs(sr - PanchangCalculator.toMin("5:20 AM")) <= 4, "SR " + p.get("sunrise"));
        assertTrue(Math.abs(ss - PanchangCalculator.toMin("6:50 PM")) <= 4, "SS " + p.get("sunset"));
    }
}
