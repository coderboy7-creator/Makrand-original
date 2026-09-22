package com.makaranda.calc.ephemeris;

import com.makaranda.calc.AstroMath;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Chunk 5 measure: panji−SIDD vs elongation after SS paridhi + mandocca.
 * Not a bija. Prints stats; fails only if inferiors still show a multi-degree slope.
 */
class GrahaSighraFullMeasureTest {

    private static final double KSDS = 85.268;
    private static final Path CSV = Path.of("panji-accuracy/graha-spashta-band-data-2023-2026.csv");

    @Test
    void inferiorResidualNotAMultiDegreeElongSlope() throws Exception {
        Stats mer = new Stats();
        Stats ven = new Stats();
        Stats sat = new Stats();
        Stats mar = new Stats();
        Stats jup = new Stats();
        for (String line : Files.readAllLines(CSV)) {
            if (line.startsWith("date") || line.isBlank()) continue;
            String[] c = line.replace("\r", "").split(",", -1);
            if (c.length < 16) continue;
            if (!"NO".equals(c[15].trim())) continue;
            LocalDate d = LocalDate.parse(c[0].trim());
            double jd = AstroMath.julianDayUt(d.getYear(), d.getMonthValue(), d.getDayOfMonth(), 6, 30, 0, 5.5);
            MakarandaSpashta.Bodies g = MakarandaSpashta.at(jd, KSDS);
            add(mer, parseDeg(c[6]), g.mercury(), g.sun());
            add(ven, parseDeg(c[10]), g.venus(), g.sun());
            add(sat, parseDeg(c[12]), g.saturn(), g.sun());
        }
        System.out.println("CHUNK5 mer n=" + mer.n + " mean=" + mer.mean() + " sd=" + mer.sd()
                + " slope=" + mer.slope() + " corr=" + mer.corr());
        System.out.println("CHUNK5 ven n=" + ven.n + " mean=" + ven.mean() + " sd=" + ven.sd()
                + " slope=" + ven.slope() + " corr=" + ven.corr());
        System.out.println("CHUNK5 sat n=" + sat.n + " mean=" + sat.mean() + " sd=" + sat.sd()
                + " slope=" + sat.slope() + " corr=" + sat.corr());
        // Multi-degree slope = |d(residual)/d(elong)| ≳ 0.15 °/° over tens of degrees of elong.
        assertTrue(Math.abs(mer.slope()) < 0.15, "Budha slope " + mer.slope() + " corr " + mer.corr());
        assertTrue(Math.abs(ven.slope()) < 0.15, "Śukra slope " + ven.slope() + " corr " + ven.corr());
    }

    private static double parseDeg(String s) {
        if (s == null || s.isBlank()) return Double.NaN;
        return Double.parseDouble(s.trim());
    }

    private static void add(Stats st, double panji, double sidd, double sun) {
        if (Double.isNaN(panji)) return;
        double res = AstroMath.norm180(panji - sidd);
        double elong = AstroMath.norm180(sidd - sun);
        st.add(elong, res);
    }

    static final class Stats {
        int n;
        double sx, sy, sxx, syy, sxy;

        void add(double x, double y) {
            n++;
            sx += x;
            sy += y;
            sxx += x * x;
            syy += y * y;
            sxy += x * y;
        }

        double mean() {
            return sy / n;
        }

        double sd() {
            return Math.sqrt(Math.max(0, syy / n - mean() * mean()));
        }

        double slope() {
            double den = sxx - sx * sx / n;
            return den == 0 ? 0 : (sxy - sx * sy / n) / den;
        }

        double corr() {
            double vx = sxx - sx * sx / n;
            double vy = syy - sy * sy / n;
            if (vx <= 0 || vy <= 0) return 0;
            return (sxy - sx * sy / n) / Math.sqrt(vx * vy);
        }
    }
}
