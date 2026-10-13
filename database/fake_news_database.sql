-- ===================================================================
-- Fake News Detection System - MySQL Database Setup Script
-- College Mini Project
-- ===================================================================

-- Step 1: Create Database
CREATE DATABASE IF NOT EXISTS fake_news_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE fake_news_db;

-- Step 2: Drop existing tables if needed (in reverse foreign key order)
-- DROP TABLE IF EXISTS news;
-- DROP TABLE IF EXISTS users;

-- Step 3: Create Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Step 4: Create News Table
CREATE TABLE IF NOT EXISTS news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    news_text LONGTEXT NOT NULL,
    result VARCHAR(20) NOT NULL,
    confidence INT NOT NULL,
    detected_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_news_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_news_user (user_id),
    INDEX idx_news_result (result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Step 5: Insert Sample Demo Data
-- Default Demo User credentials:
-- Email: demo@student.com  | Password: password123
-- Email: john@gmail.com    | Password: password123
-- The password hash below is BCrypt for 'password123'
INSERT INTO users (id, name, email, password, created_at) VALUES
(1, 'Demo Student', 'demo@student.com', '$2a$10$wE9a1zF0Uf8wF0aYkE4u3OHvK2rD2.l1x2s9.j4.pY8aM5L9yXv.q', NOW()),
(2, 'John Doe', 'john@gmail.com', '$2a$10$wE9a1zF0Uf8wF0aYkE4u3OHvK2rD2.l1x2s9.j4.pY8aM5L9yXv.q', NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Sample News detections for Demo User (user_id = 1)
INSERT INTO news (id, user_id, news_text, result, confidence, detected_at) VALUES
(1, 1, 'Scientists at Oxford University published a peer-reviewed study confirming renewable energy efficiency improvements across Europe.', 'REAL', 88, NOW() - INTERVAL 3 DAY),
(2, 1, 'SHOCKING SECRET REVEALED! Miracle fruit cures all diseases in 24 hours! Doctors hate this secret! Share before it is deleted!!!', 'FAKE', 94, NOW() - INTERVAL 2 DAY),
(3, 1, 'Breaking report: Ministry of Health issues annual public guidelines on seasonal influenza vaccination and precautions.', 'REAL', 84, NOW() - INTERVAL 1 DAY),
(4, 1, 'URGENT VIRAL ALERT! 100% guaranteed lottery trick exposed! You will not believe what billionaires do not want you to know!', 'FAKE', 92, NOW())
ON DUPLICATE KEY UPDATE result = VALUES(result);

-- Verification Queries
SELECT 'Users Count' AS entity, COUNT(*) AS total FROM users
UNION ALL
SELECT 'News Detections' AS entity, COUNT(*) AS total FROM news;
