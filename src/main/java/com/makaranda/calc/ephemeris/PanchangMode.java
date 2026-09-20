package com.makaranda.calc.ephemeris;

public enum PanchangMode {
    /** True / Drik ganita — apparent geocentric positions (observational). */
    DRIK,
    /** Siddhantic / Makaranda — Surya Siddhanta mean + manda/sighra corrections. */
    SIDDHANTIC;

    public static PanchangMode from(String raw) {
        if (raw == null) return SIDDHANTIC;
        if (raw.equalsIgnoreCase("DRIK") || raw.equalsIgnoreCase("DRIG") || raw.equalsIgnoreCase("TRUE")) {
            return DRIK;
        }
        return SIDDHANTIC;
    }
}
