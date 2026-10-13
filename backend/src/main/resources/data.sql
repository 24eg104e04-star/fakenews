-- Sample Seed Data for Fake News Detection System
-- Insert demo users with hashed password ('password123')
INSERT INTO users (id, name, email, password, created_at)
VALUES (1, 'Demo Student', 'demo@student.com', '$2a$10$wE9a1zF0Uf8wF0aYkE4u3OHvK2rD2.l1x2s9.j4.pY8aM5L9yXv.q', CURRENT_TIMESTAMP);

INSERT INTO users (id, name, email, password, created_at)
VALUES (2, 'John Doe', 'john@gmail.com', '$2a$10$wE9a1zF0Uf8wF0aYkE4u3OHvK2rD2.l1x2s9.j4.pY8aM5L9yXv.q', CURRENT_TIMESTAMP);

-- Sample News Detections for demo user
INSERT INTO news (id, user_id, news_text, result, confidence, detected_at)
VALUES (1, 1, 'Scientists at Oxford University published a peer-reviewed study confirming renewable energy efficiency improvements across Europe.', 'REAL', 88, CURRENT_TIMESTAMP);

INSERT INTO news (id, user_id, news_text, result, confidence, detected_at)
VALUES (2, 1, 'SHOCKING SECRET REVEALED! Miracle fruit cures all diseases in 24 hours! Doctors hate this secret! Share before it is deleted!!!', 'FAKE', 94, CURRENT_TIMESTAMP);

INSERT INTO news (id, user_id, news_text, result, confidence, detected_at)
VALUES (3, 1, 'Breaking report: Ministry of Health issues annual public guidelines on seasonal influenza vaccination.', 'REAL', 82, CURRENT_TIMESTAMP);
