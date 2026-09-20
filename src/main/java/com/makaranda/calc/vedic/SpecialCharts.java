package com.makaranda.calc.vedic;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.EphemerisEngine;
import com.makaranda.calc.ephemeris.PanchangMode;
import com.makaranda.calc.vedic.ChartBuilder.BirthInput;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SpecialCharts {
    private final ChartBuilder builder = new ChartBuilder();
    private final EphemerisEngine engine = new EphemerisEngine();

    /**
     * Varshaphal — solar return: moment Sun returns to natal sidereal longitude.
     * Approximation: search ±2 days around birthday.
     */
    public Map<String, Object> varshaphal(FullChart natal, int year) {
        double natalSunSid = natal.planets().get("Sun").siderealLon();
        LocalDateTime guess = LocalDateTime.of(year, natal.input().localDateTime().getMonth(),
                Math.min(natal.input().localDateTime().getDayOfMonth(), 28),
                natal.input().localDateTime().getHour(), natal.input().localDateTime().getMinute());
        LocalDateTime best = guess;
        double bestDiff = 999;
        for (int d = -3; d <= 4; d++) {
            for (int h = 0; h < 24; h++) {
                LocalDateTime t = guess.plusDays(d).withHour(h).withMinute(0);
                FullChart ch = builder.build(withTime(natal.input(), t));
                double diff = Math.abs(AstroMath.norm180(ch.planets().get("Sun").siderealLon() - natalSunSid));
                if (diff < bestDiff) {
                    bestDiff = diff;
                    best = t;
                }
            }
        }
        // refine minutes
        for (int m = 0; m < 60; m += 2) {
            LocalDateTime t = best.withMinute(m);
            FullChart ch = builder.build(withTime(natal.input(), t));
            double diff = Math.abs(AstroMath.norm180(ch.planets().get("Sun").siderealLon() - natalSunSid));
            if (diff < bestDiff) {
                bestDiff = diff;
                best = t;
            }
        }
        FullChart vr = builder.build(withTime(natal.input(), best));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("year", year);
        m.put("solarReturnTime", best.toString());
        m.put("sunMatchErrorDeg", Math.round(bestDiff * 10000) / 10000.0);
        m.put("varshaLagna", vr.lagna().signSa() + " " + vr.lagna().signDegree());
        m.put("muntha", muntha(natal, year));
        m.put("chart", vr);
        m.put("yearTheme", "Solar return Lagna " + vr.lagna().signSa()
                + " sets the year's body. Muntha (progressed lagna) highlights the house of focus.");
        return m;
    }

    private String muntha(FullChart natal, int year) {
        int age = year - natal.input().localDateTime().getYear();
        int sign = (natal.lagna().signIndex() + age) % 12;
        return VedicConstants.SIGNS_SA[sign] + " (house "
                + ((((sign - natal.lagna().signIndex() + 12) % 12) + 1)) + ")";
    }

    /**
     * Prashna (horary): chart of the question moment, with Arudha and a simple yes/no from
     * Moon's application and 1st/7th strength.
     */
    public Map<String, Object> prashna(BirthInput when, String question) {
        FullChart ch = builder.build(when);
        int moonHouse = ch.planets().get("Moon").house();
        int lagnaLordHouse = ch.planets().get(VedicConstants.SIGN_LORDS[ch.lagna().signIndex()]).house();
        boolean favourable = moonHouse == 1 || moonHouse == 5 || moonHouse == 9 || moonHouse == 11
                || moonHouse == 4 || moonHouse == 10;
        boolean obstructed = moonHouse == 6 || moonHouse == 8 || moonHouse == 12;
        String verdict;
        if (favourable && !obstructed) verdict = "Shubha — the prasna leans towards fulfilment.";
        else if (obstructed) verdict = "Kashta — delays or a 'no unless remedied'. Re-ask after a shubha tithi.";
        else verdict = "Mishra — mixed; timing depends on Moon's next aspect and dasha of the query.";

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("question", question);
        m.put("verdict", verdict);
        m.put("moonHouse", moonHouse);
        m.put("lagna", ch.lagna().signSa());
        m.put("lagnaLordHouse", lagnaLordHouse);
        m.put("tajikaNote", "Moon as significator of the query; Lagna as querent; 7th as the other. Mithila prashna also weighs the breath (left/right nadi) of the astrologer — record it in notes.");
        m.put("chart", ch);
        return m;
    }

    public Map<String, Object> gemstones(FullChart c) {
        String weakest = null;
        double min = 99;
        for (var e : c.vimshopaka().entrySet()) {
            if (e.getKey().equals("Rahu") || e.getKey().equals("Ketu")) continue;
            if (e.getValue() < min) {
                min = e.getValue();
                weakest = e.getKey();
            }
        }
        String lagnesh = VedicConstants.SIGN_LORDS[c.lagna().signIndex()];
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("lagnesh", lagnesh);
        m.put("lagneshStone", VedicConstants.GEMSTONES.get(lagnesh));
        m.put("weakestPlanet", weakest);
        m.put("weakestScore", min);
        m.put("weakestStone", VedicConstants.GEMSTONES.get(weakest));
        m.put("warning", "Never prescribe Neelam (Saturn) or Gomed (Rahu) without seeing dasha, lagna and current gochar. Prefer mantra and dana first.");
        m.put("lifeStone", VedicConstants.GEMSTONES.get(lagnesh) + " — supports the lagna lord.");
        m.put("luckyStone", VedicConstants.GEMSTONES.get(weakest) + " — supports the weakest of Shodashavarga vimshopaka.");
        m.put("metal", VedicConstants.METALS.get(lagnesh));
        m.put("colour", VedicConstants.COLOURS.get(lagnesh));
        return m;
    }

    private static BirthInput withTime(BirthInput in, LocalDateTime t) {
        return new BirthInput(t, in.timeZone(), in.tzOffsetHours(), in.latitude(), in.longitude(),
                in.place(), in.ayanamsa(), in.mode(), in.houseSystem());
    }
}
