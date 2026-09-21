package com.makaranda.calc.vedic;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.ephemeris.EphemerisEngine;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;
import com.makaranda.calc.vedic.ChartBuilder.PlanetBody;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parāśara Ṣaḍbala (virūpa) + Bhāva bala. Seven grahas only.
 * BPHS / B.V. Raman constants — not Astrotalk truncated numbers. No bija retune.
 *
 * Ṣaḍbala = Sthāna + Dig + Kāla + Ceṣṭā + Naisargika + Drik.
 * 1 rūpa = 60 virūpa. Required minima: Su 390, Mo 360, Ma 300, Me 420, Ju 390, Ve 330, Sa 300.
 */
public final class Shadbala {

    public static final String[] GRAHAS = {
            "Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn"
    };
    public static final Map<String, Double> NAISARGIKA = Map.of(
            "Sun", 60.0, "Moon", 51.43, "Mars", 17.14, "Mercury", 25.71,
            "Jupiter", 34.29, "Venus", 42.86, "Saturn", 8.57
    );
    /** BPHS required virūpa. */
    public static final Map<String, Double> REQUIRED = Map.of(
            "Sun", 390.0, "Moon", 360.0, "Mars", 300.0, "Mercury", 420.0,
            "Jupiter", 390.0, "Venus", 330.0, "Saturn", 300.0
    );
    private static final int[] SAPTAVARGA = {1, 2, 3, 7, 9, 12, 30};
    private static final String[] HORA_CYCLE = {
            "Sun", "Venus", "Mercury", "Moon", "Saturn", "Jupiter", "Mars"
    };
    private static final String[] VARA_LORD = {
            "Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn"
    };
    private static final int[] DIG_HOUSE = {
            /* Sun */ 7, /* Moon */ 4, /* Mars */ 7, /* Mercury */ 1,
            /* Jupiter */ 1, /* Venus */ 4, /* Saturn */ 10
    };
    private static final EphemerisEngine ENGINE = new EphemerisEngine();

    private Shadbala() {}

    public static Map<String, Object> fromChart(FullChart c) {
        LocalDateTime lt = c.input().localDateTime();
        double tz = c.input().tzOffsetHours();
        double lat = c.input().latitude();
        double lon = c.input().longitude();
        double jdNoon = AstroMath.julianDayUt(lt.getYear(), lt.getMonthValue(), lt.getDayOfMonth(),
                12, 0, 0, tz);
        double[] rs = ENGINE.sunriseSunsetLocal(jdNoon, lat, lon, tz);
        double sunrise = rs[0];
        double sunset = rs[1];
        double noon = rs[2];
        if (Double.isNaN(sunrise)) {
            sunrise = 6;
            sunset = 18;
            noon = 12;
        }
        double birthH = lt.getHour() + lt.getMinute() / 60.0 + lt.getSecond() / 3600.0;
        int vara = weekdaySun0(lt.toLocalDate());
        String dinaLord = VARA_LORD[vara];
        String horaLord = horaLord(sunrise, sunset, birthH, vara);
        String masaLord = VedicConstants.SIGN_LORDS[c.planets().get("Sun").signIndex()];
        String varshaLord = VARA_LORD[weekdaySun0(LocalDate.of(lt.getYear(), 4, 14))];

        double sunSid = c.planets().get("Sun").siderealLon();
        double moonSid = c.planets().get("Moon").siderealLon();
        double moonPaksha = pakshaBalaMoon(sunSid, moonSid);
        boolean shukla = AstroMath.norm360(moonSid - sunSid) < 180.0;

        Map<String, Double> nata = natonnata(birthH, noon);
        Map<String, Double> tri = tribhaga(birthH, sunrise, sunset);
        Map<String, Double> ayana = ayanaBala(c);
        Map<String, Double> drik = drikBala(c, shukla);

        Map<String, Object> grahas = new LinkedHashMap<>();
        Map<String, Double> totals = new LinkedHashMap<>();
        for (int i = 0; i < GRAHAS.length; i++) {
            String g = GRAHAS[i];
            PlanetBody p = c.planets().get(g);
            double uchcha = uchchaBala(g, p.siderealLon());
            double sapta = saptavargaja(g, p.siderealLon());
            double ojah = ojahBala(g, p.siderealLon());
            double kendra = kendradiBala(p.house());
            double drek = drekkanaBala(g, p.siderealLon());
            double sthana = uchcha + sapta + ojah + kendra + drek;

            double dig = digBala(i, p.house());

            double paksha = pakshaFor(g, moonPaksha, c, shukla);
            double ymdh = 0;
            if (g.equals(varshaLord)) ymdh += 15;
            if (g.equals(masaLord)) ymdh += 30;
            if (g.equals(dinaLord)) ymdh += 45;
            if (g.equals(horaLord)) ymdh += 60;
            double kala = nata.get(g) + paksha + tri.get(g) + ymdh + ayana.get(g);

            double chesta = chestaBala(g, p, sunSid, ayana.get(g), moonPaksha);
            double nais = NAISARGIKA.get(g);
            double dr = drik.getOrDefault(g, 0.0);

            double total = sthana + dig + kala + chesta + nais + dr;
            double req = REQUIRED.get(g);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("graha", g);
            row.put("grahaHi", VedicConstants.planetHi(g));
            row.put("sthana", rnd(sthana));
            row.put("uchcha", rnd(uchcha));
            row.put("saptavargaja", rnd(sapta));
            row.put("ojah", rnd(ojah));
            row.put("kendradi", rnd(kendra));
            row.put("drekkana", rnd(drek));
            row.put("dig", rnd(dig));
            row.put("kala", rnd(kala));
            row.put("natonnata", rnd(nata.get(g)));
            row.put("paksha", rnd(paksha));
            row.put("tribhaga", rnd(tri.get(g)));
            row.put("varshaMasaDinaHora", rnd(ymdh));
            row.put("ayana", rnd(ayana.get(g)));
            row.put("chesta", rnd(chesta));
            row.put("naisargika", rnd(nais));
            row.put("drik", rnd(dr));
            row.put("totalVirupa", rnd(total));
            row.put("rupa", rnd(total / 60.0));
            row.put("requiredVirupa", req);
            row.put("ratio", rnd(total / req));
            row.put("full", total + 1e-6 >= req);
            grahas.put(g, row);
            totals.put(g, rnd(total));
        }

        List<Map<String, Object>> bhavas = bhavaBala(c, grahas);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("system", "Parashara");
        out.put("unit", "virupa");
        out.put("note", "Ṣaḍbala in virūpa (60 = 1 rūpa). BPHS minima; not Astrotalk. Yuddha omitted unless planets share a degree.");
        out.put("noteHi", "षड्बल विरूप में (६० = १ रूप)। पराशर न्यूनतम; Astrotalk नहीं। युद्धबल तभी जब ग्रह एक अंश पर हों।");
        out.put("horaLord", horaLord);
        out.put("dinaLord", dinaLord);
        out.put("masaLord", masaLord);
        out.put("varshaLord", varshaLord);
        out.put("grahas", grahas);
        out.put("totals", totals);
        out.put("bhavas", bhavas);
        return out;
    }

    public static double uchchaBala(String planet, double siderealLon) {
        Integer sign = VedicConstants.EXALTATION_SIGN.get(planet);
        Double deg = VedicConstants.EXALTATION_DEG.get(planet);
        if (sign == null || deg == null) return 0;
        double exalt = sign * 30.0 + deg;
        double deb = AstroMath.norm360(exalt + 180.0);
        double fromDeb = Math.abs(AstroMath.norm180(siderealLon - deb));
        return 60.0 * fromDeb / 180.0;
    }

    public static double digBala(int grahaIndex, int house1to12) {
        int ideal = DIG_HOUSE[grahaIndex];
        int d = Math.abs(house1to12 - ideal);
        d = Math.min(d, 12 - d);
        return 60.0 * (1.0 - d / 6.0);
    }

    public static double kendradiBala(int house) {
        int r = ((house - 1) % 3);
        if (r == 0) return 60;      // 1,4,7,10
        if (r == 1) return 30;      // 2,5,8,11
        return 15;                  // 3,6,9,12
    }

    static double saptavargaja(String planet, double siderealLon) {
        double s = 0;
        for (int d : SAPTAVARGA) {
            int sign = VargaCalculator.vargaSign(siderealLon, d);
            s += vargaDignityVirupa(planet, sign);
        }
        return s;
    }

    static double vargaDignityVirupa(String planet, int sign) {
        String dig = VedicConstants.dignity(planet, sign);
        String lord = VedicConstants.SIGN_LORDS[sign];
        return switch (dig) {
            case "Exalted", "Moolatrikona" -> 45;
            case "Own Sign" -> 30;
            case "Friendly" -> mutualFriend(planet, lord) ? 20 : 15;
            case "Neutral" -> 10;
            case "Enemy" -> mutualEnemy(planet, lord) ? 2 : 4;
            case "Debilitated" -> 2;
            default -> 10;
        };
    }

    static double ojahBala(String planet, double siderealLon) {
        boolean oddRasi = AstroMath.signIndex(siderealLon) % 2 == 0; // Aries = odd
        int nSign = VargaCalculator.vargaSign(siderealLon, 9);
        boolean oddNav = nSign % 2 == 0;
        boolean male = planet.equals("Sun") || planet.equals("Mars") || planet.equals("Jupiter");
        double v = 0;
        if (male) {
            if (oddRasi) v += 15;
            if (oddNav) v += 15;
        } else {
            if (!oddRasi) v += 15;
            if (!oddNav) v += 15;
        }
        return v;
    }

    static double drekkanaBala(String planet, double siderealLon) {
        double within = AstroMath.norm360(siderealLon) % 30.0;
        int part = (int) Math.floor(within / 10.0); // 0,1,2
        boolean male = planet.equals("Sun") || planet.equals("Mars") || planet.equals("Jupiter");
        boolean merc = planet.equals("Mercury");
        if (male && part == 0) return 15;
        if (merc && part == 1) return 15;
        if (!male && !merc && part == 2) return 15;
        return 0;
    }

    static Map<String, Double> natonnata(double birthH, double noon) {
        double dist = Math.abs(birthH - noon);
        dist = Math.min(dist, 24 - dist);
        double diurnal = 60.0 * (1.0 - dist / 12.0);
        diurnal = AstroMath.clamp(diurnal, 0, 60);
        double nocturnal = 60.0 - diurnal;
        Map<String, Double> m = new LinkedHashMap<>();
        m.put("Sun", diurnal);
        m.put("Jupiter", diurnal);
        m.put("Saturn", diurnal);
        m.put("Moon", nocturnal);
        m.put("Mars", nocturnal);
        m.put("Venus", nocturnal);
        m.put("Mercury", 60.0);
        return m;
    }

    static double pakshaBalaMoon(double sunSid, double moonSid) {
        double elong = Math.abs(AstroMath.norm180(moonSid - sunSid));
        return 60.0 * elong / 180.0;
    }

    static double pakshaFor(String g, double moonPaksha, FullChart c, boolean shukla) {
        boolean malefic = g.equals("Sun") || g.equals("Mars") || g.equals("Saturn");
        if (g.equals("Moon")) return moonPaksha;
        if (g.equals("Mercury")) {
            PlanetBody mer = c.planets().get("Mercury");
            boolean withMal = false;
            for (String m : List.of("Sun", "Mars", "Saturn")) {
                if (c.planets().get(m).signIndex() == mer.signIndex()) withMal = true;
            }
            malefic = withMal;
        }
        if (g.equals("Venus") || g.equals("Jupiter")) malefic = false;
        return malefic ? 60.0 - moonPaksha : moonPaksha;
    }

    static Map<String, Double> tribhaga(double birthH, double sunrise, double sunset) {
        Map<String, Double> m = new LinkedHashMap<>();
        for (String g : GRAHAS) m.put(g, 0.0);
        m.put("Jupiter", 60.0);
        boolean day = birthH >= sunrise && birthH < sunset;
        if (day) {
            double span = Math.max(0.05, sunset - sunrise);
            double f = (birthH - sunrise) / span;
            String lord = f < 1.0 / 3 ? "Mercury" : f < 2.0 / 3 ? "Sun" : "Saturn";
            m.put(lord, 60.0);
        } else {
            double nightStart = sunset;
            double birth = birthH < sunrise ? birthH + 24 : birthH;
            double nightLen = (sunrise + 24) - sunset;
            double f = (birth - nightStart) / Math.max(0.05, nightLen);
            String lord = f < 1.0 / 3 ? "Moon" : f < 2.0 / 3 ? "Venus" : "Mars";
            m.put(lord, 60.0);
        }
        return m;
    }

    static Map<String, Double> ayanaBala(FullChart c) {
        Map<String, Double> m = new LinkedHashMap<>();
        double eps = 23.4392911;
        for (String g : GRAHAS) {
            double trop = c.planets().get(g).tropicalLon();
            double dec = AstroMath.asind(AstroMath.sind(eps) * AstroMath.sind(trop));
            double north = 60.0 * (eps + dec) / (2 * eps);
            north = AstroMath.clamp(north, 0, 60);
            boolean southFavored = g.equals("Moon") || g.equals("Saturn");
            m.put(g, southFavored ? 60.0 - north : north);
        }
        return m;
    }

    static double chestaBala(String g, PlanetBody p, double sunSid, double ayana, double moonPaksha) {
        if (g.equals("Sun")) return ayana;
        if (g.equals("Moon")) return moonPaksha;
        if (p.retrograde()) return 60;
        double ck = Math.abs(AstroMath.norm180(p.siderealLon() - sunSid));
        return 60.0 * ck / 180.0;
    }

    static Map<String, Double> drikBala(FullChart c, boolean shukla) {
        Map<String, Double> m = new LinkedHashMap<>();
        for (String g : GRAHAS) m.put(g, 0.0);
        for (String target : GRAHAS) {
            int th = c.planets().get(target).house();
            double sum = 0;
            for (String src : GRAHAS) {
                if (src.equals(target)) continue;
                int sh = c.planets().get(src).house();
                double w = aspectWeight(src, sh, th);
                if (w == 0) continue;
                sum += 60.0 * w * (benefic(src, c, shukla) ? 1 : -1);
            }
            m.put(target, AstroMath.clamp(sum, -60, 60));
        }
        return m;
    }

    /** House-count 1–12 from src house to target house. */
    static double aspectWeight(String src, int srcHouse, int tgtHouse) {
        int count = ((tgtHouse - srcHouse + 12) % 12) + 1;
        if (count == 1) return 0;
        double w = switch (count) {
            case 7 -> 1.0;
            case 4, 8 -> 0.75;
            case 5, 9 -> 0.50;
            case 3, 10 -> 0.25;
            default -> 0.0;
        };
        if (src.equals("Mars") && (count == 4 || count == 8)) w = 1.0;
        if (src.equals("Jupiter") && (count == 5 || count == 9)) w = 1.0;
        if (src.equals("Saturn") && (count == 3 || count == 10)) w = 1.0;
        return w;
    }

    static boolean benefic(String g, FullChart c, boolean shukla) {
        if (g.equals("Jupiter") || g.equals("Venus")) return true;
        if (g.equals("Sun") || g.equals("Mars") || g.equals("Saturn")) return false;
        if (g.equals("Moon")) return shukla;
        PlanetBody mer = c.planets().get("Mercury");
        for (String m : List.of("Sun", "Mars", "Saturn")) {
            if (c.planets().get(m).signIndex() == mer.signIndex()) return false;
        }
        return true;
    }

    public static String horaLord(double sunrise, double sunset, double birthH, int varaSun0) {
        double dayLen = Math.max(0.05, sunset - sunrise);
        double nightLen = Math.max(0.05, 24.0 - dayLen);
        int idx;
        if (birthH >= sunrise && birthH < sunset) {
            idx = (int) Math.floor((birthH - sunrise) / (dayLen / 12.0));
        } else {
            double fromSet = birthH >= sunset ? birthH - sunset : birthH + 24 - sunset;
            idx = 12 + (int) Math.floor(fromSet / (nightLen / 12.0));
        }
        idx = Math.max(0, Math.min(23, idx));
        int start = indexOf(HORA_CYCLE, VARA_LORD[varaSun0]);
        return HORA_CYCLE[(start + idx) % 7];
    }

    public static int weekdaySun0(LocalDate d) {
        // Java Mon=1 … Sun=7 → Sun=0
        DayOfWeek w = d.getDayOfWeek();
        return w == DayOfWeek.SUNDAY ? 0 : w.getValue();
    }

    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> bhavaBala(FullChart c, Map<String, Object> grahas) {
        List<Map<String, Object>> list = new ArrayList<>();
        int lagna = c.lagna().signIndex();
        for (int h = 1; h <= 12; h++) {
            int sign = (lagna + h - 1) % 12;
            String lord = VedicConstants.SIGN_LORDS[sign];
            Map<String, Object> g = (Map<String, Object>) grahas.get(lord);
            double adhi = g == null ? 0 : ((Number) g.get("totalVirupa")).doubleValue();
            // 10th = 60, 4th = 0, 1st/7th = 30 (cosine from MC)
            double ang = (h - 10) * 30.0;
            double dig = 30.0 + 30.0 * AstroMath.cosd(ang);
            double drishti = 0;
            for (String src : GRAHAS) {
                int sh = c.planets().get(src).house();
                drishti += 60.0 * aspectWeight(src, sh, h) * 0.25;
            }
            double total = adhi + dig + drishti;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("house", h);
            row.put("signIndex", sign);
            row.put("sign", VedicConstants.SIGNS_EN[sign]);
            row.put("signHi", VedicConstants.SIGNS_HI[sign]);
            row.put("lord", lord);
            row.put("lordHi", VedicConstants.planetHi(lord));
            row.put("adhipati", rnd(adhi));
            row.put("dig", rnd(dig));
            row.put("drishti", rnd(drishti));
            row.put("totalVirupa", rnd(total));
            row.put("rupa", rnd(total / 60.0));
            list.add(row);
        }
        return list;
    }

    private static boolean mutualFriend(String a, String b) {
        List<String> fa = VedicConstants.NATURAL_FRIENDS.getOrDefault(a, List.of());
        List<String> fb = VedicConstants.NATURAL_FRIENDS.getOrDefault(b, List.of());
        return fa.contains(b) && fb.contains(a);
    }

    private static boolean mutualEnemy(String a, String b) {
        List<String> ea = VedicConstants.NATURAL_ENEMIES.getOrDefault(a, List.of());
        List<String> eb = VedicConstants.NATURAL_ENEMIES.getOrDefault(b, List.of());
        return ea.contains(b) && eb.contains(a);
    }

    private static int indexOf(String[] a, String v) {
        for (int i = 0; i < a.length; i++) if (a[i].equals(v)) return i;
        return 0;
    }

    private static double rnd(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
