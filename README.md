# 🐳 Production-Grade Dockerized Backend System

> A comprehensive containerization project demonstrating production-style backend architecture with Spring Boot, MySQL, Redis caching, health checks, and multi-container orchestration using Docker Compose.

![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=flat-square&logo=docker)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-Multi--Container-2496ED?style=flat-square&logo=docker)
![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=spring-boot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-005C84?style=flat-square&logo=mysql)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=flat-square&logo=redis)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#-architecture-diagram)
- [Technologies](#-technologies-used)
- [Features](#-docker-features-implemented)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [Docker Compose Configuration](#-docker-compose-detailed-configuration)
- [Caching Strategy](#-caching-strategy)
- [Monitoring & Debugging](#-monitoring--debugging)
- [Best Practices](#-docker-best-practices)
- [Contributing](#-contributing)
- [License](#-license)

---

## Overview

This project demonstrates **enterprise-grade containerization practices** for a Java-based backend system. It showcases:

✅ **Multi-Container Architecture** - Orchestrated services with Docker Compose  
✅ **Data Persistence** - Named volumes for stateful containers  
✅ **Caching Strategy** - Redis integration for performance optimization  
✅ **Health Checks** - Automated container health monitoring  
✅ **Network Management** - Custom bridge networks for service isolation  
✅ **Environment Configuration** - 12-factor app principles with `.env` files  
✅ **Resource Optimization** - Multi-stage builds and resource limits  
✅ **Production Patterns** - Logging, restart policies, dependency management  

---

## 🏗️ Architecture Diagram

### Service Layer Architecture

```
┌────────────────────────────────────────────────────────────────────┐
│                  DOCKER COMPOSE ARCHITECTURE                       │
│                  (Custom Bridge Network)                           │
└────────────────────────────────────────────────────────────────────┘

                    ┌─────────────────────────┐
                    │   Client Layer          │
                    │  (Browser/Postman)      │
                    └────────────┬────────────┘
                                 │ HTTP/REST
                    ┌────────────▼────────────┐
                    │   Spring Boot Backend   │
                    │   Port: 8080            │
                    │   Container: backend    │
                    │   Status: Healthy ✓     │
                    └────────┬───────┬────────┘
                             │       │
                ┌────────────┘       └────────────┐
                │                                 │
        ┌───────▼──────┐              ┌──────────▼────────┐
        │   Redis      │              │   MySQL Database  │
        │   Port: 6379 │              │   Port: 3306      │
        │   Memory     │              │   Persistent      │
        │   Cache      │              │   Storage         │
        │   Container: │              │   Container:      │
        │   cache      │              │   database        │
        └──────────────┘              └───────────────────┘
             ▲                                  ▲
             │ Caching Layer                    │ JDBC/ORM
             │ (Read/Write)                     │ (Persistence)
             └──────────────┬────────────────────┘
                            │
                  Spring Data JPA
                   (Repository)
```

### Data Flow Diagram

```
Request Flow:
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ GET /api/users/{id}
       ▼
┌──────────────────────┐
│  Spring Boot         │
│  REST Controller     │
└──────┬───────────────┘
       │
       ├─→ Check Redis Cache ─────────┐
       │                              │
       │   Key: "user:{id}"           │
       │   TTL: 5 minutes             │
       │                              │
       │  ┌──────────────────────┐    │
       │  │ Cache HIT ✓          │    │
       │  │ Return from Memory   │    │
       │  │ (Fast ~1ms)          │    │
       │  └──────┬───────────────┘    │
       │         │                    │
       │  ┌──────▼──────────────┐     │
       │  │ Cache MISS ✗        │     │
       │  │ Query Database ──────────►└──┐
       │  │                    │         │
       │  └────────────────────┘         │
       │                                 ▼
       │  ┌──────────────────────────────┐
       │  │  MySQL Query Results         │
       │  │  (Slower ~10-100ms)          │
       │  └──────┬───────────────────────┘
       │         │
       │  ┌──────▼────────────────────┐
       │  │ Store in Redis            │
       │  │ Key: "user:{id}"          │
       │  │ TTL: 5 minutes (300 secs) │
       │  └──────┬────────────────────┘
       │         │
       └─────────┼─────────────────┐
                 │                 │
                 ▼                 ▼
         Response to Client
```

### Container Networking

```
┌─────────────────────────────────────────────────────┐
│        Docker Bridge Network: app-network           │
│        Driver: bridge                               │
│        Subnet: 172.18.0.0/16                        │
└─────────────────────────────────────────────────────┘

    ┌──────────────────┐  ┌─────────────────────┐  ┌──────────────┐
    │    Backend       │  │    Redis            │  │   MySQL      │
    │   Container      │  │   Container         │  │  Container   │
    ├──────────────────┤  ├─────────────────────┤  ├──────────────┤
    │  IP: 172.18.0.2  │  │  IP: 172.18.0.3     │  │ IP: 172.18.. │
    │  Hostname:       │  │  Hostname:          │  │ Hostname:    │
    │  backend         │  │  redis              │  │ mysql        │
    │                  │  │                     │  │              │
    │  Port: 8080      │  │  Port: 6379         │  │ Port: 3306   │
    │  External: 8080  │  │  External: 6379     │  │ External:    │
    └──────────────────┘  └─────────────────────┘  └──────────────┘
         │                        │                        │
         └────────────────────────┼────────────────────────┘
                      Service Discovery
                 (Docker DNS: service_name)
```

---

## 🛠️ Technologies Used

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Language** | Java | 17+ | Application development |
| **Framework** | Spring Boot | 3.x+ | REST API framework |
| **JPA** | Hibernate | 6.x | Object-Relational Mapping |
| **Data Access** | Spring Data JPA | 3.x | Database abstraction |
| **Caching** | Spring Data Redis | 3.x | Cache management |
| **Database** | MySQL | 8.0 | Relational database |
| **Cache Store** | Redis | 7.x | In-memory data store |
| **Build Tool** | Maven | 3.9+ | Dependency management |
| **Containerization** | Docker | 20.10+ | Container runtime |
| **Orchestration** | Docker Compose | 2.x+ | Multi-container management |
| **Testing** | JUnit 5 | 5.x | Unit testing |

---

## 🐳 Docker Features Implemented

### ✅ Multi-Stage Dockerfile

```dockerfile
# Stage 1: Build using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package

# Stage 2: Lightweight runtime
FROM eclipse-temurin:17-jre-jammy
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Benefits:**
- 🎯 Smaller final image (Build artifacts excluded)
- ⚡ Faster deployments
- 🔒 Reduced attack surface

### ✅ Named Volumes for Persistence

```yaml
volumes:
  mysql_data:          # Persists database even after container deletion
  redis_data:          # Persists Redis cache
```

### ✅ Custom Bridge Network

```yaml
networks:
  app-network:
    driver: bridge
```

**Benefits:**
- 🔌 Service-to-service communication via DNS
- 🔒 Container isolation
- 🎯 Predictable hostnames (e.g., `redis` instead of IP)

### ✅ Health Checks

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 40s
```

**Monitoring:**
- ✅ Healthy - Service ready
- ⚠️ Unhealthy - Needs restart
- ⏳ Starting - Initial startup phase

### ✅ Environment Configuration

```env
# Database
DB_HOST=mysql
DB_PORT=3306
DB_NAME=docker_db
DB_USER=root
DB_PASSWORD=root123

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# Application
APP_PORT=8080
LOG_LEVEL=INFO
```

### ✅ Service Dependency Management

```yaml
depends_on:
  mysql:
    condition: service_healthy
  redis:
    condition: service_started
```

### ✅ Resource Limits

```yaml
resources:
  limits:
    cpus: '1'
    memory: 1024M
  reservations:
    cpus: '0.5'
    memory: 512M
```

### ✅ Restart Policies

```yaml
restart_policy:
  condition: on-failure
  delay: 5s
  max_attempts: 3
  window: 120s
```

---

## 🚀 Getting Started

### Prerequisites

- **Docker** 20.10+ - [Install Docker Desktop](https://www.docker.com/products/docker-desktop)
- **Docker Compose** 2.x+ - (Usually included with Docker Desktop)
- **Git** - For cloning the repository
- **Postman** (optional) - For API testing

### Verify Installation

```bash
# Check Docker
docker --version
# Expected: Docker version 20.10+

# Check Docker Compose
docker-compose --version
# Expected: Docker Compose version 2.x+
```

---

## 🏃 Quick Start (3 Steps)

### Step 1: Clone Repository

```bash
git clone https://github.com/chala2001/docker-project.git
cd docker-project
```

### Step 2: Build and Start Containers

```bash
# Build images and start services
docker-compose up --build -d

# View logs
docker-compose logs -f

# Check service status
docker-compose ps
```

**Expected Output:**
```
STATUS              PORTS
Up (healthy)        0.0.0.0:8080->8080/tcp
Up (healthy)        0.0.0.0:6379->6379/tcp
Up (healthy)        3306/tcp, 33060/tcp
```

### Step 3: Test API

```bash
# Health check
curl http://localhost:8080/actuator/health

# Get all users
curl http://localhost:8080/api/users

# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'

# Stop services
docker-compose down
```

---

## 📂 Project Structure

```
docker-project/
│
├── backend/
│   ├── backend/                           # Spring Boot application
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/example/docker/
│   │   │   │   │       ├── DemoApplication.java      # Main entry
│   │   │   │   │       ├── controller/
│   │   │   │   │       │   └── UserController.java   # REST endpoints
│   │   │   │   │       ├── service/
│   │   │   │   │       │   └── UserService.java      # Business logic
│   │   │   │   │       ├── repository/
│   │   │   │   │       │   └── UserRepository.java   # Data access
│   │   │   │   │       ├── entity/
│   │   │   │   │       │   └── User.java             # JPA entity
│   │   │   │   │       └── dto/
│   │   │   │   │           └── UserDTO.java          # Data transfer object
│   │   │   │   └── resources/
│   │   │   │       ├── application.properties        # Configuration
│   │   │   │       └── application-docker.properties # Docker config
│   │   │   └── test/
│   │   │       └── java/...
│   │   ├── pom.xml                        # Maven POM
│   │   ├── Dockerfile                     # Multi-stage build
│   │   └── .dockerignore
│   │
│   └── Dockerfile                         # Backend container
│
├── docker-compose.yml                     # Orchestration file
├── .env                                   # Environment variables
├── .env.example                           # Example env file
├── .dockerignore                          # Docker build ignore
├── .gitignore                             # Git ignore
├── README.md                              # Documentation
└── docs/
    ├── CACHING_STRATEGY.md               # Redis caching patterns
    ├── DEPLOYMENT.md                     # Production deployment
    ├── MONITORING.md                     # Health & logging
    └── TROUBLESHOOTING.md                # Common issues
```

---

## 🐳 Docker Compose Detailed Configuration

### `docker-compose.yml` - Complete File

```yaml
version: '3.8'

services:
  # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  # MySQL Database Service
  # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  mysql:
    image: mysql:8.0
    container_name: app-mysql
    restart: on-failure
    
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: ${DB_NAME}
      MYSQL_USER: ${DB_USER}
      MYSQL_PASSWORD: ${DB_PASSWORD}
    
    ports:
      - "3306:3306"
    
    volumes:
      - mysql_data:/var/lib/mysql
    
    networks:
      - app-network
    
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    
    resources:
      limits:
        cpus: '1'
        memory: 512M
      reservations:
        cpus: '0.5'
        memory: 256M

  # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  # Redis Cache Service
  # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  redis:
    image: redis:7-alpine
    container_name: app-redis
    restart: on-failure
    
    ports:
      - "6379:6379"
    
    command: redis-server --appendonly yes --requirepass ${REDIS_PASSWORD}
    
    volumes:
      - redis_data:/data
    
    networks:
      - app-network
    
    healthcheck:
      test: ["CMD", "redis-cli", "--raw", "incr", "ping"]
      interval: 30s
      timeout: 10s
      retries: 3
    
    resources:
      limits:
        cpus: '0.5'
        memory: 256M

  # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  # Spring Boot Backend Service
  # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
      args:
        MAVEN_CLI_OPTS: "-DskipTests"
    
    container_name: app-backend
    restart: on-failure
    
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
      SPRING_DATASOURCE_USERNAME: ${DB_USER}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      SPRING_REDIS_HOST: ${REDIS_HOST}
      SPRING_REDIS_PORT: ${REDIS_PORT}
      SPRING_REDIS_PASSWORD: ${REDIS_PASSWORD}
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      SPRING_APPLICATION_JSON: '{"server":{"port":8080}}'
    
    ports:
      - "8080:8080"
    
    networks:
      - app-network
    
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    
    resources:
      limits:
        cpus: '1.5'
        memory: 1024M
      reservations:
        cpus: '0.75'
        memory: 512M

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Named Volumes (Data Persistence)
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
volumes:
  mysql_data:
    driver: local
  redis_data:
    driver: local

# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
# Custom Bridge Network
# ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
networks:
  app-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.18.0.0/16
```

### `.env` Configuration File

```env
# Database Configuration
DB_HOST=mysql
DB_PORT=3306
DB_NAME=docker_db
DB_USER=appuser
DB_PASSWORD=secure_password_123

# Redis Configuration
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=redis_password_123

# Application Configuration
APP_PORT=8080
LOG_LEVEL=INFO
ENVIRONMENT=production
```

---

## 💾 Caching Strategy

### Cache Flow Diagram

```
API Request
    ├─→ Spring Boot Service
    │   └─→ @Cacheable("users")
    │       ├─→ Check Redis: "users:user_id"
    │       │   ├─ HIT  → Return cached (1ms)
    │       │   └─ MISS → Query MySQL
    │       │           → Store in Redis
    │       │           → Return (100ms)
    │       └─→ Cache Config:
    │           ├─ TTL: 5 minutes (300 sec)
    │           ├─ Eviction: LRU (Least Recently Used)
    │           └─ Key Prefix: "users:"
    └─→ Response to Client
```

### Implementation Example

```java
@Service
public class UserService {
    
    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        // Only executed if cache MISS
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    @CachePut(value = "users", key = "#result.id")
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public void clearAllCache() {
        // Clears entire cache
    }
}
```

### Cache Configuration (Redis)

```yaml
spring:
  redis:
    host: ${REDIS_HOST}
    port: ${REDIS_PORT}
    password: ${REDIS_PASSWORD}
    timeout: 60000
    jedis:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
  cache:
    type: redis
    redis:
      time-to-live: 300000  # 5 minutes in milliseconds
```

---

## 🔍 Monitoring & Debugging

### View Running Containers

```bash
# List all containers
docker-compose ps

# View real-time logs
docker-compose logs -f

# Logs from specific service
docker-compose logs -f backend

# Last 100 lines
docker-compose logs --tail 100 mysql
```

### Execute Commands in Container

```bash
# Interactive shell
docker-compose exec backend sh

# Run single command
docker-compose exec mysql mysql -uroot -p$DB_PASSWORD -e "SHOW DATABASES;"

# Check environment variables
docker-compose exec backend env

# View container stats
docker stats
```

### Network Inspection

```bash
# Inspect network
docker network inspect docker-project_app-network

# Test DNS resolution
docker-compose exec backend ping redis

# Test connectivity
docker-compose exec backend curl http://mysql:3306
```

### Database Inspection

```bash
# Connect to MySQL
docker-compose exec mysql mysql -uroot -p$DB_PASSWORD

# Show tables
SHOW TABLES;

# Query data
SELECT * FROM users;

# Check connections
SHOW PROCESSLIST;
```

### Redis Inspection

```bash
# Connect to Redis CLI
docker-compose exec redis redis-cli -a $REDIS_PASSWORD

# Check cache keys
KEYS *

# Get specific key
GET "users:1"

# Monitor operations
MONITOR

# Cache statistics
INFO stats
```

---

## 🛡️ Docker Best Practices

### ✅ Security

```dockerfile
# ✅ DO: Run as non-root user
RUN useradd -m -u 1000 appuser
USER appuser

# ❌ DON'T: Run as root
# USER root

# ✅ DO: Use specific base image tags
FROM openjdk:17-jdk-slim

# ❌ DON'T: Use latest tag
# FROM openjdk:latest
```

### ✅ Resource Efficiency

```yaml
# ✅ DO: Set resource limits
resources:
  limits:
    cpus: '1'
    memory: 1024M
  reservations:
    cpus: '0.5'
    memory: 512M

# ❌ DON'T: Unlimited resources (can crash host)
```

### ✅ Logging

```yaml
# ✅ DO: Use structured logging
logging:
  driver: "json-file"
  options:
    max-size: "10m"
    max-file: "3"

# View logs
docker-compose logs --tail 100 backend
```

### ✅ Health Checks

```yaml
# ✅ DO: Implement health checks
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
  interval: 30s
  timeout: 10s
  retries: 3

# ❌ DON'T: Skip health checks (can't detect unhealthy containers)
```

### ✅ Environment Variables

```bash
# ✅ DO: Use .env files
docker-compose up  # Automatically reads .env

# ✅ DO: Never commit sensitive data
echo ".env" >> .gitignore

# ❌ DON'T: Hardcode passwords in files
```

---

## 🔧 Troubleshooting

### Common Issues

**1. Container exits immediately**
```bash
# Check logs
docker-compose logs backend

# Solution: Verify database connectivity in logs
```

**2. Port already in use**
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>

# Or change port in docker-compose.yml
ports:
  - "8081:8080"  # Map to different port
```

**3. Database connection refused**
```bash
# Check if MySQL is healthy
docker-compose exec mysql mysqladmin ping -h localhost

# Solution: Wait for MySQL startup (add start_period)
healthcheck:
  start_period: 60s
```

**4. Redis connection timeout**
```bash
# Test Redis connection
docker-compose exec backend redis-cli -h redis ping

# Verify credentials
docker-compose exec redis redis-cli -a $REDIS_PASSWORD ping
```

---

## 📊 Performance Optimization

### Cache Hit Ratio

```bash
# Monitor cache performance
docker-compose exec redis redis-cli INFO stats

# Expected output:
# keyspace_hits: 95
# keyspace_misses: 5
# Hit Ratio: 95/(95+5) = 95%
```

### Database Optimization

```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_created_at ON users(created_at);

-- Monitor slow queries
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;
```

---

## 🚀 Deployment

### Deploy to Production

See [DEPLOYMENT.md](docs/DEPLOYMENT.md) for:
- AWS ECR (Elastic Container Registry)
- ECS (Elastic Container Service)
- Docker Swarm
- Kubernetes deployment

---

## 🤝 Contributing

We welcome contributions!

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/improvement`)
3. Commit changes (`git commit -m 'feat: add improvement'`)
4. Push to branch (`git push origin feature/improvement`)
5. Open a Pull Request

---

## 📝 License

Distributed under the **MIT License**. See [LICENSE](LICENSE) for details.

---

## 👨‍💻 Author

**Chalaka Samith** - Full-Stack & DevOps Engineer  
GitHub: [@chala2001](https://github.com/chala2001)

---

<div align="center">

**Made with ❤️ for the Docker community**

[⬆ back to top](#-production-grade-dockerized-backend-system)

</div>
