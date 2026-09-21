package com.makaranda.calc.dasha;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.VedicConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class VimshottariDasha {
    private VimshottariDasha() {}

    public record Period(String lord, LocalDateTime start, LocalDateTime end, double years,
                         List<Period> children) {}

    public static List<Period> compute(double moonSidereal, LocalDateTime birth, int depth) {
        int nak = VedicConstants.nakshatraIndex(moonSidereal);
        String birthLord = VedicConstants.NAK_LORDS[nak];
        int lordIdx = indexOf(birthLord);

        double elapsedInNak = AstroMath.norm360(moonSidereal) % AstroMath.NAKSHATRA_SPAN;
        double fractionElapsed = elapsedInNak / AstroMath.NAKSHATRA_SPAN;
        double mahaYears = VedicConstants.VIMSHOTTARI_YEARS[lordIdx];
        double balanceYears = mahaYears * (1.0 - fractionElapsed);

        List<Period> mahas = new ArrayList<>();
        LocalDateTime cursor = birth.minusSeconds(Math.round((mahaYears - balanceYears) * 365.2425 * 24 * 3600));
        // Start of current maha
        LocalDateTime mahaStart = birth.minusNanos(0);
        // Better: current maha started (mahaYears - balance) before birth
        double usedYears = mahaYears - balanceYears;
        mahaStart = plusYears(birth, -usedYears);

        cursor = mahaStart;
        for (int i = 0; i < 9; i++) {
            int idx = (lordIdx + i) % 9;
            String lord = VedicConstants.VIMSHOTTARI_LORDS[idx];
            double yrs = VedicConstants.VIMSHOTTARI_YEARS[idx];
            LocalDateTime end = plusYears(cursor, yrs);
            List<Period> antars = depth >= 2 ? antardashas(lord, cursor, end, depth) : List.of();
            mahas.add(new Period(lord, cursor, end, yrs, antars));
            cursor = end;
        }
        return mahas;
    }

    private static List<Period> antardashas(String mahaLord, LocalDateTime start, LocalDateTime end, int depth) {
        int mIdx = indexOf(mahaLord);
        double mahaYears = VedicConstants.VIMSHOTTARI_YEARS[mIdx];
        List<Period> list = new ArrayList<>();
        LocalDateTime c = start;
        for (int i = 0; i < 9; i++) {
            int idx = (mIdx + i) % 9;
            String lord = VedicConstants.VIMSHOTTARI_LORDS[idx];
            double antarYears = mahaYears * VedicConstants.VIMSHOTTARI_YEARS[idx] / 120.0;
            LocalDateTime e = plusYears(c, antarYears);
            if (e.isAfter(end)) e = end;
            List<Period> pratyantar = depth >= 3 ? pratyantar(mahaLord, lord, c, e) : List.of();
            list.add(new Period(lord, c, e, antarYears, pratyantar));
            c = e;
            if (!c.isBefore(end)) break;
        }
        return list;
    }

    private static List<Period> pratyantar(String maha, String antar, LocalDateTime start, LocalDateTime end) {
        int aIdx = indexOf(antar);
        int mIdx = indexOf(maha);
        double antarYears = VedicConstants.VIMSHOTTARI_YEARS[mIdx] * VedicConstants.VIMSHOTTARI_YEARS[aIdx] / 120.0;
        List<Period> list = new ArrayList<>();
        LocalDateTime c = start;
        for (int i = 0; i < 9; i++) {
            int idx = (aIdx + i) % 9;
            String lord = VedicConstants.VIMSHOTTARI_LORDS[idx];
            double yrs = antarYears * VedicConstants.VIMSHOTTARI_YEARS[idx] / 120.0;
            LocalDateTime e = plusYears(c, yrs);
            if (e.isAfter(end)) e = end;
            list.add(new Period(lord, c, e, yrs, List.of()));
            c = e;
            if (!c.isBefore(end)) break;
        }
        return list;
    }

    public static LocalDateTime plusYears(LocalDateTime t, double years) {
        long seconds = Math.round(years * 365.2425 * 24 * 3600);
        return t.plusSeconds(seconds);
    }

    /** Inclusive start, exclusive end; last period includes its end instant. */
    public static boolean contains(Period p, LocalDateTime t, boolean last) {
        if (t == null || p == null) return false;
        if (t.isBefore(p.start())) return false;
        if (last) return !t.isAfter(p.end());
        return t.isBefore(p.end());
    }

    public static Map<String, Object> currentAt(List<Period> mahas, LocalDateTime now) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (mahas == null || mahas.isEmpty() || now == null) return m;
        for (int i = 0; i < mahas.size(); i++) {
            Period maha = mahas.get(i);
            boolean lastM = i == mahas.size() - 1;
            if (!contains(maha, now, lastM)) continue;
            m.put("mahadasha", maha.lord());
            m.put("mahaStart", maha.start());
            m.put("mahaEnd", maha.end());
            m.put("mahaYears", maha.years());
            List<Period> antars = maha.children() == null ? List.of() : maha.children();
            for (int j = 0; j < antars.size(); j++) {
                Period antar = antars.get(j);
                boolean lastA = j == antars.size() - 1;
                if (!contains(antar, now, lastA)) continue;
                m.put("antardasha", antar.lord());
                m.put("antarStart", antar.start());
                m.put("antarEnd", antar.end());
                m.put("antarYears", antar.years());
                List<Period> prat = antar.children() == null ? List.of() : antar.children();
                for (int k = 0; k < prat.size(); k++) {
                    Period pr = prat.get(k);
                    boolean lastP = k == prat.size() - 1;
                    if (!contains(pr, now, lastP)) continue;
                    m.put("pratyantardasha", pr.lord());
                    m.put("pratyantarStart", pr.start());
                    m.put("pratyantarEnd", pr.end());
                    m.put("pratyantarYears", pr.years());
                    break;
                }
                break;
            }
            break;
        }
        return m;
    }

    private static int indexOf(String lord) {
        for (int i = 0; i < VedicConstants.VIMSHOTTARI_LORDS.length; i++) {
            if (VedicConstants.VIMSHOTTARI_LORDS[i].equalsIgnoreCase(lord)) return i;
        }
        return 0;
    }
}
