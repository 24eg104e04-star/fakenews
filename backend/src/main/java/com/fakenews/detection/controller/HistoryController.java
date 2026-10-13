package com.fakenews.detection.controller;

import com.fakenews.detection.entity.News;
import com.fakenews.detection.service.NewsDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller exposing endpoints for retrieving and managing user news detection history.
 */
@RestController
@RequestMapping("/api/news/history")
public class HistoryController {

    private final NewsDetectionService newsDetectionService;

    @Autowired
    public HistoryController(NewsDetectionService newsDetectionService) {
        this.newsDetectionService = newsDetectionService;
    }

    /**
     * GET /api/news/history/{userId}
     * Returns all news articles analyzed by the given user, sorted by date (newest first).
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<News>> getUserHistory(@PathVariable Long userId) {
        List<News> history = newsDetectionService.getNewsHistory(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * DELETE /api/news/history/{id}
     * Deletes a specific history record by its ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteHistoryItem(@PathVariable Long id) {
        newsDetectionService.deleteNews(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "History record deleted successfully");
        return ResponseEntity.ok(response);
    }
}
