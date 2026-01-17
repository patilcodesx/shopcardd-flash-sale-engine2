# 🛒 ShopCardd – Hyperlocal Flash Sale Engine

Backend service for managing **high-concurrency flash sales**, enabling merchants to create limited-inventory deals and users to safely discover and claim vouchers **without overselling**.

---

## 🚀 Tech Stack

| Layer | Technology |
|------|-----------|
| Language | Java 17 |
| Framework | Spring Boot |
| Database | PostgreSQL |
| Cache & Locking | Redis |
| Containerization | Docker & Docker Compose |

---

## ✨ Features

- Create time-bound flash deals
- Geo-based deal discovery
- Redis-cached discovery results
- Concurrency-safe voucher claiming
- Distributed locking using Redis
- Prevention of overselling and duplicate claims

---

## 🧩 System Architecture

Client
↓
Load Balancer
↓
Spring Boot API
↓
Redis (Distributed Lock + Cache)
↓
PostgreSQL


---

## 📁 Project Folder Structure

shopcardd-flash-sale-engine
│
├── docker-compose.yml
├── Dockerfile
├── README.md
│
├── src
│ └── main
│ ├── java
│ │ └── com
│ │ └── shopcardd
│ │ └── flashsale
│ │ ├── controller
│ │ ├── service
│ │ ├── repository
│ │ ├── entity
│ │ ├── dto
│ │ └── config
│ └── resources
│ ├── application.yml
│ └── schema.sql
│
└── pom.xml


---

## ▶️ How to Run the Application

### Prerequisites

- Docker
- Docker Compose

---

### Start All Services

```bash
docker compose up --build

This will start:

Spring Boot API

PostgreSQL

Redis

🌐 Running Services
Service	Address
API	http://localhost:8080

PostgreSQL	localhost:5432
Redis	localhost:6379

Redis Setup & Verification

Verify Redis container:
docker ps

Expected output:
flashsale-redis   redis:7-alpine   Up

Connect to Redis CLI:
docker exec -it flashsale-redis redis-cli

Test connection:
PING

Expected:
PONG

Monitor Redis keys:
MONITOR

Common keys:
lock:deal:{dealId}
cache:deals:{lat}:{lng}:{radius}

