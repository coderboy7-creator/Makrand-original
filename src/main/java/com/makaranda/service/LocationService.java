package com.makaranda.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * India-first geocoder. Gazetteer for common Indian names (Delhi ≠ Delhi, Ontario),
 * then Nominatim (countrycodes=in). Never invent Darbhanga for an unmatched query.
 */
@Service
public class LocationService {
    private final RestTemplate http = new RestTemplate();
    @Value("${makaranda.nominatim-url}") String nominatim;

    /** KSDS / Makaranda default — अक्षांश २६।३५, देशान्तर ०१।३५ */
    private static final double KSDS_LAT = 26.5833;
    private static final double KSDS_LON = 85.268;
    private static final String KSDS_NAME = "Darbhanga, Bihar, India (KSDS अक्षांश २६।३५)";

    private static final String[][] CITIES = {
            {"darbhanga,दरभंगा", KSDS_NAME, String.valueOf(KSDS_LAT), String.valueOf(KSDS_LON)},
            {"madhubani,मधुबनी", "Madhubani, Bihar, India", "26.3700", "86.0700"},
            {"patna,पटना", "Patna, Bihar, India", "25.5941", "85.1376"},
            {"muzaffarpur,मुजफ्फरपुर", "Muzaffarpur, Bihar, India", "26.1197", "85.3910"},
            {"sitamarhi,सीतामढ़ी", "Sitamarhi, Bihar, India", "26.5887", "85.5010"},
            {"samastipur,समस्तीपुर", "Samastipur, Bihar, India", "25.8630", "85.7811"},
            {"begusarai,बेगूसराय", "Begusarai, Bihar, India", "25.4182", "86.1272"},
            {"bhagalpur,भागलपुर", "Bhagalpur, Bihar, India", "25.2425", "86.9842"},
            {"gaya,गया", "Gaya, Bihar, India", "24.7969", "85.0070"},
            {"purnia,पूर्णिया", "Purnia, Bihar, India", "25.7771", "87.4753"},
            {"hajipur,हाजीपुर", "Hajipur, Bihar, India", "25.6854", "85.2140"},
            {"motihari,मोतिहारी", "Motihari, Bihar, India", "26.6469", "84.9089"},
            {"katihar,कटिहार", "Katihar, Bihar, India", "25.5394", "87.5704"},
            {"saharsa,सहरसा", "Saharsa, Bihar, India", "25.8800", "86.6000"},
            {"chhapra,chapra,छपरा", "Chhapra, Bihar, India", "25.7810", "84.7542"},
            {"delhi,newdelhi,dilli,ncr,दिल्ली,नईदिल्ली", "New Delhi, Delhi, India", "28.6139", "77.2090"},
            {"olddelhi,पुरानीदिल्ली", "Old Delhi, Delhi, India", "28.6562", "77.2410"},
            {"noida", "Noida, Uttar Pradesh, India", "28.5355", "77.3910"},
            {"gurugram,gurgaon,गुरुग्राम,गुड़गाँव", "Gurugram, Haryana, India", "28.4595", "77.0266"},
            {"faridabad,फरीदाबाद", "Faridabad, Haryana, India", "28.4089", "77.3178"},
            {"ghaziabad,गाजियाबाद", "Ghaziabad, Uttar Pradesh, India", "28.6692", "77.4538"},
            {"mumbai,bombay,मुंबई,बम्बई", "Mumbai, Maharashtra, India", "19.0760", "72.8777"},
            {"pune,पुणे", "Pune, Maharashtra, India", "18.5204", "73.8567"},
            {"nagpur,नागपुर", "Nagpur, Maharashtra, India", "21.1458", "79.0882"},
            {"nashik,नासिक", "Nashik, Maharashtra, India", "19.9975", "73.7898"},
            {"kolkata,calcutta,कोलकाता,कलकत्ता", "Kolkata, West Bengal, India", "22.5726", "88.3639"},
            {"howrah,हावड़ा", "Howrah, West Bengal, India", "22.5958", "88.2636"},
            {"siliguri,सिलीगुड़ी", "Siliguri, West Bengal, India", "26.7271", "88.3953"},
            {"chennai,madras,चेन्नई,मद्रास", "Chennai, Tamil Nadu, India", "13.0827", "80.2707"},
            {"bengaluru,bangalore,बेंगलुरु,बैंगलोर", "Bengaluru, Karnataka, India", "12.9716", "77.5946"},
            {"mysuru,mysore,मैसूर", "Mysuru, Karnataka, India", "12.2958", "76.6394"},
            {"hyderabad,हैदराबाद", "Hyderabad, Telangana, India", "17.3850", "78.4867"},
            {"ahmedabad,अहमदाबाद", "Ahmedabad, Gujarat, India", "23.0225", "72.5714"},
            {"surat,सूरत", "Surat, Gujarat, India", "21.1702", "72.8311"},
            {"vadodara,baroda,वडोदरा", "Vadodara, Gujarat, India", "22.3072", "73.1812"},
            {"jaipur,जयपुर", "Jaipur, Rajasthan, India", "26.9124", "75.7873"},
            {"jodhpur,जोधपुर", "Jodhpur, Rajasthan, India", "26.2389", "73.0243"},
            {"udaipur,उदयपुर", "Udaipur, Rajasthan, India", "24.5854", "73.7125"},
            {"lucknow,लखनऊ", "Lucknow, Uttar Pradesh, India", "26.8467", "80.9462"},
            {"kanpur,कानपुर", "Kanpur, Uttar Pradesh, India", "26.4499", "80.3319"},
            {"varanasi,banaras,kashi,वाराणसी,बनारस,काशी", "Varanasi, Uttar Pradesh, India", "25.3176", "82.9739"},
            {"prayagraj,allahabad,प्रयागराज,इलाहाबाद", "Prayagraj, Uttar Pradesh, India", "25.4358", "81.8463"},
            {"agra,आगरा", "Agra, Uttar Pradesh, India", "27.1767", "78.0081"},
            {"meerut,मेरठ", "Meerut, Uttar Pradesh, India", "28.9845", "77.7064"},
            {"ranchi,राँची", "Ranchi, Jharkhand, India", "23.3441", "85.3096"},
            {"jamshedpur,जमशेदपुर", "Jamshedpur, Jharkhand, India", "22.8046", "86.2029"},
            {"bhubaneswar,भुवनेश्वर", "Bhubaneswar, Odisha, India", "20.2961", "85.8245"},
            {"cuttack,कटक", "Cuttack, Odisha, India", "20.4625", "85.8830"},
            {"guwahati,गुवाहाटी", "Guwahati, Assam, India", "26.1445", "91.7362"},
            {"chandigarh,चंडीगढ़", "Chandigarh, India", "30.7333", "76.7794"},
            {"amritsar,अमृतसर", "Amritsar, Punjab, India", "31.6340", "74.8723"},
            {"ludhiana,लुधियाना", "Ludhiana, Punjab, India", "30.9010", "75.8573"},
            {"shimla,शिमला", "Shimla, Himachal Pradesh, India", "31.1048", "77.1734"},
            {"dehradun,देहरादून", "Dehradun, Uttarakhand, India", "30.3165", "78.0322"},
            {"srinagar,श्रीनगर", "Srinagar, Jammu and Kashmir, India", "34.0837", "74.7973"},
            {"jammu,जम्मू", "Jammu, Jammu and Kashmir, India", "32.7266", "74.8570"},
            {"bhopal,भोपाल", "Bhopal, Madhya Pradesh, India", "23.2599", "77.4126"},
            {"indore,इंदौर", "Indore, Madhya Pradesh, India", "22.7196", "75.8577"},
            {"gwalior,ग्वालियर", "Gwalior, Madhya Pradesh, India", "26.2183", "78.1828"},
            {"raipur,रायपुर", "Raipur, Chhattisgarh, India", "21.2514", "81.6296"},
            {"kochi,cochin,कोच्चि", "Kochi, Kerala, India", "9.9312", "76.2673"},
            {"thiruvananthapuram,trivandrum,तिरुवनंतपुरम", "Thiruvananthapuram, Kerala, India", "8.5241", "76.9366"},
            {"coimbatore,कोयंबटूर", "Coimbatore, Tamil Nadu, India", "11.0168", "76.9558"},
            {"madurai,मदुरै", "Madurai, Tamil Nadu, India", "9.9252", "78.1198"},
            {"visakhapatnam,vizag,विशाखापत्तनम", "Visakhapatnam, Andhra Pradesh, India", "17.6868", "83.2185"},
            {"vijayawada,विजयवाड़ा", "Vijayawada, Andhra Pradesh, India", "16.5062", "80.6480"},
            {"goa,panaji,पणजी,गोवा", "Panaji, Goa, India", "15.4909", "73.8278"},
            {"ujjain,उज्जैन", "Ujjain, Madhya Pradesh, India", "23.1765", "75.7885"},
            {"haridwar,हरिद्वार", "Haridwar, Uttarakhand, India", "29.9457", "78.1642"},
            {"rishikesh,ऋषिकेश", "Rishikesh, Uttarakhand, India", "30.0869", "78.2676"},
            {"ayodhya,अयोध्या", "Ayodhya, Uttar Pradesh, India", "26.7922", "82.1998"},
            {"mathura,मथुरा", "Mathura, Uttar Pradesh, India", "27.4924", "77.6737"},
            {"thane,ठाणे", "Thane, Maharashtra, India", "19.2183", "72.9781"},
            {"navi mumbai,नवी मुंबई", "Navi Mumbai, Maharashtra, India", "19.0330", "73.0297"},
            {"kalyan,कल्याण", "Kalyan, Maharashtra, India", "19.2403", "73.1305"},
            {"pimpri,पिंपरी", "Pimpri-Chinchwad, Maharashtra, India", "18.6298", "73.7997"},
            {"rajkot,राजकोट", "Rajkot, Gujarat, India", "22.3039", "70.8022"},
            {"bhavnagar,भावनगर", "Bhavnagar, Gujarat, India", "21.7645", "72.1519"},
            {"jamnagar,जामनगर", "Jamnagar, Gujarat, India", "22.4707", "70.0577"},
            {"jalandhar,जालंधर", "Jalandhar, Punjab, India", "31.3260", "75.5762"},
            {"patiala,पटियाला", "Patiala, Punjab, India", "30.3398", "76.3869"},
            {"ambala,अंबाला", "Ambala, Haryana, India", "30.3782", "76.7767"},
            {"panipat,पानीपत", "Panipat, Haryana, India", "29.3909", "76.9635"},
            {"rohtak,रोहतक", "Rohtak, Haryana, India", "28.8955", "76.6066"},
            {"hisar,हिसार", "Hisar, Haryana, India", "29.1492", "75.7217"},
            {"sonipat,सोनीपत", "Sonipat, Haryana, India", "28.9931", "77.0151"},
            {"karnal,करनाल", "Karnal, Haryana, India", "29.6857", "76.9905"},
            {"moradabad,मुरादाबाद", "Moradabad, Uttar Pradesh, India", "28.8386", "78.7733"},
            {"aligarh,अलीगढ़", "Aligarh, Uttar Pradesh, India", "27.8974", "78.0880"},
            {"bareilly,बरेली", "Bareilly, Uttar Pradesh, India", "28.3670", "79.4304"},
            {"gorakhpur,गोरखपुर", "Gorakhpur, Uttar Pradesh, India", "26.7606", "83.3732"},
            {"dhanbad,धनबाद", "Dhanbad, Jharkhand, India", "23.7957", "86.4304"},
            {"bokaro,बोकारो", "Bokaro, Jharkhand, India", "23.6693", "86.1511"},
            {"durgapur,दुर्गापुर", "Durgapur, West Bengal, India", "23.5204", "87.3119"},
            {"asansol,आसनसोल", "Asansol, West Bengal, India", "23.6739", "86.9524"},
            {"warangal,वारंगल", "Warangal, Telangana, India", "17.9689", "79.5941"},
            {"tirupati,तिरुपति", "Tirupati, Andhra Pradesh, India", "13.6288", "79.4192"},
            {"hubli,hubballi,हुबली", "Hubballi, Karnataka, India", "15.3647", "75.1240"},
            {"mangaluru,mangalore,मंगलुरु", "Mangaluru, Karnataka, India", "12.9141", "74.8560"},
            {"kozhikode,calicut,कोझिकोड", "Kozhikode, Kerala, India", "11.2588", "75.7804"},
            {"thrissur,त्रिशूर", "Thrissur, Kerala, India", "10.5276", "76.2144"},
    };

    public List<Map<String, Object>> search(String q) {
        if (q == null || q.isBlank()) {
            return List.of(place(KSDS_NAME, KSDS_LAT, KSDS_LON));
        }
        String nq = norm(q);
        if (nq.isEmpty()) {
            return List.of();
        }
        List<Map<String, Object>> exact = new ArrayList<>();
        List<Map<String, Object>> prefix = new ArrayList<>();
        List<Map<String, Object>> loose = new ArrayList<>();
        for (String[] row : CITIES) {
            int rank = gazetteerRank(row[0], row[1], nq);
            if (rank < 0) continue;
            Map<String, Object> p = place(row[1], Double.parseDouble(row[2]), Double.parseDouble(row[3]));
            if (rank == 0) exact.add(p);
            else if (rank == 1) prefix.add(p);
            else loose.add(p);
        }
        List<Map<String, Object>> out = new ArrayList<>();
        addUnique(out, exact);
        addUnique(out, prefix);
        addUnique(out, loose);

        if (nq.length() >= 3) {
            addUnique(out, nominatimIndia(q));
            if (out.size() < 3) {
                addUnique(out, nominatimIndia(q + ", India"));
            }
        }
        if (out.size() > 12) return out.subList(0, 12);
        return out;
    }

    /** 0 exact, 1 prefix, 2 contains, -1 none. Display-name "India" is never a match by itself. */
    static int gazetteerRank(String aliases, String display, String nq) {
        boolean exact = false, start = false, mid = false;
        for (String a : aliases.split(",")) {
            String na = norm(a);
            if (na.isEmpty()) continue;
            if (na.equals(nq)) exact = true;
            else if (na.startsWith(nq) && nq.length() >= 2) start = true;
            else if (nq.length() >= 4 && na.length() >= 4 && (na.contains(nq) || nq.contains(na))) mid = true;
        }
        String city = norm(display.contains(",") ? display.substring(0, display.indexOf(',')) : display);
        if (city.equals(nq)) exact = true;
        else if (nq.length() >= 2 && city.startsWith(nq)) start = true;
        if (exact) return 0;
        if (start) return 1;
        if (mid) return 2;
        return -1;
    }

    private List<Map<String, Object>> nominatimIndia(String q) {
        try {
            String url = UriComponentsBuilder.fromUriString(nominatim + "/search")
                    .queryParam("q", q)
                    .queryParam("format", "json")
                    .queryParam("addressdetails", "1")
                    .queryParam("limit", "8")
                    .queryParam("countrycodes", "in")
                    .queryParam("accept-language", "en")
                    .build()
                    .encode()
                    .toUriString();
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "MakarandaJyotish/1.0 (Mithilanchal astrology; contact makaranda.app)");
            headers.set("Accept-Language", "en");
            ResponseEntity<List<Map<String, Object>>> resp = http.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {});
            List<Map<String, Object>> out = new ArrayList<>();
            if (resp.getBody() == null) return out;
            for (Map<String, Object> raw : resp.getBody()) {
                Object cc = null;
                Object addr = raw.get("address");
                if (addr instanceof Map<?, ?> am) cc = am.get("country_code");
                String display = String.valueOf(raw.get("display_name"));
                boolean india = "in".equalsIgnoreCase(String.valueOf(cc))
                        || display.toLowerCase(Locale.ROOT).contains("india");
                if (!india) continue;
                out.add(place(display,
                        Double.parseDouble(String.valueOf(raw.get("lat"))),
                        Double.parseDouble(String.valueOf(raw.get("lon")))));
            }
            return out;
        } catch (Exception e) {
            return List.of();
        }
    }

    private static void addUnique(List<Map<String, Object>> list, List<Map<String, Object>> more) {
        for (Map<String, Object> n : more) {
            if (!already(list, n)) list.add(n);
        }
    }

    private static boolean already(List<Map<String, Object>> list, Map<String, Object> n) {
        String name = String.valueOf(n.get("displayName"));
        double lat = toD(n.get("lat"));
        double lon = toD(n.get("lon"));
        for (Map<String, Object> m : list) {
            if (String.valueOf(m.get("displayName")).equalsIgnoreCase(name)) return true;
            if (Math.abs(toD(m.get("lat")) - lat) < 0.08 && Math.abs(toD(m.get("lon")) - lon) < 0.08) return true;
        }
        return false;
    }

    private static double toD(Object o) {
        try { return Double.parseDouble(String.valueOf(o)); } catch (Exception e) { return 0; }
    }

    private static Map<String, Object> place(String name, double lat, double lon) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("displayName", name);
        m.put("lat", lat);
        m.put("lon", lon);
        m.put("tz", "Asia/Kolkata");
        return m;
    }

    static String norm(String s) {
        String t = Normalizer.normalize(s, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT).trim();
        return t.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]+", "");
    }
}
