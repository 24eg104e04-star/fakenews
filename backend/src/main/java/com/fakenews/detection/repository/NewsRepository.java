package com.fakenews.detection.repository;

import com.fakenews.detection.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA Repository for News entity operations.
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findByUserIdOrderByDetectedAtDesc(Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndResult(Long userId, String result);

    void deleteByUserId(Long userId);
}
