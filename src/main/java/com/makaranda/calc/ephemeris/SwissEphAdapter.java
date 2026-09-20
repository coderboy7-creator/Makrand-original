package com.makaranda.calc.ephemeris;

import com.makaranda.calc.AstroMath;
import swisseph.SweConst;
import swisseph.SwissEph;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Swiss Ephemeris (DE-series .se1 files) for <b>Drik only</b>.
 * SIDDHANTIC stays Surya Siddhanta + Makaranda bijas.
 * If files are missing or a call falls back to Moshier, callers use Meeus/JPL Kepler.
 */
public final class SwissEphAdapter {

    private static final String[] FILES = {"sepl_18.se1", "semo_18.se1"};
    private static final int IFLAG = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SPEED;
    private static final Object LOCK = new Object();

    private static final SwissEph SWE;
    private static final boolean READY;
    private static final String BACKEND;

    static {
        SwissEph swe = null;
        boolean ok = false;
        String backend = "meeus-fallback";
        try {
            Path dir = materializeEpheDir();
            if (dir != null) {
                swe = new SwissEph(dir.toAbsolutePath().toString());
                double[] xx = new double[6];
                StringBuilder err = new StringBuilder();
                int rc = swe.swe_calc_ut(2451545.0, SweConst.SE_SUN, IFLAG, xx, err);
                if (rc != SweConst.ERR && (rc & SweConst.SEFLG_SWIEPH) != 0) {
                    ok = true;
                    backend = "swiss-ephemeris";
                } else {
                    swe.swe_close();
                    swe = null;
                }
            }
        } catch (Throwable ignored) {
            if (swe != null) {
                try { swe.swe_close(); } catch (Exception e) { /* ignore */ }
                swe = null;
            }
            ok = false;
            backend = "meeus-fallback";
        }
        SWE = swe;
        READY = ok;
        BACKEND = backend;
    }

    private SwissEphAdapter() {}

    public static boolean available() {
        return READY;
    }

    public static String backend() {
        return BACKEND;
    }

    public static double[] tropicalSunMoon(double jdUt) {
        if (!READY) throw new IllegalStateException("Swiss Ephemeris not available");
        synchronized (LOCK) {
            return new double[]{body(jdUt, SweConst.SE_SUN).lon(), body(jdUt, SweConst.SE_MOON).lon()};
        }
    }

    public static Map<String, EphemerisEngine.GeoPos> planets(double jdUt) {
        if (!READY) throw new IllegalStateException("Swiss Ephemeris not available");
        synchronized (LOCK) {
            Map<String, EphemerisEngine.GeoPos> out = new LinkedHashMap<>();
            out.put("Sun", body(jdUt, SweConst.SE_SUN));
            out.put("Moon", body(jdUt, SweConst.SE_MOON));
            out.put("Mercury", body(jdUt, SweConst.SE_MERCURY));
            out.put("Venus", body(jdUt, SweConst.SE_VENUS));
            out.put("Mars", body(jdUt, SweConst.SE_MARS));
            out.put("Jupiter", body(jdUt, SweConst.SE_JUPITER));
            out.put("Saturn", body(jdUt, SweConst.SE_SATURN));
            EphemerisEngine.GeoPos rahu = body(jdUt, SweConst.SE_TRUE_NODE);
            out.put("Rahu", rahu);
            out.put("Ketu", new EphemerisEngine.GeoPos(
                    AstroMath.norm360(rahu.lon() + 180.0), -rahu.lat(), rahu.distanceAu(), true));
            return out;
        }
    }

    private static EphemerisEngine.GeoPos body(double jdUt, int ipl) {
        double[] xx = new double[6];
        StringBuilder err = new StringBuilder();
        int rc = SWE.swe_calc_ut(jdUt, ipl, IFLAG, xx, err);
        if (rc == SweConst.ERR || (rc & SweConst.SEFLG_SWIEPH) == 0) {
            throw new IllegalStateException("Swiss Eph failed for ipl=" + ipl + ": " + err);
        }
        boolean retro = xx[3] < 0;
        return new EphemerisEngine.GeoPos(AstroMath.norm360(xx[0]), xx[1], xx[2], retro);
    }

    static Path materializeEpheDir() throws Exception {
        String env = System.getenv("MAKARANDA_EPHE_PATH");
        if (env == null || env.isBlank()) env = System.getProperty("makaranda.ephe.path");
        if (env != null && !env.isBlank()) {
            Path p = Path.of(env);
            if (hasFiles(p)) return p;
        }
        Path dest = Path.of(System.getProperty("java.io.tmpdir"), "makaranda-ephe");
        Files.createDirectories(dest);
        ClassLoader cl = SwissEphAdapter.class.getClassLoader();
        for (String name : FILES) {
            Path out = dest.resolve(name);
            try (InputStream in = cl.getResourceAsStream("ephe/" + name)) {
                if (in == null) return null;
                if (!Files.exists(out) || Files.size(out) < 1000) {
                    Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        return hasFiles(dest) ? dest : null;
    }

    private static boolean hasFiles(Path dir) {
        return Files.isRegularFile(dir.resolve("sepl_18.se1"))
                && Files.isRegularFile(dir.resolve("semo_18.se1"));
    }
}
