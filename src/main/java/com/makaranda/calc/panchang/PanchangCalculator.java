package com.makaranda.calc.panchang;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.EphemerisEngine;
import com.makaranda.calc.ephemeris.EphemerisEngine.GeoPos;
import com.makaranda.calc.ephemeris.PanchangMode;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

/**
 * Makaranda / Surya Siddhanta panchang (Kameshwar Singh Darbhanga Sanskrit
 * Vishwavidyalaya paddhati). Limbs are evaluated at sunrise; each limb's
 * start/end is the actual spashta crossing (bisection), not a linear guess.
 * Clock times are 12-hour AM/PM.
 */
public final class PanchangCalculator {

    /**
     * KSDS / Makaranda printed place: अक्षांश २६।३५, देशान्तर १।३५ (from Ujjain),
     * पल्लभा ६. 1 ghati 35 pala east of Ujjain 75°46′ = 85°16′ E.
     * palabhā 6 ⇒ φ = arctan(6/12) ≈ 26°34′ (matches 26°35′).
     */
    public static final double KSDS_AKSHANSH_DEG = 26.0 + 35.0 / 60.0;
    public static final double KSDS_DESHANTAR_GHATI = 1.0 + 35.0 / 60.0;
    public static final double KSDS_UJJAIN_LON = 75.768;
    public static final double KSDS_LON = KSDS_UJJAIN_LON + KSDS_DESHANTAR_GHATI * 6.0;
    public static final double KSDS_PALABHA = 6.0;

    private static final DateTimeFormatter CLOCK =
            DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a", Locale.ENGLISH);
    private static final double UNIX_JD = 2440587.5;
    private final EphemerisEngine engine = new EphemerisEngine();

    public Map<String, Object> compute(LocalDate date, double lat, double lon, double tzHours,
                                       AyanamsaSystem aySys, PanchangMode mode) {
        if (aySys == null) aySys = AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA;
        if (mode == null) mode = PanchangMode.SIDDHANTIC;

        double jdNoon = AstroMath.julianDayUt(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                12, 0, 0, tzHours);
        double[] riseSet = engine.sunriseSunsetLocal(jdNoon, lat, lon, tzHours);
        String sunrise = formatLocalHours(riseSet[0]);
        String sunset = formatLocalHours(riseSet[1]);
        String noon = formatLocalHours(riseSet[2]);

        double jdSunrise = jdFromLocalClock(date, sunrise, tzHours);
        if (Double.isNaN(jdSunrise)) jdSunrise = jdNoon - 6.5 / 24.0;

        AyanamsaSystem ayF = aySys;
        PanchangMode mdF = mode;
        double lonF = lon;
        DoubleUnaryOperator elongAt = jd -> {
            double[] sm = sunMoon(jd, ayF, mdF, lonF);
            return AstroMath.norm360(sm[1] - sm[0]);
        };
        DoubleUnaryOperator moonAt = jd -> sunMoon(jd, ayF, mdF, lonF)[1];
        DoubleUnaryOperator yogaAt = jd -> {
            double[] sm = sunMoon(jd, ayF, mdF, lonF);
            return AstroMath.norm360(sm[0] + sm[1]);
        };

        double[] sm0 = sunMoon(jdSunrise, aySys, mode, lon);
        double sun = sm0[0], moon = sm0[1];
        double elong = AstroMath.norm360(moon - sun);
        double yogaVal = AstroMath.norm360(sun + moon);

        int tithiNum = (int) Math.floor(elong / 12.0) + 1;
        int nak = VedicConstants.nakshatraIndex(moon);
        int yogaIdx = (int) Math.floor(yogaVal / AstroMath.NAKSHATRA_SPAN);
        int karanaIdx = (int) Math.floor(elong / 6.0);

        Map<String, Object> tithi = limbSearch(elongAt, 12.0, tithiNum - 1, jdSunrise, tzHours);
        tithi.put("name", VedicConstants.tithiEn(tithiNum));
        tithi.put("nameHi", VedicConstants.tithiHi(tithiNum));
        tithi.put("paksha", VedicConstants.pakshaEn(tithiNum));
        tithi.put("pakshaHi", VedicConstants.pakshaHi(tithiNum));
        tithi.put("nextName", VedicConstants.tithiEn(tithiNum + 1));
        tithi.put("nextNameHi", VedicConstants.tithiHi(tithiNum + 1));

        Map<String, Object> naksh = limbSearch(moonAt, AstroMath.NAKSHATRA_SPAN, nak, jdSunrise, tzHours);
        naksh.put("name", VedicConstants.NAKSHATRAS[nak]);
        naksh.put("nameHi", VedicConstants.NAKSHATRAS_HI[nak]);
        naksh.put("pada", VedicConstants.pada(moon));
        naksh.put("lord", VedicConstants.NAK_LORDS[nak]);
        naksh.put("deity", VedicConstants.NAK_DEITIES[nak]);
        int nakNext = (nak + 1) % 27;
        naksh.put("nextName", VedicConstants.NAKSHATRAS[nakNext]);
        naksh.put("nextNameHi", VedicConstants.NAKSHATRAS_HI[nakNext]);

        Map<String, Object> yoga = limbSearch(yogaAt, AstroMath.NAKSHATRA_SPAN, yogaIdx, jdSunrise, tzHours);
        yoga.put("name", VedicConstants.YOGAS_PANCHANG[yogaIdx]);
        yoga.put("nameHi", VedicConstants.YOGAS_PANCHANG_HI[yogaIdx]);
        int yNext = (yogaIdx + 1) % 27;
        yoga.put("nextName", VedicConstants.YOGAS_PANCHANG[yNext]);
        yoga.put("nextNameHi", VedicConstants.YOGAS_PANCHANG_HI[yNext]);

        Map<String, Object> karana = limbSearch(elongAt, 6.0, karanaIdx, jdSunrise, tzHours);
        karana.put("name", VedicConstants.karanaEn(karanaIdx));
        karana.put("nameHi", VedicConstants.karanaHi(karanaIdx));
        karana.put("nextName", VedicConstants.karanaEn(karanaIdx + 1));
        karana.put("nextNameHi", VedicConstants.karanaHi(karanaIdx + 1));

        int w = date.getDayOfWeek().getValue() % 7;
        Map<String, Object> muhurta = inauspicious(sunrise, sunset, w);

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("date", date.toString());
        m.put("school", "Makaranda / Surya Siddhanta — Kameshwar Singh Darbhanga Sanskrit Vishwavidyalaya paddhati");
        m.put("placeLat", lat);
        m.put("placeLon", lon);
        m.put("ayanamsa", aySys.ayanamsa(jdSunrise));
        m.put("ayanamsaName", aySys.label);
        m.put("mode", mode.name());
        m.put("timeFormat", "12-hour");
        m.put("vara", VedicConstants.WEEKDAYS[w]);
        m.put("varaHi", VedicConstants.WEEKDAYS_HI[w]);
        m.put("tithi", tithi.get("name"));
        m.put("tithiHi", tithi.get("nameHi"));
        m.put("tithiNumber", tithiNum);
        m.put("paksha", tithi.get("paksha"));
        m.put("pakshaHi", tithi.get("pakshaHi"));
        m.put("tithiStart", tithi.get("start"));
        m.put("tithiEnd", tithi.get("end"));
        m.put("tithiNext", tithi.get("nextName"));
        m.put("tithiNextHi", tithi.get("nextNameHi"));
        m.put("tithiElapsedPct", tithi.get("elapsedPct"));
        m.put("tithiLimb", tithi);
        m.put("nakshatra", naksh.get("name"));
        m.put("nakshatraHi", naksh.get("nameHi"));
        m.put("nakshatraPada", naksh.get("pada"));
        m.put("nakshatraLord", naksh.get("lord"));
        m.put("nakshatraDeity", naksh.get("deity"));
        m.put("nakshatraStart", naksh.get("start"));
        m.put("nakshatraEnd", naksh.get("end"));
        m.put("nakshatraNext", naksh.get("nextName"));
        m.put("nakshatraNextHi", naksh.get("nextNameHi"));
        m.put("nakshatraElapsedPct", naksh.get("elapsedPct"));
        m.put("nakshatraLimb", naksh);
        m.put("yoga", yoga.get("name"));
        m.put("yogaHi", yoga.get("nameHi"));
        m.put("yogaStart", yoga.get("start"));
        m.put("yogaEnd", yoga.get("end"));
        m.put("yogaNext", yoga.get("nextName"));
        m.put("yogaNextHi", yoga.get("nextNameHi"));
        m.put("yogaElapsedPct", yoga.get("elapsedPct"));
        m.put("yogaLimb", yoga);
        m.put("karana", karana.get("name"));
        m.put("karanaHi", karana.get("nameHi"));
        m.put("karanaStart", karana.get("start"));
        m.put("karanaEnd", karana.get("end"));
        m.put("karanaNext", karana.get("nextName"));
        m.put("karanaNextHi", karana.get("nextNameHi"));
        m.put("karanaLimb", karana);
        m.put("sunSidereal", AstroMath.signDegree(sun));
        m.put("moonSidereal", AstroMath.signDegree(moon));
        m.put("moonRashi", VedicConstants.SIGNS_SA[AstroMath.signIndex(moon)]);
        m.put("moonRashiHi", VedicConstants.SIGNS_HI[AstroMath.signIndex(moon)]);
        m.put("sunrise", sunrise);
        m.put("sunset", sunset);
        m.put("solarNoon", noon);
        m.put("ritu", ritu(date.getMonthValue()));
        m.put("rituHi", rituHi(date.getMonthValue()));
        m.put("ayana", date.getMonthValue() >= 7 && date.getMonthValue() <= 12 ? "Dakshinayana" : "Uttarayana");
        m.put("ayanaHi", date.getMonthValue() >= 7 && date.getMonthValue() <= 12 ? "दक्षिणायन" : "उत्तरायण");
        m.put("samvatVikram", date.getYear() + 57);
        m.put("kaliYearApprox", date.getYear() + 3102);
        m.put("muhurta", muhurta);
        m.put("abhijit", abhijit(sunrise, sunset));
        m.put("brahmaMuhurta", brahma(sunrise));
        m.put("akshansh", "26।35");
        m.put("deshantar", "01।35");
        m.put("palabha", "06");
        m.put("ksdsLat", KSDS_AKSHANSH_DEG);
        m.put("ksdsLon", Math.round(KSDS_LON * 10000.0) / 10000.0);
        m.put("drikEngine", mode == PanchangMode.DRIK ? engine.drikBackend() : "siddhantic");
        m.put("makarandaNote", mode == PanchangMode.SIDDHANTIC
                ? "मकरन्द / सूर्य सिद्धान्त (मिथिला, KSDSU)। अक्षांश २६।३५, देशान्तर १।३५, पल्लभा ६। समय तक = घं.मि. (दि./सां./रा.) व घटिका सूर्योदय से।"
                : "Drik (apparent) ganita — Swiss Ephemeris when files are present, else Meeus. Not the default Makaranda panchang.");
        return m;
    }

    /**
     * Spashta sun/moon for panchang limbs.
     * SIDDHANTIC = Makaranda Surya-Siddhanta (KSDSU printed गणित).
     * DRIK = apparent Meeus tropical minus ayanamsa.
     */
    private double[] sunMoon(double jd, AyanamsaSystem aySys, PanchangMode mode, double lonEast) {
        double ay = aySys.ayanamsa(jd);
        if (mode == PanchangMode.SIDDHANTIC) {
            Map<String, GeoPos> g = engine.compute(jd, PanchangMode.SIDDHANTIC, lonEast);
            return new double[]{
                    AstroMath.norm360(g.get("Sun").lon() - ay),
                    AstroMath.norm360(g.get("Moon").lon() - ay)
            };
        }
        double[] trop = engine.tropicalSunMoon(jd);
        return new double[]{
                AstroMath.norm360(trop[0] - ay),
                AstroMath.norm360(trop[1] - ay)
        };
    }

    private Map<String, Object> limbSearch(DoubleUnaryOperator ang, double span, int index,
                                           double jdEpoch, double tz) {
        double v = AstroMath.norm360(ang.applyAsDouble(jdEpoch));
        double within = v % span;
        double startBound = AstroMath.norm360(v - within);
        double endBound = AstroMath.norm360(startBound + span);
        double jdStart = solveCross(ang, startBound, jdEpoch, false);
        double jdEnd = solveCross(ang, endBound, jdEpoch, true);
        LocalDateTime start = jdToLocal(jdStart, tz);
        LocalDateTime end = jdToLocal(jdEnd, tz);
        double daysToEnd = jdEnd - jdEpoch;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("index", index);
        m.put("start", formatPanjiInstant(start, jdStart, jdEpoch));
        m.put("end", formatPanjiInstant(end, jdEnd, jdEpoch));
        m.put("startClock", formatPanjiInstant(start, jdStart, jdEpoch));
        m.put("endClock", formatPanjiInstant(end, jdEnd, jdEpoch));
        m.put("startGhati", ghatiPala(jdStart, jdEpoch));
        m.put("endGhati", ghatiPala(jdEnd, jdEpoch));
        m.put("startIso", start.toString());
        m.put("endIso", end.toString());
        m.put("elapsedPct", Math.round((within / span) * 1000.0) / 10.0);
        m.put("remainingHours", Math.round(daysToEnd * 24 * 10.0) / 10.0);
        return m;
    }

    /**
     * KSDS printed columns: घं.मि. as दि./सां./रा. h।mm, and घ.प. from sunrise (0–60).
     * रा. = night / early morning (book uses रा. not प्रातः for 4:03 AM).
     */
    static String formatPanjiInstant(LocalDateTime t, double jdEvent, double jdSunrise) {
        int h = t.getHour();
        int m = t.getMinute();
        String watch = (h >= 6 && h < 16) ? "दि." : (h >= 16 && h < 22) ? "सां." : "रा.";
        int h12 = h % 12;
        if (h12 == 0) h12 = 12;
        return watch + " " + h12 + "।" + String.format(Locale.ENGLISH, "%02d", m)
                + "  " + CLOCK.format(t)
                + "  (" + ghatiPala(jdEvent, jdSunrise) + " घटी)";
    }

    static String ghatiPala(double jdEvent, double jdSunrise) {
        double hours = (jdEvent - jdSunrise) * 24.0;
        while (hours < 0) hours += 24;
        while (hours >= 24) hours -= 24;
        double minutes = hours * 60.0;
        int ghati = (int) Math.floor(minutes / 24.0);
        int pala = (int) Math.round((minutes - ghati * 24.0) * 2.5);
        if (pala >= 60) { ghati += pala / 60; pala = pala % 60; }
        if (pala < 0) pala = 0;
        ghati = ((ghati % 60) + 60) % 60;
        return String.format(Locale.ENGLISH, "%d।%02d", ghati, pala);
    }

    /** Find jd where increasing angle crosses {@code target} (0–360). */
    private double solveCross(DoubleUnaryOperator ang, double target, double jd0, boolean forward) {
        double need = AstroMath.norm360(target - ang.applyAsDouble(jd0));
        if (!forward) {
            need = AstroMath.norm360(ang.applyAsDouble(jd0) - target);
        }
        if (need < 1e-5) return jd0;
        double lo = jd0;
        double hi = jd0;
        double step = forward ? 0.04 : -0.04;
        for (int i = 0; i < 80; i++) {
            hi += step;
            double gone = forward
                    ? AstroMath.norm360(ang.applyAsDouble(hi) - ang.applyAsDouble(jd0))
                    : AstroMath.norm360(ang.applyAsDouble(jd0) - ang.applyAsDouble(hi));
            if (gone >= need) break;
        }
        if (!forward) {
            double tmp = lo;
            lo = hi;
            hi = tmp;
        }
        for (int i = 0; i < 48; i++) {
            double mid = (lo + hi) / 2.0;
            double gone = forward
                    ? AstroMath.norm360(ang.applyAsDouble(mid) - ang.applyAsDouble(jd0))
                    : AstroMath.norm360(ang.applyAsDouble(jd0) - ang.applyAsDouble(mid));
            if (forward) {
                if (gone >= need) hi = mid;
                else lo = mid;
            } else {
                if (gone >= need) lo = mid;
                else hi = mid;
            }
        }
        return forward ? hi : lo;
    }

    private static LocalDateTime jdToLocal(double jd, double tzHours) {
        long seconds = Math.round((jd - UNIX_JD) * 86400.0 + tzHours * 3600.0);
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneOffset.UTC);
    }

    private double jdFromLocalClock(LocalDate date, String clock, double tzHours) {
        if (clock == null || clock.contains("—")) return Double.NaN;
        int min = toMin(clock);
        int h = min / 60;
        int m = min % 60;
        return AstroMath.julianDayUt(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                h, m, 0, tzHours);
    }

    private static String ritu(int month) {
        return switch (month) {
            case 1, 2 -> "Shishira";
            case 3, 4 -> "Vasanta";
            case 5, 6 -> "Grishma";
            case 7, 8 -> "Varsha";
            case 9, 10 -> "Sharad";
            default -> "Hemanta";
        };
    }

    private static String rituHi(int month) {
        return switch (month) {
            case 1, 2 -> "\u0936\u093f\u0936\u093f\u0930";
            case 3, 4 -> "\u0935\u0938\u0928\u094d\u0924";
            case 5, 6 -> "\u0917\u094d\u0930\u0940\u0937\u094d\u092e";
            case 7, 8 -> "\u0935\u0930\u094d\u0937\u093e";
            case 9, 10 -> "\u0936\u0930\u0926\u094d";
            default -> "\u0939\u0947\u092e\u0928\u094d\u0924";
        };
    }

    static String formatLocalHours(double local) {
        if (Double.isNaN(local)) return "—";
        while (local < 0) local += 24;
        while (local >= 24) local -= 24;
        int h = (int) Math.floor(local);
        int m = (int) Math.round((local - h) * 60);
        if (m == 60) { h = (h + 1) % 24; m = 0; }
        return fromMin(h * 60 + m);
    }

    private Map<String, Object> inauspicious(String sunrise, String sunset, int weekday) {
        int[] rahuPart = {8, 2, 7, 5, 6, 4, 3};
        int[] yamaPart = {5, 4, 3, 2, 1, 7, 6};
        int[] gulikaPart = {7, 6, 5, 4, 3, 2, 1};
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("rahuKalam", eighth(sunrise, sunset, rahuPart[weekday]));
        m.put("yamaganda", eighth(sunrise, sunset, yamaPart[weekday]));
        m.put("gulika", eighth(sunrise, sunset, gulikaPart[weekday]));
        m.put("durmuhurta", List.of(eighth(sunrise, sunset, 2), eighth(sunrise, sunset, 6)));
        return m;
    }

    private static Map<String, String> eighth(String rise, String set, int part1to8) {
        int r = toMin(rise);
        int s = toMin(set);
        if (s <= r) s += 24 * 60;
        double span = (s - r) / 8.0;
        int start = (int) Math.round(r + (part1to8 - 1) * span);
        int end = (int) Math.round(r + part1to8 * span);
        Map<String, String> m = new LinkedHashMap<>();
        m.put("start", fromMin(start));
        m.put("end", fromMin(end));
        return m;
    }

    private static Map<String, String> abhijit(String rise, String set) {
        int r = toMin(rise);
        int s = toMin(set);
        if (s <= r) s += 24 * 60;
        int mid = (r + s) / 2;
        int muh = Math.max(24, (s - r) / 15);
        Map<String, String> m = new LinkedHashMap<>();
        m.put("start", fromMin(mid - muh / 2));
        m.put("end", fromMin(mid + muh / 2));
        return m;
    }

    private static Map<String, String> brahma(String rise) {
        int r = toMin(rise);
        Map<String, String> m = new LinkedHashMap<>();
        m.put("start", fromMin(r - 96));
        m.put("end", fromMin(r - 48));
        return m;
    }

    public static int toMin(String clock) {
        if (clock == null || clock.contains("—")) return 6 * 60;
        String s = clock.trim().toUpperCase(Locale.ENGLISH);
        boolean pm = s.contains("PM");
        boolean am = s.contains("AM");
        s = s.replace("AM", "").replace("PM", "").trim();
        String[] p = s.split(":");
        int h = Integer.parseInt(p[0].trim());
        int m = Integer.parseInt(p[1].trim().replaceAll("[^0-9].*", ""));
        if (pm && h != 12) h += 12;
        if (am && h == 12) h = 0;
        return h * 60 + m;
    }

    static String fromMin(int min) {
        min = ((min % (24 * 60)) + (24 * 60)) % (24 * 60);
        int h24 = min / 60;
        int m = min % 60;
        String ap = h24 < 12 ? "AM" : "PM";
        int h12 = h24 % 12;
        if (h12 == 0) h12 = 12;
        return String.format(Locale.ENGLISH, "%d:%02d %s", h12, m, ap);
    }

    public List<Map<String, Object>> month(LocalDate start, double lat, double lon, double tz,
                                           AyanamsaSystem ay, PanchangMode mode) {
        List<Map<String, Object>> days = new ArrayList<>();
        LocalDate d = start.withDayOfMonth(1);
        LocalDate end = d.plusMonths(1);
        while (d.isBefore(end)) {
            days.add(compute(d, lat, lon, tz, ay, mode));
            d = d.plusDays(1);
        }
        return days;
    }
}
