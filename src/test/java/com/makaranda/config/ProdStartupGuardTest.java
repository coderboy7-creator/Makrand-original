package com.makaranda.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProdStartupGuardTest {

    @Test
    void rejectsDevPlaceholder() {
        ProdStartupGuard g = new ProdStartupGuard("CHANGE_ME_DEV_ONLY_MakarandaLocalHs256Key32!!", false);
        assertThrows(IllegalStateException.class, () -> g.onApplicationEvent((ApplicationReadyEvent) null));
    }

    @Test
    void rejectsH2Console() {
        ProdStartupGuard g = new ProdStartupGuard("prod-secret-must-be-at-least-32-bytes!!", true);
        assertThrows(IllegalStateException.class, () -> g.onApplicationEvent((ApplicationReadyEvent) null));
    }

    @Test
    void acceptsEnvSecretWithConsoleOff() {
        ProdStartupGuard g = new ProdStartupGuard("prod-secret-must-be-at-least-32-bytes!!", false);
        assertDoesNotThrow(() -> g.onApplicationEvent((ApplicationReadyEvent) null));
    }
}
