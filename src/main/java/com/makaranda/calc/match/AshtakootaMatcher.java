package com.makaranda.calc.match;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;
import com.makaranda.calc.vedic.ChartBuilder.PlanetBody;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AshtakootaMatcher {
    private AshtakootaMatcher() {}

    public static Map<String, Object> match(FullChart boy, FullChart girl) {
        PlanetBody bMoon = boy.planets().get("Moon");
        PlanetBody gMoon = girl.planets().get("Moon");
        PlanetBody bMars = boy.planets().get("Mars");
        PlanetBody gMars = girl.planets().get("Mars");
        int bR = bMoon.signIndex();
        int gR = gMoon.signIndex();
        int bN = bMoon.nakshatraIndex();
        int gN = gMoon.nakshatraIndex();

        double varna = varna(bR, gR);
        double vashya = vashya(bR, gR);
        double tara = tara(bN, gN);
        double yoni = yoni(bN, gN);
        double maitri = grahaMaitri(bR, gR);
        double gana = gana(bN, gN);
        double bhakoot = bhakoot(bR, gR);
        double nadi = nadi(bN, gN);
        double total = varna + vashya + tara + yoni + maitri + gana + bhakoot + nadi;

        Map<String, Object> mangal = mangalDosha(boy, girl);

        List<Map<String, Object>> kootas = new ArrayList<>();
        kootas.add(koota("Varna", 1, varna, "Spiritual / ego compatibility"));
        kootas.add(koota("Vashya", 2, vashya, "Mutual attraction and control"));
        kootas.add(koota("Tara", 3, tara, "Destiny / birth star compatibility"));
        kootas.add(koota("Yoni", 4, yoni, "Physical and sexual compatibility"));
        kootas.add(koota("Graha Maitri", 5, maitri, "Mental friendship of Moon lords"));
        kootas.add(koota("Gana", 6, gana, "Temperament — Deva / Manushya / Rakshasa"));
        kootas.add(koota("Bhakoot", 7, bhakoot, "Rashi love, wealth and family harmony"));
        kootas.add(koota("Nadi", 8, nadi, "Traditional nadi koota (Ayurvedic name) — not a medical or genetic test"));

        String verdict;
        if (total >= 32) verdict = "Uttama — Excellent match";
        else if (total >= 24) verdict = "Madhyama — Good match, proceed with rituals as advised";
        else if (total >= 18) verdict = "Adhama — Average; examine doshas and dasha overlap";
        else verdict = "Not recommended without strong remedies and family counsel";

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("total", total);
        out.put("max", 36);
        out.put("percent", Math.round(total / 36.0 * 1000) / 10.0);
        out.put("verdict", verdict);
        out.put("kootas", kootas);
        out.put("mangalDosha", mangal);
        out.put("boyMoon", Map.of("sign", bMoon.signSa(), "nakshatra", bMoon.nakshatra(), "pada", bMoon.pada()));
        out.put("girlMoon", Map.of("sign", gMoon.signSa(), "nakshatra", gMoon.nakshatra(), "pada", gMoon.pada()));
        out.put("disclaimer", "Ashtakoota is parampara arithmetic — not a marriage licence, medical opinion, or court finding.");
        out.put("disclaimerHi", "अष्टकूट परम्परा गणित है — विवाह-लाइसेंस, चिकित्सा राय या न्यायालय निर्णय नहीं।");
        out.put("notes", List.of(
                "Nadi dosha is traditionally considered cancellable if other kootas are strong and Rasi lords are friendly.",
                "Mangal dosha cancellation: Mars in own/exaltation, or both charts manglik, or Saturn/Rahu occupying same houses.",
                "Mithilanchal custom: Maithil marriages also weigh gotra, pravara and village exogamy beyond Guna milan.",
                "Guna milan is not a marriage licence, medical opinion, or court finding."
        ));
        return out;
    }

    private static Map<String, Object> koota(String name, double max, double score, String meaning) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", name);
        m.put("max", max);
        m.put("score", score);
        m.put("meaning", meaning);
        return m;
    }

    private static double varna(int b, int g) {
        int[] v = {1, 0, 1, 3, 1, 0, 3, 0, 1, 0, 3, 2}; // Brahmin=3 Kshatriya=2 Vaishya=1 Shudra=0 by rashi tradition
        // Standard: Cancer/Scorpio/Pisces Brahmin; Aries/Leo/Sag Kshatriya; Taurus/Virgo/Cap Vaishya; Gemini/Libra/Aqua Shudra
        int[] vv = {2, 1, 0, 3, 2, 1, 0, 3, 2, 1, 0, 3};
        return vv[g] <= vv[b] ? 1 : 0;
    }

    private static double vashya(int b, int g) {
        // Groups: quadruped 0, human 1, watery 2, insect 3, wild 4
        int[] grp = {0, 0, 1, 2, 0, 1, 1, 3, 0, 2, 1, 2};
        if (b == g) return 2;
        if (grp[b] == grp[g]) return 1;
        // special: Aries-Scorpio, Taurus-Libra etc.
        if ((b == 0 && g == 7) || (b == 1 && g == 6)) return 1.5;
        return 0;
    }

    private static double tara(int bN, int gN) {
        int d1 = ((gN - bN + 27) % 27) + 1;
        int d2 = ((bN - gN + 27) % 27) + 1;
        int m1 = d1 % 9; if (m1 == 0) m1 = 9;
        int m2 = d2 % 9; if (m2 == 0) m2 = 9;
        double s = 0;
        if (m1 == 1 || m1 == 3 || m1 == 5 || m1 == 7) s += 1.5;
        if (m2 == 1 || m2 == 3 || m2 == 5 || m2 == 7) s += 1.5;
        return s;
    }

    private static final int[] YONI = {
            0, 1, 2, 3, 3, 4, 5, 2, 5, 6, 6, 7, 8, 9, 8, 9, 10, 10, 4, 11, 12, 11, 13, 0, 13, 7, 1
    };
    private static final int[] YONI_GENDER = {
            0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 1, 1
    };

    private static double yoni(int bN, int gN) {
        if (YONI[bN] == YONI[gN]) {
            return YONI_GENDER[bN] != YONI_GENDER[gN] ? 4 : 2;
        }
        // enemy yonis (simplified)
        if (enemyYoni(YONI[bN], YONI[gN])) return 0;
        return 2;
    }

    private static boolean enemyYoni(int a, int b) {
        int x = Math.min(a, b), y = Math.max(a, b);
        return (x == 0 && y == 13) || (x == 3 && y == 4) || (x == 6 && y == 12) || (x == 9 && y == 10);
    }

    private static double grahaMaitri(int bR, int gR) {
        String bl = VedicConstants.SIGN_LORDS[bR];
        String gl = VedicConstants.SIGN_LORDS[gR];
        if (bl.equals(gl)) return 5;
        var fr = VedicConstants.NATURAL_FRIENDS.getOrDefault(bl, List.of());
        var en = VedicConstants.NATURAL_ENEMIES.getOrDefault(bl, List.of());
        if (fr.contains(gl)) return 4;
        if (en.contains(gl)) return 0.5;
        return 3;
    }

    private static double gana(int bN, int gN) {
        String bg = VedicConstants.NAK_GANA[bN];
        String gg = VedicConstants.NAK_GANA[gN];
        if (bg.equals(gg)) return 6;
        if ((bg.equals("Deva") && gg.equals("Manushya")) || (bg.equals("Manushya") && gg.equals("Deva"))) return 5;
        if ((bg.equals("Deva") && gg.equals("Rakshasa")) || (bg.equals("Rakshasa") && gg.equals("Deva"))) return 1;
        return 0;
    }

    private static double bhakoot(int b, int g) {
        int d = Math.abs(b - g);
        int rel = Math.min(d, 12 - d);
        // 6/8 and 2/12 traditionally 0
        int circ = ((g - b + 12) % 12) + 1;
        if (circ == 6 || circ == 8 || circ == 2 || circ == 12) return 0;
        return 7;
    }

    private static double nadi(int bN, int gN) {
        return VedicConstants.NAK_NADI[bN].equals(VedicConstants.NAK_NADI[gN]) ? 0 : 8;
    }

    private static Map<String, Object> mangalDosha(FullChart boy, FullChart girl) {
        boolean bm = isManglik(boy);
        boolean gm = isManglik(girl);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("boyManglik", bm);
        m.put("girlManglik", gm);
        m.put("cancelled", bm && gm);
        m.put("note", bm && gm
                ? "Both manglik — dosha is considered mutually cancelled."
                : (bm || gm)
                ? "Mangal dosha present on one side. Check Mars house, aspects of Jupiter and Saturn, and Navamsa."
                : "No classical Kuja dosha.");
        m.put("boyMarsHouse", boy.planets().get("Mars").house());
        m.put("girlMarsHouse", girl.planets().get("Mars").house());
        return m;
    }

    public static boolean isManglik(FullChart c) {
        int h = c.planets().get("Mars").house();
        // 1, 2, 4, 7, 8, 12 — South Indian often skips 2
        boolean raw = h == 1 || h == 2 || h == 4 || h == 7 || h == 8 || h == 12;
        if (!raw) return false;
        String dig = c.planets().get("Mars").dignity();
        if ("Exalted".equals(dig) || "Own Sign".equals(dig) || "Moolatrikona".equals(dig)) return false;
        return true;
    }
}
