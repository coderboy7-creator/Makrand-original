package com.makaranda.calc.dasha;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.dasha.VimshottariDasha.Period;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Yoginī daśā — 8 yoginīs, 36-year cycle, from janma nakṣatra (Aśvinī = Maṅgalā).
 * Lords: Moon, Sun, Jupiter, Mars, Mercury, Saturn, Venus, Rahu. No bija retune.
 */
public final class YoginiDasha {
    public static final String[] NAMES = {
            "Mangala", "Pingala", "Dhanya", "Bhramari", "Bhadrika", "Ulka", "Siddha", "Sankata"
    };
    public static final String[] NAMES_HI = {
            "मंगला", "पिंगला", "धन्या", "भ्रामरी", "भद्रिका", "उल्का", "सिद्धा", "संकटा"
    };
    public static final String[] LORDS = {
            "Moon", "Sun", "Jupiter", "Mars", "Mercury", "Saturn", "Venus", "Rahu"
    };
    public static final int[] YEARS = {1, 2, 3, 4, 5, 6, 7, 8};
    public static final int CYCLE_YEARS = 36;

    private YoginiDasha() {}

    public static List<Period> compute(double moonSidereal, LocalDateTime birth, int depth) {
        int nak = VedicConstants.nakshatraIndex(moonSidereal);
        int startIdx = ((nak % 8) + 8) % 8;
        double elapsedInNak = AstroMath.norm360(moonSidereal) % AstroMath.NAKSHATRA_SPAN;
        double fractionElapsed = elapsedInNak / AstroMath.NAKSHATRA_SPAN;
        double mahaYears = YEARS[startIdx];
        double usedYears = mahaYears * fractionElapsed;
        LocalDateTime cursor = VimshottariDasha.plusYears(birth, -usedYears);
        List<Period> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int idx = (startIdx + i) % 8;
            double yrs = YEARS[idx];
            LocalDateTime end = VimshottariDasha.plusYears(cursor, yrs);
            List<Period> antars = depth >= 2 ? antars(idx, cursor, end) : List.of();
            list.add(new Period(NAMES[idx], cursor, end, yrs, antars));
            cursor = end;
        }
        return list;
    }

    private static List<Period> antars(int mahaIdx, LocalDateTime start, LocalDateTime end) {
        double mahaYears = YEARS[mahaIdx];
        List<Period> list = new ArrayList<>();
        LocalDateTime c = start;
        for (int i = 0; i < 8; i++) {
            int idx = (mahaIdx + i) % 8;
            double yrs = mahaYears * YEARS[idx] / (double) CYCLE_YEARS;
            LocalDateTime e = VimshottariDasha.plusYears(c, yrs);
            if (e.isAfter(end)) e = end;
            list.add(new Period(NAMES[idx], c, e, yrs, List.of()));
            c = e;
            if (!c.isBefore(end)) break;
        }
        return list;
    }

    public static Map<String, Object> bundle(double moonSidereal, LocalDateTime birth, LocalDateTime now) {
        List<Period> periods = compute(moonSidereal, birth, 2);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("system", "Yogini");
        out.put("cycleYears", CYCLE_YEARS);
        out.put("names", NAMES);
        out.put("namesHi", NAMES_HI);
        out.put("lords", LORDS);
        out.put("years", YEARS);
        int nak = VedicConstants.nakshatraIndex(moonSidereal);
        int startIdx = ((nak % 8) + 8) % 8;
        out.put("birthYogini", NAMES[startIdx]);
        out.put("birthYoginiHi", NAMES_HI[startIdx]);
        out.put("birthLord", LORDS[startIdx]);
        out.put("periods", periods);
        out.put("current", VimshottariDasha.currentAt(periods, now));
        return out;
    }

    public static String lordOf(String yogini) {
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equalsIgnoreCase(yogini)) return LORDS[i];
        }
        return yogini;
    }

    public static String nameHi(String yogini) {
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equalsIgnoreCase(yogini)) return NAMES_HI[i];
        }
        return yogini;
    }
}
