package com.makaranda.calc.interpret;

import com.makaranda.calc.VedicConstants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Encyclopedia {
    private Encyclopedia() {}

    public static List<Map<String, Object>> rashis() {
        String[] nature = {
                "Cardinal fire. Head of kalapurusha. Courage, initiative, pioneering.",
                "Fixed earth. Face and throat. Beauty, food, wealth, stubborn loyalty.",
                "Dual air. Arms, lungs. Curiosity, twins, trade, restlessness.",
                "Cardinal water. Chest. Mother, home, tides of feeling, protection.",
                "Fixed fire. Heart. Kingship, dignity, children, performance.",
                "Dual earth. Belly, intestines. Discernment, seva, craft, analysis.",
                "Cardinal air. Kidneys, loins. Balance, art, the other, contracts.",
                "Fixed water. Reproductive system. Intensity, research, inheritance, secrecy.",
                "Dual fire. Thighs. Dharma, travel, teaching, the bow aimed at truth.",
                "Cardinal earth. Knees. Ambition, time, structures, the climb.",
                "Fixed air. Ankles. Networks, reform, the commons, eccentricity.",
                "Dual water. Feet. Moksha, compassion, dreams, the ocean of meaning."
        };
        String[] natureHi = {
                "चर अग्नि। कालापुरुष का मस्तक। साहस, आरम्भ, अग्रगामी स्वभाव।",
                "स्थिर पृथ्वी। मुख-कंठ। सौन्दर्य, अन्न, धन, दृढ़ निष्ठा।",
                "द्विस्वभाव वायु। बाहु-फेफड़े। जिज्ञासा, व्यापार, चंचलता।",
                "चर जल। वक्ष। माता, गृह, भाव-तरंग, रक्षा।",
                "स्थिर अग्नि। हृदय। राजत्व, गरिमा, संतान, प्रदर्शन।",
                "द्विस्वभाव पृथ्वी। उदर। विवेक, सेवा, शिल्प, विश्लेषण।",
                "चर वायु। गुर्दे। संतुलन, कला, दूसरा पक्ष, संविदा।",
                "स्थिर जल। प्रजनन तंत्र। तीव्रता, शोध, विरासत, गोपनीयता।",
                "द्विस्वभाव अग्नि। ऊरु। धर्म, यात्रा, अध्यापन, सत्य की ओर धनुष।",
                "चर पृथ्वी। जानु। महत्वाकांक्षा, काल, संरचना, आरोहण।",
                "स्थिर वायु। गुल्फ। जाल, सुधार, सार्वजनिक क्षेत्र, विलक्षणता।",
                "द्विस्वभाव जल। चरण। मोक्ष, करुणा, स्वप्न, अर्थ का सागर।"
        };
        String[] body = {"Head", "Face/Throat", "Arms/Shoulders", "Chest/Stomach", "Heart/Upper back",
                "Abdomen/Intestines", "Kidneys/Lumbar", "Pelvis/Reproductive", "Thighs/Hips",
                "Knees/Bones", "Calves/Ankles", "Feet"};
        String[] bodyHi = {"मस्तक", "मुख/कंठ", "बाहु/स्कन्ध", "वक्ष/उदर", "हृदय",
                "उदर/आंत्र", "गुर्दे", "श्रोणि", "ऊरु", "जानु", "गुल्फ", "चरण"};
        String[] elemHi = {"अग्नि", "पृथ्वी", "वायु", "जल", "अग्नि", "पृथ्वी", "वायु", "जल", "अग्नि", "पृथ्वी", "वायु", "जल"};
        String[] qualHi = {"चर", "स्थिर", "द्विस्वभाव", "चर", "स्थिर", "द्विस्वभाव", "चर", "स्थिर", "द्विस्वभाव", "चर", "स्थिर", "द्विस्वभाव"};
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("index", i);
            m.put("name", VedicConstants.SIGNS_EN[i]);
            m.put("sanskrit", VedicConstants.SIGNS_SA[i]);
            m.put("hindi", VedicConstants.SIGNS_HI[i]);
            m.put("lord", VedicConstants.SIGN_LORDS[i]);
            m.put("lordHi", VedicConstants.planetHi(VedicConstants.SIGN_LORDS[i]));
            m.put("element", VedicConstants.SIGN_ELEMENTS[i]);
            m.put("elementHi", elemHi[i]);
            m.put("quality", VedicConstants.SIGN_QUALITY[i]);
            m.put("qualityHi", qualHi[i]);
            m.put("gender", VedicConstants.SIGN_GENDER[i]);
            m.put("genderHi", "Male".equals(VedicConstants.SIGN_GENDER[i]) ? "पुं" : "स्त्री");
            m.put("body", body[i]);
            m.put("bodyHi", bodyHi[i]);
            m.put("nature", nature[i]);
            m.put("natureHi", natureHi[i]);
            m.put("exaltation", exaltationOf(i));
            list.add(m);
        }
        return list;
    }

    private static String exaltationOf(int sign) {
        List<String> p = new ArrayList<>();
        VedicConstants.EXALTATION_SIGN.forEach((k, v) -> { if (v == sign) p.add(k); });
        return p.isEmpty() ? "—" : String.join(", ", p);
    }

    public static List<Map<String, Object>> planets() {
        String[] karaka = {
                "Atma, father, king, government, bone, right eye, honour",
                "Manas, mother, queen, fluids, chest, public, food",
                "Courage, siblings, land, blood, surgery, energy",
                "Buddhi, speech, trade, skin, education, humour",
                "Dharma, children, guru, fat, wealth, wisdom, husband in female charts (traditional)",
                "Shukra, spouse, vehicles, rasa, semen/ovum, arts, luxury",
                "Ayush, grief, servants, air, teeth, longevity, discipline",
                "Foreign, shadow, obsession, maternal lineage, smoke, sudden",
                "Moksha, paternal lineage, research, flags, wounds that spiritualise"
        };
        String[] karakaHi = {
                "आत्मा, पिता, राजा, शासन, अस्थि, दक्षिण नेत्र, सम्मान",
                "मन, माता, रानी, रस, वक्ष, लोक, अन्न",
                "साहस, सहोदर, भूमि, रक्त, शल्य, ऊर्जा",
                "बुद्धि, वाणी, व्यापार, त्वक्, विद्या, हास्य",
                "धर्म, संतान, गुरु, मेद, धन, ज्ञान",
                "शुक्र, दम्पति, वाहन, रस, कला, विलास",
                "आयु, शोक, सेवक, वायु, दन्त, दीर्घायु, अनुशासन",
                "विदेश, छाया, आसक्ति, मातृवंश, धूम, आकस्मिक",
                "मोक्ष, पितृवंश, शोध, ध्वज, आध्यात्मिक घाव"
        };
        String[] day = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Saturday/Rahu kalam", "Tuesday/Ketu"};
        String[] dayHi = {"रविवार", "सोमवार", "मंगलवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार", "शनि/राहुकाल", "मंगल/केतु"};
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            String n = VedicConstants.PLANETS[i];
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", n);
            m.put("sanskrit", VedicConstants.PLANETS_SA[i]);
            m.put("hindi", VedicConstants.PLANETS_HI[i]);
            m.put("glyph", VedicConstants.PLANET_GLYPH[i]);
            m.put("karaka", karaka[i]);
            m.put("karakaHi", karakaHi[i]);
            m.put("day", day[i]);
            m.put("dayHi", dayHi[i]);
            m.put("gemstone", VedicConstants.GEMSTONES.get(n));
            m.put("metal", VedicConstants.METALS.get(n));
            m.put("colour", VedicConstants.COLOURS.get(n));
            m.put("friends", VedicConstants.NATURAL_FRIENDS.get(n));
            m.put("enemies", VedicConstants.NATURAL_ENEMIES.get(n));
            m.put("own", VedicConstants.OWN_SIGN_1.get(n));
            m.put("exaltationSign", VedicConstants.EXALTATION_SIGN.get(n));
            list.add(m);
        }
        return list;
    }

    public static List<Map<String, Object>> nakshatras() {
        String[] shortC = {
                "Healing, speed, initiated action, the horse-head physicians.",
                "Yama's restraint, bearing, sexuality as sacred labour.",
                "Agni's cut, criticism, ambition, the razor and the flame.",
                "Growth, beauty, fertility, the chariot of Brahma.",
                "Seeking, shy deer, poetry, the searching head of Soma.",
                "Storm, tears, fierce insight, Rudra's howl.",
                "Return, renewal, the quiver of Aditi, second chances.",
                "Nourishment, ethics, the most auspicious star of Brihaspati.",
                "Clinging wisdom, hypnotic speech, the naga coil.",
                "Throne of ancestors, royal pride, leaving the parental palace.",
                "Bhaga's delight, play, procreation, the front legs of the bed.",
                "Aryaman's patronage, contracts, the ripe fruit of patronage.",
                "Skilful hand, craft, the Sun's artisan, comedy.",
                "Tvashtar's design, glittering intellect, the pearl of Chitra.",
                "Independence, wind, the restlessness of Vayu.",
                "Forked purpose, triumph through alliance, Indra-Agni.",
                "Friendship, devotion, Mitra's covenant, the lotus.",
                "Elder, the eldest, Indra's banner, the protective senior.",
                "Roots, inversion, Nirriti's necessary destruction.",
                "Invincible, early victory, the fan of Apah.",
                "Universal gods, the later victory, unfinished summit.",
                "Listening, the ear of Vishnu, fame through attunement.",
                "Drum of the Vasus, wealth of rhythm, dance and property.",
                "A hundred physicians, the veiling of Varuna, mystery science.",
                "Aja Ekapada, the front funeral cot, fire that blesses from above.",
                "Ahir Budhnya, the deep serpent, rain of grace, the back of the cot.",
                "Pushan's safe passage, the fish, the end that nourishes beginnings."
        };
        String[] shortHi = {
                "चिकित्सा, वेग, आरम्भ — अश्विनी कुमार वैद्य।",
                "यम का संयम, धारण, पवित्र श्रम।",
                "अग्नि की धार, आलोचना, महत्वाकांक्षा।",
                "वृद्धि, सौन्दर्य, उर्वरता — ब्रह्मा का रथ।",
                "अन्वेषण, मृग, काव्य — सोम का खोजी शिर।",
                "झंझा, अश्रु, तीक्ष्ण दृष्टि — रुद्र की गर्जना।",
                "प्रत्यावर्तन, नवजीवन — अदिति का तरकश।",
                "पोषण, नीति — बृहस्पति का शुभ नक्षत्र।",
                "लिपटी प्रज्ञा, मोहिनी वाणी — नाग-कुण्डली।",
                "पितरों का सिंहासन, राजगर्व।",
                "भग का आनन्द, क्रीड़ा, प्रजनन।",
                "अर्यमा का आश्रय, संविदा, पका फल।",
                "निपुण हस्त, शिल्प — सवितृ का कारीगर।",
                "त्वष्टा की रचना, चमकीली बुद्धि — चित्रा-मुक्ता।",
                "स्वतन्त्रता, पवन — वायु की चंचलता।",
                "द्विधा उद्देश्य, संधि से विजय — इन्द्र-अग्नि।",
                "मैत्री, भक्ति — मित्र का प्रण, कमल।",
                "ज्येष्ठ, इन्द्र-ध्वज, रक्षक वरिष्ठ।",
                "मूल, उलटाव — निर्ऋति का आवश्यक विनाश।",
                "अजेय, पूर्व विजय — आपः का व्यजन।",
                "विश्वेदेव, उत्तर विजय, अधूरी चोटी।",
                "श्रवण, विष्णु का कर्ण, सामंजस्य से यश।",
                "वसुओं का ढोल, लय का धन, नृत्य व संपत्ति।",
                "शत वैद्य, वरुण का आवरण, गुह्य विज्ञान।",
                "अज एकपाद, अग्र शय्या, ऊर्ध्व अग्नि का आशीष।",
                "अहिर्बुध्न्य, गहरा सर्प, कृपा-वर्षा।",
                "पूषन् का सुरक्षित पथ, मत्स्य, अन्त जो आदि को पोषता है।"
        };
        String[] deityHi = {
                "अश्विनी कुमार", "यम", "अग्नि", "ब्रह्मा", "सोम", "रुद्र", "अदिति",
                "बृहस्पति", "सर्प", "पितर", "भग", "अर्यमा", "सवितृ",
                "त्वष्टा", "वायु", "इन्द्र-अग्नि", "मित्र", "इन्द्र", "निर्ऋति", "आपः",
                "विश्वेदेव", "विष्णु", "वसु", "वरुण", "अज एकपाद",
                "अहिर्बुध्न्य", "पूषन्"
        };
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 27; i++) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("index", i + 1);
            m.put("name", VedicConstants.NAKSHATRAS[i]);
            m.put("nameHi", VedicConstants.NAKSHATRAS_HI[i]);
            m.put("lord", VedicConstants.NAK_LORDS[i]);
            m.put("lordHi", VedicConstants.planetHi(VedicConstants.NAK_LORDS[i]));
            m.put("deity", VedicConstants.NAK_DEITIES[i]);
            m.put("deityHi", deityHi[i]);
            m.put("gana", VedicConstants.NAK_GANA[i]);
            m.put("ganaHi", ganaHi(VedicConstants.NAK_GANA[i]));
            m.put("yoni", VedicConstants.NAK_YONI[i]);
            m.put("yoniHi", yoniHi(VedicConstants.NAK_YONI[i]));
            m.put("nadi", VedicConstants.NAK_NADI[i]);
            m.put("nadiHi", nadiHi(VedicConstants.NAK_NADI[i]));
            m.put("span", String.format("%.2f°–%.2f°", i * (360.0 / 27), (i + 1) * (360.0 / 27)));
            m.put("character", shortC[i]);
            m.put("characterHi", shortHi[i]);
            list.add(m);
        }
        return list;
    }

    private static String ganaHi(String g) {
        return switch (g) {
            case "Deva" -> "देव";
            case "Manushya" -> "मनुष्य";
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

    public static List<Map<String, Object>> houses() {
        String[] titles = {"Tanu", "Dhana", "Sahaja", "Bandhu / Sukha", "Putra / Suta", "Ari / Ripu",
                "Yuvati / Kalatra", "Ayur / Randhra", "Dharma / Bhagya", "Karma", "Labha", "Vyaya"};
        String[] titlesHi = {"तनु", "धन", "सहज", "बन्धु / सुख", "पुत्र / सुत", "अरि / रिपु",
                "युवति / कलत्र", "आयु / रन्ध्र", "धर्म / भाग्य", "कर्म", "लाभ", "व्यय"};
        String[] texts = {
                "Body, appearance, vitality, fame, the 'I' that begins the chart.",
                "Wealth, speech, family of origin, food, face, values.",
                "Siblings, courage, short travel, writing, effort, the hands.",
                "Home, mother, vehicles, property, emotional happiness, education roots.",
                "Children, intellect, romance, poorva punya, speculation, mantra.",
                "Enemies, disease, debts, service, maternal uncle, daily labour.",
                "Spouse, desire, the other, business partner, travel abroad (western overlay).",
                "Longevity, death-and-rebirth, occult, inheritance, chronic, sexuality as mystery.",
                "Dharma, luck, father, guru, long pilgrimage, higher mind.",
                "Career, status, karma-yoga, government, the sky of achievement.",
                "Gains, friends, elder sibling, income, ayanas of desire fulfilled.",
                "Loss, expenses, foreign residence, bed-pleasures, moksha, the feet that leave."
        };
        String[] textsHi = {
                "शरीर, आकृति, प्राण, यश — कुंडली का 'अहम्'।",
                "धन, वाणी, जन्म-परिवार, अन्न, मुख, मूल्य।",
                "सहोदर, साहस, लघु यात्रा, लेखन, प्रयास, हाथ।",
                "गृह, माता, वाहन, संपत्ति, भाव-सुख, शिक्षा-मूल।",
                "संतान, बुद्धि, प्रेम, पूर्व पुण्य, सट्टा, मन्त्र।",
                "शत्रु, रोग, ऋण, सेवा, मामा, दैनिक श्रम।",
                "दम्पति, काम, दूसरा, व्यापार-साथी, विदेश यात्रा।",
                "आयु, मृत्यु-पुनर्जन्म, गुह्य, विरासत, दीर्घ रोग।",
                "धर्म, भाग्य, पिता, गुरु, तीर्थ, उच्च मन।",
                "कर्म, पद, कर्मयोग, शासन, उपलब्धि का आकाश।",
                "लाभ, मित्र, ज्येष्ठ सहोदर, आय।",
                "हानि, व्यय, विदेशवास, शयन-सुख, मोक्ष, विदाई के चरण।"
        };
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("number", i + 1);
            m.put("title", titles[i]);
            m.put("titleHi", titlesHi[i]);
            m.put("text", texts[i]);
            m.put("textHi", textsHi[i]);
            m.put("kendra", i == 0 || i == 3 || i == 6 || i == 9);
            m.put("trikona", i == 0 || i == 4 || i == 8);
            m.put("dusthana", i == 5 || i == 7 || i == 11);
            m.put("upachaya", i == 2 || i == 5 || i == 9 || i == 10);
            list.add(m);
        }
        return list;
    }
}
