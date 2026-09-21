package com.makaranda.calc;

public final class AstroMath {
    private AstroMath() {}

    public static final double DEG = Math.PI / 180.0;
    public static final double J2000 = 2451545.0;
    public static final double NAKSHATRA_SPAN = 360.0 / 27.0; // 13°20'
    public static final double PADA_SPAN = NAKSHATRA_SPAN / 4.0; // 3°20'

    public static double norm360(double d) {
        d = d % 360.0;
        if (d < 0) d += 360.0;
        return d;
    }

    public static double norm180(double d) {
        d = norm360(d);
        if (d > 180.0) d -= 360.0;
        return d;
    }

    public static double sind(double d) { return Math.sin(d * DEG); }
    public static double cosd(double d) { return Math.cos(d * DEG); }
    public static double tand(double d) { return Math.tan(d * DEG); }

    public static double atan2d(double y, double x) {
        return norm360(Math.toDegrees(Math.atan2(y, x)));
    }

    public static double asind(double x) { return Math.toDegrees(Math.asin(clamp(x, -1, 1))); }
    public static double acosd(double x) { return Math.toDegrees(Math.acos(clamp(x, -1, 1))); }

    public static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    public static double julianDay(int year, int month, double day) {
        int y = year;
        int m = month;
        if (m <= 2) {
            y -= 1;
            m += 12;
        }
        int A = y / 100;
        int B = 2 - A + A / 4;
        return Math.floor(365.25 * (y + 4716))
                + Math.floor(30.6001 * (m + 1))
                + day + B - 1524.5;
    }

    public static double julianDayUt(int year, int month, int day, int hour, int minute, double second, double tzHours) {
        double ut = hour + minute / 60.0 + second / 3600.0 - tzHours;
        return julianDay(year, month, day + ut / 24.0);
    }

    public static double centuriesJ2000(double jd) {
        return (jd - J2000) / 36525.0;
    }

    public static String dms(double deg) {
        deg = norm360(deg);
        int d = (int) Math.floor(deg);
        double mf = (deg - d) * 60.0;
        int m = (int) Math.floor(mf);
        double s = (mf - m) * 60.0;
        return String.format("%d° %02d' %05.2f\"", d, m, s);
    }

    public static String signDegree(double longitude) {
        longitude = norm360(longitude);
        int sign = (int) Math.floor(longitude / 30.0);
        double within = longitude - sign * 30.0;
        int d = (int) Math.floor(within);
        double mf = (within - d) * 60.0;
        int m = (int) Math.floor(mf);
        double s = (mf - m) * 60.0;
        return String.format("%s %d° %02d' %04.1f\"", VedicConstants.SIGNS_EN[sign], d, m, s);
    }

    public static int signIndex(double longitude) {
        return (int) Math.floor(norm360(longitude) / 30.0);
    }

    public static int houseFromLagna(int lagnaSign, int planetSign) {
        return ((planetSign - lagnaSign + 12) % 12) + 1;
    }

    /** House 1–12 from Placidus/Sripati cusps (cusps[1..12] sidereal longitudes). */
    public static int houseFromCusps(double longitude, double[] cusps) {
        if (cusps == null || cusps.length < 13) return 1;
        double lon = norm360(longitude);
        for (int h = 1; h <= 12; h++) {
            int next = h == 12 ? 1 : h + 1;
            double span = norm360(cusps[next] - cusps[h]);
            double d = norm360(lon - cusps[h]);
            if (d < span || span < 1e-9) return h;
        }
        return 1;
    }

    public static double keplerE(double Mdeg, double e) {
        double M = Math.toRadians(norm360(Mdeg + 180.0) - 180.0);
        double E = M;
        for (int i = 0; i < 12; i++) {
            double dE = (M - E + e * Math.sin(E)) / (1 - e * Math.cos(E));
            E += dE;
            if (Math.abs(dE) < 1e-10) break;
        }
        return E;
    }

    public static double trueAnomaly(double E, double e) {
        double tv = 2 * Math.atan2(Math.sqrt(1 + e) * Math.sin(E / 2),
                Math.sqrt(1 - e) * Math.cos(E / 2));
        return tv;
    }

    public static double polynomial(double t, double... c) {
        double s = 0, p = 1;
        for (double v : c) {
            s += v * p;
            p *= t;
        }
        return s;
    }
}
