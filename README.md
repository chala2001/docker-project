# 🚀 Dockerized Spring Boot Backend with MySQL & Redis

## 📌 Project Overview

This project demonstrates a production-style backend system built with:

- Spring Boot (REST API)
- MySQL (Relational Database)
- Redis (Caching Layer)
- Docker & Docker Compose
- Environment-based configuration
- Health checks
- Multi-stage Docker builds

The system is fully containerized and runs as a multi-container architecture.

---

## 🏗 Architecture

Backend (Spring Boot)
      ↓
Redis (Cache)
      ↓
MySQL (Persistent Database)

All services communicate over a custom Docker network.

---

## ⚙ Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Data Redis
- MySQL 8
- Redis 7
- Docker
- Docker Compose

---

## 🐳 Docker Features Implemented

- Multi-stage Dockerfile
- Named volumes for database persistence
- Custom bridge network
- Container restart policies
- Health checks
- Environment variables via `.env`
- Service dependency management
- Resource limits configuration

---

## 🔁 Caching Strategy

- User data cached in Redis
- TTL applied (Time-To-Live)
- Cache fallback to MySQL
- Write-through pattern for updates

---

## 📂 Project Structure

backend/
├── src/
├── Dockerfile
├── docker-compose.yml
├── .env
└── pom.xml

---

## 🚀 How to Run

### 1️⃣ Clone Repository

git clone <your-repo-url>

### 2️⃣ Navigate to project

cd backend

### 3️⃣ Run Docker Compose

docker compose up --build

### 4️⃣ Access API

http://localhost:8080/api/users

---

## 🧠 What This Project Demonstrates

- Container orchestration fundamentals
- Service-to-service networking
- Stateful container management
- Environment-based configuration
- Production-style backend containerization

---

## 🎯 Future Improvements

- CI/CD Pipeline (GitHub Actions)
- Kubernetes Deployment
- Monitoring (Prometheus + Grafana)
- Nginx Reverse Proxy
- API Gateway
- Multi-service architecture

---

## 👨‍💻 Author
Chalaka Samith
