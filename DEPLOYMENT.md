# Deployment Guide – Fake News Detection System

This guide outlines step-by-step instructions to deploy the **Fake News Detection System** across different environments, from a single-command local production run to cloud hosting (Render, Railway, Docker, AWS).

---

## 1. Architecture Overview for Deployment

The application is bundled into a **single self-contained executable JAR** file:
- **Embedded Frontend**: The HTML, CSS, JavaScript, and assets are packaged directly inside `src/main/resources/static/`.
- **REST APIs**: Handled by Spring Boot on the same host and port (`/api/*`).
- **Zero CORS in Production**: Because frontend and backend are served from the same domain/port, browsers have zero CORS restrictions.
- **Dynamic API Base URL**: Automatically routes to the current domain in production or port `8080` in local dev.
- **Smart Database Configuration**: Automatically connects to MySQL using environment variables, or falls back to an embedded persistent database if credentials are not configured.

---

## 2. Option 1: Standalone JAR Deployment (Fastest Local & VPS)

### Step 1: Build the Production JAR
In your project directory, open a terminal and run:
```powershell
cd backend
.\mvnw.cmd clean package -DskipTests
```
*(On Linux/macOS: `./mvnw clean package -DskipTests`)*

This produces the production JAR file at:
```text
backend/target/fake-news-detection-backend-1.0.0.jar
```

### Step 2: Run the Application
Run the packaged JAR with any Java 17+ runtime:
```bash
java -jar backend/target/fake-news-detection-backend-1.0.0.jar
```

To run with custom MySQL credentials:
```bash
java -DSPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/fake_news_db" \
     -DSPRING_DATASOURCE_USERNAME="root" \
     -DSPRING_DATASOURCE_PASSWORD="your_password" \
     -jar backend/target/fake-news-detection-backend-1.0.0.jar
```

Open your browser at: **`http://localhost:8080`**

---

## 3. Option 2: Docker & Docker Compose Deployment (Recommended)

Both `Dockerfile` and `docker-compose.yml` are pre-configured in the project root.

### One-Command Deployment:
```bash
docker compose up --build -d
```

### What this does:
1. Spins up a **MySQL 8.0** container initialized with `database/fake_news_database.sql`.
2. Compiles and packages the Spring Boot application using a multi-stage Alpine JRE container.
3. Automatically connects the web app to MySQL over an internal Docker network.
4. Exposes the full application on **`http://localhost:8080`**.

### To view logs or stop:
```bash
# View live logs
docker compose logs -f

# Stop containers
docker compose down
```

---

## 4. Option 3: Deploy Free on Render (render.com)

[Render](https://render.com) offers free hosting for web services and databases.

### Step 1: Push Code to GitHub
Push this repository to your GitHub account:
```bash
git init
git add .
git commit -m "Initial commit for deployment"
git branch -M main
git remote add origin https://github.com/your-username/fake-news-detection.git
git push -u origin main
```

### Step 2: Create a Free MySQL Database on Render (or Aiven / PlanetScale)
1. In the Render Dashboard, click **New +** $\rightarrow$ **PostgreSQL** or use a free MySQL service (e.g., [Aiven](https://aiven.io) or [Clever Cloud](https://www.clever-cloud.com)).
2. Note your connection URL, username, and password.

### Step 3: Deploy the Web Service on Render
1. Click **New +** $\rightarrow$ **Web Service**.
2. Connect your GitHub repository.
3. Configure the settings:
   - **Environment**: `Docker`
   - **Dockerfile Path**: `Dockerfile`
   - **Plan**: `Free`
4. Under **Environment Variables**, add:
   | Key | Value |
   | :--- | :--- |
   | `PORT` | `8080` |
   | `SPRING_DATASOURCE_URL` | Your remote MySQL JDBC URL |
   | `SPRING_DATASOURCE_USERNAME` | Your remote DB username |
   | `SPRING_DATASOURCE_PASSWORD` | Your remote DB password |
5. Click **Create Web Service**. Render will automatically build the Docker image and deploy it with a free HTTPS URL (e.g., `https://fake-news-detection.onrender.com`).

---

## 5. Option 4: Deploy Free on Railway (railway.app)

1. Sign in to [Railway](https://railway.app) using GitHub.
2. Click **New Project** $\rightarrow$ **Provision MySQL**.
3. In the same project, click **New** $\rightarrow$ **GitHub Repo** $\rightarrow$ select your `fake-news-detection` repository.
4. Railway will automatically detect the `Dockerfile` and start building.
5. In your web service **Variables**, link Railway's MySQL environment variables:
   - `SPRING_DATASOURCE_URL`: `jdbc:mysql://${{MYSQLHOST}}:${{MYSQLPORT}}/${{MYSQLDATABASE}}?createDatabaseIfNotExist=true&useSSL=false`
   - `SPRING_DATASOURCE_USERNAME`: `${{MYSQLUSER}}`
   - `SPRING_DATASOURCE_PASSWORD`: `${{MYSQLPASSWORD}}`
6. Click **Generate Domain** under Networking to get your public live URL.

---

## 6. Environment Variables Reference

| Variable | Default Value | Description |
| :--- | :--- | :--- |
| `PORT` | `8080` | HTTP port the server listens on |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/fake_news_db...` | JDBC URL for MySQL database |
| `SPRING_DATASOURCE_USERNAME` | `root` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `YOUR_PASSWORD` | Database password (falls back to embedded DB if placeholder) |
| `JAVA_OPTS` | `-Xms256m -Xmx512m` | JVM memory parameters |

---

## 7. Production Verification Checklist

- [x] Static frontend bundled inside executable JAR (`src/main/resources/static/`).
- [x] Dynamic API URL detection (`API_BASE_URL` works seamlessly locally & in cloud).
- [x] Environment variable overrides enabled in `application.properties`.
- [x] Production Dockerfile (multi-stage build, Alpine JRE runtime).
- [x] `docker-compose.yml` for unified app + database deployment.
- [x] Embedded database auto-fallback so deployment never crashes on DB latency.
- [x] Zero compilation errors and all automated tests passing.
