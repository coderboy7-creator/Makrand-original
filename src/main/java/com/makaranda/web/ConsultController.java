package com.makaranda.web;

import com.makaranda.domain.Consultation;
import com.makaranda.domain.User;
import com.makaranda.repo.ConsultationRepository;
import com.makaranda.repo.UserRepository;
import com.makaranda.service.AuthService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consult")
public class ConsultController {
    private final ConsultationRepository bookings;
    private final UserRepository users;

    public ConsultController(ConsultationRepository bookings, UserRepository users) {
        this.bookings = bookings;
        this.users = users;
    }

    @GetMapping("/astrologers")
    public List<Map<String, Object>> astrologers() {
        return users.findByRoleAndActiveTrue(User.Role.ASTROLOGER).stream().map(AuthService::publicUser).toList();
    }

    @GetMapping("/mine")
    public List<Consultation> mine(@AuthenticationPrincipal User user) {
        if (user.getRole() == User.Role.ASTROLOGER || user.getRole() == User.Role.ADMIN) {
            return bookings.findByAstrologerIdOrderBySlotStartDesc(user.getId());
        }
        return bookings.findByClientUserIdOrderBySlotStartDesc(user.getId());
    }

    @PostMapping("/book")
    public Consultation book(@AuthenticationPrincipal User user, @RequestBody Consultation body) {
        body.setClientUserId(user.getId());
        body.setStatus(Consultation.Status.PENDING);
        if (body.getSlotStart() == null) body.setSlotStart(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0));
        if (body.getSlotEnd() == null) body.setSlotEnd(body.getSlotStart().plusMinutes(30));
        User astro = users.findById(body.getAstrologerId()).orElseThrow();
        body.setAmountInr(astro.getConsultationFeeInr());
        body.setMeetUrl("https://meet.jit.si/makaranda-" + UUID.randomUUID().toString().substring(0, 8));
        return bookings.save(body);
    }

    @PostMapping("/{id}/pay")
    public Consultation pay(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody Map<String, String> body) {
        Consultation c = bookings.findById(id).orElseThrow();
        assertParty(user, c);
        c.setPaymentRef(body.getOrDefault("paymentRef", "MOCK-" + UUID.randomUUID().toString().substring(0, 8)));
        c.setStatus(Consultation.Status.PAID);
        c.setStatus(Consultation.Status.CONFIRMED);
        return bookings.save(c);
    }

    @PostMapping("/{id}/status")
    public Consultation status(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody Map<String, String> body) {
        Consultation c = bookings.findById(id).orElseThrow();
        assertParty(user, c);
        c.setStatus(Consultation.Status.valueOf(body.get("status")));
        if (body.containsKey("notes")) c.setNotes(body.get("notes"));
        return bookings.save(c);
    }

    private static void assertParty(User user, Consultation c) {
        if (user.getRole() == User.Role.ADMIN) return;
        if (user.getId().equals(c.getClientUserId()) || user.getId().equals(c.getAstrologerId())) return;
        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Not your consultation");
    }
}
