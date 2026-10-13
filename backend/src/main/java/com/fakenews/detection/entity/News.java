package com.fakenews.detection.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * News Entity representing news analysis records in the database.
 * Mapped to 'news' table in MySQL.
 */
@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "news_text", columnDefinition = "LONGTEXT", nullable = false)
    private String newsText;

    @Column(nullable = false, length = 20)
    private String result; // "REAL" or "FAKE"

    @Column(nullable = false)
    private Integer confidence; // e.g. 85, 90

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    public News() {
    }

    public News(Long id, Long userId, String newsText, String result, Integer confidence, LocalDateTime detectedAt) {
        this.id = id;
        this.userId = userId;
        this.newsText = newsText;
        this.result = result;
        this.confidence = confidence;
        this.detectedAt = detectedAt;
    }

    @PrePersist
    protected void onDetect() {
        if (this.detectedAt == null) {
            this.detectedAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNewsText() {
        return newsText;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }
}
