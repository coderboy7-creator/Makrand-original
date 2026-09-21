package com.makaranda.calc.interpret;

import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.dasha.VimshottariDasha;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;
import com.makaranda.calc.vedic.ChartBuilder.PlanetBody;
import com.makaranda.calc.yoga.YogaDetector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Deterministic "custom LLM" layer — a structured interpreter that composes
 * Sanskrit-school readings from chart facts. Optional external LLM can wrap this
 * JSON as context (see LlmClient).
 */
public final class InterpretationEngine {

    public Map<String, Object> interpret(FullChart c) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("summary", summary(c));
        out.put("summaryHi", summaryHi(c));
        out.put("personality", personality(c));
        out.put("personalityHi", personalityHi(c));
        out.put("lagna", houseRead(c, 1, "Self, body, vitality, appearance"));
        out.put("lagnaEssay", lagnaEssay(c));
        out.put("lagnaEssayHi", lagnaEssayHi(c));
        out.put("wealth", houseRead(c, 2, "Speech, family, savings, food"));
        out.put("career", houseRead(c, 10, "Karma, status, profession, public life"));
        out.put("livelihood", livelihood(c));
        out.put("livelihoodHi", livelihoodHi(c));
        out.put("marriage", houseRead(c, 7, "Spouse, partnerships, public dealings"));
        out.put("fortune", houseRead(c, 9, "Dharma, father, guru, long journeys"));
        out.put("mind", mind(c));
        out.put("mercury", grahaByBhava(c, "Mercury"));
        out.put("saturn", grahaByBhava(c, "Saturn"));
        out.put("currentDasha", currentDasha(c));
        out.put("mahaByHouse", mahaByHouse(c));
        out.put("yogas", YogaDetector.analyse(c));
        out.put("remedies", remedies(c));
        out.put("houses", allHouses(c));
        return out;
    }

    public Map<String, Object> dashaPrediction(FullChart c, String lord, String antarLord) {
        Map<String, Object> m = new LinkedHashMap<>();
        PlanetBody p = c.planets().get(lord);
        m.put("mahadasha", lord);
        m.put("antardasha", antarLord);
        m.put("placement", p == null ? "" : p.name() + " in " + p.signSa() + " in house " + p.house() + " (" + p.dignity() + ")");
        m.put("themes", themesFor(lord, p == null ? 1 : p.house()));
        m.put("advice", adviceFor(lord));
        if (antarLord != null && c.planets().containsKey(antarLord)) {
            PlanetBody a = c.planets().get(antarLord);
            m.put("antarThemes", themesFor(antarLord, a.house()));
        }
        return m;
    }

    private String summary(FullChart c) {
        PlanetBody moon = c.planets().get("Moon");
        PlanetBody sun = c.planets().get("Sun");
        return "Lagna " + c.lagna().signSa() + " (" + c.lagna().sign() + ") rises at "
                + c.lagna().signDegree() + ". Chandra in " + moon.nakshatra() + " pada " + moon.pada()
                + ", rashi " + moon.signSa() + ". Surya in " + sun.signSa() + " house " + sun.house()
                + ". Ayanamsa: " + c.ayanamsaLabel() + " (" + String.format("%.4f", c.ayanamsaDeg())
                + "°). Panchang mode: " + c.panchangMode() + ".";
    }

    private String personality(FullChart c) {
        int s = c.lagna().signIndex();
        String lagna = switch (s) {
            case 0 -> "Pioneer energy, direct speech, competitive vitality.";
            case 1 -> "Steadfast, sensual, builds value slowly, fiercely loyal.";
            case 2 -> "Curious, dual-minded, lives through words and networks.";
            case 3 -> "Protective, tidal moods, nourishes clan and home.";
            case 4 -> "Radiant, proud, needs meaningful stage and self-respect.";
            case 5 -> "Discerning, service-minded, crafts skill into livelihood.";
            case 6 -> "Diplomatic, aesthetic, seeks fairness and companionship.";
            case 7 -> "Intense, investigative, transforms what it touches.";
            case 8 -> "Philosophical, restless for meaning, teacher or pilgrim.";
            case 9 -> "Ambitious, structured, climbs through endurance.";
            case 10 -> "Unconventional, humanitarian, lives by inner law.";
            default -> "Empathic, imaginal, dissolves boundaries, serves quietly.";
        };
        PlanetBody moon = c.planets().get("Moon");
        return lagna + " Moon in " + moon.nakshatra() + " colours the manas with "
                + VedicConstants.NAK_DEITIES[moon.nakshatraIndex()] + "'s signature ("
                + VedicConstants.NAK_GANA[moon.nakshatraIndex()] + " gana).";
    }

    private Map<String, Object> houseRead(FullChart c, int house, String domain) {
        List<String> occupants = new ArrayList<>();
        for (PlanetBody p : c.planets().values()) {
            if (p.house() == house) occupants.add(p.name() + " (" + p.dignity() + ")");
        }
        int sign = (c.lagna().signIndex() + house - 1) % 12;
        String lord = VedicConstants.SIGN_LORDS[sign];
        PlanetBody lp = c.planets().get(lord);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("house", house);
        m.put("domain", domain);
        m.put("sign", VedicConstants.SIGNS_SA[sign]);
        m.put("lord", lord);
        m.put("lordHouse", lp.house());
        m.put("lordDignity", lp.dignity());
        m.put("occupants", occupants);
        m.put("reading", "House " + house + " is " + VedicConstants.SIGNS_SA[sign]
                + " ruled by " + lord + " placed in house " + lp.house() + " in "
                + lp.signSa() + " (" + lp.dignity() + "). "
                + (occupants.isEmpty() ? "No graha occupies it — results flow mainly through the lord."
                : "Occupied by " + String.join(", ", occupants) + "."));
        return m;
    }

    private Map<String, Object> mind(FullChart c) {
        PlanetBody moon = c.planets().get("Moon");
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("rashi", moon.signSa());
        m.put("nakshatra", moon.nakshatra());
        m.put("pada", moon.pada());
        m.put("house", moon.house());
        m.put("dignity", moon.dignity());
        m.put("reading", "The Moon in " + moon.signSa() + " / " + moon.nakshatra()
                + " pada " + moon.pada() + " in house " + moon.house()
                + " describes emotional weather, mother and public. Dignity: " + moon.dignity() + ".");
        return m;
    }

    private Map<String, Object> currentDasha(FullChart c) {
        var periods = VimshottariDasha.compute(c.planets().get("Moon").siderealLon(),
                c.input().localDateTime(), 3);
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> m = new LinkedHashMap<>();
        for (var maha : periods) {
            if (!now.isBefore(maha.start()) && now.isBefore(maha.end())) {
                m.put("mahadasha", maha.lord());
                m.put("mahaStart", maha.start().toString());
                m.put("mahaEnd", maha.end().toString());
                for (var antar : maha.children()) {
                    if (!now.isBefore(antar.start()) && now.isBefore(antar.end())) {
                        m.put("antardasha", antar.lord());
                        m.put("antarStart", antar.start().toString());
                        m.put("antarEnd", antar.end().toString());
                        for (var pr : antar.children()) {
                            if (!now.isBefore(pr.start()) && now.isBefore(pr.end())) {
                                m.put("pratyantardasha", pr.lord());
                                m.put("pratyantarStart", pr.start().toString());
                                m.put("pratyantarEnd", pr.end().toString());
                            }
                        }
                    }
                }
                m.put("prediction", dashaPrediction(c, maha.lord(), (String) m.get("antardasha")));
            }
        }
        return m;
    }

    private List<String> remedies(FullChart c) {
        List<String> r = new ArrayList<>();
        for (PlanetBody p : c.planets().values()) {
            if ("Debilitated".equals(p.dignity()) || "Enemy".equals(p.dignity())) {
                r.add(p.name() + " is " + p.dignity() + " in " + p.signSa()
                        + ". Consider " + VedicConstants.GEMSTONES.get(p.name())
                        + " only after dasha confirmation; mantra and dana are safer first steps.");
            }
        }
        r.add("Surya arghya at sunrise facing east — core Mithila nitya karma.");
        r.add("Saturday sesame / Saturday oil lamp if Saturn is functional malefic.");
        r.add("Pitra tarpan on Amavasya at Ganga-Bagmati sangam tradition for Maithil families.");
        return r;
    }

    private List<Map<String, Object>> allHouses(FullChart c) {
        String[] domains = {
                "Self, body, fame", "Wealth, speech, family", "Siblings, courage, effort",
                "Home, mother, vehicles", "Children, intellect, romance", "Enemies, disease, service",
                "Marriage, desire, other", "Longevity, occult, transformation", "Dharma, luck, father",
                "Career, karma, status", "Gains, friends, elder sibling", "Loss, foreign, moksha"
        };
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i <= 12; i++) list.add(houseRead(c, i, domains[i - 1]));
        return list;
    }

    private String themesFor(String lord, int house) {
        String planet = switch (lord) {
            case "Sun" -> "authority, father, government, vitality, soul-purpose";
            case "Moon" -> "mind, mother, public, fluids, home, popularity";
            case "Mars" -> "energy, property, siblings, surgery, conflict, land";
            case "Mercury" -> "commerce, writing, analysis, trade, nervous system";
            case "Jupiter" -> "wisdom, children, wealth, guru, law, grace";
            case "Venus" -> "marriage, arts, vehicles, luxury, rasa";
            case "Saturn" -> "labour, delay, structure, servants, longevity, grief that ripens";
            case "Rahu" -> "foreign, unconventional ambition, smoke-and-mirrors, sudden rise";
            case "Ketu" -> "detachment, research, moksha, past-life skill, losses that liberate";
            default -> "karmic unfolding";
        };
        return "Mahadasha of " + lord + " activates house " + house + " and significations of " + planet + ".";
    }

    private String summaryHi(FullChart c) {
        PlanetBody moon = c.planets().get("Moon");
        PlanetBody sun = c.planets().get("Sun");
        return "लग्न " + c.lagna().signHi() + " " + c.lagna().signDegree()
                + "। चन्द्र " + moon.nakshatraHi() + " चरण " + moon.pada()
                + ", राशि " + moon.signHi() + "। सूर्य " + sun.signHi()
                + " भाव " + sun.house() + "। अयनांश " + c.ayanamsaLabel()
                + "। गणिता " + c.panchangMode() + "।";
    }

    private String personalityHi(FullChart c) {
        String[] lines = {
                "अग्रणी ऊर्जा, सीधी वाणी, स्पर्धात्मक प्राण।",
                "स्थिर, रसप्रिय, मूल्य धीरे बाँधते हैं, निष्ठा गहरी।",
                "जिज्ञासु, द्वि-मन, शब्द और जाल से जीते हैं।",
                "रक्षक, ज्वारीय भाव, कुल और घर पोषण करते हैं।",
                "तेजस्वी, स्वाभिमान, अर्थपूर्ण मंच चाहते हैं।",
                "विवेकी, सेवा-बुद्धि, कौशल से आजीविका।",
                "सौम्य, सौन्दर्यप्रिय, न्याय और संग चाहते हैं।",
                "तीव्र, शोधक, जिसे छूते हैं बदल देते हैं।",
                "दार्शनिक, अर्थ-यात्री, गुरु या तीर्थ पथ।",
                "महत्त्वाकांक्षी, संरचना, धैर्य से चढ़ते हैं।",
                "अपरम्परा, लोकहित, अन्तर-नियम से जीते हैं।",
                "संवेदक, कल्पना, सीमा गलाकर शांत सेवा।"
        };
        PlanetBody moon = c.planets().get("Moon");
        return lines[c.lagna().signIndex()] + " चन्द्र " + moon.nakshatraHi()
                + " मन को " + VedicConstants.NAK_DEITIES[moon.nakshatraIndex()] + " की छाप देते हैं।";
    }

    private String lagnaEssay(FullChart c) {
        PlanetBody lagnaLord = c.planets().get(c.lagna().rashiLord());
        return "Lagna is " + c.lagna().sign() + ". Lagnesha " + c.lagna().rashiLord()
                + " sits in house " + lagnaLord.house() + " (" + lagnaLord.dignity()
                + "). Body, fame and the opening of life follow that lord — not a canned essay.";
    }

    private String lagnaEssayHi(FullChart c) {
        PlanetBody lagnaLord = c.planets().get(c.lagna().rashiLord());
        return "लग्न " + c.lagna().signHi() + "। लग्नेश " + VedicConstants.planetHi(c.lagna().rashiLord())
                + " भाव " + lagnaLord.house() + " में (" + VedicConstants.dignityHi(lagnaLord.dignity())
                + ")। तनु, कीर्ति और जीवन-आरम्भ उसी स्वामी से — डिब्बाबंद पाठ नहीं।";
    }

    private String livelihood(FullChart c) {
        Map<String, Object> tenth = houseRead(c, 10, "Karma, status, profession, public life");
        PlanetBody mer = c.planets().get("Mercury");
        PlanetBody sat = c.planets().get("Saturn");
        return tenth.get("reading") + " Mercury (skill/trade) in house " + mer.house()
                + "; Saturn (labour/office) in house " + sat.house() + ".";
    }

    private String livelihoodHi(FullChart c) {
        int sign = (c.lagna().signIndex() + 9) % 12;
        String lord = VedicConstants.SIGN_LORDS[sign];
        PlanetBody lp = c.planets().get(lord);
        PlanetBody mer = c.planets().get("Mercury");
        PlanetBody sat = c.planets().get("Saturn");
        return "दशम " + VedicConstants.SIGNS_HI[sign] + ", स्वामी "
                + VedicConstants.planetHi(lord) + " भाव " + lp.house() + " में। बुध (कौशल/वाणिज्य) भाव "
                + mer.house() + "; शनि (श्रम/पद) भाव " + sat.house() + "। आजीविका इन्हीं से पढ़ें।";
    }

    private Map<String, Object> grahaByBhava(FullChart c, String name) {
        PlanetBody p = c.planets().get(name);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("planet", name);
        m.put("planetHi", VedicConstants.planetHi(name));
        m.put("house", p.house());
        m.put("sign", p.sign());
        m.put("signHi", p.signHi());
        m.put("dignity", p.dignity());
        m.put("reading", name + " in house " + p.house() + " / " + p.sign() + " (" + p.dignity() + "). "
                + bhavaNote(name, p.house()));
        m.put("readingHi", VedicConstants.planetHi(name) + " भाव " + p.house() + " / " + p.signHi()
                + " (" + VedicConstants.dignityHi(p.dignity()) + ")। " + bhavaNoteHi(name, p.house()));
        return m;
    }

    private String bhavaNote(String planet, int h) {
        if ("Mercury".equals(planet)) {
            return switch (h) {
                case 1 -> "Speech and wit colour the body and first impression.";
                case 2 -> "Family accounts, food talk, and savings skill.";
                case 3 -> "Writing, siblings, short trade routes.";
                case 4 -> "Study at home, property papers.";
                case 5 -> "Buddhi, mantra, speculation — watch nerves.";
                case 6 -> "Service, editing, disputes of words.";
                case 7 -> "Contracts, clients, a partner who talks shop.";
                case 8 -> "Research, occult study, tax and shared money.";
                case 9 -> "Teaching, dharma-texts, long study.";
                case 10 -> "Profession through pen, trade, or analysis.";
                case 11 -> "Gains from networks and commerce.";
                default -> "Foreign papers, losses that teach, moksha-study.";
            };
        }
        return switch (h) {
            case 1 -> "Duty sits on the body; slow vitality, serious face.";
            case 2 -> "Family karma, delayed wealth, spare speech.";
            case 3 -> "Courage through labour; siblings and effort mature late.";
            case 4 -> "Heavy home, land after time, mother-duty.";
            case 5 -> "Children and intellect need patience; mantra over speculation.";
            case 6 -> "Service, debt, disease-work — Saturn is usable here.";
            case 7 -> "Late or sober partnership; public contracts that last.";
            case 8 -> "Longevity yoga or chronic tests; occult labour.";
            case 9 -> "Dharma through austerity; father and guru as taskmasters.";
            case 10 -> "Office, government, climb by endurance.";
            case 11 -> "Slow gains, elder friends, structured income.";
            default -> "Vyaya, foreign, retreat — isolation that ripens.";
        };
    }

    private String bhavaNoteHi(String planet, int h) {
        if ("Mercury".equals(planet)) {
            return switch (h) {
                case 1 -> "वाणी और बुद्धि तनु व प्रथम प्रभाव रँगते हैं।";
                case 2 -> "कुल-लेखा, अन्न-संवाद, बचत-कौशल।";
                case 3 -> "लेखन, सहज, लघु व्यापार-मार्ग।";
                case 4 -> "गृह-अध्ययन, भूमि-पत्र।";
                case 5 -> "बुद्धि, मन्त्र, सट्टा — तंत्रिका देखें।";
                case 6 -> "सेवा, संपादन, शब्द-विवाद।";
                case 7 -> "अनुबन्ध, ग्राहक, व्यापार-संग।";
                case 8 -> "शोध, गुप्त विद्या, कर व साझा धन।";
                case 9 -> "अध्यापन, धर्म-ग्रन्थ, दीर्घ अध्ययन।";
                case 10 -> "लेखनी, वाणिज्य या विश्लेषण से कर्म।";
                case 11 -> "जाल और वाणिज्य से लाभ।";
                default -> "विदेश-पत्र, हानि जो सिखाए, मोक्ष-पाठ।";
            };
        }
        return switch (h) {
            case 1 -> "कर्तव्य तनु पर; धीमा प्राण, गम्भीर मुख।";
            case 2 -> "कुल-कर्म, विलम्बित धन, मित वाणी।";
            case 3 -> "श्रम से साहस; सहज देर से पकते हैं।";
            case 4 -> "भारी गृह, समय से भूमि, मातृ-कर्तव्य।";
            case 5 -> "सुत-बुद्धि को धैर्य; सट्टे से मन्त्र श्रेष्ठ।";
            case 6 -> "सेवा, ऋण, रोग-कर्म — शनि यहाँ काम आते हैं।";
            case 7 -> "विलम्ब/गम्भीर संग; टिकने वाले अनुबन्ध।";
            case 8 -> "आयु-योग या दीर्घ परीक्षा; गुप्त श्रम।";
            case 9 -> "तप से धर्म; पिता-गुरु कठोर शिक्षक।";
            case 10 -> "पद, शासन, धैर्य से चढ़ाई।";
            case 11 -> "धीमा लाभ, ज्येष्ठ मित्र, संरचना आय।";
            default -> "व्यय, विदेश, एकान्त जो पकता है।";
        };
    }

    private Map<String, Object> mahaByHouse(FullChart c) {
        Map<String, Object> cur = currentDasha(c);
        String lord = String.valueOf(cur.getOrDefault("mahadasha", ""));
        PlanetBody p = c.planets().get(lord);
        int h = p == null ? 1 : p.house();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("mahadasha", lord);
        m.put("house", h);
        m.put("reading", themesFor(lord, h) + " " + adviceFor(lord));
        m.put("readingHi", "महादशा " + VedicConstants.planetHi(lord) + " भाव " + h
                + " जगाती है। " + adviceForHi(lord));
        return m;
    }

    private String adviceForHi(String lord) {
        return switch (lord) {
            case "Saturn" -> "धीमा संचय। सेवा, सरलता, वचन-पालन। Shortcut न लें।";
            case "Rahu" -> "हर चमकते विदेश-अवसर के पीछे न भागें। एक असाधारण पथ।";
            case "Ketu" -> "पद-क्रीड़ा छोड़ें। शोध, साधना, विशेषज्ञ कौशल।";
            case "Jupiter" -> "शिक्षा, परामर्श, धर्म से विस्तार। विवाह-सुत-अध्ययन अनुकूल।";
            case "Venus" -> "रस बिना अति। सम्बन्ध और सौन्दर्य कर्म-योग बनें।";
            case "Mars" -> "कर्म करें, व्यर्थ कलह न चुनें। भूमि, शिल्प, खेल।";
            case "Sun" -> "स्वच्छ नेतृत्व। शासन, चिकित्सा, पितृ-आकृति। अहं-शुद्धि साधना।";
            case "Moon" -> "निद्रा, माता, भाव-आहार रक्षा। लोक-मुख कार्य अनुकूल।";
            case "Mercury" -> "लिखें, व्यापार, सीखें। अनुबन्ध में मिश्र संकेत देखें।";
            default -> "दशा को सचेत जीएँ।";
        };
    }

    private String adviceFor(String lord) {
        return switch (lord) {
            case "Saturn" -> "Prefer slow compounding. Serve, simplify, keep promises. Avoid shortcuts.";
            case "Rahu" -> "Do not chase every shiny foreign opportunity. One focused unconventional path.";
            case "Ketu" -> "Release status games. Research, spiritual practice, and specialist skill excel.";
            case "Jupiter" -> "Teach, counsel, expand ethically. Good for marriage, children, higher study.";
            case "Venus" -> "Cultivate rasa without excess. Relationships and aesthetics become karma-yoga.";
            case "Mars" -> "Act, but do not pick needless fights. Land, engineering, sport, surgery thrive.";
            case "Sun" -> "Lead cleanly. Government, medicine, father-figures. Ego hygiene is the sadhana.";
            case "Moon" -> "Protect sleep, mother, and emotional diet. Public-facing work favoured.";
            case "Mercury" -> "Write, trade, learn. Watch anxiety and mixed signals in contracts.";
            default -> "Live the dasha consciously.";
        };
    }
}
