package com.fakenews.detection.controller;

import com.fakenews.detection.dto.NewsDetectionResponse;
import com.fakenews.detection.dto.NewsRequest;
import com.fakenews.detection.dto.StatsResponse;
import com.fakenews.detection.service.NewsDetectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing endpoints for News Detection analysis and Dashboard Statistics.
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsDetectionService newsDetectionService;

    @Autowired
    public NewsController(NewsDetectionService newsDetectionService) {
        this.newsDetectionService = newsDetectionService;
    }

    /**
     * POST /api/news/detect
     * Analyzes submitted news text and returns the detection verdict (REAL/FAKE).
     */
    @PostMapping("/detect")
    public ResponseEntity<NewsDetectionResponse> detectNews(@Valid @RequestBody NewsRequest request) {
        NewsDetectionResponse response = newsDetectionService.detectAndSave(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/news/stats/{userId}
     * Returns total checked, fake count, and real count for a specific user.
     */
    @GetMapping("/stats/{userId}")
    public ResponseEntity<StatsResponse> getStats(@PathVariable Long userId) {
        StatsResponse stats = newsDetectionService.getStats(userId);
        return ResponseEntity.ok(stats);
    }
}
