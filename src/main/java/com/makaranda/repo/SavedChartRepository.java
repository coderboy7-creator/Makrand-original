package com.makaranda.repo;

import com.makaranda.domain.SavedChart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedChartRepository extends JpaRepository<SavedChart, Long> {
    List<SavedChart> findByUserIdOrderByCreatedAtDesc(Long userId);
}
