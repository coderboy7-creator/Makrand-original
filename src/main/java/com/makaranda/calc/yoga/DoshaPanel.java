package com.makaranda.calc.yoga;

import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.match.AshtakootaMatcher;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;
import com.makaranda.calc.vedic.ChartBuilder.PlanetBody;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Honest natal + gochar doṣa strip for the kundali sheet.
 * Pattern flags only — not a curse list, not Astrotalk scare copy.
 */
public final class DoshaPanel {
    public static final String[] KAAL_SARP = {
            "Anant", "Kulik", "Vasuki", "Shankhapal", "Padma", "Mahapadma",
            "Takshak", "Karkotak", "Shankhchud", "Ghatak", "Vishdhar", "Sheshnag"
    };
    public static final String[] KAAL_SARP_HI = {
            "अनन्त", "कुलिक", "वासुकि", "शङ्खपाल", "पद्म", "महापद्म",
            "तक्षक", "कर्कोटक", "शङ्खचूड़", "घातक", "विषधर", "शेषनाग"
    };

    private DoshaPanel() {}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> from(FullChart c, Map<String, Object> gochar) {
        Map<String, Object> mangal = mangal(c);
        Map<String, Object> ksp = kaalsarpa(c);
        Map<String, Object> sati = sadeSati(gochar);

        List<Map<String, Object>> items = new ArrayList<>();
        items.add(mangal);
        items.add(ksp);
        items.add(sati);

        int flagged = 0;
        for (Map<String, Object> it : items) {
            if (Boolean.TRUE.equals(it.get("present")) || Boolean.TRUE.equals(it.get("active"))) flagged++;
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("items", items);
        out.put("mangal", mangal);
        out.put("kaalsarpa", ksp);
        out.put("sadeSati", sati);
        out.put("flagged", flagged);
        out.put("note", "Pattern list from the chart and today's Saturn. Not a prediction, medical or legal verdict.");
        out.put("noteHi", "जन्म-कुंडली व आज के शनि से गणित-सूची। भय-प्रचार, चिकित्सा या कानूनी सलाह नहीं।");
        if (gochar != null) out.put("gocharDate", gochar.get("date"));
        return out;
    }

    public static Map<String, Object> mangal(FullChart c) {
        PlanetBody mar = c.planets().get("Mars");
        int h = mar.house();
        boolean inHouse = h == 1 || h == 2 || h == 4 || h == 7 || h == 8 || h == 12;
        boolean cancelled = inHouse && AshtakootaMatcher.isManglik(c) == false;
        boolean present = inHouse && !cancelled;
        String status = !inHouse ? "absent" : (cancelled ? "cancelled" : "present");
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", "mangal");
        m.put("name", "Mangal / Kuja");
        m.put("nameHi", "मंगल / कुज दोष");
        m.put("present", present);
        m.put("active", present);
        m.put("cancelled", cancelled);
        m.put("status", status);
        m.put("house", h);
        m.put("dignity", mar.dignity());
        m.put("text", present
                ? "Mars is in house " + h + " (1, 2, 4, 7, 8 or 12). Weigh matching and cancellation; this is a house flag."
                : (cancelled
                ? "Mars sits in a manglik house (" + h + ") but own / exalted / mūlatrikona dignity cancels the flag."
                : "Mars is not in 1, 2, 4, 7, 8 or 12. Kuja flag is not present."));
        m.put("textHi", present
                ? "मंगल भाव " + h + " में (१, २, ४, ७, ८ या १२)। मिलान व शमन देखें; यह भाव-चिह्न है।"
                : (cancelled
                ? "मंगल मंगलिक भाव (" + h + ") में हैं, पर स्व/उच्च/मूलत्रिकोण से चिह्न निरस्त।"
                : "मंगल १, २, ४, ७, ८, १२ में नहीं। कुज चिह्न नहीं है।"));
        return m;
    }

    public static Map<String, Object> kaalsarpa(FullChart c) {
        String type = YogaDetector.kaalSarp(c);
        boolean present = type != null;
        String typeHi = typeHi(type);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", "kaalsarpa");
        m.put("name", "Kaal Sarp");
        m.put("nameHi", "कालसर्प");
        m.put("present", present);
        m.put("active", present);
        m.put("status", present ? "present" : "absent");
        m.put("type", type);
        m.put("typeHi", typeHi);
        int rahuHouse = c.planets().get("Rahu").house();
        m.put("rahuHouse", rahuHouse);
        m.put("text", present
                ? "All seven tara-grahas lie between Rahu and Ketu (" + type + "). Intensity and delay are traditional notes — not a curse."
                : "Grahas are not all between the nodes. Kaal Sarp flag is not present.");
        m.put("textHi", present
                ? "सात ग्रह राहु-केतु के बीच (" + typeHi + ")। तीव्रता/विलंब की परम्परा-टिप्पणी — शाप नहीं।"
                : "ग्रह राहु-केतु के बीच नहीं बँधे। कालसर्प चिह्न नहीं है।");
        return m;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> sadeSati(Map<String, Object> gochar) {
        Map<String, Object> src = gochar == null ? Map.of() : (Map<String, Object>) gochar.getOrDefault("sadeSati", Map.of());
        boolean active = Boolean.TRUE.equals(src.get("active"));
        int rel = src.get("houseFromMoon") instanceof Number n ? n.intValue() : 0;
        boolean ashtama = Boolean.TRUE.equals(src.get("ashtamaShani"));
        boolean ardha = Boolean.TRUE.equals(src.get("ardhashtama"));
        String phase = switch (rel) {
            case 12 -> "Rising (12th from Moon)";
            case 1 -> "Peak (over natal Moon)";
            case 2 -> "Setting (2nd from Moon)";
            default -> "Not in Sade Sati";
        };
        String phaseHi = switch (rel) {
            case 12 -> "आरोह (चन्द्र से १२वाँ)";
            case 1 -> "शीर्ष (जन्म-चन्द्र पर)";
            case 2 -> "अवरोह (चन्द्र से २रा)";
            default -> "साढ़ेसाती नहीं";
        };
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", "sadesati");
        m.put("name", "Sade Sati");
        m.put("nameHi", "साढ़ेसाती");
        m.put("present", active);
        m.put("active", active);
        m.put("status", active ? "active" : "absent");
        m.put("houseFromMoon", rel);
        m.put("phase", phase);
        m.put("phaseHi", phaseHi);
        m.put("ashtamaShani", ashtama);
        m.put("ardhashtama", ardha);
        m.put("text", active
                ? phase + ". Saturn from natal Moon — patience and duty; not a sentence."
                : (ashtama
                ? "Not Sade Sati. Ashtama Śani (8th from Moon) is a separate, milder flag."
                : (ardha
                ? "Not Sade Sati. Ardhāṣṭama (4th from Moon) is a separate flag."
                : "Saturn is not 12th, 1st or 2nd from natal Moon. Sade Sati is not running.")));
        m.put("textHi", active
                ? phaseHi + "। जन्म-चन्द्र से शनि — धैर्य व कर्तव्य; दण्ड नहीं।"
                : (ashtama
                ? "साढ़ेसाती नहीं। अष्टम शनि (चन्द्र से ८वाँ) अलग, हलका चिह्न।"
                : (ardha
                ? "साढ़ेसाती नहीं। अर्धाष्टम (चन्द्र से ४था) अलग चिह्न।"
                : "शनि जन्म-चन्द्र से १२, १ या २ में नहीं। साढ़ेसाती नहीं चल रही।")));
        return m;
    }

    private static String typeHi(String type) {
        if (type == null) return null;
        for (int i = 0; i < KAAL_SARP.length; i++) {
            if (KAAL_SARP[i].equalsIgnoreCase(type)) return KAAL_SARP_HI[i];
        }
        return type;
    }
}
