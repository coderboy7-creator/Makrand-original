package com.makaranda.calc.transit;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.EphemerisEngine;
import com.makaranda.calc.ephemeris.EphemerisEngine.GeoPos;
import com.makaranda.calc.ephemeris.PanchangMode;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TransitCalculator {
    private final EphemerisEngine engine = new EphemerisEngine();

    public Map<String, Object> gochar(FullChart natal, LocalDate date, AyanamsaSystem ay, PanchangMode mode) {
        double tz = natal.input().tzOffsetHours();
        double jd = AstroMath.julianDayUt(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 12, 0, 0, tz);
        double ayan = ay.ayanamsa(jd);
        Map<String, GeoPos> geo = engine.compute(jd, mode);

        int moonSign = natal.planets().get("Moon").signIndex();
        int lagnaSign = natal.lagna().signIndex();

        List<Map<String, Object>> transits = new ArrayList<>();
        for (String name : VedicConstants.PLANETS) {
            double sid = AstroMath.norm360(geo.get(name).lon() - ayan);
            int sign = AstroMath.signIndex(sid);
            int fromMoon = ((sign - moonSign + 12) % 12) + 1;
            int fromLagna = ((sign - lagnaSign + 12) % 12) + 1;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("planet", name);
            row.put("sign", VedicConstants.SIGNS_SA[sign]);
            row.put("signEn", VedicConstants.SIGNS_EN[sign]);
            row.put("longitude", AstroMath.signDegree(sid));
            row.put("houseFromMoon", fromMoon);
            row.put("houseFromLagna", fromLagna);
            row.put("retrograde", geo.get(name).retrograde());
            row.put("effect", transitEffect(name, fromMoon));
            transits.add(row);
        }

        Map<String, Object> sadeSati = sadeSati(moonSign, transits);
        Map<String, Object> kantaka = kantakaShani(moonSign, transits);
        Map<String, Object> guruGochar = guru(lagnaSign, moonSign, transits);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("date", date.toString());
        out.put("ayanamsa", ay.label);
        out.put("mode", mode.name());
        out.put("natalMoon", VedicConstants.SIGNS_SA[moonSign]);
        out.put("natalLagna", VedicConstants.SIGNS_SA[lagnaSign]);
        out.put("planets", transits);
        out.put("sadeSati", sadeSati);
        out.put("kantakaShani", kantaka);
        out.put("jupiter", guruGochar);
        return out;
    }

    private Map<String, Object> sadeSati(int moonSign, List<Map<String, Object>> transits) {
        int satSign = -1;
        for (Map<String, Object> p : transits) {
            if ("Saturn".equals(p.get("planet"))) satSign = indexSign((String) p.get("sign"));
        }
        int rel = ((satSign - moonSign + 12) % 12) + 1;
        boolean active = rel == 12 || rel == 1 || rel == 2;
        String phase = switch (rel) {
            case 12 -> "Rising (12th from Moon) — beginning, fatigue, endings";
            case 1 -> "Peak (over natal Moon) — mind, mother, homeland under pressure";
            case 2 -> "Setting (2nd from Moon) — speech, wealth, family tests";
            default -> "Not in Sade Sati";
        };
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("active", active);
        m.put("houseFromMoon", rel);
        m.put("phase", phase);
        m.put("ashtamaShani", rel == 8);
        m.put("ardhashtama", rel == 4);
        m.put("remedy", active
                ? "Shani mantra, sesame oil lamp on Saturdays, serve the elderly, blue sapphire only after full chart check."
                : "No Sade Sati. Watch Ashtama/Ardhashtama separately.");
        return m;
    }

    private Map<String, Object> kantakaShani(int moonSign, List<Map<String, Object>> transits) {
        int rel = 1;
        for (Map<String, Object> p : transits) {
            if ("Saturn".equals(p.get("planet"))) rel = (Integer) p.get("houseFromMoon");
        }
        boolean k = rel == 4 || rel == 7 || rel == 10;
        return Map.of("active", k, "houseFromMoon", rel,
                "note", k ? "Kantaka Shani — Saturn transiting kendra from Moon. Friction in home, marriage or career." : "Not Kantaka.");
    }

    private Map<String, Object> guru(int lagna, int moon, List<Map<String, Object>> transits) {
        int relL = 1, relM = 1, sign = 0;
        boolean retro = false;
        for (Map<String, Object> p : transits) {
            if ("Jupiter".equals(p.get("planet"))) {
                relL = (Integer) p.get("houseFromLagna");
                relM = (Integer) p.get("houseFromMoon");
                sign = indexSign((String) p.get("sign"));
                retro = Boolean.TRUE.equals(p.get("retrograde"));
            }
        }
        boolean auspicious = relL == 2 || relL == 5 || relL == 7 || relL == 9 || relL == 11
                || relM == 2 || relM == 5 || relM == 7 || relM == 9 || relM == 11;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("sign", VedicConstants.SIGNS_SA[sign]);
        m.put("houseFromLagna", relL);
        m.put("houseFromMoon", relM);
        m.put("retrograde", retro);
        m.put("auspicious", auspicious);
        m.put("note", auspicious
                ? "Guru gochar supports growth, learning, marriage and dharma this year."
                : "Jupiter is not in a classic shubha house from Lagna/Moon. Prefer inner work over expansion.");
        return m;
    }

    private static int indexSign(String sa) {
        for (int i = 0; i < VedicConstants.SIGNS_SA.length; i++) {
            if (VedicConstants.SIGNS_SA[i].equals(sa)) return i;
        }
        return 0;
    }

    private static String transitEffect(String planet, int fromMoon) {
        return switch (planet) {
            case "Saturn" -> switch (fromMoon) {
                case 1, 2, 12 -> "Heavy Sade Sati period — patience, duty, structural change.";
                case 8 -> "Ashtama Shani — hidden fears, chronic tests, occult insight.";
                case 4, 7, 10 -> "Kantaka — pressure on home, spouse or profession.";
                case 3, 6, 11 -> "Upachaya — slow but constructive labour bears fruit.";
                default -> "Neutral-to-restraining Saturn transit.";
            };
            case "Jupiter" -> switch (fromMoon) {
                case 2, 5, 7, 9, 11 -> "Benefic expansion, teachers, children, fortune.";
                case 6, 8, 12 -> "Wisdom through service, loss or retreat.";
                default -> "Moderate Jupiter influence.";
            };
            case "Mars" -> fromMoon == 1 || fromMoon == 3 || fromMoon == 6 || fromMoon == 10 || fromMoon == 11
                    ? "Energy, conflict potential, initiative."
                    : "Watch accidents and inflammation; channel heat.";
            case "Rahu" -> "Ambition, foreign, unconventional — amplify the house it transits.";
            case "Ketu" -> "Detachment, past-life skill, spiritualisation of the house.";
            case "Sun" -> "Focus, authority, vitality in this house for ~1 month.";
            case "Moon" -> "Daily mind and mood; honour the rashi for emotional weather.";
            case "Venus" -> "Comforts, arts, relationships colour this house.";
            case "Mercury" -> "Commerce, speech and study; good for contracts if unafflicted.";
            default -> "";
        };
    }
}
