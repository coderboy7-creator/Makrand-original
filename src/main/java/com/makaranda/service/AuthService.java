package com.makaranda.service;

import com.makaranda.domain.User;
import com.makaranda.repo.UserRepository;
import com.makaranda.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public Map<String, Object> register(String name, String email, String password, User.Role role) {
        if (users.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        User u = new User();
        u.setName(name);
        u.setEmail(email.toLowerCase().trim());
        u.setPasswordHash(encoder.encode(password));
        u.setRole(role == null ? User.Role.CLIENT : role);
        users.save(u);
        return tokenPayload(u);
    }

    public Map<String, Object> login(String email, String password) {
        User u = users.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!encoder.matches(password, u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        if (!u.isActive()) throw new IllegalArgumentException("Account disabled");
        return tokenPayload(u);
    }

    public Map<String, Object> tokenPayload(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("token", jwt.issue(u));
        m.put("user", publicUser(u));
        return m;
    }

    public static Map<String, Object> publicUser(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("email", u.getEmail());
        m.put("name", u.getName());
        m.put("role", u.getRole().name());
        m.put("phone", u.getPhone());
        m.put("bio", u.getBio());
        m.put("experienceYears", u.getExperienceYears());
        m.put("rating", u.getRating());
        m.put("consultationFeeInr", u.getConsultationFeeInr());
        return m;
    }
}
