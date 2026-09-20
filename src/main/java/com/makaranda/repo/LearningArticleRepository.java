package com.makaranda.repo;

import com.makaranda.domain.LearningArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningArticleRepository extends JpaRepository<LearningArticle, Long> {
    List<LearningArticle> findByPublishedTrue();
    List<LearningArticle> findByCategoryAndPublishedTrue(String category);
    Optional<LearningArticle> findBySlug(String slug);
}
