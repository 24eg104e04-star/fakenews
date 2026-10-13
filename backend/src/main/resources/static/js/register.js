/**
 * Fake News Detection System - Registration JavaScript
 * Handles user registration with client & server-side validation
 */

// Dynamic API base URL: connects to port 8080 during local dev (Live Server/file), or relative /api in production
const API_BASE_URL = (window.location.port === "5500" || window.location.port === "5173" || window.location.protocol === "file:")
    ? "http://localhost:8080/api"
    : `${window.location.origin}/api`;

document.addEventListener("DOMContentLoaded", () => {
    // If user is already logged in, redirect to dashboard
    if (localStorage.getItem("user")) {
        window.location.href = "dashboard.html";
        return;
    }

    const registerForm = document.getElementById("registerForm");
    const nameInput = document.getElementById("name");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirmPassword");
    const alertBox = document.getElementById("alertBox");
    const submitBtn = document.getElementById("submitBtn");

    registerForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        showAlert("", "none");

        const name = nameInput.value.trim();
        const email = emailInput.value.trim();
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        // 1. Check empty fields
        if (!name) {
            showAlert("Please enter your full name.", "danger");
            nameInput.focus();
            return;
        }

        if (!email) {
            showAlert("Please enter your email address.", "danger");
            emailInput.focus();
            return;
        }

        // 2. Email format validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            showAlert("Please enter a valid email address.", "danger");
            emailInput.focus();
            return;
        }

        // 3. Password length check
        if (!password || password.length < 6) {
            showAlert("Password must be at least 6 characters long.", "danger");
            passwordInput.focus();
            return;
        }

        // 4. Password match check
        if (password !== confirmPassword) {
            showAlert("Password and Confirm Password do not match.", "danger");
            confirmPasswordInput.focus();
            return;
        }

        // Disable button during request
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Creating Account...';

        try {
            const response = await fetch(`${API_BASE_URL}/auth/register`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ name, email, password })
            });

            const data = await response.json();

            if (response.ok) {
                showAlert("Account registered successfully! Redirecting to login...", "success");
                registerForm.reset();

                setTimeout(() => {
                    window.location.href = "login.html";
                }, 1200);
            } else {
                const errorMsg = data.message || "Registration failed. Please check your details.";
                showAlert(errorMsg, "danger");
            }
        } catch (error) {
            console.error("Registration fetch error:", error);
            showAlert("Unable to connect to backend server. Make sure Spring Boot is running on port 8080.", "danger");
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = 'Create Account <i class="bi bi-arrow-right"></i>';
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
