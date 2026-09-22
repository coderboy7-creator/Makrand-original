package com.makaranda.security;

import com.makaranda.domain.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void rejectsShortSecret() {
        assertThrows(IllegalArgumentException.class, () -> new JwtService("too-short", 72));
    }

    @Test
    void issuesAndParsesWithLongSecret() {
        JwtService jwt = new JwtService("CHANGE_ME_DEV_ONLY_MakarandaLocalHs256Key32!!", 72);
        User u = new User();
        u.setId(1L);
        u.setEmail("admin@makaranda.app");
        u.setName("Test");
        u.setRole(User.Role.ADMIN);
        String token = jwt.issue(u);
        assertTrue(token.split("\\.").length == 3);
        assertTrue("admin@makaranda.app".equals(jwt.parse(token).getSubject()));
    }
}
