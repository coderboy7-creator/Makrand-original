package com.makaranda.calc.ephemeris;

import com.makaranda.calc.AstroMath;

/**
 * Sidereal ayanamsa engines. Default for this platform is Surya Siddhanta
 * (Makaranda) as used in Mithilanchal panchang.
 */
public enum AyanamsaSystem {
    SURYA_SIDDHANTA_MAKARANDA("Surya Siddhanta (Makaranda) — Mithilanchal", true),
    SURYA_SIDDHANTA_REVATI("Surya Siddhanta Revati", true),
    LAHIRI("Lahiri (Chitrapaksha)", false),
    TRUE_CHITRA("True Chitra Paksha", false),
    RAMAN("B.V. Raman", false),
    KRISHNAMURTI("K.S. Krishnamurti (KP)", false),
    YUKTESHWAR("Sri Yukteshwar", false),
    FAGAN_BRADLEY("Fagan-Bradley (Western Sidereal)", false),
    PUSHYA_PAKSHA("Pushya Paksha", false),
    ARYABHATA("Aryabhata", false),
    JNBIRTH("JN Bhasin", false),
    TROPICAL("Tropical (Ayanamsa = 0)", false);

    public final String label;
    public final boolean siddhanticFamily;

    AyanamsaSystem(String label, boolean siddhanticFamily) {
        this.label = label;
        this.siddhanticFamily = siddhanticFamily;
    }

    public static AyanamsaSystem from(String raw) {
        if (raw == null || raw.isBlank()) return SURYA_SIDDHANTA_MAKARANDA;
        String k = raw.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        if ("KP".equals(k) || "KP_NEW".equals(k) || "KRISHNAMURTI_KP".equals(k) || "KSK".equals(k)) {
            return KRISHNAMURTI;
        }
        try {
            return AyanamsaSystem.valueOf(k);
        } catch (IllegalArgumentException ex) {
            return SURYA_SIDDHANTA_MAKARANDA;
        }
    }

    /**
     * Ayanamsa in degrees at Julian Day UT.
     */
    public double ayanamsa(double jdUt) {
        double t = AstroMath.centuriesJ2000(jdUt);
        double year = 2000.0 + (jdUt - AstroMath.J2000) / 365.24219;
        return switch (this) {
            case TROPICAL -> 0.0;
            case LAHIRI -> lahiri(t);
            case TRUE_CHITRA -> trueChitra(t);
            case RAMAN -> 21.443 + (50.2388475 / 3600.0) * (year - 1900.0);
            case KRISHNAMURTI -> lahiri(t) - 0.097; // KP is ~5'48" behind Lahiri historically; close
            case YUKTESHWAR -> 22.460413 + (50.2388475 / 3600.0) * (year - 1900.0) - 1.333333;
            case FAGAN_BRADLEY -> 24.041435 + (50.270958 / 3600.0) * (year - 1900.0) + 0.000139 * t * t;
            case PUSHYA_PAKSHA -> lahiri(t) - 3.366667; // ~3°22' behind Lahiri
            case ARYABHATA -> ssRevati(year) + 0.214;
            case JNBIRTH -> lahiri(t) - 0.883;
            case SURYA_SIDDHANTA_REVATI -> ssRevati(year);
            case SURYA_SIDDHANTA_MAKARANDA -> makaranda(year);
        };
    }

    /**
     * IAE / Lahiri Chitrapaksha. Standard linear model used by Indian ephemerides:
     * 23°15'00" on 21 Mar 1956, precession 50.2388475"/year.
     */
    private static double lahiri(double t) {
        // Swiss-Eph compatible approximation at J2000 ≈ 23.853°
        return 23.852931 + 0.013961239 * (t * 100.0) + 0.000000277 * t * t * 10000;
    }

    /**
     * KSDS / Makaranda printed spashta (तिथि-नक्षत्र-योग) follows Chitrapaksha
     * (Lahiri), the IAE standard used in modern Mithila विश्वविद्यालय पञ्चांग —
     * not the textbook 54″/year from 499 CE, which sits ~1.3° behind in the
     * 21st century and shifts nakshatra/yoga by hours. Textbook SS ayanamsa
     * remains available as {@link #SURYA_SIDDHANTA_REVATI}.
     */
    private static double makaranda(double year) {
        double t = (year - 2000.0) / 100.0;
        return lahiri(t);
    }

    private static double ssRevati(double year) {
        return 54.0 * (year - 522.0) / 3600.0;
    }

    /**
     * Spica (Chitra) fixed at 180°. J2000 ecliptic longitude of Spica ≈ 203.737°.
     */
    private static double trueChitra(double t) {
        double spicaTrop = 203.737 + 1.396971 * t * 100.0 / 100.0 * 50.29 / 3600 * 100; // rough precession
        spicaTrop = 203.737 + (50.290966 / 3600.0) * (t * 100.0);
        return AstroMath.norm360(spicaTrop - 180.0);
    }
}
