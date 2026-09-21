package com.makaranda.calc;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Chunk 1: lock KSDSU graha-spaṣṭa CSV. Does not call the ephemeris.
 */
class GrahaSpashtaBandGoldTest {

    private static final Path GOLD = Path.of("panji-accuracy/graha-spashta-band-data-2023-2026.csv");
    private static final String[] PLANETS = {"mars", "mercury", "jupiter", "venus", "saturn", "ketu"};

    @Test
    void canonicalFileHas166RowsAndAshadhaExcluded() throws Exception {
        assertTrue(Files.isRegularFile(GOLD), "missing " + GOLD.toAbsolutePath());
        List<String[]> rows = load(GOLD);
        assertEquals(166, rows.size(), "data rows");

        int yes = 0;
        int y2023 = 0, y2025 = 0, y2026 = 0;
        Set<String> dates = new HashSet<>();
        for (String[] r : rows) {
            String date = r[0];
            String page = r[1];
            String excl = r[15].trim();
            dates.add(date);
            if (date.startsWith("2023")) y2023++;
            else if (date.startsWith("2025")) y2025++;
            else if (date.startsWith("2026")) y2026++;
            if ("YES".equalsIgnoreCase(excl)) {
                yes++;
                assertTrue(page.contains("ashadha"), "YES only on Āṣāḍha, got " + page + " " + date);
            }
        }
        assertEquals(29, yes);
        assertEquals(137, rows.size() - yes);
        assertEquals(74, y2023);
        assertEquals(74, y2025);
        assertEquals(18, y2026);
        assertEquals(163, dates.size());
        assertTrue(dates.contains("2023-02-06"));
        assertTrue(dates.contains("2025-10-08"));
        assertTrue(dates.contains("2026-01-18"));
    }

    @Test
    void rawParsesToListedDegrees() throws Exception {
        int checked = 0;
        int mismatches = 0;
        int missing = 0;
        for (String[] r : load(GOLD)) {
            for (int i = 0; i < PLANETS.length; i++) {
                String raw = r[3 + 2 * i];
                String degStr = r[4 + 2 * i];
                if (raw == null || raw.isBlank() || degStr == null || degStr.isBlank()) {
                    missing++;
                    continue;
                }
                double listed = Double.parseDouble(degStr);
                double got = dmsToDeg(raw);
                checked++;
                if (Math.abs(got - listed) > 0.0015) {
                    mismatches++;
                }
            }
        }
        assertEquals(0, mismatches, "raw→deg mismatches");
        assertTrue(checked > 900, "checked " + checked);
        assertTrue(missing >= 8 && missing <= 20, "blank cells " + missing);
    }

    @Test
    void subset2023FileAgreesWhereBothExist() throws Exception {
        Path old = Path.of("panji-accuracy/graha-spashta-band-data.csv");
        if (!Files.isRegularFile(old)) {
            return;
        }
        List<String[]> full = load(GOLD);
        List<String[]> sub = load(old);
        assertEquals(77, sub.size());
        int compared = 0;
        for (String[] a : sub) {
            String[] b = find(full, a[0], a[1]);
            assertTrue(b != null, "missing in gold " + a[0] + " " + a[1]);
            for (int i = 0; i < PLANETS.length; i++) {
                String da = a[4 + 2 * i];
                String db = b[4 + 2 * i];
                if (da == null || da.isBlank() || db == null || db.isBlank()) continue;
                assertEquals(Double.parseDouble(da), Double.parseDouble(db), 0.0015,
                        a[0] + " " + PLANETS[i]);
                compared++;
            }
        }
        assertEquals(77 * 6, compared);
    }

    private static String[] find(List<String[]> rows, String date, String page) {
        for (String[] r : rows) {
            if (date.equals(r[0]) && page.equals(r[1])) return r;
        }
        return null;
    }

    static double dmsToDeg(String raw) {
        String s = raw.trim().replace("।", "|").replace(".", "|");
        String[] p = s.split("\\|");
        List<Integer> n = new ArrayList<>();
        for (String x : p) {
            if (!x.isBlank()) n.add(Integer.parseInt(x.trim()));
        }
        if (n.size() != 4) {
            throw new IllegalArgumentException("raw " + raw);
        }
        return 30.0 * n.get(0) + n.get(1) + n.get(2) / 60.0 + n.get(3) / 3600.0;
    }

    static List<String[]> load(Path path) throws Exception {
        List<String[]> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String header = br.readLine();
            assertTrue(header != null && header.startsWith("date,"), header);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                List<String> cols = parseCsvLine(line);
                assertTrue(cols.size() >= 16, "cols " + cols.size() + " " + line.substring(0, Math.min(40, line.length())));
                while (cols.size() < 18) {
                    cols.add("");
                }
                if (cols.size() > 18) {
                    StringBuilder notes = new StringBuilder(cols.get(17));
                    for (int i = 18; i < cols.size(); i++) {
                        notes.append(',').append(cols.get(i));
                    }
                    cols = new ArrayList<>(cols.subList(0, 17));
                    cols.add(notes.toString());
                }
                out.add(cols.toArray(String[]::new));
            }
        }
        return out;
    }

    /** RFC-style quotes; doubled quotes inside. */
    static List<String> parseCsvLine(String line) {
        List<String> cols = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean q = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (q) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        q = false;
                    }
                } else {
                    cur.append(c);
                }
            } else if (c == '"') {
                q = true;
            } else if (c == ',') {
                cols.add(cur.toString());
                cur.setLength(0);
            } else if (c != '\r') {
                cur.append(c);
            }
        }
        cols.add(cur.toString());
        return cols;
    }
}
