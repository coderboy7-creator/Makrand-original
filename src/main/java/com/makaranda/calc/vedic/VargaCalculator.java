package com.makaranda.calc.vedic;

import com.makaranda.calc.AstroMath;

/**
 * All 16 classical Vargas (Parashara Shodashavarga) D1–D60.
 * Longitude in is sidereal degrees.
 */
public final class VargaCalculator {
    private VargaCalculator() {}

    public static double vargaLongitude(double lon, int division) {
        lon = AstroMath.norm360(lon);
        return switch (division) {
            case 1 -> lon;
            case 2 -> hora(lon);
            case 3 -> drekkana(lon);
            case 4 -> chaturthamsa(lon);
            case 7 -> saptamsa(lon);
            case 9 -> navamsa(lon);
            case 10 -> dasamsa(lon);
            case 12 -> dwadasamsa(lon);
            case 16 -> shodasamsa(lon);
            case 20 -> vimsamsa(lon);
            case 24 -> siddhamsa(lon);
            case 27 -> bhamsa(lon);
            case 30 -> trimsamsa(lon);
            case 40 -> khavedamsa(lon);
            case 45 -> akshavedamsa(lon);
            case 60 -> shashtiamsa(lon);
            default -> cyclic(lon, division);
        };
    }

    public static int vargaSign(double lon, int division) {
        return AstroMath.signIndex(vargaLongitude(lon, division));
    }

    /** D9 Navamsa — 9 parts of 3°20'. Odd signs start from own, even from 7th. */
    public static double navamsa(double lon) {
        int sign = AstroMath.signIndex(lon);
        double within = lon - sign * 30;
        int part = (int) Math.floor(within / (30.0 / 9.0));
        int start = sign % 2 == 0 ? sign : (sign + 6) % 12; // 0-based: odd Mesha starts own
        // Parashara: movable signs start from Aries? Standard:
        // Fire signs start from Aries, Earth from Capricorn, Air from Libra, Water from Cancer.
        // Also equivalent: odd signs from own, even from 7th — that's a common method.
        // Using element method (Parashara):
        int elementStart = switch (sign % 4) {
            case 0 -> 0;  // fire -> Aries
            case 1 -> 9;  // earth -> Capricorn
            case 2 -> 6;  // air -> Libra
            default -> 3; // water -> Cancer
        };
        int ns = (elementStart + part) % 12;
        double remainder = (within % (30.0 / 9.0)) * 9.0;
        return ns * 30.0 + remainder;
    }

    public static double hora(double lon) {
        int sign = AstroMath.signIndex(lon);
        double within = lon - sign * 30;
        boolean first = within < 15.0;
        boolean odd = sign % 2 == 0;
        int hs = (odd && first) || (!odd && !first) ? 4 : 3; // Leo or Cancer
        double rem = (within % 15) * 2;
        return hs * 30 + rem;
    }

    public static double drekkana(double lon) {
        int sign = AstroMath.signIndex(lon);
        int part = (int) Math.floor((lon - sign * 30) / 10.0);
        int ds = (sign + part * 4) % 12;
        double rem = ((lon - sign * 30) % 10) * 3;
        return ds * 30 + rem;
    }

    public static double chaturthamsa(double lon) {
        int sign = AstroMath.signIndex(lon);
        int part = (int) Math.floor((lon - sign * 30) / 7.5);
        int ds = (sign + part * 3) % 12;
        double rem = ((lon - sign * 30) % 7.5) * 4;
        return ds * 30 + rem;
    }

    public static double saptamsa(double lon) {
        int sign = AstroMath.signIndex(lon);
        double within = lon - sign * 30;
        int part = (int) Math.floor(within / (30.0 / 7.0));
        int start = sign % 2 == 0 ? sign : (sign + 6) % 12;
        int ds = (start + part) % 12;
        double rem = (within % (30.0 / 7.0)) * 7;
        return ds * 30 + rem;
    }

    public static double dasamsa(double lon) {
        int sign = AstroMath.signIndex(lon);
        double within = lon - sign * 30;
        int part = (int) Math.floor(within / 3.0);
        int start = sign % 2 == 0 ? sign : (sign + 8) % 12; // odd from own, even from 9th
        int ds = (start + part) % 12;
        double rem = (within % 3.0) * 10;
        return ds * 30 + rem;
    }

    public static double dwadasamsa(double lon) {
        int sign = AstroMath.signIndex(lon);
        double within = lon - sign * 30;
        int part = (int) Math.floor(within / 2.5);
        int ds = (sign + part) % 12;
        double rem = (within % 2.5) * 12;
        return ds * 30 + rem;
    }

    public static double shodasamsa(double lon) {
        return cyclicFromNature(lon, 16);
    }

    public static double vimsamsa(double lon) {
        return cyclicFromNature(lon, 20);
    }

    public static double siddhamsa(double lon) {
        int sign = AstroMath.signIndex(lon);
        double within = lon - sign * 30;
        int part = (int) Math.floor(within / (30.0 / 24.0));
        int start = sign % 2 == 0 ? 3 : 9; // odd Cancer, even Capricorn
        int ds = (start + part) % 12;
        double rem = (within % (30.0 / 24.0)) * 24;
        return ds * 30 + rem;
    }

    public static double bhamsa(double lon) {
        return cyclic(lon, 27);
    }

    public static double trimsamsa(double lon) {
        int sign = AstroMath.signIndex(lon);
        double within = lon - sign * 30;
        boolean odd = sign % 2 == 0;
        int ds;
        double rem;
        if (odd) {
            if (within < 5) { ds = 0; rem = within * 6; }
            else if (within < 10) { ds = 10; rem = (within - 5) * 6; }
            else if (within < 18) { ds = 8; rem = (within - 10) * 30 / 8; }
            else if (within < 25) { ds = 2; rem = (within - 18) * 30 / 7; }
            else { ds = 6; rem = (within - 25) * 6; }
        } else {
            if (within < 5) { ds = 1; rem = within * 6; }
            else if (within < 12) { ds = 5; rem = (within - 5) * 30 / 7; }
            else if (within < 20) { ds = 11; rem = (within - 12) * 30 / 8; }
            else if (within < 25) { ds = 9; rem = (within - 20) * 6; }
            else { ds = 7; rem = (within - 25) * 6; }
        }
        return ds * 30 + Math.min(rem, 29.999);
    }

    public static double khavedamsa(double lon) {
        int sign = AstroMath.signIndex(lon);
        double within = lon - sign * 30;
        int part = (int) Math.floor(within / (30.0 / 40.0));
        int start = sign % 2 == 0 ? 0 : 6;
        int ds = (start + part) % 12;
        double rem = (within % (30.0 / 40.0)) * 40;
        return ds * 30 + rem;
    }

    public static double akshavedamsa(double lon) {
        return cyclicFromNature(lon, 45);
    }

    public static double shashtiamsa(double lon) {
        return cyclic(lon, 60);
    }

    private static double cyclic(double lon, int n) {
        int sign = AstroMath.signIndex(lon);
        double within = lon - sign * 30;
        int part = (int) Math.floor(within / (30.0 / n));
        int ds = (sign + part) % 12;
        double rem = (within % (30.0 / n)) * n;
        return ds * 30 + rem;
    }

    private static double cyclicFromNature(double lon, int n) {
        int sign = AstroMath.signIndex(lon);
        double within = lon - sign * 30;
        int part = (int) Math.floor(within / (30.0 / n));
        int start = switch (sign % 3) {
            case 0 -> 0; // movable
            case 1 -> 4; // fixed -> Leo
            default -> 8; // dual -> Sagittarius
        };
        int ds = (start + part) % 12;
        double rem = (within % (30.0 / n)) * n;
        return ds * 30 + rem;
    }
}
