package com.makaranda.web;

import com.makaranda.domain.LearningArticle;
import com.makaranda.repo.LearningArticleRepository;
import com.makaranda.service.JyotishService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/learn")
public class LearnController {
    private final JyotishService jyotish;
    private final LearningArticleRepository articles;

    public LearnController(JyotishService jyotish, LearningArticleRepository articles) {
        this.jyotish = jyotish;
        this.articles = articles;
    }

    @GetMapping("/encyclopedia")
    public Map<String, Object> encyclopedia() {
        return jyotish.encyclopedia();
    }

    @GetMapping("/articles")
    public Object articles(@RequestParam(required = false) String category) {
        if (category == null || category.isBlank()) return articles.findByPublishedTrue();
        return articles.findByCategoryAndPublishedTrue(category);
    }

    @GetMapping("/articles/{slug}")
    public LearningArticle article(@PathVariable String slug) {
        return articles.findBySlug(slug).orElseThrow();
    }
}
