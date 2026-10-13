package com.fakenews.detection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for News Detection submission.
 */
public class NewsRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "News article text cannot be empty")
    @Size(min = 10, message = "News article must be at least 10 characters long to analyze")
    private String newsText;

    public NewsRequest() {
    }

    public NewsRequest(Long userId, String newsText) {
        this.userId = userId;
        this.newsText = newsText;
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
        this.newsText = newsText != null ? newsText.trim() : null;
    }
}
