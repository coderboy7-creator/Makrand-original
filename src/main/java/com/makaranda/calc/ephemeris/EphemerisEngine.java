package com.makaranda.calc.ephemeris;

import com.makaranda.calc.AstroMath;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dual-mode planetary engine.
 * <ul>
 *   <li>DRIK — JPL Keplerian elements (1800–2050) + Meeus lunar theory</li>
 *   <li>SIDDHANTIC — Surya Siddhanta mean motions from Kali Yuga with manda/sighra</li>
 * </ul>
 */
public final class EphemerisEngine {

    public record GeoPos(double lon, double lat, double distanceAu, boolean retrograde) {}

    public Map<String, GeoPos> compute(double jdUt, PanchangMode mode) {
        return compute(jdUt, mode, 85.8918);
    }

    public Map<String, GeoPos> compute(double jdUt, PanchangMode mode, double lonEast) {
        if (mode == PanchangMode.SIDDHANTIC) {
            return siddhantic(jdUt, lonEast);
        }
        return drik(jdUt);
    }

    public double obliquity(double jdUt) {
        double t = AstroMath.centuriesJ2000(jdUt);
        return 23.439291 - 0.0130042 * t - 0.00000016 * t * t;
    }

    public double gmstDegrees(double jdUt) {
        double t = AstroMath.centuriesJ2000(jdUt);
        double gmst = 280.46061837
                + 360.98564736629 * (jdUt - AstroMath.J2000)
                + 0.000387933 * t * t
                - t * t * t / 38710000.0;
        return AstroMath.norm360(gmst);
    }

    public double localSidereal(double jdUt, double longitudeEast) {
        return AstroMath.norm360(gmstDegrees(jdUt) + longitudeEast);
    }

    /**
     * Tropical ascendant (ecliptic longitude).
     */
    public double tropicalAscendant(double jdUt, double latDeg, double lonEast) {
        double eps = obliquity(jdUt);
        double ramc = localSidereal(jdUt, lonEast) * AstroMath.DEG;
        double e = eps * AstroMath.DEG;
        double phi = latDeg * AstroMath.DEG;
        double y = Math.cos(ramc);
        double x = -(Math.sin(ramc) * Math.cos(e) + Math.tan(phi) * Math.sin(e));
        return AstroMath.norm360(Math.toDegrees(Math.atan2(y, x)));
    }

    public double tropicalMc(double jdUt, double lonEast) {
        double eps = obliquity(jdUt);
        double ramc = localSidereal(jdUt, lonEast);
        double y = AstroMath.sind(ramc);
        double x = AstroMath.cosd(ramc) * AstroMath.cosd(eps);
        return AstroMath.atan2d(y, x);
    }

    /**
     * Sunrise / sunset as <b>local clock hours</b> (IST for India).
     * Mithila printed panchang (Makaranda / KSDS) places madhyāhna at 12:00
     * civil time and only varies dinamaan from solar declination — so we do
     * the same, otherwise Darbhanga's 85.9°E shifts noon ~14 minutes off the
     * panji.
     *
     * @return [riseHours, setHours, noonHours] in local time 0–24
     */
    public double[] sunriseSunset(double jdUtDate0, double lat, double lonEast) {
        return sunriseSunsetLocal(jdUtDate0, lat, lonEast, 5.5);
    }

    public double[] sunriseSunsetLocal(double jdUtDate0, double lat, double lonEast, double tzHours) {
        double jd = jdUtDate0;
        if (jd < 2000000) jd = AstroMath.J2000;
        double dec = solarDeclination(jd);
        double latR = lat * AstroMath.DEG;
        double decR = dec * AstroMath.DEG;
        // KSDS printed dinamaan: 50′ (34′ refraction + 16′ SD) and apparent noon
        // (equation of time). 29 Jul 2022 Darbhanga book सू.उ. ५।२० सू.अ. ६।५०.
        double cosH = (AstroMath.sind(-0.8333) - Math.sin(latR) * Math.sin(decR))
                / (Math.cos(latR) * Math.cos(decR));
        if (cosH > 1 || cosH < -1) return new double[]{Double.NaN, Double.NaN, 12.0};
        double Hhours = Math.toDegrees(Math.acos(AstroMath.clamp(cosH, -1, 1))) / 15.0;
        double noon = 12.0 - equationOfTimeMinutes(jd) / 60.0;
        return new double[]{noon - Hhours, noon + Hhours, noon};
    }

    /**
     * Apparent geocentric tropical longitudes of Sun and Moon (Meeus).
     * KSDS / Makaranda printed <i>spashta</i> tithi, karana, nakshatra and yoga
     * follow this drigganita elongation, not mean Surya-Siddhanta graha —
     * the 2016–17 विश्वविद्यालय पञ्चांग tithi ends match it for any date.
     * Siddhantic mean+manda remains available via {@link #compute} for kundali.
     */
    public double[] tropicalSunMoon(double jdUt) {
        double jdTt = jdUt + deltaTSeconds(jdUt) / 86400.0;
        return new double[]{tropicalSun(jdTt), moon(jdTt).lon()};
    }

    /** Espenak/Meeus ΔT (seconds, TT−UT) for 2005–2050; usable nearby. */
    static double deltaTSeconds(double jdUt) {
        double y = 2000.0 + (jdUt - AstroMath.J2000) / 365.2425;
        double u = y - 2000.0;
        return 62.92 + 0.32217 * u + 0.005589 * u * u;
    }

    /** Apparent tropical solar longitude (NOAA / Meeus). */
    public double tropicalSun(double jdUt) {
        double t = AstroMath.centuriesJ2000(jdUt);
        double L0 = AstroMath.norm360(280.46646 + 36000.76983 * t + 0.0003032 * t * t);
        double M = AstroMath.norm360(357.52911 + 35999.05029 * t - 0.0001537 * t * t);
        double C = (1.914602 - 0.004817 * t) * AstroMath.sind(M)
                + (0.019993 - 0.000101 * t) * AstroMath.sind(2 * M)
                + 0.000289 * AstroMath.sind(3 * M);
        double trueL = L0 + C;
        double omega = 125.04 - 1934.136 * t;
        return AstroMath.norm360(trueL - 0.00569 - 0.00478 * AstroMath.sind(omega));
    }

    /** Apparent solar declination (degrees) — NOAA / Meeus. */
    public double solarDeclination(double jdUt) {
        double t = AstroMath.centuriesJ2000(jdUt);
        double L0 = AstroMath.norm360(280.46646 + 36000.76983 * t + 0.0003032 * t * t);
        double M = AstroMath.norm360(357.52911 + 35999.05029 * t - 0.0001537 * t * t);
        double C = (1.914602 - 0.004817 * t) * AstroMath.sind(M)
                + (0.019993 - 0.000101 * t) * AstroMath.sind(2 * M)
                + 0.000289 * AstroMath.sind(3 * M);
        double trueL = L0 + C;
        double omega = 125.04 - 1934.136 * t;
        double lambda = trueL - 0.00569 - 0.00478 * AstroMath.sind(omega);
        return AstroMath.asind(AstroMath.sind(obliquity(jdUt)) * AstroMath.sind(lambda));
    }

    public double sunDeclination(double jdUt, double sunLonTrop) {
        double eps = obliquity(jdUt);
        return AstroMath.asind(AstroMath.sind(eps) * AstroMath.sind(sunLonTrop));
    }

    private double equationOfTimeMinutes(double jdUt) {
        double t = AstroMath.centuriesJ2000(jdUt);
        double L0 = AstroMath.norm360(280.46646 + 36000.76983 * t);
        double M = AstroMath.norm360(357.52911 + 35999.05029 * t);
        double e = 0.016708634 - 0.000042037 * t;
        double y = Math.pow(AstroMath.tand(obliquity(jdUt) / 2), 2);
        double E = y * AstroMath.sind(2 * L0)
                - 2 * e * AstroMath.sind(M)
                + 4 * e * y * AstroMath.sind(M) * AstroMath.cosd(2 * L0)
                - 0.5 * y * y * AstroMath.sind(4 * L0)
                - 1.25 * e * e * AstroMath.sind(2 * M);
        return 4 * Math.toDegrees(E); // minutes of time
    }

    /* ======================== DRIK (JPL + Meeus) ======================== */

    private Map<String, GeoPos> drik(double jdUt) {
        double t = AstroMath.centuriesJ2000(jdUt);
        double[] earth = helio(t, EARTH);
        Map<String, GeoPos> out = new LinkedHashMap<>();
        out.put("Sun", geoFromHelio(new double[]{0, 0, 0}, earth, false));
        out.put("Moon", moon(jdUt));
        out.put("Mercury", geoFromHelio(helio(t, MERCURY), earth, true));
        out.put("Venus", geoFromHelio(helio(t, VENUS), earth, true));
        out.put("Mars", geoFromHelio(helio(t, MARS), earth, true));
        out.put("Jupiter", geoFromHelio(helio(t, JUPITER), earth, true));
        out.put("Saturn", geoFromHelio(helio(t, SATURN), earth, true));
        GeoPos node = meanNode(jdUt);
        out.put("Rahu", node);
        out.put("Ketu", new GeoPos(AstroMath.norm360(node.lon + 180.0), -node.lat, node.distanceAu, false));
        // retrograde detection via finite difference
        Map<String, GeoPos> later = drikNoMoon(jdUt + 0.5);
        for (String p : new String[]{"Mercury", "Venus", "Mars", "Jupiter", "Saturn"}) {
            GeoPos a = out.get(p);
            GeoPos b = later.get(p);
            double dlon = AstroMath.norm180(b.lon - a.lon);
            out.put(p, new GeoPos(a.lon, a.lat, a.distanceAu, dlon < 0));
        }
        return out;
    }

    private Map<String, GeoPos> drikNoMoon(double jdUt) {
        double t = AstroMath.centuriesJ2000(jdUt);
        double[] earth = helio(t, EARTH);
        Map<String, GeoPos> out = new LinkedHashMap<>();
        out.put("Mercury", geoFromHelio(helio(t, MERCURY), earth, true));
        out.put("Venus", geoFromHelio(helio(t, VENUS), earth, true));
        out.put("Mars", geoFromHelio(helio(t, MARS), earth, true));
        out.put("Jupiter", geoFromHelio(helio(t, JUPITER), earth, true));
        out.put("Saturn", geoFromHelio(helio(t, SATURN), earth, true));
        return out;
    }

    private GeoPos geoFromHelio(double[] p, double[] e, boolean planet) {
        double x = p[0] - e[0];
        double y = p[1] - e[1];
        double z = p[2] - e[2];
        double dist = Math.sqrt(x * x + y * y + z * z);
        double lon = AstroMath.atan2d(y, x);
        double lat = AstroMath.asind(z / dist);
        return new GeoPos(lon, lat, dist, false);
    }

    /**
     * Keplerian elements: a, adot, e, edot, I, Idot, L, Ldot, varpi, varpidot, Omega, Omegadot
     * plus extra b,c,s,f (f in degrees / century)
     */
    private static final double[] MERCURY = {
            0.38709927, 0.00000037, 0.20563593, 0.00001906, 7.00497902, -0.00594749,
            252.25032350, 149472.67411175, 77.45779628, 0.16047689, 48.33076593, -0.12534081,
            0, 0, 0, 0
    };
    private static final double[] VENUS = {
            0.72333566, 0.00000390, 0.00677672, -0.00004107, 3.39467605, -0.00078890,
            181.97909950, 58517.81538729, 131.60246718, 0.00268329, 76.67984255, -0.27769418,
            0, 0, 0, 0
    };
    private static final double[] EARTH = {
            1.00000261, 0.00000562, 0.01671123, -0.00004392, -0.00001531, -0.01294668,
            100.46457166, 35999.37244981, 102.93768193, 0.32327364, 0.0, 0.0,
            0, 0, 0, 0
    };
    private static final double[] MARS = {
            1.52371034, 0.00001847, 0.09339410, 0.00007882, 1.84954124, -0.00813131,
            -4.55343205, 19140.30268499, -23.94362959, 0.44441088, 49.55953891, -0.29257343,
            0, 0, 0, 0
    };
    private static final double[] JUPITER = {
            5.20288700, -0.00011607, 0.04838624, -0.00013253, 1.30439695, -0.00183714,
            34.39644051, 3034.74612775, 14.72857493, 0.21252668, 100.47390909, 0.20469106,
            -0.00012452, 0.06064060, -0.35635438, 38.35125000
    };
    private static final double[] SATURN = {
            9.53667594, -0.00125060, 0.05386179, -0.00050991, 2.48599187, 0.00193609,
            49.95424423, 1222.49362201, 92.59887831, -0.41897216, 113.66242448, -0.28867794,
            0.00025899, -0.13434469, 0.87320147, 38.35125000
    };

    /** Returns heliocentric ecliptic rectangular coordinates (AU). */
    private double[] helio(double t, double[] el) {
        double a = el[0] + el[1] * t;
        double e = el[2] + el[3] * t;
        double I = el[4] + el[5] * t;
        double L = AstroMath.norm360(el[6] + el[7] * t);
        double varpi = el[8] + el[9] * t;
        double Omega = el[10] + el[11] * t;
        double M = AstroMath.norm360(L - varpi);
        if (el[15] != 0) {
            M = M + el[12] * t * t + el[13] * AstroMath.cosd(el[15] * t) + el[14] * AstroMath.sind(el[15] * t);
        }
        double E = AstroMath.keplerE(M, e);
        double xv = a * (Math.cos(E) - e);
        double yv = a * Math.sqrt(1 - e * e) * Math.sin(E);
        double v = Math.atan2(yv, xv);
        double r = Math.sqrt(xv * xv + yv * yv);
        double w = varpi - Omega; // argument of perihelion
        double xh = r * (AstroMath.cosd(Omega) * Math.cos(v + w * AstroMath.DEG)
                - AstroMath.sind(Omega) * Math.sin(v + w * AstroMath.DEG) * AstroMath.cosd(I));
        double yh = r * (AstroMath.sind(Omega) * Math.cos(v + w * AstroMath.DEG)
                + AstroMath.cosd(Omega) * Math.sin(v + w * AstroMath.DEG) * AstroMath.cosd(I));
        double zh = r * Math.sin(v + w * AstroMath.DEG) * AstroMath.sind(I);
        return new double[]{xh, yh, zh};
    }

    private GeoPos moon(double jdUt) {
        double t = AstroMath.centuriesJ2000(jdUt);
        double Lp = AstroMath.norm360(218.3164477 + 481267.88123421 * t - 0.0015786 * t * t + t * t * t / 538841.0);
        double D = AstroMath.norm360(297.8501921 + 445267.1114034 * t - 0.0018819 * t * t + t * t * t / 545868.0);
        double M = AstroMath.norm360(357.5291092 + 35999.0502909 * t - 0.0001536 * t * t);
        double Mp = AstroMath.norm360(134.9633964 + 477198.8675055 * t + 0.0087414 * t * t + t * t * t / 69699.0);
        double F = AstroMath.norm360(93.2720950 + 483202.0175233 * t - 0.0036539 * t * t);
        double E = 1 - 0.002516 * t - 0.0000074 * t * t;

        // Largest ELP/Meeus longitude terms (coefficient in 0.000001 deg)
        double lon = 0;
        lon += term(6288774, D, M, Mp, F, 0, 0, 1, 0, E);
        lon += term(1274027, D, M, Mp, F, 2, 0, -1, 0, E);
        lon += term(658314, D, M, Mp, F, 2, 0, 0, 0, E);
        lon += term(213618, D, M, Mp, F, 0, 0, 2, 0, E);
        lon += term(-185116, D, M, Mp, F, 0, 1, 0, 0, E);
        lon += term(-114332, D, M, Mp, F, 0, 0, 0, 2, E);
        lon += term(58793, D, M, Mp, F, 2, 0, -2, 0, E);
        lon += term(57066, D, M, Mp, F, 2, -1, -1, 0, E);
        lon += term(53322, D, M, Mp, F, 2, 0, 1, 0, E);
        lon += term(45758, D, M, Mp, F, 2, -1, 0, 0, E);
        lon += term(-40923, D, M, Mp, F, 0, 1, -1, 0, E);
        lon += term(-34720, D, M, Mp, F, 1, 0, 0, 0, E);
        lon += term(-30383, D, M, Mp, F, 0, 1, 1, 0, E);
        lon += term(15327, D, M, Mp, F, 2, 0, 0, -2, E);
        lon += term(-12528, D, M, Mp, F, 0, 0, 1, 2, E);
        lon += term(10980, D, M, Mp, F, 0, 0, 1, -2, E);
        lon += term(10675, D, M, Mp, F, 4, 0, -1, 0, E);
        lon += term(10034, D, M, Mp, F, 0, 0, 3, 0, E);
        lon += term(8548, D, M, Mp, F, 4, 0, -2, 0, E);
        lon += term(-7888, D, M, Mp, F, 2, 1, -1, 0, E);
        lon += term(-6766, D, M, Mp, F, 2, 1, 0, 0, E);
        lon += term(-5163, D, M, Mp, F, 1, 0, -1, 0, E);
        lon += term(4987, D, M, Mp, F, 1, 1, 0, 0, E);
        lon += term(4036, D, M, Mp, F, 2, -1, 1, 0, E);
        lon += term(3994, D, M, Mp, F, 2, 0, 2, 0, E);
        lon += term(3861, D, M, Mp, F, 4, 0, 0, 0, E);
        lon += term(3665, D, M, Mp, F, 2, 0, -3, 0, E);
        lon += term(-2689, D, M, Mp, F, 0, 1, -2, 0, E);
        lon += term(-2389, D, M, Mp, F, 2, -1, -2, 0, E);
        lon += term(2234, D, M, Mp, F, 2, -2, 0, 0, E);
        lon += term(-2120, D, M, Mp, F, 2, -2, -1, 0, E);
        lon += term(-2069, D, M, Mp, F, 2, 0, 1, -2, E);
        lon += term(2048, D, M, Mp, F, 2, -2, -1, 0, E);
        lon += term(-1773, D, M, Mp, F, 2, 0, 0, 2, E);
        lon += term(-1595, D, M, Mp, F, 4, 0, -1, 0, E);
        lon += term(1215, D, M, Mp, F, 0, 0, 2, 2, E);
        lon += term(-1110, D, M, Mp, F, 3, 0, -1, 0, E);
        lon += term(-892, D, M, Mp, F, 2, 1, 1, 0, E);
        lon += term(-810, D, M, Mp, F, 2, 2, -1, 0, E);
        lon += term(759, D, M, Mp, F, 2, 1, -2, 0, E);
        lon += term(-713, D, M, Mp, F, 2, -1, 0, 2, E);
        lon += term(-700, D, M, Mp, F, 0, 1, 2, 0, E);
        lon += term(691, D, M, Mp, F, 4, 0, 1, 0, E);
        double A1 = AstroMath.norm360(119.75 + 131.849 * t);
        double A2 = AstroMath.norm360(53.09 + 479264.290 * t);
        lon += 3958 * AstroMath.sind(A1);
        lon += 1962 * AstroMath.sind(Lp - F);
        lon += 318 * AstroMath.sind(A2);

        double lat = 0;
        lat += term(5128122, D, M, Mp, F, 0, 0, 0, 1, E);
        lat += term(280602, D, M, Mp, F, 0, 0, 1, 1, E);
        lat += term(277693, D, M, Mp, F, 0, 0, 1, -1, E);
        lat += term(173237, D, M, Mp, F, 2, 0, 0, -1, E);
        lat += term(55413, D, M, Mp, F, 2, 0, -1, 1, E);
        lat += term(46271, D, M, Mp, F, 2, 0, -1, -1, E);
        lat += term(32573, D, M, Mp, F, 2, 0, 0, 1, E);
        lat += term(17198, D, M, Mp, F, 0, 0, 2, 1, E);
        lat += term(9266, D, M, Mp, F, 2, 0, 1, -1, E);
        lat += term(8822, D, M, Mp, F, 0, 0, 2, -1, E);

        double longitude = AstroMath.norm360(Lp + lon / 1_000_000.0);
        double latitude = lat / 1_000_000.0;
        return new GeoPos(longitude, latitude, 0.00257, false);
    }

    private static double term(int coef, double D, double M, double Mp, double F,
                               int d, int m, int mp, int f, double E) {
        double ang = d * D + m * M + mp * Mp + f * F;
        double c = coef;
        if (Math.abs(m) == 1) c *= E;
        if (Math.abs(m) == 2) c *= E * E;
        return c * AstroMath.sind(ang);
    }

    private GeoPos meanNode(double jdUt) {
        double t = AstroMath.centuriesJ2000(jdUt);
        double omega = 125.0445479 - 1934.1362891 * t + 0.0020754 * t * t;
        return new GeoPos(AstroMath.norm360(omega), 0, 0.00257, true);
    }

    /* ======================== SURYA SIDDHANTA / MAKARANDA ======================== */

    /**
     * Kali Yuga epoch: 18 February 3102 BCE (Julian) = JD 588465.5 (Ujjain mean noon historically;
     * we use 3102-02-18 00:00 UT as a practical epoch, then apply mean motions).
     */
    private static final double KALI_JD = 588465.5;
    private static final double MAHAYUGA_DAYS = 1_577_917_828.0;
    private static final double UJJAIN_LON = 75.768;
    private static final double SUN_APOGEE_KALI = 77.0 + 16.0 / 60.0;
    /** Makaranda/KSDSU school bijas on SS mean elements (not per-date). */
    private static final double MAKARANDA_SUN_MEAN_BIJA = -0.19;
    private static final double MAKARANDA_MOON_MEAN_BIJA = 2.31;
    private static final double MAKARANDA_MOON_APSIS_OFFSET = -90.0;
    private static final double MAKARANDA_SUN_APOGEE_OFFSET = 174.3;

    // Revolutions per Mahayuga (Surya Siddhanta)
    private static final double REV_SUN = 4_320_000.0;
    private static final double REV_MOON = 57_753_336.0;
    private static final double REV_MARS = 2_296_832.0;
    private static final double REV_MER = 17_937_060.0;
    private static final double REV_JUP = 364_220.0;
    private static final double REV_VEN = 7_022_376.0;
    private static final double REV_SAT = 146_568.0;
    private static final double REV_RAHU = -232_238.0;
    private static final double REV_APSIDES_MOON = 488_203.0;

    private Map<String, GeoPos> siddhantic(double jdUt, double lonEast) {
        double ahargana = (jdUt - KALI_JD) + (lonEast - UJJAIN_LON) / 360.0;
        double sunMean = AstroMath.norm360(rev(REV_SUN, ahargana) + MAKARANDA_SUN_MEAN_BIJA);
        double moonMean = AstroMath.norm360(rev(REV_MOON, ahargana) + MAKARANDA_MOON_MEAN_BIJA);
        double marsMean = rev(REV_MARS, ahargana);
        double merMean = rev(REV_MER, ahargana);
        double jupMean = rev(REV_JUP, ahargana);
        double venMean = rev(REV_VEN, ahargana);
        double satMean = rev(REV_SAT, ahargana);
        double rahuMean = rev(REV_RAHU, ahargana);
        double moonApsis = AstroMath.norm360(rev(REV_APSIDES_MOON, ahargana) + MAKARANDA_MOON_APSIS_OFFSET);
        double years = ahargana / 365.258756;
        double sunApogee = AstroMath.norm360(SUN_APOGEE_KALI + years * 11.4 / 3600.0
                + MAKARANDA_SUN_APOGEE_OFFSET);

        double sun = mandaEpicycle(sunMean, sunApogee, 14.0);
        double moon = mandaEpicycle(moonMean, moonApsis, 32.0);
        double mars = sighra(mandaEpicycle(marsMean, 130.0, 75.0), sun, 1.524);
        double mer = sighra(mandaEpicycle(sunMean, merMean, 35.0), merMean, 0.387);
        double jup = sighra(mandaEpicycle(jupMean, 171.0, 32.0), sun, 5.2);
        double ven = sighra(mandaEpicycle(sunMean, venMean, 12.0), venMean, 0.723);
        double sat = sighra(mandaEpicycle(satMean, 236.0, 49.0), sun, 9.5);

        // Convert SS sidereal longitudes to tropical by adding ayanamsa so downstream
        // subtraction of ayanamsa yields SS sidereal. We return "tropical-equivalent"
        // = sidereal + ayanamsa(Makaranda).
        double ay = AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA.ayanamsa(jdUt);

        Map<String, GeoPos> out = new LinkedHashMap<>();
        out.put("Sun", trop(sun, ay));
        out.put("Moon", trop(moon, ay));
        out.put("Mercury", trop(mer, ay));
        out.put("Venus", trop(ven, ay));
        out.put("Mars", trop(mars, ay));
        out.put("Jupiter", trop(jup, ay));
        out.put("Saturn", trop(sat, ay));
        out.put("Rahu", trop(rahuMean, ay));
        out.put("Ketu", trop(AstroMath.norm360(rahuMean + 180), ay));
        return out;
    }

    private static GeoPos trop(double sidereal, double ayanamsa) {
        return new GeoPos(AstroMath.norm360(sidereal + ayanamsa), 0, 1, false);
    }

    private static double rev(double revolutionsPerMahayuga, double ahargana) {
        return AstroMath.norm360(revolutionsPerMahayuga * 360.0 * (ahargana / MAHAYUGA_DAYS));
    }

    /** SS manda phala: epicycle circumference in degrees on a 360° deferent. */
    private static double mandaEpicycle(double mean, double mandocca, double circumDeg) {
        double M = AstroMath.norm360(mean - mandocca);
        double phala = AstroMath.atan2d(circumDeg * AstroMath.sind(M),
                360.0 + circumDeg * AstroMath.cosd(M));
        return AstroMath.norm360(mean + AstroMath.norm180(phala));
    }

    private static double manda(double mean, double anomaly, double e) {
        // manda_phala ≈ (180/π)*e*sin(M) in degrees if e is eccentricity
        double mandaPhala = Math.toDegrees(e) * 2 > 20
                ? (360.0 * e) * AstroMath.sind(anomaly)
                : Math.toDegrees(Math.asin(AstroMath.clamp(e * AstroMath.sind(anomaly) * 2 / (1 + e), -1, 1)));
        // simpler: 2e sin M in radians converted
        double corr = Math.toDegrees(2 * e * AstroMath.sind(anomaly));
        return AstroMath.norm360(mean + corr);
    }

    /**
     * Sighra correction for superior/inferior planets. For superior, sighra anomaly = sun - planet;
     * for inferior, planet's heliocentric - sun.
     */
    private static double sighra(double mandaLon, double sighraRef, double aAu) {
        double anomaly = AstroMath.norm360(sighraRef - mandaLon);
        double rho = aAu; // AU of planet vs 1 AU earth
        double k = 1.0 / rho;
        double corr = AstroMath.atan2d(AstroMath.sind(anomaly), (rho + AstroMath.cosd(anomaly)));
        // For superior planets sighra phala is atan(sin(σ)/(r/R + cos σ))
        if (aAu > 1) {
            corr = AstroMath.atan2d(AstroMath.sind(anomaly), (k + AstroMath.cosd(anomaly)));
            return AstroMath.norm360(mandaLon + AstroMath.norm180(corr));
        }
        // inferior: geocentric = sun + atan( sin(helio-sun) / (1/a + cos) ) roughly
        double helioMinusSun = AstroMath.norm180(sighraRef - mandaLon);
        double phala = AstroMath.atan2d(AstroMath.sind(helioMinusSun) * aAu, 1 + aAu * AstroMath.cosd(helioMinusSun));
        return AstroMath.norm360(mandaLon + phala);
    }

    public double[] houseCuspsSripati(double jdUt, double lat, double lonEast) {
        double asc = tropicalAscendant(jdUt, lat, lonEast);
        double mc = tropicalMc(jdUt, lonEast);
        double[] c = new double[13];
        c[1] = asc;
        c[10] = mc;
        c[7] = AstroMath.norm360(asc + 180);
        c[4] = AstroMath.norm360(mc + 180);
        // Sripati: trisect each quadrant
        fillQuadrant(c, 10, 1);
        fillQuadrant(c, 1, 4);
        fillQuadrant(c, 4, 7);
        fillQuadrant(c, 7, 10);
        return c;
    }

    private static void fillQuadrant(double[] c, int start, int end) {
        double a = c[start];
        double b = c[end];
        double span = AstroMath.norm360(b - a);
        int h1 = (start % 12) + 1;
        int h2 = (h1 % 12) + 1;
        c[h1] = AstroMath.norm360(a + span / 3.0);
        c[h2] = AstroMath.norm360(a + 2 * span / 3.0);
    }
}
