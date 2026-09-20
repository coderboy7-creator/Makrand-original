package com.makaranda.calc.vedic;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.EphemerisEngine;
import com.makaranda.calc.ephemeris.EphemerisEngine.GeoPos;
import com.makaranda.calc.ephemeris.PanchangMode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ChartBuilder {

    private final EphemerisEngine engine = new EphemerisEngine();

    public record BirthInput(
            LocalDateTime localDateTime,
            String timeZone,
            double tzOffsetHours,
            double latitude,
            double longitude,
            String place,
            AyanamsaSystem ayanamsa,
            PanchangMode mode,
            String houseSystem
    ) {}

    public record PlanetBody(
            String name, String sanskrit, String glyph,
            double tropicalLon, double siderealLon, double latitude,
            int signIndex, String sign, String signSa,
            int house, String dignity, boolean retrograde,
            int nakshatraIndex, String nakshatra, int pada, String nakLord,
            String dms, String signDegree,
            String nameHi, String signHi, String nakshatraHi, String dignityHi
    ) {}

    public record FullChart(
            BirthInput input,
            double julianDayUt,
            double ayanamsaDeg,
            String ayanamsaLabel,
            String panchangMode,
            PlanetBody lagna,
            Map<String, PlanetBody> planets,
            double[] houseCuspsSidereal,
            Map<Integer, Map<String, Object>> vargas,
            Map<String, Double> vimshopaka
    ) {}

    public FullChart build(BirthInput in) {
        double tz = in.tzOffsetHours;
        if (in.timeZone != null && !in.timeZone.isBlank()) {
            try {
                ZonedDateTime z = in.localDateTime.atZone(ZoneId.of(in.timeZone));
                tz = z.getOffset().getTotalSeconds() / 3600.0;
            } catch (Exception ignored) {}
        }
        LocalDateTime lt = in.localDateTime;
        double jd = AstroMath.julianDayUt(lt.getYear(), lt.getMonthValue(), lt.getDayOfMonth(),
                lt.getHour(), lt.getMinute(), lt.getSecond() + lt.getNano() / 1e9, tz);

        AyanamsaSystem aySys = in.ayanamsa == null ? AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA : in.ayanamsa;
        PanchangMode mode = in.mode == null ? PanchangMode.SIDDHANTIC : in.mode;
        double ay = aySys.ayanamsa(jd);

        Map<String, GeoPos> geo = engine.compute(jd, mode);
        double tropAsc = engine.tropicalAscendant(jd, in.latitude, in.longitude);
        double sidAsc = AstroMath.norm360(tropAsc - ay);

        PlanetBody lagna = body("Lagna", "Lagna", "Asc", tropAsc, 0, sidAsc, false, sidAsc, 1);

        Map<String, PlanetBody> planets = new LinkedHashMap<>();
        int lagnaSign = AstroMath.signIndex(sidAsc);
        for (String name : VedicConstants.PLANETS) {
            GeoPos g = geo.get(name);
            double sid = AstroMath.norm360(g.lon() - ay);
            int house = AstroMath.houseFromLagna(lagnaSign, AstroMath.signIndex(sid));
            planets.put(name, body(name, sanskrit(name), glyph(name), g.lon(), g.lat(), sid, g.retrograde(), sidAsc, house));
        }

        double[] cusps = new double[13];
        String hs = in.houseSystem == null ? "WHOLE_SIGN" : in.houseSystem;
        if ("SRIPATI".equalsIgnoreCase(hs) || "PORPHYRY".equalsIgnoreCase(hs)) {
            double[] trop = engine.houseCuspsSripati(jd, in.latitude, in.longitude);
            for (int i = 1; i <= 12; i++) cusps[i] = AstroMath.norm360(trop[i] - ay);
        } else if ("EQUAL".equalsIgnoreCase(hs)) {
            for (int i = 1; i <= 12; i++) cusps[i] = AstroMath.norm360(sidAsc + (i - 1) * 30);
        } else {
            double start = lagnaSign * 30.0;
            for (int i = 1; i <= 12; i++) cusps[i] = AstroMath.norm360(start + (i - 1) * 30);
        }

        Map<Integer, Map<String, Object>> vargas = new LinkedHashMap<>();
        int[] divs = VedicConstants.VIMSHOPAKA_DIVS;
        for (int d : divs) {
            Map<String, Object> v = new LinkedHashMap<>();
            v.put("division", d);
            v.put("name", vargaName(d));
            List<Map<String, Object>> plist = new ArrayList<>();
            int vLagnaSign = VargaCalculator.vargaSign(sidAsc, d);
            Map<String, Object> lg = new LinkedHashMap<>();
            lg.put("name", "Lagna");
            lg.put("nameHi", "लग्न");
            lg.put("signIndex", vLagnaSign);
            lg.put("sign", VedicConstants.SIGNS_EN[vLagnaSign]);
            lg.put("signSa", VedicConstants.SIGNS_SA[vLagnaSign]);
            lg.put("signHi", VedicConstants.SIGNS_HI[vLagnaSign]);
            lg.put("longitude", VargaCalculator.vargaLongitude(sidAsc, d));
            lg.put("house", 1);
            plist.add(lg);
            for (PlanetBody p : planets.values()) {
                int vs = VargaCalculator.vargaSign(p.siderealLon, d);
                int house = AstroMath.houseFromLagna(vLagnaSign, vs);
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("name", p.name);
                row.put("nameHi", VedicConstants.planetHi(p.name));
                row.put("signIndex", vs);
                row.put("sign", VedicConstants.SIGNS_EN[vs]);
                row.put("signSa", VedicConstants.SIGNS_SA[vs]);
                row.put("signHi", VedicConstants.SIGNS_HI[vs]);
                row.put("longitude", VargaCalculator.vargaLongitude(p.siderealLon, d));
                row.put("house", house);
                String dig = VedicConstants.dignity(p.name, vs);
                row.put("dignity", dig);
                row.put("dignityHi", VedicConstants.dignityHi(dig));
                plist.add(row);
            }
            v.put("bodies", plist);
            vargas.put(d, v);
        }

        Map<String, Double> vimshopaka = new LinkedHashMap<>();
        for (String name : VedicConstants.PLANETS) {
            PlanetBody p = planets.get(name);
            double score = 0;
            for (int i = 0; i < divs.length; i++) {
                int vs = VargaCalculator.vargaSign(p.siderealLon, divs[i]);
                String dig = VedicConstants.dignity(name, vs);
                double w = switch (dig) {
                    case "Exalted", "Moolatrikona" -> 1.0;
                    case "Own Sign" -> 0.875;
                    case "Friendly" -> 0.75;
                    case "Neutral" -> 0.5;
                    case "Enemy" -> 0.25;
                    case "Debilitated" -> 0.0;
                    default -> 0.5;
                };
                score += VedicConstants.VIMSHOPAKA_WEIGHTS_D[i] * w;
            }
            vimshopaka.put(name, Math.round(score * 100.0) / 100.0);
        }

        return new FullChart(in, jd, ay, aySys.label, mode.name(), lagna, planets, cusps, vargas, vimshopaka);
    }

    private PlanetBody body(String name, String sa, String glyph, double trop, double lat, double sid,
                            boolean retro, double sidAsc, int house) {
        int sign = AstroMath.signIndex(sid);
        int nak = VedicConstants.nakshatraIndex(sid);
        String dig = "Lagna".equals(name) ? "—" : VedicConstants.dignity(name, sign);
        return new PlanetBody(
                name, sa, glyph, trop, sid, lat, sign,
                VedicConstants.SIGNS_EN[sign], VedicConstants.SIGNS_SA[sign],
                house, dig, retro, nak, VedicConstants.NAKSHATRAS[nak], VedicConstants.pada(sid),
                VedicConstants.NAK_LORDS[nak],
                AstroMath.dms(sid), AstroMath.signDegree(sid),
                VedicConstants.planetHi(name), VedicConstants.SIGNS_HI[sign],
                VedicConstants.nakHi(nak), VedicConstants.dignityHi(dig)
        );
    }

    private static String sanskrit(String name) {
        for (int i = 0; i < VedicConstants.PLANETS.length; i++) {
            if (VedicConstants.PLANETS[i].equals(name)) return VedicConstants.PLANETS_SA[i];
        }
        return name;
    }

    private static String glyph(String name) {
        for (int i = 0; i < VedicConstants.PLANETS.length; i++) {
            if (VedicConstants.PLANETS[i].equals(name)) return VedicConstants.PLANET_GLYPH[i];
        }
        return "";
    }

    public static String vargaName(int d) {
        return switch (d) {
            case 1 -> "Rasi (D1)";
            case 2 -> "Hora (D2)";
            case 3 -> "Drekkana (D3)";
            case 4 -> "Chaturthamsa (D4)";
            case 7 -> "Saptamsa (D7)";
            case 9 -> "Navamsa (D9)";
            case 10 -> "Dasamsa (D10)";
            case 12 -> "Dwadasamsa (D12)";
            case 16 -> "Shodasamsa (D16)";
            case 20 -> "Vimsamsa (D20)";
            case 24 -> "Siddhamsa / Chaturvimsamsa (D24)";
            case 27 -> "Bhamsa / Nakshatramsa (D27)";
            case 30 -> "Trimsamsa (D30)";
            case 40 -> "Khavedamsa (D40)";
            case 45 -> "Akshavedamsa (D45)";
            case 60 -> "Shashtiamsa (D60)";
            default -> "D" + d;
        };
    }
}
