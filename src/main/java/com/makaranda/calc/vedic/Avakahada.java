package com.makaranda.calc.vedic;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.VedicConstants;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * अवकहड़ा from Chandra: varṇa, vaśya, yoni, gaṇa, nāḍī, rāśi, caraṇa, tattva,
 * nāma-akṣara, pāyā. Classical tables; not fitted to any commercial PDF.
 */
public final class Avakahada {

    private static final String[] AVASTHA = {"Bala", "Kumara", "Yuva", "Vriddha", "Mrita"};
    private static final String[] AVASTHA_HI = {"बाल", "कुमार", "युवा", "वृद्ध", "मृत"};

    /** 27 nakṣatra × 4 pāda nāma-akṣara (standard Maithil / north-Indian list). */
    private static final String[][] NAMA_AKSHARA = {
            {"चु", "चे", "चो", "ला"}, {"ली", "लू", "ले", "लो"}, {"अ", "इ", "उ", "ए"},
            {"ओ", "वा", "वी", "वू"}, {"वे", "वो", "का", "की"}, {"कु", "घ", "ङ", "छ"},
            {"के", "को", "हा", "हि"}, {"हु", "हे", "हो", "ड"}, {"डी", "डू", "डे", "डो"},
            {"मा", "मी", "मू", "मे"}, {"मो", "टा", "टी", "टू"}, {"टे", "टो", "पा", "पी"},
            {"पू", "ष", "ण", "ठ"}, {"पे", "पो", "रा", "री"}, {"रु", "रे", "रो", "ता"},
            {"ती", "तू", "ते", "तो"}, {"ना", "नी", "नू", "ने"}, {"नो", "या", "यी", "यू"},
            {"ये", "यो", "भा", "भी"}, {"भू", "धा", "फा", "ढा"}, {"भे", "भो", "जा", "जी"},
            {"खि", "खू", "खे", "खो"}, {"गा", "गी", "गु", "गे"}, {"गो", "सा", "सी", "सू"},
            {"से", "सो", "दा", "दी"}, {"दू", "थ", "झ", "ञ"}, {"दे", "दो", "च", "चा"}
    };

    private Avakahada() {}

    public static Map<String, Object> fromMoon(double moonSidereal) {
        double lon = AstroMath.norm360(moonSidereal);
        int sign = AstroMath.signIndex(lon);
        int nak = VedicConstants.nakshatraIndex(lon);
        int pada = VedicConstants.pada(lon);
        double inSign = lon % 30.0;
        String[] vashya = vashya(sign, inSign);
        String[] paya = paya(nak);
        String[] tatva = tattva(sign);
        String akshara = NAMA_AKSHARA[nak][pada - 1];
        String varna = VedicConstants.NAK_VARNA[nak];
        String yoni = VedicConstants.NAK_YONI[nak];
        String gana = VedicConstants.NAK_GANA[nak];
        String nadi = VedicConstants.NAK_NADI[nak];
        String rashiLord = VedicConstants.SIGN_LORDS[sign];

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("varna", varna);
        m.put("varnaHi", varnaHi(varna));
        m.put("vashya", vashya[0]);
        m.put("vashyaHi", vashya[1]);
        m.put("yoni", yoni);
        m.put("yoniHi", yoniHi(yoni));
        m.put("gana", gana);
        m.put("ganaHi", ganaHi(gana));
        m.put("nadi", nadi);
        m.put("nadiHi", nadiHi(nadi));
        m.put("rashi", VedicConstants.SIGNS_EN[sign]);
        m.put("rashiSa", VedicConstants.SIGNS_SA[sign]);
        m.put("rashiHi", VedicConstants.SIGNS_HI[sign]);
        m.put("rashiLord", rashiLord);
        m.put("rashiLordHi", VedicConstants.planetHi(rashiLord));
        m.put("nakshatra", VedicConstants.NAKSHATRAS[nak]);
        m.put("nakshatraHi", VedicConstants.nakHi(nak));
        m.put("nakLord", VedicConstants.NAK_LORDS[nak]);
        m.put("nakLordHi", VedicConstants.planetHi(VedicConstants.NAK_LORDS[nak]));
        m.put("pada", pada);
        m.put("tattva", tatva[0]);
        m.put("tattvaHi", tatva[1]);
        m.put("namakshara", akshara);
        m.put("paya", paya[0]);
        m.put("payaHi", paya[1]);
        return m;
    }

    /**
     * Bālādi avasthā from sign-degree. Odd signs (Meṣa…) 0–6 Bāla … 24–30 Mṛta;
     * even signs reverse (Bṛhat Jātaka / Jātaka Pārijāta).
     */
    public static String avastha(double siderealLon) {
        return avasthaPair(siderealLon)[0];
    }

    public static String avasthaHi(double siderealLon) {
        return avasthaPair(siderealLon)[1];
    }

    static String[] avasthaPair(double siderealLon) {
        double lon = AstroMath.norm360(siderealLon);
        int sign = AstroMath.signIndex(lon);
        double deg = lon % 30.0;
        if (sign % 2 == 1) deg = 30.0 - deg;
        int slot = (int) Math.floor(deg / 6.0);
        if (slot < 0) slot = 0;
        if (slot > 4) slot = 4;
        return new String[]{AVASTHA[slot], AVASTHA_HI[slot]};
    }

    private static String[] vashya(int sign, double inSign) {
        return switch (sign) {
            case 0, 1, 4 -> pair("Quadruped", "चतुष्पद");
            case 2, 5, 6, 10 -> pair("Human", "मानव");
            case 3, 11 -> pair("Watery", "जलचर");
            case 7 -> pair("Insect", "कीट");
            case 8 -> inSign < 15 ? pair("Human", "मानव") : pair("Quadruped", "चतुष्पद");
            case 9 -> inSign < 15 ? pair("Quadruped", "चतुष्पद") : pair("Watery", "जलचर");
            default -> pair("Human", "मानव");
        };
    }

    private static String[] paya(int nak) {
        return switch (nak % 4) {
            case 0 -> pair("Gold", "स्वर्ण");
            case 1 -> pair("Silver", "रजत");
            case 2 -> pair("Copper", "ताम्र");
            default -> pair("Iron", "लोह");
        };
    }

    private static String[] tattva(int sign) {
        return switch (sign % 4) {
            case 0 -> pair("Fire", "अग्नि");
            case 1 -> pair("Earth", "पृथ्वी");
            case 2 -> pair("Air", "वायु");
            default -> pair("Water", "जल");
        };
    }

    private static String varnaHi(String v) {
        return switch (v) {
            case "Brahmin" -> "ब्राह्मण";
            case "Kshatriya" -> "क्षत्रिय";
            case "Vaishya" -> "वैश्य";
            case "Shudra" -> "शूद्र";
            case "Mleccha" -> "म्लेच्छ";
            case "Butcher" -> "व्याध";
            default -> v;
        };
    }

    private static String yoniHi(String y) {
        return switch (y) {
            case "Horse" -> "अश्व";
            case "Elephant" -> "गज";
            case "Sheep" -> "मेष";
            case "Serpent" -> "सर्प";
            case "Dog" -> "श्वान";
            case "Cat" -> "मार्जार";
            case "Rat" -> "मूषक";
            case "Cow" -> "गौ";
            case "Buffalo" -> "महिष";
            case "Tiger" -> "व्याघ्र";
            case "Deer" -> "मृग";
            case "Monkey" -> "वानर";
            case "Mongoose" -> "नकुल";
            case "Lion" -> "सिंह";
            default -> y;
        };
    }

    private static String ganaHi(String g) {
        return switch (g) {
            case "Deva" -> "देव";
            case "Manushya" -> "मानव";
            case "Rakshasa" -> "राक्षस";
            default -> g;
        };
    }

    private static String nadiHi(String n) {
        return switch (n) {
            case "Adi" -> "आदि";
            case "Madhya" -> "मध्य";
            case "Antya" -> "अन्त्य";
            default -> n;
        };
    }

    private static String[] pair(String en, String hi) {
        return new String[]{en, hi};
    }
}
