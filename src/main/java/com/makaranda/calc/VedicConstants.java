package com.makaranda.calc;

import java.util.List;
import java.util.Map;

public final class VedicConstants {
    private VedicConstants() {}

    public static final String[] SIGNS_EN = {
            "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
            "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"
    };
    public static final String[] SIGNS_SA = {
            "Mesha", "Vrishabha", "Mithuna", "Karka", "Simha", "Kanya",
            "Tula", "Vrischika", "Dhanu", "Makara", "Kumbha", "Meena"
    };
    public static final String[] SIGNS_HI = {
            "मेष", "वृषभ", "मिथुन", "कर्क", "सिंह", "कन्या",
            "तुला", "वृश्चिक", "धनु", "मकर", "कुम्भ", "मीन"
    };
    public static final String[] SIGN_LORDS = {
            "Mars", "Venus", "Mercury", "Moon", "Sun", "Mercury",
            "Venus", "Mars", "Jupiter", "Saturn", "Saturn", "Jupiter"
    };
    public static final String[] SIGN_ELEMENTS = {
            "Fire", "Earth", "Air", "Water", "Fire", "Earth",
            "Air", "Water", "Fire", "Earth", "Air", "Water"
    };
    public static final String[] SIGN_QUALITY = {
            "Movable", "Fixed", "Dual", "Movable", "Fixed", "Dual",
            "Movable", "Fixed", "Dual", "Movable", "Fixed", "Dual"
    };
    public static final String[] SIGN_GENDER = {
            "Male", "Female", "Male", "Female", "Male", "Female",
            "Male", "Female", "Male", "Female", "Male", "Female"
    };

    public static final String[] PLANETS = {
            "Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn", "Rahu", "Ketu"
    };
    public static final String[] PLANETS_SA = {
            "Surya", "Chandra", "Mangala", "Budha", "Guru", "Shukra", "Shani", "Rahu", "Ketu"
    };
    public static final String[] PLANETS_HI = {
            "सूर्य", "चन्द्र", "मंगल", "बुध", "गुरु", "शुक्र", "शनि", "राहु", "केतु"
    };
    public static final String[] PLANET_GLYPH = {
            "☉", "☽", "♂", "☿", "♃", "♀", "♄", "☊", "☋"
    };

    public static final String[] NAKSHATRAS = {
            "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra", "Punarvasu",
            "Pushya", "Ashlesha", "Magha", "Purva Phalguni", "Uttara Phalguni", "Hasta",
            "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha", "Mula", "Purva Ashadha",
            "Uttara Ashadha", "Shravana", "Dhanishta", "Shatabhisha", "Purva Bhadrapada",
            "Uttara Bhadrapada", "Revati"
    };
    public static final String[] NAK_LORDS = {
            "Ketu", "Venus", "Sun", "Moon", "Mars", "Rahu", "Jupiter",
            "Saturn", "Mercury", "Ketu", "Venus", "Sun", "Moon",
            "Mars", "Rahu", "Jupiter", "Saturn", "Mercury", "Ketu", "Venus",
            "Sun", "Moon", "Mars", "Rahu", "Jupiter",
            "Saturn", "Mercury"
    };
    public static final String[] NAK_DEITIES = {
            "Ashwini Kumaras", "Yama", "Agni", "Brahma", "Soma", "Rudra", "Aditi",
            "Brihaspati", "Sarpas", "Pitris", "Bhaga", "Aryaman", "Savitar",
            "Tvashtar", "Vayu", "Indra-Agni", "Mitra", "Indra", "Nirriti", "Apah",
            "Vishvedevas", "Vishnu", "Vasus", "Varuna", "Aja Ekapada",
            "Ahir Budhnya", "Pushan"
    };
    public static final String[] NAK_GANA = {
            "Deva", "Manushya", "Rakshasa", "Manushya", "Deva", "Manushya", "Deva",
            "Deva", "Rakshasa", "Rakshasa", "Manushya", "Manushya", "Deva",
            "Rakshasa", "Deva", "Rakshasa", "Deva", "Rakshasa", "Rakshasa", "Manushya",
            "Manushya", "Deva", "Rakshasa", "Rakshasa", "Manushya",
            "Manushya", "Deva"
    };
    public static final String[] NAK_YONI = {
            "Horse", "Elephant", "Sheep", "Serpent", "Serpent", "Dog", "Cat",
            "Sheep", "Cat", "Rat", "Rat", "Cow", "Buffalo",
            "Tiger", "Buffalo", "Tiger", "Deer", "Deer", "Dog", "Monkey",
            "Mongoose", "Monkey", "Lion", "Horse", "Lion",
            "Cow", "Elephant"
    };
    public static final String[] NAK_NADI = {
            "Adi", "Madhya", "Antya", "Antya", "Madhya", "Adi", "Adi",
            "Madhya", "Antya", "Antya", "Madhya", "Adi", "Adi",
            "Madhya", "Antya", "Antya", "Madhya", "Adi", "Adi", "Madhya",
            "Antya", "Antya", "Madhya", "Adi", "Adi",
            "Madhya", "Antya"
    };
    public static final String[] NAK_VARNA = {
            "Vaishya", "Mleccha", "Brahmin", "Shudra", "Shudra", "Butcher", "Vaishya",
            "Kshatriya", "Mleccha", "Shudra", "Brahmin", "Kshatriya", "Vaishya",
            "Shudra", "Butcher", "Mleccha", "Shudra", "Shudra", "Butcher", "Brahmin",
            "Kshatriya", "Kshatriya", "Shudra", "Butcher", "Brahmin",
            "Kshatriya", "Shudra"
    };

    public static final String[] TITHIS = {
            "Pratipada", "Dwitiya", "Tritiya", "Chaturthi", "Panchami", "Shashthi", "Saptami",
            "Ashtami", "Navami", "Dashami", "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi",
            "Purnima/Amavasya"
    };
    public static final String[] TITHIS_HI = {
            "प्रतिपदा", "द्वितीया", "तृतीया", "चतुर्थी", "पंचमी", "षष्ठी", "सप्तमी",
            "अष्टमी", "नवमी", "दशमी", "एकादशी", "द्वादशी", "त्रयोदशी", "चतुर्दशी",
            "पूर्णिमा/अमावस्या"
    };
    public static final String[] NAKSHATRAS_HI = {
            "अश्विनी", "भरणी", "कृत्तिका", "रोहिणी", "मृगशिरा", "आर्द्रा", "पुनर्वसु",
            "पुष्य", "आश्लेषा", "मघा", "पूर्वा फाल्गुनी", "उत्तरा फाल्गुनी", "हस्त",
            "चित्रा", "स्वाती", "विशाखा", "अनुराधा", "ज्येष्ठा", "मूल", "पूर्वाषाढ़ा",
            "उत्तराषाढ़ा", "श्रवण", "धनिष्ठा", "शतभिषा", "पूर्वा भाद्रपद",
            "उत्तरा भाद्रपद", "रेवती"
    };
    public static final String[] YOGAS_PANCHANG_HI = {
            "विष्कम्भ", "प्रीति", "आयुष्मान", "सौभाग्य", "शोभन", "अतिगण्ड", "सुकर्म",
            "धृति", "शूल", "गण्ड", "वृद्धि", "ध्रुव", "व्याघात", "हर्षण",
            "वज्र", "सिद्धि", "व्यतीपात", "वरीयान्", "परिघ", "शिव", "सिद्ध",
            "साध्य", "शुभ", "शुक्ल", "ब्रह्म", "इन्द्र", "वैधृति"
    };
    public static final String[] Karanas = {
            "Bava", "Balava", "Kaulava", "Taitila", "Gara", "Vanija", "Vishti",
            "Shakuni", "Chatushpada", "Naga", "Kimstughna"
    };
    public static final String[] KARANAS_HI = {
            "बव", "बालव", "कौलव", "तैतिल", "गर", "वणिज", "विष्टि",
            "शकुनि", "चतुष्पाद", "नाग", "किंस्तुघ्न"
    };
    public static final String[] YOGAS_PANCHANG = {
            "Vishkambha", "Priti", "Ayushman", "Saubhagya", "Shobhana", "Atiganda", "Sukarman",
            "Dhriti", "Shula", "Ganda", "Vriddhi", "Dhruva", "Vyaghata", "Harshana",
            "Vajra", "Siddhi", "Vyatipata", "Variyan", "Parigha", "Shiva", "Siddha",
            "Sadhya", "Shubha", "Shukla", "Brahma", "Indra", "Vaidhriti"
    };
    public static final String[] WEEKDAYS = {
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };
    public static final String[] WEEKDAYS_HI = {
            "रविवार", "सोमवार", "मंगलवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार"
    };

    public static final int[] VIMSHOTTARI_YEARS = {7, 20, 6, 10, 7, 18, 16, 19, 17};
    public static final String[] VIMSHOTTARI_LORDS = {
            "Ketu", "Venus", "Sun", "Moon", "Mars", "Rahu", "Jupiter", "Saturn", "Mercury"
    };

    public static final Map<String, Integer> EXALTATION_SIGN = Map.of(
            "Sun", 0, "Moon", 1, "Mars", 9, "Mercury", 5, "Jupiter", 3,
            "Venus", 11, "Saturn", 6, "Rahu", 1, "Ketu", 7
    );
    public static final Map<String, Double> EXALTATION_DEG = Map.of(
            "Sun", 10.0, "Moon", 3.0, "Mars", 28.0, "Mercury", 15.0, "Jupiter", 5.0,
            "Venus", 27.0, "Saturn", 20.0, "Rahu", 20.0, "Ketu", 20.0
    );
    public static final Map<String, Integer> OWN_SIGN_1 = Map.of(
            "Sun", 4, "Moon", 3, "Mars", 0, "Mercury", 2, "Jupiter", 8,
            "Venus", 1, "Saturn", 9, "Rahu", 10, "Ketu", 7
    );
    public static final Map<String, Integer> OWN_SIGN_2 = Map.of(
            "Mars", 7, "Mercury", 5, "Jupiter", 11, "Venus", 6, "Saturn", 10
    );
    public static final Map<String, Integer> DEBILITATION_SIGN = Map.of(
            "Sun", 6, "Moon", 7, "Mars", 3, "Mercury", 11, "Jupiter", 9,
            "Venus", 5, "Saturn", 0, "Rahu", 7, "Ketu", 1
    );
    public static final Map<String, Integer> MOOLTRIKONA = Map.of(
            "Sun", 4, "Moon", 1, "Mars", 0, "Mercury", 5, "Jupiter", 8,
            "Venus", 6, "Saturn", 10, "Rahu", 10, "Ketu", 7
    );

    public static final String[] FRIENDS_SUN = {"Moon", "Mars", "Jupiter"};
    public static final String[] ENEMIES_SUN = {"Venus", "Saturn"};

    public static final Map<String, List<String>> NATURAL_FRIENDS = Map.of(
            "Sun", List.of("Moon", "Mars", "Jupiter"),
            "Moon", List.of("Sun", "Mercury"),
            "Mars", List.of("Sun", "Moon", "Jupiter"),
            "Mercury", List.of("Sun", "Venus"),
            "Jupiter", List.of("Sun", "Moon", "Mars"),
            "Venus", List.of("Mercury", "Saturn"),
            "Saturn", List.of("Mercury", "Venus"),
            "Rahu", List.of("Venus", "Saturn", "Mercury"),
            "Ketu", List.of("Mars", "Jupiter")
    );
    public static final Map<String, List<String>> NATURAL_ENEMIES = Map.of(
            "Sun", List.of("Venus", "Saturn"),
            "Moon", List.of(),
            "Mars", List.of("Mercury"),
            "Mercury", List.of("Moon"),
            "Jupiter", List.of("Mercury", "Venus"),
            "Venus", List.of("Sun", "Moon"),
            "Saturn", List.of("Sun", "Moon", "Mars"),
            "Rahu", List.of("Sun", "Moon"),
            "Ketu", List.of("Moon", "Venus")
    );

    public static final double[] VIMSHOPAKA_WEIGHTS_D = {
            /* D1 */ 3.5, /* D2 */ 1.0, /* D3 */ 1.0, /* D4 */ 0.5,
            /* D7 */ 0.5, /* D9 */ 3.0, /* D10 */ 0.5, /* D12 */ 0.5,
            /* D16 */ 2.0, /* D20 */ 0.5, /* D24 */ 0.5, /* D27 */ 0.5,
            /* D30 */ 1.0, /* D40 */ 0.5, /* D45 */ 0.5, /* D60 */ 4.0
    };
    public static final int[] VIMSHOPAKA_DIVS = {1, 2, 3, 4, 7, 9, 10, 12, 16, 20, 24, 27, 30, 40, 45, 60};

    public static final Map<String, String> GEMSTONES = Map.of(
            "Sun", "Ruby (Manikya)",
            "Moon", "Pearl (Moti)",
            "Mars", "Red Coral (Moonga)",
            "Mercury", "Emerald (Panna)",
            "Jupiter", "Yellow Sapphire (Pukhraj)",
            "Venus", "Diamond (Heera) / White Sapphire",
            "Saturn", "Blue Sapphire (Neelam)",
            "Rahu", "Hessonite (Gomed)",
            "Ketu", "Cat's Eye (Lehsunia)"
    );
    public static final Map<String, String> METALS = Map.of(
            "Sun", "Gold", "Moon", "Silver", "Mars", "Copper", "Mercury", "Bronze",
            "Jupiter", "Gold", "Venus", "Silver", "Saturn", "Iron", "Rahu", "Lead", "Ketu", "Lead"
    );
    public static final Map<String, String> COLOURS = Map.of(
            "Sun", "Copper-red / Orange", "Moon", "White", "Mars", "Red", "Mercury", "Green",
            "Jupiter", "Yellow", "Venus", "White / Pastel", "Saturn", "Blue / Black",
            "Rahu", "Smoke / Brown", "Ketu", "Grey / Flag"
    );

    public static String tithiEn(int tithiNum1to30) {
        int n = ((tithiNum1to30 - 1) % 30 + 30) % 30 + 1;
        if (n == 15) return "Purnima";
        if (n == 30) return "Amavasya";
        int inPaksha = n <= 15 ? n : n - 15;
        return TITHIS[inPaksha - 1];
    }

    public static String tithiHi(int tithiNum1to30) {
        int n = ((tithiNum1to30 - 1) % 30 + 30) % 30 + 1;
        if (n == 15) return "पूर्णिमा";
        if (n == 30) return "अमावस्या";
        int inPaksha = n <= 15 ? n : n - 15;
        return TITHIS_HI[inPaksha - 1];
    }

    public static String pakshaEn(int tithiNum1to30) {
        int n = ((tithiNum1to30 - 1) % 30 + 30) % 30 + 1;
        return n <= 15 ? "Shukla" : "Krishna";
    }

    public static String pakshaHi(int tithiNum1to30) {
        return pakshaEn(tithiNum1to30).equals("Shukla") ? "शुक्ल" : "कृष्ण";
    }

    public static String karanaEn(int idx0to59) {
        int idx = ((idx0to59 % 60) + 60) % 60;
        if (idx == 0) return "Kimstughna";
        if (idx == 57) return "Shakuni";
        if (idx == 58) return "Chatushpada";
        if (idx == 59) return "Naga";
        return Karanas[(idx - 1) % 7];
    }

    public static String karanaHi(int idx0to59) {
        int idx = ((idx0to59 % 60) + 60) % 60;
        if (idx == 0) return KARANAS_HI[10];
        if (idx == 57) return KARANAS_HI[7];
        if (idx == 58) return KARANAS_HI[8];
        if (idx == 59) return KARANAS_HI[9];
        return KARANAS_HI[(idx - 1) % 7];
    }

    public static int nakshatraIndex(double siderealLon) {
        return (int) Math.floor(AstroMath.norm360(siderealLon) / AstroMath.NAKSHATRA_SPAN);
    }

    public static int pada(double siderealLon) {
        double within = AstroMath.norm360(siderealLon) % AstroMath.NAKSHATRA_SPAN;
        return (int) Math.floor(within / AstroMath.PADA_SPAN) + 1;
    }

    public static String planetHi(String name) {
        if ("Lagna".equals(name)) return "लग्न";
        for (int i = 0; i < PLANETS.length; i++) {
            if (PLANETS[i].equalsIgnoreCase(name)) return PLANETS_HI[i];
        }
        return name;
    }

    public static String dignityHi(String d) {
        if (d == null) return "—";
        return switch (d) {
            case "Exalted" -> "उच्च";
            case "Debilitated" -> "नीच";
            case "Own Sign" -> "स्वगृह";
            case "Moolatrikona" -> "मूलत्रिकोण";
            case "Friendly" -> "मित्र";
            case "Enemy" -> "शत्रु";
            case "Neutral" -> "सम";
            default -> d;
        };
    }

    public static String nakHi(int idx) {
        int i = ((idx % 27) + 27) % 27;
        return NAKSHATRAS_HI[i];
    }

    public static String dignity(String planet, int sign) {
        Integer ex = EXALTATION_SIGN.get(planet);
        if (ex != null && ex == sign) return "Exalted";
        Integer deb = DEBILITATION_SIGN.get(planet);
        if (deb != null && deb == sign) return "Debilitated";
        Integer mt = MOOLTRIKONA.get(planet);
        if (mt != null && mt == sign) return "Moolatrikona";
        Integer o1 = OWN_SIGN_1.get(planet);
        if (o1 != null && o1 == sign) return "Own Sign";
        Integer o2 = OWN_SIGN_2.get(planet);
        if (o2 != null && o2 == sign) return "Own Sign";
        List<String> fr = NATURAL_FRIENDS.get(planet);
        List<String> en = NATURAL_ENEMIES.get(planet);
        String lord = SIGN_LORDS[sign];
        if (fr != null && fr.contains(lord)) return "Friendly";
        if (en != null && en.contains(lord)) return "Enemy";
        return "Neutral";
    }
}
