package com.makaranda.config;

import com.makaranda.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final Environment env;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, Environment env) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.env = env;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/v1/auth/**").permitAll()
                            .requestMatchers("/api/v1/public/**").permitAll()
                            .requestMatchers("/api/v1/jyotish/**").permitAll()
                            .requestMatchers("/api/v1/learn/**").permitAll()
                            .requestMatchers("/api/v1/location/**").permitAll()
                            .requestMatchers("/api/docs/**", "/api/swagger/**", "/v3/api-docs/**").permitAll();
                    if (!env.matchesProfiles("prod")) {
                        auth.requestMatchers("/h2/**").permitAll();
                    }
                    auth.requestMatchers(HttpMethod.GET, "/", "/index.html", "/assets/**", "/static/**",
                                    "/*.js", "/*.css", "/*.svg", "/*.png", "/*.ico", "/manifest.json").permitAll()
                            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                            .requestMatchers("/api/v1/crm/**", "/api/v1/consult/**").authenticated()
                            .anyRequest().permitAll();
                })
                .headers(h -> {
                    if (env.matchesProfiles("prod")) {
                        h.frameOptions(f -> f.deny());
                    } else {
                        h.frameOptions(f -> f.sameOrigin());
                    }
                })
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOriginPatterns(List.of("*"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", c);
        return source;
    }
}
