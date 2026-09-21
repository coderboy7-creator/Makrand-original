package com.makaranda.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.dasha.VimshottariDasha;
import com.makaranda.calc.dasha.YoginiDasha;
import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.PanchangMode;
import com.makaranda.calc.interpret.Encyclopedia;
import com.makaranda.calc.interpret.InterpretationEngine;
import com.makaranda.calc.match.AshtakootaMatcher;
import com.makaranda.calc.muhurta.MuhurtaCalculator;
import com.makaranda.calc.panchang.PanchangCalculator;
import com.makaranda.calc.transit.TransitCalculator;
import com.makaranda.calc.vedic.Ashtakavarga;
import com.makaranda.calc.vedic.Shadbala;
import com.makaranda.calc.vedic.ChartBuilder;
import com.makaranda.calc.vedic.ChartBuilder.BirthInput;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;
import com.makaranda.calc.vedic.SpecialCharts;
import com.makaranda.calc.yoga.YogaDetector;
import com.makaranda.dto.BirthRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class JyotishService {
    private final ChartBuilder charts = new ChartBuilder();
    private final PanchangCalculator panchang = new PanchangCalculator();
    private final TransitCalculator transits = new TransitCalculator();
    private final MuhurtaCalculator muhurta = new MuhurtaCalculator();
    private final SpecialCharts special = new SpecialCharts();
    private final InterpretationEngine interpreter = new InterpretationEngine();
    private final ObjectMapper mapper;

    @Value("${makaranda.default-lat}") double defaultLat;
    @Value("${makaranda.default-lon}") double defaultLon;
    @Value("${makaranda.default-place}") String defaultPlace;

    public JyotishService() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public BirthInput toInput(BirthRequest r) {
        if (r == null) r = new BirthRequest();
        if (r.dateTime == null) r.dateTime = LocalDateTime.now();
        if (r.latitude == null) r.latitude = defaultLat;
        if (r.longitude == null) r.longitude = defaultLon;
        if (r.place == null || r.place.isBlank()) r.place = defaultPlace;
        if (r.tzOffsetHours == null) r.tzOffsetHours = 5.5;
        if (r.timeZone == null) r.timeZone = "Asia/Kolkata";
        return new BirthInput(
                r.dateTime, r.timeZone, r.tzOffsetHours, r.latitude, r.longitude, r.place,
                AyanamsaSystem.from(r.ayanamsa), PanchangMode.from(r.panchangMode),
                r.houseSystem == null ? "WHOLE_SIGN" : r.houseSystem
        );
    }

    public FullChart chart(BirthRequest r) {
        return charts.build(toInput(r));
    }

    public Map<String, Object> chartPayload(BirthRequest r) {
        FullChart c = chart(r);
        Map<String, Object> m = mapper.convertValue(c, Map.class);
        m.put("interpretation", interpreter.interpret(c));
        m.put("yogas", YogaDetector.analyse(c));
        m.put("gemstones", special.gemstones(c));
        m.put("dasha", dashaTree(c));
        m.put("ashtakavarga", Ashtakavarga.fromChart(c));
        m.put("shadbala", Shadbala.fromChart(c));
        return m;
    }

    public Map<String, Object> ashtakavarga(BirthRequest r) {
        return Ashtakavarga.fromChart(chart(r));
    }

    public Map<String, Object> shadbala(BirthRequest r) {
        return Shadbala.fromChart(chart(r));
    }

    public Map<String, Object> dashaTree(FullChart c) {
        double moon = c.planets().get("Moon").siderealLon();
        LocalDateTime birth = c.input().localDateTime();
        LocalDateTime now = LocalDateTime.now();
        var periods = VimshottariDasha.compute(moon, birth, 3);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("system", "Vimshottari");
        out.put("cycleYears", 120);
        out.put("nakshatra", VedicConstants.NAKSHATRAS[VedicConstants.nakshatraIndex(moon)]);
        out.put("nakLord", VedicConstants.NAK_LORDS[VedicConstants.nakshatraIndex(moon)]);
        out.put("periods", mapper.convertValue(periods, List.class));
        out.put("current", VimshottariDasha.currentAt(periods, now));
        out.put("currentAtBirth", VimshottariDasha.currentAt(periods, birth));
        Map<String, Object> yog = YoginiDasha.bundle(moon, birth, now);
        yog.put("periods", mapper.convertValue(yog.get("periods"), List.class));
        yog.put("current", mapper.convertValue(yog.get("current"), Map.class));
        out.put("yogini", yog);
        return out;
    }

    public Map<String, Object> panchang(LocalDate date, Double lat, Double lon, Double tz,
                                        String ayanamsa, String mode) {
        AyanamsaSystem ay = (ayanamsa == null || ayanamsa.isBlank())
                ? AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA : AyanamsaSystem.from(ayanamsa);
        PanchangMode md = (mode == null || mode.isBlank())
                ? PanchangMode.SIDDHANTIC : PanchangMode.from(mode);
        return panchang.compute(
                date == null ? LocalDate.now() : date,
                lat == null ? defaultLat : lat,
                lon == null ? defaultLon : lon,
                tz == null ? 5.5 : tz,
                ay, md
        );
    }

    public List<Map<String, Object>> panchangMonth(LocalDate month, Double lat, Double lon, Double tz,
                                                   String ayanamsa, String mode) {
        return panchang.month(month == null ? LocalDate.now() : month,
                lat == null ? defaultLat : lat, lon == null ? defaultLon : lon,
                tz == null ? 5.5 : tz, AyanamsaSystem.from(ayanamsa), PanchangMode.from(mode));
    }

    public Map<String, Object> match(BirthRequest boy, BirthRequest girl) {
        return AshtakootaMatcher.match(chart(boy), chart(girl));
    }

    public Map<String, Object> transits(BirthRequest natal, LocalDate date) {
        FullChart c = chart(natal);
        return transits.gochar(c, date == null ? LocalDate.now() : date,
                AyanamsaSystem.from(natal.ayanamsa), PanchangMode.from(natal.panchangMode));
    }

    public List<Map<String, Object>> muhurta(String purpose, LocalDate from, int days,
                                             Double lat, Double lon, Double tz, String ay, String mode) {
        return muhurta(purpose, from, days, lat, lon, tz, ay, mode, null, null, null, null, null, null, null);
    }

    public List<Map<String, Object>> muhurta(String purpose, LocalDate from, int days,
                                             Double lat, Double lon, Double tz, String ay, String mode,
                                             Double fromLat, Double fromLon, String fromPlace,
                                             Double toLat, Double toLon, String toPlace, String direction) {
        MuhurtaCalculator.Purpose p;
        try {
            p = MuhurtaCalculator.Purpose.valueOf(purpose.toUpperCase());
        } catch (Exception e) {
            p = MuhurtaCalculator.Purpose.GENERAL;
        }
        double oLat = fromLat != null ? fromLat : (lat != null ? lat : defaultLat);
        double oLon = fromLon != null ? fromLon : (lon != null ? lon : defaultLon);
        MuhurtaCalculator.TravelQuery tq = null;
        if (p == MuhurtaCalculator.Purpose.TRAVEL) {
            tq = new MuhurtaCalculator.TravelQuery(
                    fromPlace == null || fromPlace.isBlank() ? defaultPlace : fromPlace,
                    oLat, oLon, toPlace, toLat, toLon, direction);
        }
        AyanamsaSystem ayan = (ay == null || ay.isBlank())
                ? AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA : AyanamsaSystem.from(ay);
        PanchangMode md = (mode == null || mode.isBlank())
                ? PanchangMode.SIDDHANTIC : PanchangMode.from(mode);
        return muhurta.search(p, from == null ? LocalDate.now() : from, days <= 0 ? 45 : days,
                oLat, oLon, tz == null ? 5.5 : tz, ayan, md, tq);
    }

    public Map<String, Object> muhurtaBundle(com.makaranda.dto.MuhurtaRequest req) {
        if (req == null) req = new com.makaranda.dto.MuhurtaRequest();
        List<Map<String, Object>> days = muhurta(req.purpose, req.from, req.days, req.lat, req.lon,
                req.tzOffsetHours, req.ayanamsa, req.panchangMode,
                req.fromLat, req.fromLon, req.fromPlace, req.toLat, req.toLon, req.toPlace, req.direction);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("purpose", req.purpose);
        out.put("days", days);
        if ("TRAVEL".equalsIgnoreCase(req.purpose)) {
            MuhurtaCalculator.TravelQuery tq = new MuhurtaCalculator.TravelQuery(
                    req.fromPlace == null ? defaultPlace : req.fromPlace,
                    req.fromLat != null ? req.fromLat : defaultLat,
                    req.fromLon != null ? req.fromLon : defaultLon,
                    req.toPlace, req.toLat, req.toLon, req.direction);
            String dir = MuhurtaCalculator.resolveDirection(tq);
            out.put("fromPlace", tq.fromPlace());
            out.put("toPlace", tq.toPlace());
            out.put("direction", dir);
            out.put("directionHi", MuhurtaCalculator.directionHi(dir));
            if (req.toLat != null && req.toLon != null) {
                double b = MuhurtaCalculator.bearingDeg(tq.fromLat(), tq.fromLon(), req.toLat, req.toLon);
                out.put("bearingDeg", Math.round(b * 10) / 10.0);
            }
            out.put("noteHi", "यात्रा मुहूर्त दिशा-शूल, चर नक्षत्र और वार पर आधारित है। मकरन्द पंचांग, सूर्योदय स्थान = प्रस्थान स्थान।");
        }
        return out;
    }

    public Map<String, Object> varshaphal(BirthRequest natal, int year) {
        return special.varshaphal(chart(natal), year);
    }

    public Map<String, Object> prashna(BirthRequest when, String question) {
        return special.prashna(toInput(when), question == null ? "General query" : question);
    }

    public Map<String, Object> interpret(BirthRequest r) {
        return interpreter.interpret(chart(r));
    }

    public Map<String, Object> encyclopedia() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("rashis", Encyclopedia.rashis());
        m.put("planets", Encyclopedia.planets());
        m.put("nakshatras", Encyclopedia.nakshatras());
        m.put("houses", Encyclopedia.houses());
        m.put("ayanamsas", ayanamsaCatalog());
        return m;
    }

    public List<Map<String, Object>> ayanamsaCatalog() {
        return java.util.Arrays.stream(AyanamsaSystem.values()).map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.name());
            m.put("label", a.label);
            m.put("siddhanticFamily", a.siddhanticFamily);
            m.put("default", a == AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA);
            return m;
        }).toList();
    }

    public Map<String, Object> dailyHoroscope(LocalDate date, Double lat, Double lon, String ay, String mode) {
        Map<String, Object> panch = panchang(date, lat, lon, 5.5, ay, mode);
        List<Map<String, Object>> rashis = new java.util.ArrayList<>();
        String[] lucky = {"Red", "White", "Green", "Silver", "Gold", "Blue", "Pink", "Maroon", "Yellow", "Black", "Grey", "Sea-green"};
        String[] luckyHi = {"लाल", "श्वेत", "हरा", "रजत", "स्वर्ण", "नीला", "गुलाबी", "मरून", "पीला", "काला", "धूसर", "समुद्री हरा"};
        String[] numbers = {"9", "2", "5", "2", "1", "5", "6", "9", "3", "8", "8", "3"};
        String moonR = String.valueOf(panch.get("moonRashi"));
        for (int i = 0; i < 12; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("sign", com.makaranda.calc.VedicConstants.SIGNS_EN[i]);
            row.put("sanskrit", com.makaranda.calc.VedicConstants.SIGNS_SA[i]);
            row.put("hindi", com.makaranda.calc.VedicConstants.SIGNS_HI[i]);
            row.put("luckyColour", lucky[i]);
            row.put("luckyColourHi", luckyHi[i]);
            row.put("luckyNumber", numbers[i]);
            row.put("prediction", dailyLine(i, panch, moonR));
            row.put("predictionHi", dailyLineHi(i, panch));
            rashis.add(row);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("date", (date == null ? LocalDate.now() : date).toString());
        out.put("panchang", panch);
        out.put("rashis", rashis);
        return out;
    }

    private String dailyLine(int sign, Map<String, Object> p, String moonRashi) {
        String tithi = p.get("paksha") + " " + p.get("tithi");
        String nak = String.valueOf(p.get("nakshatra"));
        String[] verbs = {
                "Take the first step before noon; Mars favours initiative.",
                "Hold your ground on money and food; Venus asks for quality not quantity.",
                "Conversations multiply. Write it down before you send.",
                "Family and water rituals soothe. Do not over-promise.",
                "A stage appears. Lead without burning your team.",
                "Edit, organise, serve. Health loves routine today.",
                "Partnerships need a fair contract. Beauty in the details.",
                "Research, don't react. Secrets want a lock, not a leak.",
                "A teacher or long road opens. Dharma over drama.",
                "Climb the mountain one ledger at a time. Saturn applauds.",
                "Network, reform, donate. The group mind is your ally.",
                "Rest, imagine, forgive. The ocean does not hurry."
        };
        return verbs[sign] + " Today's tithi is " + tithi + ", Moon in " + moonRashi
                + " / " + nak + ". Prefer Abhijit muhurta; skip Rahu Kalam.";
    }

    private String dailyLineHi(int sign, Map<String, Object> p) {
        String tithi = p.get("pakshaHi") + " " + p.get("tithiHi");
        String nak = String.valueOf(p.get("nakshatraHi"));
        String moonR = String.valueOf(p.get("moonRashiHi"));
        String[] verbs = {
                "मध्याह्न से पूर्व पहला कदम उठाएँ; मंगल आरम्भ का साथ देता है।",
                "धन और अन्न पर दृढ़ रहें; शुक्र मात्रा नहीं, गुण माँगता है।",
                "संवाद बढ़ेंगे। भेजने से पहले लिख लें।",
                "परिवार और जल-कर्म शान्ति देते हैं। अधिक वचन न दें।",
                "मंच दिखेगा। दल को जलाए बिना नेतृत्व करें।",
                "संपादन, व्यवस्था, सेवा। स्वास्थ्य को नियमितता प्रिय है।",
                "साझेदारी को न्यायपूर्ण अनुबन्ध चाहिये। सौन्दर्य विवरण में है।",
                "शोध करें, प्रतिक्रिया न दें। रहस्य ताले माँगते हैं, रिसाव नहीं।",
                "गुरु या लम्बा मार्ग खुलता है। नाटक से धर्म श्रेष्ठ।",
                "पहाड़ एक खाता-पंक्ति से चढ़ें। शनि ताली बजाता है।",
                "जाल बाँधें, सुधारें, दान दें। समूह-मन मित्र है।",
                "विश्राम, कल्पना, क्षमा। सागर नहीं भागता।"
        };
        return verbs[sign] + " आज तिथि " + tithi + ", चन्द्र " + moonR
                + " / " + nak + "। अभिजित श्रेष्ठ; राहुकाल छोड़ें।";
    }
}
