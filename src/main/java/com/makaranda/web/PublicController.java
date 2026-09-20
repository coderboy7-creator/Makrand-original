package com.makaranda.web;

import com.makaranda.domain.User;
import com.makaranda.calc.ephemeris.AyanamsaSystem;
import com.makaranda.calc.ephemeris.PanchangMode;
import com.makaranda.repo.UserRepository;
import com.makaranda.service.AuthService;
import com.makaranda.service.JyotishService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {
    private final JyotishService jyotish;
    private final UserRepository users;

    @Value("${makaranda.default-place}") String place;
    @Value("${makaranda.default-lat}") double lat;
    @Value("${makaranda.default-lon}") double lon;
    @Value("${makaranda.default-ayanamsa}") String ayanamsa;
    @Value("${makaranda.default-panchang-mode}") String mode;

    public PublicController(JyotishService jyotish, UserRepository users) {
        this.jyotish = jyotish;
        this.users = users;
    }

    @GetMapping("/astrologers")
    public Object astrologers() {
        return users.findByRoleAndActiveTrue(User.Role.ASTROLOGER).stream().map(AuthService::publicUser).toList();
    }

    @GetMapping("/config")
    public Map<String, Object> config() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", "Makaranda Jyotish");
        m.put("tagline", "Surya Siddhanta • Makaranda Panchang • Mithilanchal");
        m.put("defaultPlace", place);
        m.put("defaultLat", lat);
        m.put("defaultLon", lon);
        m.put("defaultTz", "Asia/Kolkata");
        m.put("defaultAyanamsa", ayanamsa);
        m.put("defaultPanchangMode", mode);
        m.put("ayanamsas", jyotish.ayanamsaCatalog());
        m.put("panchangModes", new Object[]{
                Map.of("id", PanchangMode.SIDDHANTIC.name(), "label", "Siddhantic / Makaranda (default)"),
                Map.of("id", PanchangMode.DRIK.name(), "label", "Drik / Apparent (observational)")
        });
        m.put("houseSystems", new String[]{"WHOLE_SIGN", "EQUAL", "SRIPATI"});
        m.put("chartStyles", new String[]{"NORTH", "SOUTH", "EAST"});
        m.put("serverTime", Instant.now().toString());
        return m;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        double ay = AyanamsaSystem.SURYA_SIDDHANTA_MAKARANDA.ayanamsa(2460000.5);
        return Map.of("status", "ok", "engine", "makaranda-1.0", "sampleAyanamsa", ay);
    }
}
