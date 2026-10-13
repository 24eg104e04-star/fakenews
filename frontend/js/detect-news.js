/**
 * Fake News Detection System - News Detection JavaScript
 * Communicates with POST /api/news/detect to analyze news articles
 */

// Dynamic API base URL: connects to port 8080 during local dev (Live Server/file), or relative /api in production
const API_BASE_URL = (window.location.port === "5500" || window.location.port === "5173" || window.location.protocol === "file:")
    ? "http://localhost:8080/api"
    : `${window.location.origin}/api`;

document.addEventListener("DOMContentLoaded", () => {
    // 1. Session Protection Check
    const storedUser = localStorage.getItem("user");
    if (!storedUser) {
        window.location.href = "login.html";
        return;
    }

    let user;
    try {
        user = JSON.parse(storedUser);
    } catch (e) {
        localStorage.removeItem("user");
        window.location.href = "login.html";
        return;
    }

    // Render user name in header and navbar
    const userNameElements = document.querySelectorAll(".user-name-display");
    userNameElements.forEach(el => el.textContent = user.name || "Student");

    // Attach logout handlers
    const logoutBtns = document.querySelectorAll(".btn-logout");
    logoutBtns.forEach(btn => {
        btn.addEventListener("click", (e) => {
            e.preventDefault();
            localStorage.removeItem("user");
            window.location.href = "login.html";
        });
    });

    const newsTextarea = document.getElementById("newsText");
    const charCountEl = document.getElementById("charCount");
    const wordCountEl = document.getElementById("wordCount");
    const checkNewsBtn = document.getElementById("checkNewsBtn");
    const alertBox = document.getElementById("detectionAlert");
    const resultSection = document.getElementById("resultSection");

    // Quick Sample Buttons
    const sampleRealBtn = document.getElementById("sampleRealBtn");
    const sampleFakeBtn = document.getElementById("sampleFakeBtn");
    const clearTextBtn = document.getElementById("clearTextBtn");

    const SAMPLE_REAL = "According to a study published by researchers at Oxford University, solar and wind energy efficiency achieved a record 24% increase across European power grids this quarter, confirmed by an official report from the ministry of energy.";
    const SAMPLE_FAKE = "SHOCKING SECRET REVEALED! Miracle fruit cures all terminal diseases in 24 hours! Big pharma and doctors hate this secret! Share this before it is deleted by authorities!!!";

    if (sampleRealBtn) {
        sampleRealBtn.addEventListener("click", () => {
            newsTextarea.value = SAMPLE_REAL;
            updateCounts();
            hideAlert();
        });
    }

    if (sampleFakeBtn) {
        sampleFakeBtn.addEventListener("click", () => {
            newsTextarea.value = SAMPLE_FAKE;
            updateCounts();
            hideAlert();
        });
    }

    if (clearTextBtn) {
        clearTextBtn.addEventListener("click", () => {
            newsTextarea.value = "";
            updateCounts();
            hideAlert();
            if (resultSection) resultSection.style.display = "none";
        });
    }

    // Textarea input event for live counters
    newsTextarea.addEventListener("input", updateCounts);

    function updateCounts() {
        const text = newsTextarea.value;
        const charCount = text.length;
        const words = text.trim() ? text.trim().split(/\s+/).length : 0;

        if (charCountEl) charCountEl.textContent = `${charCount} characters`;
        if (wordCountEl) wordCountEl.textContent = `${words} words`;
    }

    // Handle Form Submit / News Detection
    const detectionForm = document.getElementById("detectionForm");
    detectionForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        hideAlert();

        const newsText = newsTextarea.value.trim();

        // Frontend validation
        if (!newsText) {
            showAlert("Please enter or paste a news article text to analyze.", "danger");
            newsTextarea.focus();
            return;
        }

        if (newsText.length < 15) {
            showAlert("News article is too short. Please provide at least 15 characters for a meaningful analysis.", "danger");
            newsTextarea.focus();
            return;
        }

        // Show loading state
        checkNewsBtn.disabled = true;
        checkNewsBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Analyzing Article...';

        try {
            const response = await fetch(`${API_BASE_URL}/news/detect`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    userId: user.id,
                    newsText: newsText
                })
            });

            const data = await response.json();

            if (response.ok) {
                renderResult(data);
                // Scroll smoothly to result
                resultSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
            } else {
                showAlert(data.message || "Failed to analyze news article.", "danger");
            }
        } catch (error) {
            console.error("Detection API error:", error);
            showAlert("Unable to connect to backend server. Make sure Spring Boot is running on port 8080.", "danger");
        } finally {
            checkNewsBtn.disabled = false;
            checkNewsBtn.innerHTML = '<i class="bi bi-shield-check me-2"></i>Check News Article';
        }
    });

    function renderResult(data) {
        if (!resultSection) return;

        const isReal = data.result === "REAL";
        const resultCard = document.getElementById("resultCard");
        const verdictBadge = document.getElementById("verdictBadge");
        const verdictIcon = document.getElementById("verdictIcon");
        const verdictText = document.getElementById("verdictText");
        const confidenceVal = document.getElementById("confidenceValue");
        const confidenceBar = document.getElementById("confidenceProgressBar");
        const verdictMsg = document.getElementById("verdictMessage");
        const indicatorsList = document.getElementById("indicatorsList");
        const detectionDate = document.getElementById("detectionDate");
        const previewText = document.getElementById("previewSubmittedText");

        // Set card style
        resultCard.className = `result-card ${isReal ? "result-real" : "result-fake"}`;

        // Set badge
        verdictBadge.className = `verdict-badge-large ${isReal ? "real" : "fake"}`;
        verdictIcon.className = `bi ${isReal ? "bi-patch-check-fill" : "bi-exclamation-octagon-fill"}`;
        verdictText.textContent = isReal ? "REAL NEWS" : "FAKE NEWS";

        // Set Confidence
        confidenceVal.textContent = `${data.confidence}%`;
        confidenceBar.style.width = `${data.confidence}%`;
        confidenceBar.className = `confidence-bar ${isReal ? "real" : "fake"}`;

        // Set explanation message
        verdictMsg.textContent = data.message;

        // Render detected indicators
        if (data.indicators && data.indicators.length > 0) {
            indicatorsList.innerHTML = data.indicators.map(ind => {
                const isCred = ind.toLowerCase().includes("credible") || ind.toLowerCase().includes("attribution");
                const chipClass = isCred ? "chip-credible" : "chip-suspicious";
                const chipIcon = isCred ? "bi-shield-check" : "bi-flag-fill";
                return `
                    <span class="indicator-chip ${chipClass}">
                        <i class="bi ${chipIcon}"></i> ${escapeHtml(ind)}
                    </span>
                `;
            }).join("");
        } else {
            indicatorsList.innerHTML = `
                <span class="indicator-chip chip-credible">
                    <i class="bi bi-shield-check"></i> Standard factual language structure; no sensational rules triggered.
                </span>
            `;
        }

        // Set date
        const dateObj = data.detectedAt ? new Date(data.detectedAt) : new Date();
        detectionDate.textContent = dateObj.toLocaleString(undefined, {
            dateStyle: 'medium',
            timeStyle: 'short'
        });

        // Set preview of analyzed text
        if (previewText) {
            previewText.textContent = data.newsText;
        }

        resultSection.style.display = "block";
    }

    function showAlert(message, type) {
        if (!alertBox) return;
        alertBox.style.display = "block";
        alertBox.textContent = message;
        alertBox.className = `alert-custom alert-${type}`;
    }

    function hideAlert() {
        if (!alertBox) return;
        alertBox.style.display = "none";
        alertBox.textContent = "";
        alertBox.className = "alert-custom";
    }

    function escapeHtml(text) {
        if (!text) return "";
        return text
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }
});
