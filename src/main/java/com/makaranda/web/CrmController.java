package com.makaranda.web;

import com.makaranda.domain.ClientProfile;
import com.makaranda.domain.SavedChart;
import com.makaranda.domain.User;
import com.makaranda.repo.ClientProfileRepository;
import com.makaranda.repo.SavedChartRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/crm")
public class CrmController {
    private final ClientProfileRepository clients;
    private final SavedChartRepository charts;

    public CrmController(ClientProfileRepository clients, SavedChartRepository charts) {
        this.clients = clients;
        this.charts = charts;
    }

    @GetMapping("/clients")
    public List<ClientProfile> list(@AuthenticationPrincipal User user) {
        if (user.getRole() == User.Role.ASTROLOGER || user.getRole() == User.Role.ADMIN) {
            return clients.findByAstrologerIdOrderByUpdatedAtDesc(user.getId());
        }
        return clients.findByOwnerUserIdOrderByUpdatedAtDesc(user.getId());
    }

    @PostMapping("/clients")
    public ClientProfile save(@AuthenticationPrincipal User user, @RequestBody ClientProfile body) {
        if (body.getId() != null) {
            ClientProfile existing = clients.findById(body.getId()).orElseThrow();
            assertOwns(user, existing);
            body.setCreatedAt(existing.getCreatedAt());
            body.setOwnerUserId(existing.getOwnerUserId());
            body.setAstrologerId(existing.getAstrologerId());
        } else {
            body.setOwnerUserId(user.getId());
            if (user.getRole() == User.Role.ASTROLOGER) body.setAstrologerId(user.getId());
        }
        body.setUpdatedAt(Instant.now());
        return clients.save(body);
    }

    @DeleteMapping("/clients/{id}")
    public void delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        ClientProfile existing = clients.findById(id).orElseThrow();
        assertOwns(user, existing);
        clients.deleteById(id);
    }

    private static void assertOwns(User user, ClientProfile c) {
        if (user.getRole() == User.Role.ADMIN) return;
        boolean astro = user.getRole() == User.Role.ASTROLOGER && user.getId().equals(c.getAstrologerId());
        boolean owner = user.getId().equals(c.getOwnerUserId());
        if (!astro && !owner) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "Not your client");
        }
    }

    @GetMapping("/charts")
    public List<SavedChart> charts(@AuthenticationPrincipal User user) {
        return charts.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @PostMapping("/charts")
    public SavedChart saveChart(@AuthenticationPrincipal User user, @RequestBody SavedChart body) {
        body.setUserId(user.getId());
        return charts.save(body);
    }
}
