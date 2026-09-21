package com.makaranda.calc.yoga;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;
import com.makaranda.calc.vedic.ChartBuilder.PlanetBody;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class YogaDetector {
    private YogaDetector() {}

    public static Map<String, Object> analyse(FullChart c) {
        List<Map<String, Object>> yogas = new ArrayList<>();
        List<Map<String, Object>> doshas = new ArrayList<>();

        PlanetBody moon = c.planets().get("Moon");
        PlanetBody jup = c.planets().get("Jupiter");
        PlanetBody sun = c.planets().get("Sun");
        PlanetBody mer = c.planets().get("Mercury");
        PlanetBody ven = c.planets().get("Venus");
        PlanetBody mar = c.planets().get("Mars");
        PlanetBody sat = c.planets().get("Saturn");
        PlanetBody rahu = c.planets().get("Rahu");
        PlanetBody ketu = c.planets().get("Ketu");

        // Gajakesari: Jupiter in kendra from Moon
        if (kendraFrom(moon.house(), jup.house())) {
            yogas.add(yoga("Gajakesari Yoga", "Raja", "Jupiter in a kendra from the Moon. Wisdom, fame and stability."));
        }
        // Budhaditya
        if (sun.signIndex() == mer.signIndex()) {
            yogas.add(yoga("Budhaditya Yoga", "Dhana / Buddhi", "Sun and Mercury conjunct. Intelligence, speech and skill."));
        }
        // Malavya (Venus in kendra in own/exalt Taurus, Libra, Pisces)
        if (kendra(ven.house()) && ("Own Sign".equals(ven.dignity()) || "Exalted".equals(ven.dignity()) || "Moolatrikona".equals(ven.dignity()))) {
            yogas.add(yoga("Malavya Yoga (Panch Mahapurusha)", "Raja", "Venus strong in kendra. Luxury, vehicles, arts and charm."));
        }
        if (kendra(mar.house()) && ownOrEx(mar)) {
            yogas.add(yoga("Ruchaka Yoga (Panch Mahapurusha)", "Raja", "Mars strong in kendra. Courage, command and landed strength."));
        }
        if (kendra(mer.house()) && ownOrEx(mer)) {
            yogas.add(yoga("Bhadra Yoga (Panch Mahapurusha)", "Raja", "Mercury strong in kendra. Intellect and commerce."));
        }
        if (kendra(jup.house()) && ownOrEx(jup)) {
            yogas.add(yoga("Hamsa Yoga (Panch Mahapurusha)", "Raja", "Jupiter strong in kendra. Dharma, grace and high status."));
        }
        if (kendra(sat.house()) && ownOrEx(sat)) {
            yogas.add(yoga("Sasa Yoga (Panch Mahapurusha)", "Raja", "Saturn strong in kendra. Authority, longevity, industry."));
        }
        // Kemadruma: no planet in 2nd or 12th from Moon (excluding Sun traditionally sometimes)
        if (!anyPlanetInHouseFrom(c, moon.house(), 2) && !anyPlanetInHouseFrom(c, moon.house(), 12)) {
            doshas.add(yoga("Kemadruma Yoga", "Dosha", "No planets in 2nd or 12th from Moon. Isolation unless cancelled by kendra planets from Moon."));
        }
        if (kendraFrom(moon.house(), sun.house()) || kendraFrom(moon.house(), jup.house())) {
            // cancellation note already covered
        }
        // Adhi yoga: benefics in 6,7,8 from Moon
        int ben = 0;
        if (houseFrom(moon.house(), ven.house()) == 6 || houseFrom(moon.house(), ven.house()) == 7 || houseFrom(moon.house(), ven.house()) == 8) ben++;
        if (houseFrom(moon.house(), jup.house()) == 6 || houseFrom(moon.house(), jup.house()) == 7 || houseFrom(moon.house(), jup.house()) == 8) ben++;
        if (houseFrom(moon.house(), mer.house()) == 6 || houseFrom(moon.house(), mer.house()) == 7 || houseFrom(moon.house(), mer.house()) == 8) ben++;
        if (ben >= 2) yogas.add(yoga("Adhi Yoga", "Raja", "Benefics occupying 6/7/8 from the Moon. Leadership and comforts."));

        // Dharma-Karmadhipati: lords of 9 and 10 conjunct or mutual kendra
        int lord9 = lordOfHouse(c, 9);
        int lord10 = lordOfHouse(c, 10);
        PlanetBody p9 = planetByIndex(c, lord9);
        PlanetBody p10 = planetByIndex(c, lord10);
        if (p9 != null && p10 != null && (p9.signIndex() == p10.signIndex() || kendraFrom(p9.house(), p10.house()))) {
            yogas.add(yoga("Dharma-Karmadhipati Yoga", "Raja", "Lords of 9th and 10th connected. Rise through righteous action."));
        }

        // Dhana: lords of 2 and 11 connected or in 2/11
        PlanetBody p2 = planetByIndex(c, lordOfHouse(c, 2));
        PlanetBody p11 = planetByIndex(c, lordOfHouse(c, 11));
        if (p2 != null && p11 != null && (p2.signIndex() == p11.signIndex() || p2.house() == 2 || p2.house() == 11 || p11.house() == 2 || p11.house() == 11)) {
            yogas.add(yoga("Dhana Yoga", "Dhana", "Wealth houses linked. Capacity to accumulate resources."));
        }

        // Lakshmi: 9th lord in kendra/trikona strong
        if (p9 != null && (kendra(p9.house()) || trikona(p9.house())) && !p9.dignity().equals("Debilitated")) {
            yogas.add(yoga("Lakshmi Yoga (partial)", "Dhana", "9th lord well placed. Fortune, grace and support of destiny."));
        }

        // Chandra-Mangala
        if (moon.signIndex() == mar.signIndex()) {
            yogas.add(yoga("Chandra-Mangala Yoga", "Dhana", "Moon-Mars conjunction. Enterprise and liquid wealth, with emotional heat."));
        }

        // Viparita Raja: lords of 6/8/12 in 6/8/12
        if (dusthanaLordInDusthana(c)) {
            yogas.add(yoga("Viparita Raja Yoga", "Raja", "Dusthana lords occupying dusthanas. Rise after adversity."));
        }

        // Neecha Bhanga
        for (PlanetBody p : c.planets().values()) {
            if ("Debilitated".equals(p.dignity()) && neechaBhanga(c, p)) {
                yogas.add(yoga("Neecha Bhanga Raja Yoga — " + p.name(), "Raja",
                        p.name() + " is debilitated but cancellation conditions apply. Fall becomes rise."));
            }
        }

        // Amala: benefic in 10th from Moon or Lagna
        if (isBeneficIn(c, 10) || houseFrom(moon.house(), jup.house()) == 10 || houseFrom(moon.house(), ven.house()) == 10) {
            yogas.add(yoga("Amala Yoga", "Raja", "Benefic in the 10th (from Lagna or Moon). Unblemished reputation."));
        }

        // Vasumati: benefics in upachaya
        // Shubha kartari
        if (kartari(c, true)) yogas.add(yoga("Shubha Kartari Yoga", "Shubha", "Benefics hem the Lagna. Protection and ease."));
        if (kartari(c, false)) doshas.add(yoga("Papa Kartari Yoga", "Dosha", "Malefics hem the Lagna. Pressure and obstruction."));

        // Mangal dosha
        int mh = mar.house();
        if (mh == 1 || mh == 2 || mh == 4 || mh == 7 || mh == 8 || mh == 12) {
            doshas.add(yoga("Mangal / Kuja Dosha", "Dosha", "Mars occupies a manglik house (" + mh + "). Weigh cancellation rules before matching."));
        }

        // Kaal Sarp
        String ksp = kaalSarp(c);
        if (ksp != null) {
            doshas.add(yoga("Kaal Sarp Dosha — " + ksp, "Dosha",
                    "All planets lie between Rahu and Ketu (" + ksp + " type). Intensity, delays, inner pressure; also unusual rise."));
        }

        // Pitra dosha heuristic: Sun with Rahu/Ketu or Sun in 9th afflicted by Saturn
        if (sun.signIndex() == rahu.signIndex() || sun.signIndex() == ketu.signIndex()
                || (sun.house() == 9 && (sat.house() == 9 || rahu.house() == 9))) {
            doshas.add(yoga("Pitra Dosha (indicative)", "Dosha",
                    "Sun afflicted by nodes or 9th-house pressure. Ancestral karma; shraddha and Surya remedies."));
        }

        // Grahan mala: Sun/Moon with nodes
        if (sun.signIndex() == rahu.signIndex() || sun.signIndex() == ketu.signIndex()) {
            doshas.add(yoga("Surya Grahan Yoga", "Dosha", "Sun conjunct a node. Identity and father themes."));
        }
        if (moon.signIndex() == rahu.signIndex() || moon.signIndex() == ketu.signIndex()) {
            doshas.add(yoga("Chandra Grahan Yoga", "Dosha", "Moon conjunct a node. Mind and mother themes."));
        }

        // Kemadruma cancellation if kendra from moon has planet
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("yogas", yogas);
        out.put("doshas", doshas);
        out.put("yogaCount", yogas.size());
        out.put("doshaCount", doshas.size());
        return out;
    }

    private static boolean ownOrEx(PlanetBody p) {
        return "Own Sign".equals(p.dignity()) || "Exalted".equals(p.dignity()) || "Moolatrikona".equals(p.dignity());
    }

    private static boolean kendra(int h) { return h == 1 || h == 4 || h == 7 || h == 10; }
    private static boolean trikona(int h) { return h == 1 || h == 5 || h == 9; }

    private static boolean kendraFrom(int from, int house) {
        int rel = ((house - from + 12) % 12) + 1;
        return rel == 1 || rel == 4 || rel == 7 || rel == 10;
    }

    private static int houseFrom(int from, int house) {
        return ((house - from + 12) % 12) + 1;
    }

    private static boolean anyPlanetInHouseFrom(FullChart c, int from, int rel) {
        for (PlanetBody p : c.planets().values()) {
            if (p.name().equals("Rahu") || p.name().equals("Ketu")) continue;
            if (houseFrom(from, p.house()) == rel) return true;
        }
        return false;
    }

    private static int lordOfHouse(FullChart c, int house) {
        int sign = (c.lagna().signIndex() + house - 1) % 12;
        String lord = VedicConstants.SIGN_LORDS[sign];
        return indexPlanet(lord);
    }

    private static int indexPlanet(String name) {
        for (int i = 0; i < VedicConstants.PLANETS.length; i++) {
            if (VedicConstants.PLANETS[i].equals(name)) return i;
        }
        return 0;
    }

    private static PlanetBody planetByIndex(FullChart c, int i) {
        if (i < 0 || i >= 7) return c.planets().get(VedicConstants.PLANETS[Math.max(0, Math.min(6, i))]);
        return c.planets().get(VedicConstants.PLANETS[i]);
    }

    private static boolean dusthanaLordInDusthana(FullChart c) {
        int count = 0;
        for (int h : new int[]{6, 8, 12}) {
            PlanetBody p = planetByIndex(c, lordOfHouse(c, h));
            if (p != null && (p.house() == 6 || p.house() == 8 || p.house() == 12)) count++;
        }
        return count >= 2;
    }

    private static boolean neechaBhanga(FullChart c, PlanetBody p) {
        int exaltSign = VedicConstants.EXALTATION_SIGN.getOrDefault(p.name(), 0);
        // Lord of debilitated sign in kendra, or exaltation lord in kendra
        int debSign = p.signIndex();
        String signLord = VedicConstants.SIGN_LORDS[debSign];
        PlanetBody sl = c.planets().get(signLord);
        if (sl != null && kendra(sl.house())) return true;
        String exLord = VedicConstants.SIGN_LORDS[exaltSign];
        PlanetBody el = c.planets().get(exLord);
        return el != null && kendra(el.house());
    }

    private static boolean isBeneficIn(FullChart c, int house) {
        for (String n : new String[]{"Jupiter", "Venus", "Mercury", "Moon"}) {
            if (c.planets().get(n).house() == house) return true;
        }
        return false;
    }

    private static boolean kartari(FullChart c, boolean shubha) {
        boolean secondBen = false, twelfthBen = false, secondMal = false, twelfthMal = false;
        for (PlanetBody p : c.planets().values()) {
            boolean ben = p.name().equals("Jupiter") || p.name().equals("Venus") || (p.name().equals("Mercury") && !p.retrograde());
            boolean mal = p.name().equals("Saturn") || p.name().equals("Mars") || p.name().equals("Sun") || p.name().equals("Rahu") || p.name().equals("Ketu");
            if (p.house() == 2) { if (ben) secondBen = true; if (mal) secondMal = true; }
            if (p.house() == 12) { if (ben) twelfthBen = true; if (mal) twelfthMal = true; }
        }
        return shubha ? (secondBen && twelfthBen) : (secondMal && twelfthMal);
    }

    /** Rahu-house type name, or null if grahas are not all between the nodes. */
    public static String kaalSarp(FullChart c) {
        double rahu = c.planets().get("Rahu").siderealLon();
        double ketu = c.planets().get("Ketu").siderealLon();
        boolean allForward = true;
        boolean allBackward = true;
        for (String n : new String[]{"Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn"}) {
            double l = c.planets().get(n).siderealLon();
            if (!between(l, rahu, ketu)) allForward = false;
            if (!between(l, ketu, rahu)) allBackward = false;
        }
        if (!allForward && !allBackward) return null;
        String[] names = {"Anant", "Kulik", "Vasuki", "Shankhapal", "Padma", "Mahapadma", "Takshak", "Karkotak",
                "Shankhchud", "Ghatak", "Vishdhar", "Sheshnag"};
        int type = c.planets().get("Rahu").house() - 1;
        return names[Math.max(0, Math.min(11, type))];
    }

    private static boolean between(double lon, double a, double b) {
        double x = AstroMath.norm360(lon - a);
        double span = AstroMath.norm360(b - a);
        return x > 0 && x < span;
    }

    private static Map<String, Object> yoga(String name, String type, String text) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", name);
        m.put("type", type);
        m.put("text", text);
        return m;
    }
}
