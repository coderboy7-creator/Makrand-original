package com.makaranda.calc.muhurta;

import com.makaranda.calc.panchang.PanchangCalculator;
import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.PanchangMode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class MuhurtaCalculator {

    private final PanchangCalculator panchang = new PanchangCalculator();

    public enum Purpose { MARRIAGE, BUSINESS, TRAVEL, PROPERTY, NAMAKARANA, ANNAPRASHANA, EDUCATION, MEDICAL, GENERAL }

    public record TravelQuery(String fromPlace, double fromLat, double fromLon,
                              String toPlace, Double toLat, Double toLon, String direction) {}

    private static final Set<String> GOOD_TITHI = Set.of(
            "Dwitiya", "Tritiya", "Panchami", "Saptami", "Dashami", "Ekadashi", "Trayodashi", "Purnima"
    );
    private static final Set<String> BAD_TITHI = Set.of("Chaturthi", "Navami", "Chaturdashi", "Amavasya");
    private static final Set<String> GOOD_NAK_MARRIAGE = Set.of(
            "Rohini", "Mrigashira", "Magha", "Uttara Phalguni", "Hasta", "Swati",
            "Anuradha", "Mula", "Uttara Ashadha", "Uttara Bhadrapada", "Revati"
    );
    private static final Set<String> GOOD_NAK_TRAVEL = Set.of(
            "Ashwini", "Punarvasu", "Pushya", "Hasta", "Anuradha", "Shravana", "Revati",
            "Mrigashira", "Swati", "Dhanishta", "Shatabhisha"
    );
    private static final Set<String> GOOD_NAK_BUSINESS = Set.of(
            "Rohini", "Pushya", "Hasta", "Chitra", "Anuradha", "Uttara Phalguni", "Uttara Ashadha", "Revati"
    );
    private static final Set<String> INAUSPICIOUS_YOGA = Set.of(
            "Atiganda", "Shula", "Ganda", "Vyaghata", "Vyatipata", "Parigha", "Vaidhriti");

    /** Disha shool — do not travel this way on this weekday (Sun=0). */
    private static final String[] SHOOL = {"W", "E", "N", "N", "S", "W", "E"};

    public List<Map<String, Object>> search(Purpose purpose, LocalDate from, int days,
                                            double lat, double lon, double tz,
                                            AyanamsaSystem ay, PanchangMode mode) {
        return search(purpose, from, days, lat, lon, tz, ay, mode, null);
    }

    public List<Map<String, Object>> search(Purpose purpose, LocalDate from, int days,
                                            double lat, double lon, double tz,
                                            AyanamsaSystem ay, PanchangMode mode,
                                            TravelQuery travel) {
        if (ay == null) ay = AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA;
        if (mode == null) mode = PanchangMode.SIDDHANTIC;
        String dir = null;
        double bearing = Double.NaN;
        if (purpose == Purpose.TRAVEL) {
            dir = resolveDirection(travel);
            if (travel != null && travel.toLat != null && travel.toLon != null) {
                bearing = bearingDeg(travel.fromLat, travel.fromLon, travel.toLat, travel.toLon);
                if (dir == null) dir = compass8(bearing);
            }
        }
        double panchLat = (purpose == Purpose.TRAVEL && travel != null) ? travel.fromLat : lat;
        double panchLon = (purpose == Purpose.TRAVEL && travel != null) ? travel.fromLon : lon;

        List<Map<String, Object>> hits = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate d = from.plusDays(i);
            Map<String, Object> p = panchang.compute(d, panchLat, panchLon, tz, ay, mode);
            int score = score(purpose, p, dir, d);
            boolean shool = purpose == Purpose.TRAVEL && dir != null && isShool(d, dir);
            if (purpose == Purpose.TRAVEL) {
                if (!shool && score < 6) continue;
            } else if (score < 6) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", d.toString());
            row.put("score", score);
            row.put("grade", shool ? "Nishiddha" : (score >= 10 ? "Uttama" : score >= 8 ? "Madhyama" : "Sadharana"));
            row.put("tithi", p.get("pakshaHi") + " " + p.get("tithiHi"));
            row.put("tithiEn", p.get("paksha") + " " + p.get("tithi"));
            row.put("tithiEnd", p.get("tithiEnd"));
            row.put("nakshatra", p.get("nakshatraHi"));
            row.put("nakshatraEn", p.get("nakshatra"));
            row.put("nakshatraEnd", p.get("nakshatraEnd"));
            row.put("yoga", p.get("yogaHi"));
            row.put("vara", p.get("varaHi"));
            row.put("varaEn", p.get("vara"));
            row.put("sunrise", p.get("sunrise"));
            row.put("abhijit", p.get("abhijit"));
            row.put("avoid", p.get("muhurta"));
            if (purpose == Purpose.TRAVEL) {
                row.put("direction", dir);
                row.put("directionHi", directionHi(dir));
                row.put("bearingDeg", Double.isNaN(bearing) ? null : Math.round(bearing * 10) / 10.0);
                row.put("fromPlace", travel == null ? "Darbhanga, Bihar, India" : travel.fromPlace);
                row.put("toPlace", travel == null ? null : travel.toPlace);
                row.put("dishaShool", shool);
                row.put("dishaShoolToday", shoolLabel(d));
                row.put("departAfter", p.get("sunrise"));
                row.put("avoidRahu", p.get("muhurta"));
            }
            row.put("reason", reasons(purpose, p, score, dir, shool));
            hits.add(row);
        }
        if (purpose == Purpose.TRAVEL) {
            hits.sort((a, b) -> {
                boolean sa = Boolean.TRUE.equals(a.get("dishaShool"));
                boolean sb = Boolean.TRUE.equals(b.get("dishaShool"));
                if (sa != sb) return sa ? 1 : -1;
                return Integer.compare((Integer) b.get("score"), (Integer) a.get("score"));
            });
        } else {
            hits.sort((a, b) -> Integer.compare((Integer) b.get("score"), (Integer) a.get("score")));
        }
        return hits;
    }

    public static String resolveDirection(TravelQuery travel) {
        if (travel == null) return null;
        if (travel.direction != null && !travel.direction.isBlank()) {
            return canonDir(travel.direction);
        }
        if (travel.toLat != null && travel.toLon != null) {
            return compass8(bearingDeg(travel.fromLat, travel.fromLon, travel.toLat, travel.toLon));
        }
        return null;
    }

    public static double bearingDeg(double lat1, double lon1, double lat2, double lon2) {
        double φ1 = Math.toRadians(lat1);
        double φ2 = Math.toRadians(lat2);
        double Δλ = Math.toRadians(lon2 - lon1);
        double y = Math.sin(Δλ) * Math.cos(φ2);
        double x = Math.cos(φ1) * Math.sin(φ2) - Math.sin(φ1) * Math.cos(φ2) * Math.cos(Δλ);
        return (Math.toDegrees(Math.atan2(y, x)) + 360.0) % 360.0;
    }

    public static String compass8(double bearing) {
        String[] dirs = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        int i = (int) Math.round(bearing / 45.0) % 8;
        return dirs[i];
    }

    private static String canonDir(String raw) {
        String s = raw.trim().toUpperCase(Locale.ROOT)
                .replace("PURVA", "E").replace("EAST", "E").replace("पूर्व", "E")
                .replace("PASCHIM", "W").replace("WEST", "W").replace("पश्चिम", "W")
                .replace("UTTAR", "N").replace("NORTH", "N").replace("उत्तर", "N")
                .replace("DAKSHIN", "S").replace("SOUTH", "S").replace("दक्षिण", "S")
                .replace("ISHAN", "NE").replace("AGNI", "SE").replace("NAIRITYA", "SW")
                .replace("VAYAVYA", "NW");
        return switch (s) {
            case "N", "NE", "E", "SE", "S", "SW", "W", "NW" -> s;
            default -> s.length() <= 2 ? s : null;
        };
    }

    public static String directionHi(String dir) {
        if (dir == null) return "—";
        return switch (dir) {
            case "N" -> "उत्तर";
            case "NE" -> "ईशान (उत्तर-पूर्व)";
            case "E" -> "पूर्व";
            case "SE" -> "आग्नेय (दक्षिण-पूर्व)";
            case "S" -> "दक्षिण";
            case "SW" -> "नैऋत्य (दक्षिण-पश्चिम)";
            case "W" -> "पश्चिम";
            case "NW" -> "वायव्य (उत्तर-पश्चिम)";
            default -> dir;
        };
    }

    private boolean isShool(LocalDate d, String dir) {
        int w = d.getDayOfWeek().getValue() % 7;
        String shool = SHOOL[w];
        if (dir == null) return false;
        if (dir.equals(shool)) return true;
        // cardinal shool also blocks neighbouring 45°
        if (shool.equals("E") && (dir.equals("NE") || dir.equals("SE"))) return true;
        if (shool.equals("W") && (dir.equals("NW") || dir.equals("SW"))) return true;
        if (shool.equals("N") && (dir.equals("NE") || dir.equals("NW"))) return true;
        if (shool.equals("S") && (dir.equals("SE") || dir.equals("SW"))) return true;
        return false;
    }

    private String shoolLabel(LocalDate d) {
        int w = d.getDayOfWeek().getValue() % 7;
        return directionHi(SHOOL[w]);
    }

    private int score(Purpose purpose, Map<String, Object> p, String dir, LocalDate d) {
        int s = 5;
        String tithi = String.valueOf(p.get("tithi"));
        String nak = String.valueOf(p.get("nakshatra"));
        String yoga = String.valueOf(p.get("yoga"));
        String vara = String.valueOf(p.get("vara"));
        if (GOOD_TITHI.contains(tithi)) s += 2;
        if (BAD_TITHI.contains(tithi)) s -= 3;
        if (INAUSPICIOUS_YOGA.contains(yoga)) s -= 2;
        else s += 1;
        Set<String> goodNak = switch (purpose) {
            case MARRIAGE -> GOOD_NAK_MARRIAGE;
            case TRAVEL -> GOOD_NAK_TRAVEL;
            case BUSINESS, PROPERTY -> GOOD_NAK_BUSINESS;
            default -> GOOD_NAK_BUSINESS;
        };
        if (goodNak.contains(nak)) s += 3;
        if (purpose == Purpose.MARRIAGE && (vara.equals("Tuesday") || vara.equals("Saturday"))) s -= 2;
        if (purpose == Purpose.BUSINESS && (vara.equals("Wednesday") || vara.equals("Thursday") || vara.equals("Friday"))) s += 1;
        if (purpose == Purpose.MEDICAL && vara.equals("Tuesday")) s -= 1;
        if ("Vishti".equals(String.valueOf(p.get("karana")))) s -= 2;
        if (purpose == Purpose.TRAVEL && dir != null) {
            if (isShool(d, dir)) s -= 6;
            s += directionWeekdayBonus(dir, vara);
        }
        return s;
    }

    private int directionWeekdayBonus(String dir, String vara) {
        return switch (dir) {
            case "E", "NE" -> (vara.equals("Sunday") || vara.equals("Thursday")) ? 3 : 0;
            case "W", "SW" -> (vara.equals("Monday") || vara.equals("Wednesday")) ? 3 : 0;
            case "N", "NW" -> (vara.equals("Wednesday") || vara.equals("Thursday") || vara.equals("Friday")) ? 3 : 0;
            case "S", "SE" -> (vara.equals("Tuesday") || vara.equals("Saturday")) ? 2 : 0;
            default -> vara.equals("Thursday") ? 1 : 0;
        };
    }

    private String reasons(Purpose purpose, Map<String, Object> p, int score, String dir, boolean shool) {
        if (purpose == Purpose.TRAVEL) {
            String base = "यात्रा " + directionHi(dir) + " — अंक " + score + "/12, "
                    + p.get("tithiHi") + " तिथि (तक " + p.get("tithiEnd") + "), "
                    + p.get("nakshatraHi") + " नक्षत्र (तक " + p.get("nakshatraEnd") + "), "
                    + p.get("varaHi") + "।";
            if (shool) {
                return base + " आज दिशा-शूल " + shoolLabelHint(p) + " की ओर है — यह दिशा निषिद्ध।";
            }
            return base + " सूर्योदय के बाद, राहुकाल छोड़कर, अभिजित में प्रस्थान श्रेष्ठ।";
        }
        return purpose.name() + " muhurta scored " + score + "/12 on "
                + p.get("tithi") + " tithi (till " + p.get("tithiEnd") + "), "
                + p.get("nakshatra") + " (till " + p.get("nakshatraEnd") + "). Avoid Rahu Kalam.";
    }

    private String shoolLabelHint(Map<String, Object> p) {
        return String.valueOf(p.get("varaHi"));
    }
}
