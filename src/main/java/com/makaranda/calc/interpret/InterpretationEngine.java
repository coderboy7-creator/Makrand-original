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
        out.put("personality", personality(c));
        out.put("lagna", houseRead(c, 1, "Self, body, vitality, appearance"));
        out.put("wealth", houseRead(c, 2, "Speech, family, savings, food"));
        out.put("career", houseRead(c, 10, "Karma, status, profession, public life"));
        out.put("marriage", houseRead(c, 7, "Spouse, partnerships, public dealings"));
        out.put("fortune", houseRead(c, 9, "Dharma, father, guru, long journeys"));
        out.put("mind", mind(c));
        out.put("currentDasha", currentDasha(c));
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
