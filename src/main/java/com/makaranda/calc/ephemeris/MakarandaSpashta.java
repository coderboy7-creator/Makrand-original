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
    /**
     * SS {@code REV_RAHU} mean sits on DRIK/panji <b>Ketu</b> (2023–26 gold).
     * +180° makes {@code rahu} the ascending node. Chunk 3; not a bija.
     */
    public static final double RAHU_MEAN_OFFSET = 180.0;
    public static final double REV_APSIDES_MOON = 488_203.0;

    /**
     * SS / Makaranda Kali mandocca and odd-quadrant paridhi (Burgess).
     * Chunk 5: inferiors use these mandoccas (not śīghrocca as mandocca).
     * Śīghra k = paridhi/360, not AU. Sun/Moon bijas untouched.
     */
    public static final double MARS_MANDOCCA = 130.0;
    public static final double MER_MANDOCCA = 220.0 + 28.0 / 60.0;
    public static final double JUP_MANDOCCA = 171.0 + 18.0 / 60.0;
    public static final double VEN_MANDOCCA = 79.0 + 50.0 / 60.0;
    public static final double SAT_MANDOCCA = 236.0 + 37.0 / 60.0;

    public static final double MARS_MANDA_PARIDHI = 75.0;
    public static final double MER_MANDA_PARIDHI = 30.0;
    public static final double JUP_MANDA_PARIDHI = 32.0;
    public static final double VEN_MANDA_PARIDHI = 12.0;
    public static final double SAT_MANDA_PARIDHI = 49.0;

    public static final double MARS_SIGHRA_PARIDHI = 235.0;
    public static final double MER_SIGHRA_PARIDHI = 133.0;
    public static final double JUP_SIGHRA_PARIDHI = 72.0;
    public static final double VEN_SIGHRA_PARIDHI = 262.0;
    public static final double SAT_SIGHRA_PARIDHI = 39.0;

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
        double rahuMean = AstroMath.norm360(rev(REV_RAHU, ah) + RAHU_MEAN_OFFSET);
        double moonApsis = AstroMath.norm360(rev(REV_APSIDES_MOON, ah) + MOON_APSIS_OFFSET);
        double years = ah / 365.258756;
        double sunApogee = AstroMath.norm360(SUN_APOGEE_KALI + years * 11.4 / 3600.0 + SUN_APOGEE_OFFSET);

        double sunPhala = mandaPhala(sunMean, sunApogee, SUN_MANDA_ODD);
        double sun = AstroMath.norm360(sunMean + sunPhala);
        double moon = mandaLon(moonMean, moonApsis, MOON_MANDA_ODD);
        // Chunk 5: SS paridhi + correct mandocca. Inferiors: mean = Sūrya, śīghrocca = graha mean.
        double mars = superiorSpashta(marsMean, MARS_MANDOCCA, MARS_MANDA_PARIDHI, sun, MARS_SIGHRA_PARIDHI);
        double mer = inferiorSpashta(sunMean, MER_MANDOCCA, MER_MANDA_PARIDHI, merMean, MER_SIGHRA_PARIDHI);
        double jup = superiorSpashta(jupMean, JUP_MANDOCCA, JUP_MANDA_PARIDHI, sun, JUP_SIGHRA_PARIDHI);
        double ven = inferiorSpashta(sunMean, VEN_MANDOCCA, VEN_MANDA_PARIDHI, venMean, VEN_SIGHRA_PARIDHI);
        double sat = superiorSpashta(satMean, SAT_MANDOCCA, SAT_MANDA_PARIDHI, sun, SAT_SIGHRA_PARIDHI);

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

    /**
     * Superior graha: manda on own mean, then śīghra vs Sūrya spaṣṭa.
     * Paridhi/360, not AU. Half-equation iteration threw Śani ~180° on 6 Feb 2023 — not used.
     */
    static double superiorSpashta(double mean, double mandocca, double mandaParidhi,
                                  double sunSpashta, double sighraParidhi) {
        double manda = mandaLon(mean, mandocca, mandaParidhi);
        return AstroMath.norm360(manda + mandaPhala(sunSpashta, manda, sighraParidhi));
    }

    /**
     * Inferior: manda on Sūrya mean vs graha mandocca (not śīghrocca), then śīghra.
     */
    static double inferiorSpashta(double sunMean, double mandocca, double mandaParidhi,
                                  double sighrocca, double sighraParidhi) {
        double manda = mandaLon(sunMean, mandocca, mandaParidhi);
        return AstroMath.norm360(manda + mandaPhala(sighrocca, manda, sighraParidhi));
    }

    /**
     * Śīghra (annual) equation. Inferior: spaṣṭa = Sūrya + phala with k = a/AU.
     * Superior: spaṣṭa = manda + phala with k = 1/a. Same epicycle form as manda.
     * The old superior form {@code atan2(sin θ, k+cos θ)} made phala ≈ θ so
     * Mangal/Guru/Śani collapsed onto Sūrya (Chunk 2).
     */
    static double sighra(double mandaLon, double sighraRef, double aAu) {
        double anomaly = AstroMath.norm360(sighraRef - mandaLon);
        if (aAu > 1) {
            double k = 1.0 / aAu;
            double corr = AstroMath.atan2d(k * AstroMath.sind(anomaly),
                    1.0 + k * AstroMath.cosd(anomaly));
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
