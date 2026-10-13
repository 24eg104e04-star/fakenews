package com.fakenews.detection.service;

import com.fakenews.detection.dto.NewsDetectionResponse;
import com.fakenews.detection.dto.NewsRequest;
import com.fakenews.detection.dto.StatsResponse;
import com.fakenews.detection.entity.News;
import com.fakenews.detection.exception.ResourceNotFoundException;
import com.fakenews.detection.repository.NewsRepository;
import com.fakenews.detection.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for Fake News Detection.
 * Implements an isolated, explainable rule-based heuristic scoring engine.
 * Architecture is designed to easily plug in a Machine Learning / Python model later.
 */
@Service
public class NewsDetectionService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    // High-risk sensational / clickbait keywords and phrases
    private static final List<String> SUSPICIOUS_PHRASES = Arrays.asList(
            "shocking",
            "breaking news",
            "breaking",
            "miracle",
            "100% guaranteed",
            "secret",
            "you won't believe",
            "you will not believe",
            "urgent",
            "viral",
            "conspiracy",
            "cure-all",
            "unbelievable truth",
            "banned by doctors",
            "hidden secret",
            "exposed",
            "illuminati",
            "act now",
            "share before deleted",
            "share this before it's deleted",
            "what they don't want you to know",
            "doctors hate this",
            "mind-blowing",
            "magic cure",
            "aliens confirmed",
            "instant wealth",
            "lottery trick",
            "wake up sheeple"
    );

    // Journalistic and credible attribution markers (counter-indicators)
    private static final List<String> CREDIBLE_PHRASES = Arrays.asList(
            "according to",
            "spokesperson",
            "study published",
            "researchers at",
            "official report",
            "reuters",
            "associated press",
            "bbc",
            "peer-reviewed",
            "department of",
            "ministry of",
            "university",
            "scientific journal",
            "press release",
            "stated in an interview",
            "confirmed by authorities"
    );

    @Autowired
    public NewsDetectionService(NewsRepository newsRepository, UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
    }

    /**
     * Analyzes news article text using heuristic rules, saves the detection to MySQL,
     * and returns the detection result.
     */
    public NewsDetectionResponse detectAndSave(NewsRequest request) {
        // Validate user existence
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        String text = request.getNewsText().trim();
        String lowerText = text.toLowerCase();

        List<String> detectedIndicators = new ArrayList<>();
        int suspiciousScore = 0;
        int credibleScore = 0;

        // Rule 1: Check Suspicious Keywords and Phrases
        for (String phrase : SUSPICIOUS_PHRASES) {
            if (lowerText.contains(phrase)) {
                suspiciousScore++;
                detectedIndicators.add("Suspicious keyword/phrase detected: '" + phrase + "'");
            }
        }

        // Rule 2: Excessive Exclamation Marks and Question Marks
        Pattern excessivePunctuation = Pattern.compile("(!{2,}|\\?{2,}|\\${2,})");
        Matcher punctMatcher = excessivePunctuation.matcher(text);
        if (punctMatcher.find()) {
            suspiciousScore += 2;
            detectedIndicators.add("Excessive sensational punctuation (e.g. '!!', '??', '$$')");
        } else if (text.contains("!")) {
            suspiciousScore += 1;
            detectedIndicators.add("Emotional exclamation mark used in article");
        }

        // Rule 3: Check Excessive Capitalization (SHOUTING)
        String[] words = text.split("\\s+");
        int upperCaseWords = 0;
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z]", "");
            if (cleanWord.length() >= 3 && cleanWord.equals(cleanWord.toUpperCase())) {
                upperCaseWords++;
            }
        }
        if (words.length > 5 && ((double) upperCaseWords / words.length) > 0.20) {
            suspiciousScore += 2;
            detectedIndicators.add("Excessive ALL-CAPS words detected (" + upperCaseWords + " words)");
        }

        // Rule 4: Content Length Check
        if (words.length < 20) {
            suspiciousScore += 1;
            detectedIndicators.add("Brief news text (" + words.length + " words); lacks comprehensive factual context");
        }

        // Rule 5: Credible Journalistic Markers (Mitigating factors)
        for (String phrase : CREDIBLE_PHRASES) {
            if (lowerText.contains(phrase)) {
                credibleScore++;
                detectedIndicators.add("Credible source attribution found: '" + phrase + "'");
            }
        }

        // Decision logic
        // Rule threshold: 0-2 suspicious indicators -> REAL; 3 or more -> FAKE
        String result;
        int confidence;
        String message;

        int netSuspicion = suspiciousScore - (credibleScore / 2);

        if (netSuspicion >= 3 || (suspiciousScore >= 2 && credibleScore == 0 && text.contains("!"))) {
            result = "FAKE";
            // Confidence calculation between 80% and 96%
            confidence = Math.min(96, 80 + (netSuspicion * 3));
            message = "The news appears to be FAKE based on sensational keywords, emotional phrasing, and lack of verified attributions.";
        } else {
            result = "REAL";
            // Confidence calculation between 75% and 92%
            confidence = Math.min(92, 78 + (credibleScore * 4) + Math.max(0, (2 - suspiciousScore) * 3));
            message = "The news appears to be REAL based on neutral journalistic tone, verified citations, or absence of clickbait indicators.";
        }

        // Save detection record in MySQL
        News news = new News();
        news.setUserId(request.getUserId());
        news.setNewsText(text);
        news.setResult(result);
        news.setConfidence(confidence);
        news.setDetectedAt(LocalDateTime.now());

        News savedNews = newsRepository.save(news);

        return new NewsDetectionResponse(
                savedNews.getId(),
                savedNews.getUserId(),
                savedNews.getNewsText(),
                savedNews.getResult(),
                savedNews.getConfidence(),
                message,
                savedNews.getDetectedAt(),
                detectedIndicators,
                "Demo rule-based heuristic detection system (college mini project prototype)."
        );
    }

    /**
     * Retrieves all news checked by a specific user, sorted from newest to oldest.
     */
    public List<News> getNewsHistory(Long userId) {
        return newsRepository.findByUserIdOrderByDetectedAtDesc(userId);
    }

    /**
     * Calculates real statistics for the user's dashboard.
     */
    public StatsResponse getStats(Long userId) {
        long total = newsRepository.countByUserId(userId);
        long fake = newsRepository.countByUserIdAndResult(userId, "FAKE");
        long real = newsRepository.countByUserIdAndResult(userId, "REAL");

        return new StatsResponse(total, fake, real);
    }

    /**
     * Deletes a specific history record.
     */
    public void deleteNews(Long newsId) {
        if (!newsRepository.existsById(newsId)) {
            throw new ResourceNotFoundException("News record not found with id: " + newsId);
        }
        newsRepository.deleteById(newsId);
    }
}
