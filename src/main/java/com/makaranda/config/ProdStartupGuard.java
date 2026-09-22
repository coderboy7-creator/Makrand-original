package com.makaranda.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Fail closed in prod: JWT from env, H2 console off. */
@Component
@Profile("prod")
public class ProdStartupGuard implements ApplicationListener<ApplicationReadyEvent> {

    private final String jwtSecret;
    private final boolean h2Console;

    public ProdStartupGuard(
            @Value("${makaranda.jwt-secret:}") String jwtSecret,
            @Value("${spring.h2.console.enabled:false}") boolean h2Console) {
        this.jwtSecret = jwtSecret;
        this.h2Console = h2Console;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (jwtSecret == null || jwtSecret.length() < 32
                || jwtSecret.contains("DEV_ONLY") || jwtSecret.contains("CHANGE_ME")) {
            throw new IllegalStateException(
                    "MAKARANDA_JWT_SECRET is required in prod (≥32 chars, not the local placeholder)");
        }
        if (h2Console) {
            throw new IllegalStateException("H2 console must be disabled in prod");
        }
    }
}
