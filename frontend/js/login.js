/**
 * Fake News Detection System - Login JavaScript
 * Handles user authentication via Spring Boot REST API
 */

// Dynamic API base URL: connects to port 8080 during local dev (Live Server/file), or relative /api in production
const API_BASE_URL = (window.location.port === "5500" || window.location.port === "5173" || window.location.protocol === "file:")
    ? "http://localhost:8080/api"
    : `${window.location.origin}/api`;

document.addEventListener("DOMContentLoaded", () => {
    // Check if user is already logged in; if so, forward to dashboard
    const existingUser = localStorage.getItem("user");
    if (existingUser) {
        window.location.href = "dashboard.html";
        return;
    }

    const loginForm = document.getElementById("loginForm");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const alertBox = document.getElementById("alertBox");
    const submitBtn = document.getElementById("submitBtn");
    const fillDemoBtn = document.getElementById("fillDemoBtn");

    // Quick helper for demo presentation
    if (fillDemoBtn) {
        fillDemoBtn.addEventListener("click", () => {
            emailInput.value = "demo@student.com";
            passwordInput.value = "password123";
        });
    }

    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        showAlert("", "none");

        const email = emailInput.value.trim();
        const password = passwordInput.value;

        // Frontend validation
        if (!email) {
            showAlert("Please enter your email address.", "danger");
            emailInput.focus();
            return;
        }

        if (!password) {
            showAlert("Please enter your password.", "danger");
            passwordInput.focus();
            return;
        }

        // Disable button during network request
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Logging in...';

        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();

            if (response.ok) {
                // Successful login
                showAlert("Login successful! Redirecting to dashboard...", "success");

                // Store logged-in user in localStorage as required
                const userSession = {
                    id: data.id,
                    name: data.name,
                    email: data.email
                };
                localStorage.setItem("user", JSON.stringify(userSession));

                setTimeout(() => {
                    window.location.href = "dashboard.html";
                }, 800);
            } else {
                // Display friendly error message from backend
                const errorMsg = data.message || "Invalid email or password";
                showAlert(errorMsg, "danger");
            }
        } catch (error) {
            console.error("Login fetch error:", error);
            showAlert("Unable to connect to backend server. Make sure Spring Boot is running on port 8080.", "danger");
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = 'Login to System <i class="bi bi-arrow-right"></i>';
        }
    });

    function showAlert(message, type) {
        if (!alertBox) return;
        if (type === "none" || !message) {
            alertBox.style.display = "none";
            alertBox.textContent = "";
            alertBox.className = "alert-custom";
        } else {
            alertBox.style.display = "block";
            alertBox.textContent = message;
            alertBox.className = `alert-custom alert-${type}`;
        }
    }
});
