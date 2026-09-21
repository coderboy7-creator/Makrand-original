package com.makaranda.calc.ephemeris;

import com.makaranda.calc.AstroMath;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dual-mode planetary engine.
 * <ul>
 *   <li>DRIK — Swiss Ephemeris when sepl/semo files are present; else Meeus + JPL Kepler</li>
 *   <li>SIDDHANTIC — Surya Siddhanta mean motions from Kali Yuga with manda/sighra</li>
 * </ul>
 * Swiss Eph is never used for SIDDHANTIC.
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

    /** Drik tropical grahas: Swiss Eph when loaded, otherwise Meeus/Kepler. */
    public String drikBackend() {
        return SwissEphAdapter.available() ? SwissEphAdapter.backend() : "meeus-fallback";
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
        return sunriseSunsetLocal(jdUtDate0, lat, lonEast, tzHours, PanchangMode.DRIK);
    }

    /**
     * SIDDHANTIC madhyāhna uses Makaranda bhujāntara (not NOAA EoT, not a scale).
     * Drik keeps NOAA. Dinamaan still from apparent declination.
     */
    public double[] sunriseSunsetLocal(double jdUtDate0, double lat, double lonEast, double tzHours,
                                      PanchangMode mode) {
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
        // SIDDHANTIC still uses NOAA EoT for madhyāhna. Bhujāntara-only (P13 try)
        // put 29 Jul 2022 SS at 6:45 vs book 6:50 (fails 4 min). NOAA scale-0 was
        // the same dead end. Keep NOAA; MakarandaSpashta.bhujantaraMinutes is the
        // next lever together with udayāntara — not a 1-D scale.
        double eot = equationOfTimeMinutes(jd);
        double noon = 12.0 - eot / 60.0;
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

    /* ======================== DRIK (Swiss Eph, else JPL + Meeus) ======================== */

    private Map<String, GeoPos> drik(double jdUt) {
        if (SwissEphAdapter.available()) {
            try {
                return SwissEphAdapter.planets(jdUt);
            } catch (RuntimeException ignored) {
                // keep Meeus/Kepler so Drik still answers if a date is outside se1 coverage
            }
        }
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

    /** SIDDHANTIC grahas: {@link MakarandaSpashta} (frozen bijas + sphuṭa paridhi + bhujāntara). */
    private Map<String, GeoPos> siddhantic(double jdUt, double lonEast) {
        MakarandaSpashta.Bodies g = MakarandaSpashta.at(jdUt, lonEast);
        // Convert SS sidereal longitudes to tropical by adding ayanamsa so downstream
        // subtraction of ayanamsa yields SS sidereal. We return "tropical-equivalent"
        // = sidereal + ayanamsa(Makaranda).
        double ay = AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA.ayanamsa(jdUt);

        Map<String, GeoPos> out = new LinkedHashMap<>();
        out.put("Sun", trop(g.sun(), ay));
        out.put("Moon", trop(g.moon(), ay));
        out.put("Mercury", trop(g.mercury(), ay));
        out.put("Venus", trop(g.venus(), ay));
        out.put("Mars", trop(g.mars(), ay));
        out.put("Jupiter", trop(g.jupiter(), ay));
        out.put("Saturn", trop(g.saturn(), ay));
        out.put("Rahu", trop(g.rahu(), ay));
        out.put("Ketu", trop(AstroMath.norm360(g.rahu() + 180), ay));
        return out;
    }

    private static GeoPos trop(double sidereal, double ayanamsa) {
        return new GeoPos(AstroMath.norm360(sidereal + ayanamsa), 0, 1, false);
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

    /**
     * Tropical Placidus cusps (1–12). Polar latitudes fall back to Sripati
     * (Porphyry-style quadrants). Does not call Swiss Ephemeris.
     */
    public double[] houseCuspsPlacidus(double jdUt, double lat, double lonEast) {
        double eps = obliquity(jdUt);
        if (Math.abs(lat) >= 90.0 - eps - 1e-6) {
            return houseCuspsSripati(jdUt, lat, lonEast);
        }
        double ramc = localSidereal(jdUt, lonEast);
        double sine = AstroMath.sind(eps);
        double cose = AstroMath.cosd(eps);
        double tane = AstroMath.tand(eps);
        double tanfi = AstroMath.tand(lat);
        double[] c = new double[13];
        c[1] = tropicalAscendant(jdUt, lat, lonEast);
        c[10] = tropicalMc(jdUt, lonEast);
        double a = AstroMath.asind(AstroMath.tand(lat) * tane);
        double fh1 = Math.toDegrees(Math.atan(AstroMath.sind(a / 3.0) / tane));
        double fh2 = Math.toDegrees(Math.atan(AstroMath.sind(a * 2.0 / 3.0) / tane));
        c[11] = placidusCusp(ramc, 30, fh1, 3.0, tanfi, sine, cose);
        c[12] = placidusCusp(ramc, 60, fh2, 1.5, tanfi, sine, cose);
        c[2] = placidusCusp(ramc, 120, fh2, 1.5, tanfi, sine, cose);
        c[3] = placidusCusp(ramc, 150, fh1, 3.0, tanfi, sine, cose);
        c[4] = AstroMath.norm360(c[10] + 180);
        c[5] = AstroMath.norm360(c[11] + 180);
        c[6] = AstroMath.norm360(c[12] + 180);
        c[7] = AstroMath.norm360(c[1] + 180);
        c[8] = AstroMath.norm360(c[2] + 180);
        c[9] = AstroMath.norm360(c[3] + 180);
        return c;
    }

    private static double placidusCusp(double ramc, double addRa, double fh, double poleDiv,
                                       double tanfi, double sine, double cose) {
        double rectasc = AstroMath.norm360(addRa + ramc);
        double first = houseAsc(rectasc, fh, sine, cose);
        double tant = AstroMath.tand(AstroMath.asind(sine * AstroMath.sind(first)));
        if (Math.abs(tant) < 1e-10) return rectasc;
        double f = Math.toDegrees(Math.atan(AstroMath.sind(AstroMath.asind(tanfi * tant) / poleDiv) / tant));
        double cusp = houseAsc(rectasc, f, sine, cose);
        for (int i = 0; i < 2; i++) {
            tant = AstroMath.tand(AstroMath.asind(sine * AstroMath.sind(cusp)));
            if (Math.abs(tant) < 1e-10) return rectasc;
            f = Math.toDegrees(Math.atan(AstroMath.sind(AstroMath.asind(tanfi * tant) / poleDiv) / tant));
            cusp = houseAsc(rectasc, f, sine, cose);
        }
        return cusp;
    }

    /** Ecliptic longitude of the ascendant for a given RA and pole height (degrees). */
    private static double houseAsc(double x1, double f, double sine, double cose) {
        x1 = AstroMath.norm360(x1);
        int n = (int) (x1 / 90.0) + 1;
        double ass;
        if (n == 1) ass = houseAscQ(x1, f, sine, cose);
        else if (n == 2) ass = 180 - houseAscQ(180 - x1, -f, sine, cose);
        else if (n == 3) ass = 180 + houseAscQ(x1 - 180, -f, sine, cose);
        else ass = 360 - houseAscQ(360 - x1, f, sine, cose);
        return AstroMath.norm360(ass);
    }

    private static double houseAscQ(double x, double f, double sine, double cose) {
        double ass = -AstroMath.tand(f) * sine + cose * AstroMath.cosd(x);
        double sinx = AstroMath.sind(x);
        if (Math.abs(ass) < 1e-12) ass = 0;
        if (Math.abs(sinx) < 1e-12) sinx = 0;
        if (sinx == 0) {
            ass = ass < 0 ? -1e-12 : 1e-12;
            return 90;
        }
        if (ass == 0) return sinx < 0 ? -90 : 90;
        ass = Math.toDegrees(Math.atan(sinx / ass));
        if (ass < 0) ass += 180;
        return ass;
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
