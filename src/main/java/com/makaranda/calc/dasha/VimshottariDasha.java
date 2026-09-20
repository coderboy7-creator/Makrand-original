package com.makaranda.calc.dasha;

import com.makaranda.calc.AstroMath;
import com.makaranda.calc.VedicConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private static int indexOf(String lord) {
        for (int i = 0; i < VedicConstants.VIMSHOTTARI_LORDS.length; i++) {
            if (VedicConstants.VIMSHOTTARI_LORDS[i].equalsIgnoreCase(lord)) return i;
        }
        return 0;
    }
}
