package com.fakenews.detection.service;

import com.fakenews.detection.dto.NewsDetectionResponse;
import com.fakenews.detection.dto.NewsRequest;
import com.fakenews.detection.dto.StatsResponse;
import com.fakenews.detection.entity.User;
import com.fakenews.detection.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NewsDetectionServiceTest {

    @Autowired
    private NewsDetectionService newsDetectionService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setup() {
        testUser = userRepository.findByEmail("tester@college.edu")
                .orElseGet(() -> {
                    User user = new User();
                    user.setName("Test Student");
                    user.setEmail("tester@college.edu");
                    user.setPassword("password123");
                    return userRepository.save(user);
                });
    }

    @Test
    public void testDetectFakeNewsWithSensationalPhrases() {
        String fakeArticle = "SHOCKING! You won't believe this secret miracle cure that doctors hate! Share before it is deleted!!!";
        NewsRequest request = new NewsRequest(testUser.getId(), fakeArticle);

        NewsDetectionResponse response = newsDetectionService.detectAndSave(request);

        assertNotNull(response);
        assertEquals("FAKE", response.getResult());
        assertTrue(response.getConfidence() >= 80, "Fake news confidence should be >= 80%");
        assertFalse(response.getIndicators().isEmpty(), "Indicators should not be empty");
    }

    @Test
    public void testDetectRealNewsWithCrediblePhrases() {
        String realArticle = "According to a study published by researchers at Oxford University, solar energy efficiency reached a new milestone this quarter as reported in the official scientific journal.";
        NewsRequest request = new NewsRequest(testUser.getId(), realArticle);

        NewsDetectionResponse response = newsDetectionService.detectAndSave(request);

        assertNotNull(response);
        assertEquals("REAL", response.getResult());
        assertTrue(response.getConfidence() >= 75, "Real news confidence should be >= 75%");
    }

    @Test
    public void testGetStats() {
        StatsResponse stats = newsDetectionService.getStats(testUser.getId());
        assertNotNull(stats);
        assertTrue(stats.getTotal() >= 0);
    }
}
