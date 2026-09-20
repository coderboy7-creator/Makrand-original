package com.makaranda.web;

import com.makaranda.domain.AppSetting;
import com.makaranda.domain.LearningArticle;
import com.makaranda.domain.User;
import com.makaranda.repo.AppSettingRepository;
import com.makaranda.repo.ConsultationRepository;
import com.makaranda.repo.LearningArticleRepository;
import com.makaranda.repo.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final UserRepository users;
    private final ConsultationRepository bookings;
    private final AppSettingRepository settings;
    private final LearningArticleRepository articles;

    public AdminController(UserRepository users, ConsultationRepository bookings,
                           AppSettingRepository settings, LearningArticleRepository articles) {
        this.users = users;
        this.bookings = bookings;
        this.settings = settings;
        this.articles = articles;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("users", users.count());
        m.put("astrologers", users.findByRole(User.Role.ASTROLOGER).size());
        m.put("consultations", bookings.count());
        m.put("articles", articles.count());
        m.put("recentBookings", bookings.findAll().stream().limit(20).toList());
        return m;
    }

    @GetMapping("/users")
    public Object users() { return users.findAll(); }

    @PostMapping("/users/{id}/active")
    public User toggle(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        User u = users.findById(id).orElseThrow();
        u.setActive(body.getOrDefault("active", true));
        return users.save(u);
    }

    @GetMapping("/settings")
    public Object settings() { return settings.findAll(); }

    @PostMapping("/settings")
    public AppSetting saveSetting(@RequestBody AppSetting s) {
        return settings.save(s);
    }

    @PostMapping("/articles")
    public LearningArticle saveArticle(@RequestBody LearningArticle a) {
        return articles.save(a);
    }

    @DeleteMapping("/articles/{id}")
    public void deleteArticle(@PathVariable Long id) {
        articles.deleteById(id);
    }
}
