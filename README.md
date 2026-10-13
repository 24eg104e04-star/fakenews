# Fake News Detection System

A complete, clean, beginner-friendly **Java Full Stack Web Application** developed as a college mini-project. The application identifies potentially misleading, clickbait, and fake news articles using a Java-based heuristic rule detection engine, stores user analyses in a MySQL database, and presents real-time verification results and statistics through a responsive frontend.

---

## 1. Description

In today's digital era, misinformation and sensationalized clickbait spread rapidly across social media platforms. The **Fake News Detection System** provides an interactive web portal where registered users can submit news articles or headlines for authenticity analysis. 

The application follows an enterprise-standard multi-tiered architecture:
- **Frontend**: Clean, responsive UI built with HTML5, CSS3, JavaScript (Fetch API), and Bootstrap 5.
- **Backend**: RESTful web services powered by Java 17+, Spring Boot 3, and Spring Data JPA / Hibernate.
- **Database**: Relational data persistence with MySQL 8+.
- **Detection Engine**: An isolated, explainable Java heuristic rule-based service analyzing sensational vocabulary, punctuation patterns, capitalization ratios, and journalistic credibility markers.

---

## 2. Key Features

- **User Authentication**: Secure registration and login with BCrypt password hashing and duplicate email prevention.
- **Session Management**: Lightweight client-side session persistence via `localStorage` with protected dashboard routes.
- **Rule-Based Fake News Detection**:
  - Analyzes high-risk sensational keywords (e.g., *shocking*, *miracle*, *100% guaranteed*, *you won't believe*, *secret*).
  - Flags excessive emotional punctuation (`!!`, `??`, `$$`).
  - Detects aggressive capitalization / shouting (ALL-CAPS word ratio).
  - Identifies legitimate journalistic source attributions (e.g., *according to*, *study published*, *official report*, *reuters*).
- **Explainable Verdict & Confidence**: Calculates a clear authenticity verdict (`REAL NEWS` vs `FAKE NEWS`), confidence percentage (75%–96%), and a detailed list of triggered heuristic indicators.
- **MySQL Database Persistence**: Automatically records every checked article, result, confidence score, user ID, and timestamp.
- **Personal Detection History**: Searchable and filterable history table allowing users to review all past analyses or delete individual records.
- **Real-Time Dashboard Analytics**: Computes real-time user statistics: Total Checked, Fake News Count, Real News Count, and Authenticity Ratio.
- **Clean Viva/Demo Utilities**: One-click demo login (`demo@student.com` / `password123`) and sample test article buttons ("Sample Real News", "Sample Fake News") for easy demonstration during project presentation.

---

## 3. Technologies Used

| Tier | Technologies |
| :--- | :--- |
| **Frontend** | HTML5, CSS3, JavaScript (ES6+ Fetch API), Bootstrap 5.3, Bootstrap Icons |
| **Backend** | Java 17+, Spring Boot 3.3.4, Spring Web (MVC / REST), Spring Data JPA |
| **Security & Hashing** | Spring Security Crypto (`BCryptPasswordEncoder`) |
| **ORM / Database** | Hibernate 6, MySQL Connector/J, MySQL Server 8+ |
| **Build & Tooling** | Apache Maven 3.9+ (includes Maven Wrapper `mvnw`), VS Code / IntelliJ IDEA |

---

## 4. Prerequisites & Requirements

Before running the application, make sure you have installed:
1. **Java Development Kit (JDK)**: Java 17 or later (Java 21 / Java 24 supported). Verify via `java -version`.
2. **Apache Maven**: Maven 3.8+ (or use the included `./mvnw` wrapper). Verify via `mvn -version`.
3. **MySQL Server**: MySQL 8.0+ running on port `3306`.
4. **Web Browser**: Google Chrome, Microsoft Edge, or Mozilla Firefox.
5. **Code Editor**: VS Code (with the "Live Server" extension) or IntelliJ IDEA.

---

## 5. Database Setup (MySQL)

### Step 1: Start MySQL Server
Ensure your MySQL service is running (e.g., through Windows Services, MySQL Workbench, or Command Prompt).

### Step 2: Create Database & Tables
Open **MySQL Workbench** or the **MySQL Command Line Client**, connect to your local instance, and run the SQL script located at:
```text
database/fake_news_database.sql
```

Alternatively, run the following SQL statements:
```sql
CREATE DATABASE IF NOT EXISTS fake_news_db;
USE fake_news_db;
```
*(Spring Boot JPA is configured with `hibernate.ddl-auto=update` and `createDatabaseIfNotExist=true`, meaning tables will be automatically created or updated upon backend startup!)*

### Step 3: Configure Database Password
Open `backend/src/main/resources/application.properties` and enter your MySQL root password:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fake_news_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```
> **Note:** Replace `YOUR_PASSWORD` with your actual MySQL root password (e.g., `root`, `admin123`, etc.).

---

## 6. How to Run the Backend (Spring Boot)

1. Open your terminal or Command Prompt and navigate to the `backend` folder:
   ```bash
   cd backend
   ```

2. Run the Spring Boot application using Maven:
   ```bash
   mvn spring-boot:run
   ```
   *(Or on Windows using the included wrapper:)*
   ```cmd
   .\mvnw.cmd spring-boot:run
   ```

3. When started successfully, you will see:
   ```text
   =================================================
    Fake News Detection System Backend is RUNNING! 
    Server URL: http://localhost:8080               
   =================================================
   ```

---

## 7. How to Run the Frontend (HTML/CSS/JS)

1. Open the project root or the `frontend` folder in **VS Code**.
2. Make sure you have the **Live Server** extension installed in VS Code (`ritwickdey.liveserver`).
3. Right-click on `frontend/index.html` and select **"Open with Live Server"**.
4. The application will launch in your browser at:
   ```text
   http://127.0.0.1:5500/frontend/index.html
   ```
   or
   ```text
   http://localhost:5500/frontend/index.html
   ```

*(Alternatively, you can open `index.html` directly in any modern browser!)*

---

## 8. REST API Documentation

Base URL: `http://localhost:8080`

### 8.1 Authentication Endpoints

#### 1. Register User
- **Method / URL**: `POST /api/auth/register`
- **Request Body**:
  ```json
  {
    "name": "John Doe",
    "email": "john@gmail.com",
    "password": "password123"
  }
  ```
- **Response** (`201 Created`):
  ```json
  {
    "message": "Registration successful",
    "userId": 2,
    "name": "John Doe",
    "email": "john@gmail.com"
  }
  ```

#### 2. User Login
- **Method / URL**: `POST /api/auth/login`
- **Request Body**:
  ```json
  {
    "email": "john@gmail.com",
    "password": "password123"
  }
  ```
- **Response** (`200 OK`):
  ```json
  {
    "id": 2,
    "name": "John Doe",
    "email": "john@gmail.com",
    "message": "Login successful"
  }
  ```

---

### 8.2 News Detection & History Endpoints

#### 3. Detect News Article
- **Method / URL**: `POST /api/news/detect`
- **Request Body**:
  ```json
  {
    "userId": 1,
    "newsText": "SHOCKING! You won't believe this secret miracle cure that doctors hate! Share before it is deleted!!!"
  }
  ```
- **Response** (`200 OK`):
  ```json
  {
    "id": 15,
    "userId": 1,
    "newsText": "SHOCKING! You won't believe this secret miracle cure...",
    "result": "FAKE",
    "confidence": 95,
    "message": "The news appears to be FAKE based on sensational keywords, emotional phrasing, and lack of verified attributions.",
    "detectedAt": "2026-10-13T21:18:33",
    "indicators": [
      "Suspicious keyword/phrase detected: 'shocking'",
      "Suspicious keyword/phrase detected: 'miracle'",
      "Suspicious keyword/phrase detected: 'you won't believe'",
      "Suspicious keyword/phrase detected: 'secret'",
      "Excessive sensational punctuation (e.g. '!!', '??', '$$')"
    ],
    "analysisNote": "Demo rule-based heuristic detection system (college mini project prototype)."
  }
  ```

#### 4. Get User Detection History
- **Method / URL**: `GET /api/news/history/{userId}`
- **Response** (`200 OK`):
  ```json
  [
    {
      "id": 15,
      "userId": 1,
      "newsText": "...",
      "result": "FAKE",
      "confidence": 95,
      "detectedAt": "2026-10-13T21:18:33"
    }
  ]
  ```

#### 5. Get User Dashboard Statistics
- **Method / URL**: `GET /api/news/stats/{userId}`
- **Response** (`200 OK`):
  ```json
  {
    "total": 10,
    "fake": 4,
    "real": 6
  }
  ```

#### 6. Delete History Record
- **Method / URL**: `DELETE /api/news/history/{id}`
- **Response** (`200 OK`):
  ```json
  {
    "message": "History record deleted successfully"
  }
  ```

---

## 9. Project Directory Structure

```text
fake-news-detection-system/
│
├── frontend/
│   │
│   ├── index.html                # Public landing page with features & workflow
│   ├── login.html                # User login page with quick demo fill
│   ├── register.html             # User registration with validations
│   ├── dashboard.html            # Protected user dashboard with live statistics
│   ├── detect-news.html          # Interactive news submission & detection UI
│   ├── history.html              # Past detections table with search & filter
│   │
│   ├── css/
│   │   ├── style.css             # Design tokens, typography, navbar, footer, badges
│   │   ├── login.css             # Authentication card layout & focus states
│   │   ├── register.css          # Registration input styling & password hints
│   │   ├── dashboard.css         # Stat cards, action tiles, and greeting banner
│   │   └── news.css              # Large textarea, result badges, progress bar, table
│   │
│   ├── js/
│   │   ├── login.js              # Authentication handler & localStorage session
│   │   ├── register.js           # Form validation & registration API caller
│   │   ├── dashboard.js          # Live stats loader & recent activity renderer
│   │   ├── detect-news.js        # News submission, spinner, & result display
│   │   └── history.js            # History table pagination, search, filter & delete
│   │
│   └── assets/
│       ├── images/               # Media assets
│       └── icons/                # System icon assets
│
├── backend/
│   │
│   ├── pom.xml                   # Maven dependencies and build configuration
│   ├── mvnw & mvnw.cmd           # Maven Wrapper executable scripts
│   │
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/
│       │   │       └── fakenews/
│       │   │           └── detection/
│       │   │               │
│       │   │               ├── FakeNewsDetectionApplication.java
│       │   │               │
│       │   │               ├── controller/
│       │   │               │   ├── AuthController.java
│       │   │               │   ├── NewsController.java
│       │   │               │   └── HistoryController.java
│       │   │               │
│       │   │               ├── service/
│       │   │               │   ├── UserService.java
│       │   │               │   └── NewsDetectionService.java
│       │   │               │
│       │   │               ├── repository/
│       │   │               │   ├── UserRepository.java
│       │   │               └── └── NewsRepository.java
│       │   │               │
│       │   │               ├── entity/
│       │   │               │   ├── User.java
│       │   │               │   └── News.java
│       │   │               │
│       │   │               ├── dto/
│       │   │               │   ├── LoginRequest.java
│       │   │               │   ├── RegisterRequest.java
│       │   │               │   ├── NewsRequest.java
│       │   │               │   ├── AuthResponse.java
│       │   │               │   ├── NewsDetectionResponse.java
│       │   │               │   ├── StatsResponse.java
│       │   │               │   └── ApiResponse.java
│       │   │               │
│       │   │               ├── config/
│       │   │               │   ├── CorsConfig.java
│       │   │               │   └── SecurityConfig.java
│       │   │               │
│       │   │               └── exception/
│       │   │                   ├── GlobalExceptionHandler.java
│       │   │                   ├── ResourceNotFoundException.java
│       │   │                   ├── UserAlreadyExistsException.java
│       │   │                   └── InvalidCredentialsException.java
│       │   │
│       │   └── resources/
│       │       ├── application.properties
│       │       └── data.sql
│       │
│       └── test/                 # Automated Unit & Integration Tests (100% Pass)
│           ├── java/com/fakenews/detection/
│           │   ├── controller/AuthControllerTest.java
│           │   └── service/NewsDetectionServiceTest.java
│           └── resources/application.properties
│
├── database/
│   └── fake_news_database.sql    # Full SQL schema, indexes & seed data
│
├── README.md                     # Comprehensive project documentation
└── .gitignore                    # Git ignore configuration
```

---

## 10. Technical Explanations for Viva & Project Presentation

### A. How Frontend Communicates with Backend
1. The frontend uses the native JavaScript **Fetch API** (`async/await`) to make asynchronous HTTP requests (`GET`, `POST`, `DELETE`) to the Spring Boot REST endpoints hosted at `http://localhost:8080/api`.
2. Payloads are exchanged in **JSON** (`Content-Type: application/json`).
3. Cross-Origin Resource Sharing (CORS) is enabled on the backend via [`CorsConfig.java`](file:///backend/src/main/java/com/fakenews/detection/config/CorsConfig.java) so browser requests from Live Server (`http://127.0.0.1:5500`) are accepted.
4. User session data is stored in the browser's `localStorage` after a successful login. Protected pages check this session on load; if absent, the user is redirected to `login.html`.

### B. How Backend Communicates with MySQL
1. The backend implements **Spring Data JPA** with **Hibernate** as the Object-Relational Mapping (ORM) provider.
2. The Java classes [`User.java`](file:///backend/src/main/java/com/fakenews/detection/entity/User.java) and [`News.java`](file:///backend/src/main/java/com/fakenews/detection/entity/News.java) are annotated with `@Entity` and mapped directly to MySQL tables `users` and `news`.
3. Repositories (`UserRepository`, `NewsRepository`) extend `JpaRepository<T, ID>`, providing built-in CRUD operations and query methods (`findByEmail`, `countByUserIdAndResult`) without writing error-prone manual SQL queries.
4. Spring Boot injects a HikariCP connection pool configured in `application.properties` to manage active database connections efficiently.

### C. Fake News Detection Algorithm (Rule-Based Heuristic)
The detection logic resides entirely in [`NewsDetectionService.java`](file:///backend/src/main/java/com/fakenews/detection/service/NewsDetectionService.java):
1. **Sensational & Clickbait Vocabulary Analysis**: Scans the lowercase text for high-risk phrases (e.g., *shocking*, *miracle*, *100% guaranteed*, *you won't believe*, *conspiracy*, *doctors hate this*, *illuminati*).
2. **Punctuation Pattern Recognition**: Regular expressions scan for emotional or sensational punctuation strings (`!!`, `???`, `$$$`).
3. **Capitalization / Shouting Ratio**: Calculates the percentage of uppercase words ($\ge 3$ characters). A ratio $> 20\%$ indicates sensational shouting.
4. **Journalistic Credibility Counter-Weight**: Checks for authoritative attribution markers (*according to*, *study published*, *official report*, *peer-reviewed*, *reuters*, *associated press*).
5. **Decision & Confidence Formula**:
   - Computes `netSuspicion = suspiciousScore - (credibleScore / 2)`.
   - If `netSuspicion >= 3` $\rightarrow$ classified as **`FAKE`** with confidence $80\% - 96\%$.
   - If `netSuspicion < 3` $\rightarrow$ classified as **`REAL`** with confidence $75\% - 92\%$.
6. **Machine-Learning Modularity**: The detection logic is cleanly isolated in a single service method, allowing students to easily swap the heuristic rules with a Python / Flask / FastAPI machine-learning model (e.g., PassiveAggressiveClassifier, TF-IDF, BERT) in future iterations.

---

## 11. Testing Guide

You can test all scenarios easily:

### 1. Registration Testing
- **Valid Registration**: Register with a new email $\rightarrow$ redirected to login with success message.
- **Duplicate Email**: Attempt registering with the same email $\rightarrow$ error: *"Email is already registered"*.
- **Password Mismatch**: Enter different passwords in Confirm Password $\rightarrow$ client-side alert.
- **Short Password**: Enter `< 6` characters $\rightarrow$ validation error.

### 2. Login Testing
- **Correct Credentials**: Login with `demo@student.com` / `password123` $\rightarrow$ dashboard displays *"Welcome, Demo Student"*.
- **Invalid Password**: Enter wrong password $\rightarrow$ alert: *"Invalid email or password"*.
- **Non-existent User**: Enter unregistered email $\rightarrow$ alert: *"Invalid email or password"*.

### 3. News Detection Testing
- **Test Real News Sample**: Click **"Sample Real News"** and hit **"Check News"** $\rightarrow$ Result: `REAL NEWS` (emerald green badge, $> 80\%$ confidence).
- **Test Fake News Sample**: Click **"Sample Fake News"** and hit **"Check News"** $\rightarrow$ Result: `FAKE NEWS` (crimson red badge, $> 90\%$ confidence, lists detected clickbait indicators).

### 4. History & Dashboard Testing
- **History View**: Open `history.html` $\rightarrow$ displays all submitted articles in chronological order.
- **Filter**: Filter by "Real News Only" or "Fake News Only".
- **Dashboard Stats**: Open `dashboard.html` $\rightarrow$ verify that Total, Fake, and Real numbers accurately reflect database counts.
- **Logout**: Click **"Logout"** $\rightarrow$ clears session and redirects to `login.html`.

---

## 12. Future Enhancements

- Integrate a trained **Machine Learning NLP Model** (e.g., Python Scikit-Learn TF-IDF or HuggingFace Transformers) via REST API.
- Add **News URL Scraping** (fetch article body automatically from news websites like BBC, CNN).
- Integration with external **Fact-Checking APIs** (Google Fact Check Tools API, Snopes, PolitiFact).
- Role-based **Admin Dashboard** to manage users and review detection accuracy.
- **JWT (JSON Web Token)** authentication with token expiration and refresh tokens.

---

## 13. License & Academic Disclaimer

This project is developed for educational and academic evaluation purposes as a College Full Stack Mini-Project. The detection algorithm demonstrates rule-based heuristics and is not intended to replace professional fact-checking organizations.
