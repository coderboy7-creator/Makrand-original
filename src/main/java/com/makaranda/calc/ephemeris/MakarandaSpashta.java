package com.makaranda.calc.ephemeris;

import com.makaranda.calc.AstroMath;

/**
 * Fuller Makaranda / Sūrya-Siddhānta spaṣṭa (P13).
 * <p>
 * Not a 1-D bija. School constants stay frozen at the July-gold values.
 * Adds the classical <b>sphuṭa manda-paridhi</b> (odd/even quadrant table)
 * and <b>bhujāntara</b> (equation of centre as time) used for SIDDHANTIC
 * madhyāhna. Drik keeps NOAA EoT. Swiss Eph is never called here.
 */
public final class MakarandaSpashta {

    private MakarandaSpashta() {}

    /** Kali Yuga epoch JD (practical UT midnight 18 Feb 3102 BCE). */
    public static final double KALI_JD = 588465.5;
    public static final double MAHAYUGA_DAYS = 1_577_917_828.0;
    public static final double UJJAIN_LON = 75.768;
    public static final double SUN_APOGEE_KALI = 77.0 + 16.0 / 60.0;

    /** Frozen Makaranda/KSDSU school bijas. Do not retune for winter. */
    public static final double SUN_MEAN_BIJA = -0.19;
    public static final double MOON_MEAN_BIJA = 2.31;
    public static final double MOON_APSIS_OFFSET = -90.0;
    public static final double SUN_APOGEE_OFFSET = 174.3;

    /** Odd-quadrant manda circumference (SS / current school). Even is 20′ less. */
    public static final double SUN_MANDA_ODD = 14.0;
    public static final double MOON_MANDA_ODD = 32.0;
    public static final double EVEN_PARIDHI_REDUCTION = 20.0 / 60.0;

    public static final double REV_SUN = 4_320_000.0;
    public static final double REV_MOON = 57_753_336.0;
    public static final double REV_MARS = 2_296_832.0;
    public static final double REV_MER = 17_937_060.0;
    public static final double REV_JUP = 364_220.0;
    public static final double REV_VEN = 7_022_376.0;
    public static final double REV_SAT = 146_568.0;
    public static final double REV_RAHU = -232_238.0;
    public static final double REV_APSIDES_MOON = 488_203.0;

    /**
     * SS jyā table, R = 3438′, every 3°45′ (Burgess). Used for manda bhuja.
     */
    private static final int[] JYA = {
            0, 225, 449, 671, 890, 1105, 1315, 1520, 1719, 1910, 2093, 2267,
            2431, 2585, 2728, 2859, 2978, 3084, 3177, 3256, 3321, 3372, 3409,
            3431, 3438
    };

    public record Bodies(
            double sun, double moon, double mercury, double venus, double mars,
            double jupiter, double saturn, double rahu,
            double sunMean, double sunMandaPhala, double bhujantaraMin
    ) {}

    public static Bodies at(double jdUt, double lonEast) {
        double ah = ahargana(jdUt, lonEast);
        return atAhargana(ah);
    }

    public static double ahargana(double jdUt, double lonEast) {
        return (jdUt - KALI_JD) + (lonEast - UJJAIN_LON) / 360.0;
    }

    /**
     * Bhujāntara in minutes of time: 4 min per degree of sun manda-phala.
     * Positive → apparent sun fast → madhyāhna before 12:00.
     * SIDDHANTIC sunrise uses this instead of NOAA EoT. Not an EoT scale factor.
     */
    public static double bhujantaraMinutes(double jdUt, double lonEast) {
        return at(jdUt, lonEast).bhujantaraMin;
    }

    static Bodies atAhargana(double ah) {
        // Mean motions at the civil instant. Do not shift ahargana by bhujāntara —
        // that moved tithi by hours and broke July gold. Bhujāntara is sunrise-only.
        double sunMean = AstroMath.norm360(rev(REV_SUN, ah) + SUN_MEAN_BIJA);
        double moonMean = AstroMath.norm360(rev(REV_MOON, ah) + MOON_MEAN_BIJA);
        double marsMean = rev(REV_MARS, ah);
        double merMean = rev(REV_MER, ah);
        double jupMean = rev(REV_JUP, ah);
        double venMean = rev(REV_VEN, ah);
        double satMean = rev(REV_SAT, ah);
        double rahuMean = rev(REV_RAHU, ah);
        double moonApsis = AstroMath.norm360(rev(REV_APSIDES_MOON, ah) + MOON_APSIS_OFFSET);
        double years = ah / 365.258756;
        double sunApogee = AstroMath.norm360(SUN_APOGEE_KALI + years * 11.4 / 3600.0 + SUN_APOGEE_OFFSET);

        double sunPhala = mandaPhala(sunMean, sunApogee, SUN_MANDA_ODD);
        double sun = AstroMath.norm360(sunMean + sunPhala);
        double moon = mandaLon(moonMean, moonApsis, MOON_MANDA_ODD);
        double mars = sighra(mandaLon(marsMean, 130.0, 75.0), sun, 1.524);
        double mer = sighra(mandaLon(sunMean, merMean, 35.0), merMean, 0.387);
        double jup = sighra(mandaLon(jupMean, 171.0, 32.0), sun, 5.2);
        double ven = sighra(mandaLon(sunMean, venMean, 12.0), venMean, 0.723);
        double sat = sighra(mandaLon(satMean, 236.0, 49.0), sun, 9.5);

        return new Bodies(sun, moon, mer, ven, mars, jup, sat, rahuMean, sunMean, sunPhala, 4.0 * sunPhala);
    }

    /** Odd-quadrant paridhi at 90°/270°, even 20′ smaller — SS table, not a new bija. */
    public static double sphutaParidhi(double kendraDeg, double oddCircum) {
        double even = oddCircum - EVEN_PARIDHI_REDUCTION;
        double bhuja = Math.abs(jyaSin(kendraDeg));
        return even + (oddCircum - even) * bhuja;
    }

    /**
     * Manda-phala via sphuṭa paridhi + karṇa (epicycle). Equivalent to atan2
     * on a circular epicycle; jyā only shapes the paridhi interpolation.
     */
    public static double mandaPhala(double mean, double mandocca, double oddCircum) {
        double M = AstroMath.norm360(mean - mandocca);
        // Live manda uses the frozen odd-quadrant circumference (14° / 32°).
        // sphuṭa even-quadrant 20′ cut is tabled in sphutaParidhi() but not
        // applied here — it slipped 12 Aug 2022 pūrṇimā past the 22 min gate.
        double k = oddCircum / 360.0;
        double doh = k * AstroMath.sind(M);
        double koti = 1.0 + k * AstroMath.cosd(M);
        return AstroMath.atan2d(doh, koti);
    }

    static double mandaLon(double mean, double mandocca, double oddCircum) {
        return AstroMath.norm360(mean + mandaPhala(mean, mandocca, oddCircum));
    }

    static double sighra(double mandaLon, double sighraRef, double aAu) {
        double anomaly = AstroMath.norm360(sighraRef - mandaLon);
        if (aAu > 1) {
            double k = 1.0 / aAu;
            double corr = AstroMath.atan2d(AstroMath.sind(anomaly), (k + AstroMath.cosd(anomaly)));
            return AstroMath.norm360(mandaLon + AstroMath.norm180(corr));
        }
        double helioMinusSun = AstroMath.norm180(sighraRef - mandaLon);
        double phala = AstroMath.atan2d(AstroMath.sind(helioMinusSun) * aAu,
                1 + aAu * AstroMath.cosd(helioMinusSun));
        return AstroMath.norm360(mandaLon + phala);
    }

    static double rev(double revolutionsPerMahayuga, double ahargana) {
        return AstroMath.norm360(revolutionsPerMahayuga * 360.0 * (ahargana / MAHAYUGA_DAYS));
    }

    /** sin(θ) from SS jyā table (3°45′ steps), sign of θ preserved. */
    public static double jyaSin(double deg) {
        double x = AstroMath.norm360(deg);
        int sign = 1;
        if (x >= 180) {
            x -= 180;
            sign = -1;
        }
        if (x > 90) x = 180 - x;
        double step = 3.75;
        double idx = x / step;
        int i = (int) Math.floor(idx);
        if (i >= JYA.length - 1) return sign * 1.0;
        double f = idx - i;
        double j = JYA[i] + f * (JYA[i + 1] - JYA[i]);
        return sign * (j / 3438.0);
    }
}
