package com.makaranda.calc.vedic;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;
import com.makaranda.calc.vedic.ChartBuilder.PlanetBody;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Krishnamurti Paddhati as a <em>mode</em> on the same chart — not a second app.
 * Star-lord = nakṣatra lord. Sub-lord = Vimśottarī proportional slice of that
 * nakṣatra. Cusps are Placidus (sidereal). Default remains whole-sign Makaranda.
 */
public final class KpSystem {
    private KpSystem() {}

    public record Division(
            String starLord, String starLordHi,
            String subLord, String subLordHi,
            String signLord, String signLordHi,
            int nakIndex, String nakshatra, String nakshatraHi,
            int signIndex, String sign, String signHi,
            String dms
    ) {}

    public static boolean placidusHouses(String houseSystem) {
        if (houseSystem == null) return false;
        String h = houseSystem.trim().toUpperCase().replace('-', '_');
        return "PLACIDUS".equals(h) || "KP".equals(h) || "KP_PLACIDUS".equals(h);
    }

    public static Division of(double siderealLon) {
        double lon = AstroMath.norm360(siderealLon);
        int sign = AstroMath.signIndex(lon);
        int nak = VedicConstants.nakshatraIndex(lon);
        String star = VedicConstants.NAK_LORDS[nak];
        String sub = subLord(lon, star);
        String signLord = VedicConstants.SIGN_LORDS[sign];
        return new Division(
                star, VedicConstants.planetHi(star),
                sub, VedicConstants.planetHi(sub),
                signLord, VedicConstants.planetHi(signLord),
                nak, VedicConstants.NAKSHATRAS[nak], VedicConstants.nakHi(nak),
                sign, VedicConstants.SIGNS_EN[sign], VedicConstants.SIGNS_HI[sign],
                AstroMath.dms(lon)
        );
    }

    /** Sub-lord inside the nakṣatra, Vimśottarī years / 120 starting at the star-lord. */
    public static String subLord(double siderealLon, String starLord) {
        double within = AstroMath.norm360(siderealLon) % AstroMath.NAKSHATRA_SPAN;
        double frac = within / AstroMath.NAKSHATRA_SPAN;
        int start = lordIndex(starLord);
        double acc = 0;
        for (int i = 0; i < 9; i++) {
            int li = (start + i) % 9;
            double w = VedicConstants.VIMSHOTTARI_YEARS[li] / 120.0;
            if (frac < acc + w - 1e-12) return VedicConstants.VIMSHOTTARI_LORDS[li];
            acc += w;
        }
        return VedicConstants.VIMSHOTTARI_LORDS[(start + 8) % 9];
    }

    public static Map<String, Object> tables(FullChart c, double[] placidusSiderealCusps) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("system", "KP");
        out.put("houseSystem", "PLACIDUS");
        out.put("ayanamsa", c.ayanamsaLabel());
        out.put("ayanamsaId", c.input().ayanamsa() == null ? "" : c.input().ayanamsa().name());
        out.put("defaultPaddhati", false);
        out.put("noteHi", "कृष्णमूर्ति पद्धति एक सेटिंग है, दूसरी ऐप नहीं। मूल मकरन्द पूर्ण-राशि कुंडली नहीं बदलती।");
        out.put("note", "KP is a setting on this monolith — not a second app. Default remains whole-sign Makaranda.");

        double[] cusps = placidusSiderealCusps;
        List<Map<String, Object>> cuspRows = new ArrayList<>();
        for (int h = 1; h <= 12; h++) {
            Division d = of(cusps[h]);
            Map<String, Object> row = rowOf("Cusp " + h, "भाव " + h, cusps[h], d, h);
            row.put("house", h);
            cuspRows.add(row);
        }
        out.put("cusps", cuspRows);

        List<Map<String, Object>> bodies = new ArrayList<>();
        Division lagnaDiv = of(c.lagna().siderealLon());
        int lagnaH = AstroMath.houseFromCusps(c.lagna().siderealLon(), cusps);
        bodies.add(rowOf("Lagna", "लग्न", c.lagna().siderealLon(), lagnaDiv, lagnaH));
        for (String name : VedicConstants.PLANETS) {
            PlanetBody p = c.planets().get(name);
            if (p == null) continue;
            Division d = of(p.siderealLon());
            int house = AstroMath.houseFromCusps(p.siderealLon(), cusps);
            bodies.add(rowOf(p.name(), VedicConstants.planetHi(p.name()), p.siderealLon(), d, house));
        }
        out.put("bodies", bodies);
        return out;
    }

    private static Map<String, Object> rowOf(String name, String nameHi, double lon, Division d, int house) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("name", name);
        row.put("nameHi", nameHi);
        row.put("longitude", Math.round(lon * 1_000_000.0) / 1_000_000.0);
        row.put("dms", d.dms());
        row.put("sign", d.sign());
        row.put("signHi", d.signHi());
        row.put("signIndex", d.signIndex());
        row.put("signLord", d.signLord());
        row.put("signLordHi", d.signLordHi());
        row.put("nakshatra", d.nakshatra());
        row.put("nakshatraHi", d.nakshatraHi());
        row.put("starLord", d.starLord());
        row.put("starLordHi", d.starLordHi());
        row.put("subLord", d.subLord());
        row.put("subLordHi", d.subLordHi());
        row.put("house", house);
        return row;
    }

    private static int lordIndex(String name) {
        for (int i = 0; i < VedicConstants.VIMSHOTTARI_LORDS.length; i++) {
            if (VedicConstants.VIMSHOTTARI_LORDS[i].equalsIgnoreCase(name)) return i;
        }
        return 0;
    }
}
