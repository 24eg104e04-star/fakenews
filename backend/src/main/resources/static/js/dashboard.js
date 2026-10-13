/**
 * Fake News Detection System - Dashboard JavaScript
 * Displays real-time statistics and recent detections from Spring Boot REST API
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

    // 2. Render user name in header and navbar
    const userNameElements = document.querySelectorAll(".user-name-display");
    userNameElements.forEach(el => el.textContent = user.name || "Student");

    const userEmailElements = document.querySelectorAll(".user-email-display");
    userEmailElements.forEach(el => el.textContent = user.email || "");

    // 3. Attach logout handlers
    const logoutBtns = document.querySelectorAll(".btn-logout");
    logoutBtns.forEach(btn => {
        btn.addEventListener("click", (e) => {
            e.preventDefault();
            localStorage.removeItem("user");
            window.location.href = "login.html";
        });
    });

    // 4. Load dashboard statistics & recent history
    loadDashboardStats(user.id);
    loadRecentHistory(user.id);
});

/**
 * Fetches real statistics from backend API: GET /api/news/stats/{userId}
 */
async function loadDashboardStats(userId) {
    const totalEl = document.getElementById("statTotal");
    const fakeEl = document.getElementById("statFake");
    const realEl = document.getElementById("statReal");
    const ratioEl = document.getElementById("statRatio");

    try {
        const response = await fetch(`${API_BASE_URL}/news/stats/${userId}`);
        if (!response.ok) {
            throw new Error(`Failed to fetch stats: ${response.status}`);
        }

        const data = await response.json();

        if (totalEl) totalEl.textContent = data.total;
        if (fakeEl) fakeEl.textContent = data.fake;
        if (realEl) realEl.textContent = data.real;

        if (ratioEl) {
            if (data.total > 0) {
                const realPercent = Math.round((data.real / data.total) * 100);
                ratioEl.textContent = `${realPercent}% Real`;
            } else {
                ratioEl.textContent = "0%";
            }
        }
    } catch (error) {
        console.error("Error loading stats:", error);
        if (totalEl) totalEl.textContent = "0";
        if (fakeEl) fakeEl.textContent = "0";
        if (realEl) realEl.textContent = "0";
        if (ratioEl) ratioEl.textContent = "0%";
    }
}

/**
 * Fetches latest 3 history records to show on dashboard preview
 */
async function loadRecentHistory(userId) {
    const tableBody = document.getElementById("recentHistoryBody");
    const noHistoryMsg = document.getElementById("noRecentHistory");
    if (!tableBody) return;

    try {
        const response = await fetch(`${API_BASE_URL}/news/history/${userId}`);
        if (!response.ok) {
            throw new Error("Failed to fetch history");
        }

        const history = await response.json();

        if (!history || history.length === 0) {
            if (noHistoryMsg) noHistoryMsg.style.display = "block";
            tableBody.innerHTML = "";
            return;
        }

        if (noHistoryMsg) noHistoryMsg.style.display = "none";

        // Take up to 3 most recent detections
        const recent = history.slice(0, 3);
        tableBody.innerHTML = recent.map((item, index) => {
            const isReal = item.result === "REAL";
            const badgeClass = isReal ? "badge-real" : "badge-fake";
            const icon = isReal ? "bi-check-circle-fill" : "bi-exclamation-triangle-fill";
            const dateStr = item.detectedAt ? new Date(item.detectedAt).toLocaleDateString(undefined, {
                month: 'short', day: 'numeric', year: 'numeric'
            }) : "Recent";

            return `
                <tr>
                    <td><strong>#${index + 1}</strong></td>
                    <td>
                        <div class="news-snippet-text" title="${escapeHtml(item.newsText)}">
                            ${escapeHtml(item.newsText)}
                        </div>
                    </td>
                    <td>
                        <span class="${badgeClass}">
                            <i class="bi ${icon}"></i> ${item.result}
                        </span>
                    </td>
                    <td>
                        <strong>${item.confidence}%</strong>
                    </td>
                    <td class="text-muted">
                        ${dateStr}
                    </td>
                </tr>
            `;
        }).join("");
    } catch (error) {
        console.error("Error loading recent history:", error);
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
