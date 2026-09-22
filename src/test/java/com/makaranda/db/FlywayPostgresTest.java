package com.makaranda.db;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Real PostgreSQL (embedded) + Flyway V1. Not H2. */
class FlywayPostgresTest {

    @Test
    void flywayMigratesUsersOnPostgres() throws Exception {
        try (EmbeddedPostgres pg = EmbeddedPostgres.builder().start()) {
            Flyway flyway = Flyway.configure()
                    .dataSource(pg.getPostgresDatabase())
                    .locations("classpath:db/migration")
                    .load();
            flyway.migrate();
            try (Connection c = pg.getPostgresDatabase().getConnection();
                 Statement s = c.createStatement()) {
                try (ResultSet tables = s.executeQuery(
                        "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='public' AND table_name='users'")) {
                    assertTrue(tables.next());
                    assertEquals(1, tables.getInt(1));
                }
                try (ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM users")) {
                    assertTrue(rs.next());
                    assertEquals(0, rs.getInt(1));
                }
                s.executeUpdate("INSERT INTO users (email, password_hash, name, role) "
                        + "VALUES ('a@b.c', 'x', 'A', 'CLIENT')");
                try (ResultSet rs = s.executeQuery("SELECT email FROM users")) {
                    assertTrue(rs.next());
                    assertEquals("a@b.c", rs.getString(1));
                }
            }
        }
    }
}
