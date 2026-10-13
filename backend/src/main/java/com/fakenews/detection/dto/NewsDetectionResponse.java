package com.fakenews.detection.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Fake News Detection result.
 */
public class NewsDetectionResponse {

    private Long id;
    private Long userId;
    private String newsText;
    private String result; // "REAL" or "FAKE"
    private Integer confidence; // 0 - 100 percentage
    private String message;
    private LocalDateTime detectedAt;
    private List<String> indicators;
    private String analysisNote;

    public NewsDetectionResponse() {
    }

    public NewsDetectionResponse(Long id, Long userId, String newsText, String result,
                                 Integer confidence, String message, LocalDateTime detectedAt,
                                 List<String> indicators, String analysisNote) {
        this.id = id;
        this.userId = userId;
        this.newsText = newsText;
        this.result = result;
        this.confidence = confidence;
        this.message = message;
        this.detectedAt = detectedAt;
        this.indicators = indicators;
        this.analysisNote = analysisNote;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public List<String> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<String> indicators) {
        this.indicators = indicators;
    }

    public String getAnalysisNote() {
        return analysisNote;
    }

    public void setAnalysisNote(String analysisNote) {
        this.analysisNote = analysisNote;
    }
}
