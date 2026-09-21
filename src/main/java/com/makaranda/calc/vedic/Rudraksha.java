package com.makaranda.calc.vedic;

import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Rudrākṣa as upāya from janma nakṣatra lord — not a shop.
 * Mukhi follows the classical graha map (Sun 1 … Ketu 9).
 */
public final class Rudraksha {
    private Rudraksha() {}

    /** Mukhi for the Vimśottarī lord of a nakṣatra. */
    public static int mukhiForLord(String planet) {
        if (planet == null) return 5;
        return switch (planet) {
            case "Sun" -> 1;
            case "Moon" -> 2;
            case "Mars" -> 3;
            case "Mercury" -> 4;
            case "Jupiter" -> 5;
            case "Venus" -> 6;
            case "Saturn" -> 7;
            case "Rahu" -> 8;
            case "Ketu" -> 9;
            default -> 5;
        };
    }

    public static Map<String, Object> fromChart(FullChart c) {
        int nak = c.planets().get("Moon").nakshatraIndex();
        String lord = VedicConstants.NAK_LORDS[nak];
        int mukhi = mukhiForLord(lord);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("mukhi", mukhi);
        m.put("name", mukhi + "-mukhi Rudraksha");
        m.put("nameHi", mukhiHi(mukhi) + " रुद्राक्ष");
        m.put("nakshatra", VedicConstants.NAKSHATRAS[nak]);
        m.put("nakshatraHi", VedicConstants.nakHi(nak));
        m.put("lord", lord);
        m.put("lordHi", VedicConstants.planetHi(lord));
        m.put("deity", deity(mukhi));
        m.put("deityHi", deityHi(mukhi));
        m.put("mantra", "Om Namah Shivaya");
        m.put("mantraHi", "ॐ नमः शिवाय");
        m.put("sell", false);
        m.put("note", "Wear or give as dana after a Monday or pradosha. We do not sell beads.");
        m.put("noteHi", "सोमवार या प्रदोष पर धारण या दान। हम रुद्राक्ष नहीं बेचते।");
        return m;
    }

    static String mukhiHi(int n) {
        String[] d = {"०", "१", "२", "३", "४", "५", "६", "७", "८", "९"};
        return (n >= 0 && n <= 9 ? d[n] : String.valueOf(n)) + " मुखी";
    }

    static String deity(int mukhi) {
        return switch (mukhi) {
            case 1 -> "Shiva";
            case 2 -> "Ardhanarishvara";
            case 3 -> "Agni";
            case 4 -> "Brahma";
            case 5 -> "Kalagni Rudra";
            case 6 -> "Kartikeya";
            case 7 -> "Mahalakshmi";
            case 8 -> "Ganesha";
            case 9 -> "Durga";
            default -> "Shiva";
        };
    }

    static String deityHi(int mukhi) {
        return switch (mukhi) {
            case 1 -> "शिव";
            case 2 -> "अर्धनारीश्वर";
            case 3 -> "अग्नि";
            case 4 -> "ब्रह्मा";
            case 5 -> "कालग्नि रुद्र";
            case 6 -> "कार्तिकेय";
            case 7 -> "महालक्ष्मी";
            case 8 -> "गणेश";
            case 9 -> "दुर्गा";
            default -> "शिव";
        };
    }
}
