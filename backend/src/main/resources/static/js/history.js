/**
 * Fake News Detection System - Detection History JavaScript
 * Retrieves, filters, and manages news detection history via REST API
 */

// Dynamic API base URL: connects to port 8080 during local dev (Live Server/file), or relative /api in production
const API_BASE_URL = (window.location.port === "5500" || window.location.port === "5173" || window.location.protocol === "file:")
    ? "http://localhost:8080/api"
    : `${window.location.origin}/api`;
let currentHistory = [];

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

    // Load full history
    loadHistory(user.id);

    // Search and filter event listeners
    const searchInput = document.getElementById("searchHistoryInput");
    const filterSelect = document.getElementById("filterResultSelect");

    if (searchInput) {
        searchInput.addEventListener("input", () => applyFilters());
    }

    if (filterSelect) {
        filterSelect.addEventListener("change", () => applyFilters());
    }
});

/**
 * Loads detection history from backend: GET /api/news/history/{userId}
 */
async function loadHistory(userId) {
    const tableContainer = document.getElementById("historyTableContainer");
    const emptyState = document.getElementById("emptyHistoryState");
    const loadingState = document.getElementById("historyLoadingState");

    if (loadingState) loadingState.style.display = "block";
    if (tableContainer) tableContainer.style.display = "none";
    if (emptyState) emptyState.style.display = "none";

    try {
        const response = await fetch(`${API_BASE_URL}/news/history/${userId}`);
        if (!response.ok) {
            throw new Error(`Failed to load history: ${response.status}`);
        }

        currentHistory = await response.json();
        renderHistoryList(currentHistory);
    } catch (error) {
        console.error("Error loading history:", error);
        if (emptyState) {
            emptyState.style.display = "block";
            const emptyText = emptyState.querySelector(".empty-state-text");
            if (emptyText) emptyText.textContent = "Unable to connect to database. Make sure Spring Boot is running.";
        }
    } finally {
        if (loadingState) loadingState.style.display = "none";
    }
}

/**
 * Renders history rows into the table
 */
function renderHistoryList(items) {
    const tableBody = document.getElementById("historyTableBody");
    const tableContainer = document.getElementById("historyTableContainer");
    const emptyState = document.getElementById("emptyHistoryState");
    const totalBadge = document.getElementById("totalRecordsBadge");

    if (totalBadge) totalBadge.textContent = `${items.length} records`;

    if (!items || items.length === 0) {
        if (tableContainer) tableContainer.style.display = "none";
        if (emptyState) emptyState.style.display = "block";
        return;
    }

    if (emptyState) emptyState.style.display = "none";
    if (tableContainer) tableContainer.style.display = "block";

    tableBody.innerHTML = items.map((item, index) => {
        const isReal = item.result === "REAL";
        const badgeClass = isReal ? "badge-real" : "badge-fake";
        const icon = isReal ? "bi-check-circle-fill" : "bi-exclamation-triangle-fill";
        const dateStr = item.detectedAt ? new Date(item.detectedAt).toLocaleString(undefined, {
            dateStyle: 'medium',
            timeStyle: 'short'
        }) : "N/A";

        return `
            <tr>
                <td><strong>#${index + 1}</strong></td>
                <td>
                    <div class="news-snippet-text mb-1" id="snippet-${item.id}">
                        ${escapeHtml(item.newsText)}
                    </div>
                    <button class="btn btn-sm btn-link p-0 text-decoration-none" onclick="toggleFullText(${item.id})">
                        <small><i class="bi bi-arrows-expand me-1"></i>Read full</small>
                    </button>
                </td>
                <td>
                    <span class="${badgeClass}">
                        <i class="bi ${icon}"></i> ${item.result}
                    </span>
                </td>
                <td>
                    <strong>${item.confidence}%</strong>
                </td>
                <td class="text-muted small">
                    ${dateStr}
                </td>
                <td>
                    <button class="btn btn-sm btn-danger-soft" title="Delete record" onclick="deleteHistoryRecord(${item.id})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    }).join("");
}

/**
 * Filters the history based on search query and result selection
 */
function applyFilters() {
    const searchInput = document.getElementById("searchHistoryInput");
    const filterSelect = document.getElementById("filterResultSelect");

    const query = searchInput ? searchInput.value.toLowerCase().trim() : "";
    const filter = filterSelect ? filterSelect.value : "ALL";

    const filtered = currentHistory.filter(item => {
        const matchesQuery = !query || item.newsText.toLowerCase().includes(query);
        const matchesResult = (filter === "ALL") || (item.result === filter);
        return matchesQuery && matchesResult;
    });

    renderHistoryList(filtered);
}

/**
 * Deletes a record from backend and updates UI
 */
async function deleteHistoryRecord(newsId) {
    if (!confirm("Are you sure you want to delete this news detection record?")) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/news/history/${newsId}`, {
            method: "DELETE"
        });

        if (response.ok) {
            // Remove locally and re-render
            currentHistory = currentHistory.filter(item => item.id !== newsId);
            applyFilters();
        } else {
            alert("Failed to delete record.");
        }
    } catch (error) {
        console.error("Delete error:", error);
        alert("Error connecting to server.");
    }
}

/**
 * Toggles full text display for truncated snippet
 */
function toggleFullText(id) {
    const el = document.getElementById(`snippet-${id}`);
    if (el) {
        if (el.style.display === "block") {
            el.style.display = "-webkit-box";
        } else {
            el.style.display = "block";
        }
    }
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
