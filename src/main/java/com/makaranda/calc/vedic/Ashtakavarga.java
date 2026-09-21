package com.makaranda.calc.vedic;

import com.makaranda.calc.VedicConstants;
import com.makaranda.calc.vedic.ChartBuilder.FullChart;
import com.makaranda.calc.vedic.ChartBuilder.PlanetBody;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parāśara Bhinnāṣṭakavarga (unsodhita) + Sarvāṣṭakavarga.
 * Seven grahas, eight karakas (Sun…Saturn + Lagna). Bindus 0–8 per rāśi.
 * Rahu/Ketu are not contributors. Does not retune bijas.
 */
public final class Ashtakavarga {

    public static final String[] KARAKAS = {
            "Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn", "Lagna"
    };
    public static final String[] BAV_GRAHAS = {
            "Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn"
    };

    /** Classical BPHS totals. */
    public static final Map<String, Integer> CLASSICAL_TOTALS = Map.of(
            "Sun", 48, "Moon", 49, "Mars", 39, "Mercury", 54,
            "Jupiter", 56, "Venus", 52, "Saturn", 39
    );
    public static final int SAV_TOTAL = 337;

    /**
     * Houses (1–12) from each karaka that donate a bindu into that graha's BAV.
     * Order of KARAKAS. BPHS / Mantreśvara standard; not Astrotalk.
     */
    private static final int[][][] HOUSES = {
            /* Sun */ {
                    {1, 2, 4, 7, 8, 9, 10, 11},
                    {3, 6, 10, 11},
                    {1, 2, 4, 7, 8, 9, 10, 11},
                    {3, 5, 6, 9, 10, 11, 12},
                    {5, 6, 9, 11},
                    {6, 7, 12},
                    {1, 2, 4, 7, 8, 9, 10, 11},
                    {3, 4, 6, 10, 11, 12}
            },
            /* Moon */ {
                    {3, 6, 7, 8, 10, 11},
                    {1, 3, 6, 7, 10, 11},
                    {2, 3, 5, 6, 9, 10, 11},
                    {1, 3, 4, 5, 7, 8, 10, 11},
                    {1, 2, 4, 7, 8, 10, 11},
                    {3, 4, 5, 7, 9, 10, 11},
                    {3, 5, 6, 11},
                    {3, 6, 10, 11}
            },
            /* Mars */ {
                    {3, 5, 6, 10, 11},
                    {3, 6, 11},
                    {1, 2, 4, 7, 8, 10, 11},
                    {3, 5, 6, 11},
                    {6, 10, 11, 12},
                    {6, 8, 11, 12},
                    {1, 4, 7, 8, 9, 10, 11},
                    {1, 3, 6, 10, 11}
            },
            /* Mercury */ {
                    {5, 6, 9, 11, 12},
                    {2, 4, 6, 8, 10, 11},
                    {1, 2, 4, 7, 8, 9, 10, 11},
                    {1, 3, 5, 6, 9, 10, 11},
                    {6, 8, 9, 11, 12},
                    {1, 2, 3, 4, 5, 8, 9, 11},
                    {1, 2, 4, 7, 8, 9, 10, 11},
                    {1, 2, 4, 6, 8, 10, 11}
            },
            /* Jupiter */ {
                    {1, 2, 3, 4, 7, 8, 9, 10, 11},
                    {2, 5, 7, 9, 11},
                    {1, 2, 4, 7, 8, 10, 11},
                    {1, 2, 4, 5, 6, 9, 10, 11},
                    {1, 2, 3, 4, 7, 8, 10, 11},
                    {2, 5, 6, 9, 10, 11},
                    {3, 5, 6, 12},
                    {1, 2, 4, 5, 6, 7, 9, 10, 11}
            },
            /* Venus */ {
                    {8, 11, 12},
                    {1, 2, 3, 4, 5, 8, 9, 11, 12},
                    {3, 5, 6, 9, 11, 12},
                    {3, 5, 6, 9, 11},
                    {5, 8, 9, 10, 11},
                    {1, 2, 3, 4, 5, 8, 9, 10, 11},
                    {3, 4, 5, 8, 9, 10, 11},
                    {1, 2, 3, 4, 5, 8, 9, 11}
            },
            /* Saturn */ {
                    {1, 2, 4, 7, 8, 9, 10, 11},
                    {3, 6, 11},
                    {3, 5, 6, 11, 12},
                    {6, 8, 9, 10, 11, 12},
                    {5, 6, 11, 12},
                    {6, 11, 12},
                    {3, 5, 6, 11},
                    {1, 3, 4, 6, 10, 11}
            }
    };

    private Ashtakavarga() {}

    /** Synthetic: karaka name → rāśi 0–11 (Lagna included). */
    public static Map<String, Object> fromSigns(Map<String, Integer> karakaSigns) {
        int[][] bav = new int[7][12];
        @SuppressWarnings("unchecked")
        List<String>[][] who = new List[7][12];
        for (int g = 0; g < 7; g++) {
            for (int s = 0; s < 12; s++) who[g][s] = new ArrayList<>();
        }
        for (int g = 0; g < 7; g++) {
            for (int k = 0; k < 8; k++) {
                Integer from = karakaSigns.get(KARAKAS[k]);
                if (from == null) {
                    throw new IllegalArgumentException("missing karaka " + KARAKAS[k]);
                }
                int fromSign = ((from % 12) + 12) % 12;
                boolean[] give = new boolean[13];
                for (int h : HOUSES[g][k]) give[h] = true;
                for (int s = 0; s < 12; s++) {
                    int house = ((s - fromSign + 12) % 12) + 1;
                    if (give[house]) {
                        bav[g][s]++;
                        who[g][s].add(KARAKAS[k]);
                    }
                }
            }
        }
        return pack(bav, who, karakaSigns.get("Lagna"));
    }

    public static Map<String, Object> fromChart(FullChart c) {
        Map<String, Integer> signs = new LinkedHashMap<>();
        signs.put("Lagna", c.lagna().signIndex());
        for (String g : BAV_GRAHAS) {
            PlanetBody p = c.planets().get(g);
            if (p == null) throw new IllegalStateException("chart missing " + g);
            signs.put(g, p.signIndex());
        }
        return fromSigns(signs);
    }

    private static Map<String, Object> pack(int[][] bav, List<String>[][] who, Integer lagnaSign) {
        int lagna = lagnaSign == null ? 0 : ((lagnaSign % 12) + 12) % 12;
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("system", "Parashara");
        out.put("reduction", "unsodhita");
        out.put("note", "Bhinnāṣṭakavarga 0–8 bindus; Sarvāṣṭakavarga sums to 337. No trikona/ekādhipatya reduction.");
        out.put("noteHi", "भिन्नाष्टकवर्ग प्रति राशि ०–८ बिन्दु; सर्वाष्टकवर्ग योग ३३७। त्रिकोण/एकाधिपत्य शोधन नहीं।");

        Map<String, Object> bavMap = new LinkedHashMap<>();
        int[] savSign = new int[12];
        int savSum = 0;
        Map<String, Integer> totals = new LinkedHashMap<>();
        for (int g = 0; g < 7; g++) {
            int tot = 0;
            int[] bySign = bav[g];
            int[] byHouse = new int[12];
            List<List<String>> contribSign = new ArrayList<>();
            List<List<String>> contribHouse = new ArrayList<>();
            for (int s = 0; s < 12; s++) {
                tot += bySign[s];
                savSign[s] += bySign[s];
                int h = (s - lagna + 12) % 12;
                byHouse[h] = bySign[s];
                contribSign.add(List.copyOf(who[g][s]));
            }
            for (int h = 0; h < 12; h++) {
                int s = (lagna + h) % 12;
                contribHouse.add(List.copyOf(who[g][s]));
            }
            savSum += tot;
            totals.put(BAV_GRAHAS[g], tot);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("graha", BAV_GRAHAS[g]);
            row.put("grahaHi", VedicConstants.planetHi(BAV_GRAHAS[g]));
            row.put("bySign", toList(bySign));
            row.put("byHouse", toList(byHouse));
            row.put("contributorsBySign", contribSign);
            row.put("contributorsByHouse", contribHouse);
            row.put("total", tot);
            row.put("classicalTotal", CLASSICAL_TOTALS.get(BAV_GRAHAS[g]));
            bavMap.put(BAV_GRAHAS[g], row);
        }
        int[] savHouse = new int[12];
        for (int s = 0; s < 12; s++) {
            savHouse[(s - lagna + 12) % 12] = savSign[s];
        }
        Map<String, Object> sav = new LinkedHashMap<>();
        sav.put("bySign", toList(savSign));
        sav.put("byHouse", toList(savHouse));
        sav.put("total", savSum);
        sav.put("classicalTotal", SAV_TOTAL);

        List<Map<String, Object>> signs = new ArrayList<>();
        for (int s = 0; s < 12; s++) {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("signIndex", s);
            r.put("sign", VedicConstants.SIGNS_EN[s]);
            r.put("signHi", VedicConstants.SIGNS_HI[s]);
            r.put("houseFromLagna", ((s - lagna + 12) % 12) + 1);
            r.put("sav", savSign[s]);
            signs.add(r);
        }

        out.put("bav", bavMap);
        out.put("sav", sav);
        out.put("totals", totals);
        out.put("savTotal", savSum);
        out.put("signs", signs);
        out.put("lagnaSign", lagna);
        return out;
    }

    private static List<Integer> toList(int[] a) {
        List<Integer> l = new ArrayList<>(a.length);
        for (int v : a) l.add(v);
        return l;
    }
}
