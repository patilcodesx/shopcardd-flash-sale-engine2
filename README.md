# 🛒 ShopCardd – Hyperlocal Flash Sale Engine

Backend service for managing high-concurrency flash sales, enabling merchants to create limited-inventory deals and users to safely discover and claim vouchers without overselling.

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

```
Client
  ↓
Load Balancer
  ↓
Spring Boot API
  ↓
Redis (Distributed Lock + Cache)
  ↓
PostgreSQL
```


---

## 📁 Project Folder Structure

shopcardd-flash-sale-engine
│
├── docker-compose.yml
├── Dockerfile
├── README.md
│
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── shopcardd
│       │           └── flashsale
│       │               ├── controller
│       │               │   └── DealController.java
│       │               │
│       │               ├── service
│       │               │   ├── DealService.java
│       │               │   ├── ClaimService.java
│       │               │
│       │               ├── repository
│       │               │   ├── DealRepository.java
│       │               │   ├── ClaimRepository.java
│       │               │
│       │               ├── entity
│       │               │   ├── Deal.java
│       │               │   ├── Claim.java
│       │               │
│       │               ├── dto
│       │               │   ├── DealRequest.java
│       │               │   ├── DealResponse.java
│       │               │
│       │               ├── config
│       │               │   ├── RedisConfig.java
│       │               │   └── CacheConfig.java
│       │               │
│       │               └── FlashSaleApplication.java
│       │
│       └── resources
│           ├── application.yml
│           └── schema.sql
│
└── pom.xml



## ▶️ How to Run the Application

### Prerequisites

- Docker
- Docker Compose

---

### Start All Services

```bash
docker compose up --build
```


Docker Compose automatically starts:

- Spring Boot application
- PostgreSQL database
- Redis server

---

## 🌐 Running Services

| Service | Address |
|--------|---------|
| API | http://localhost:8080 |
| PostgreSQL | localhost:5432 |
| Redis | localhost:6379 |

---

## 🔴 Redis Setup & Verification

Redis runs automatically inside Docker.

Verify Redis container:

```bash
docker ps
```


Expected output:

flashsale-redis   redis:7-alpine   Up

---

Connect to Redis CLI:

```bash
docker exec -it flashsale-redis redis-cli
```


Test connection:

```bash
PING
```


Expected response:

PONG

---

Monitor Redis keys:

```bash
MONITOR
```


Common keys:

lock:deal:{dealId}
cache:deals:{lat}:{lng}:{radius}

---

## 🔗 API Endpoints

---

### Create Deal

POST /deals

```json
{
  "merchant_id": "merchant-123",
  "title": "Flat 50% Off",
  "total_vouchers": 100,
  "valid_until": "2026-12-31T23:59:59Z",
  "location": {
    "lat": 19.0760,
    "long": 72.8777
  }
}
```
---

### Discover Deals

GET /deals/discover?lat=19.0760&lng=72.8777&radius=5

Behavior:

- Active deals only
- Geo-distance filtering using Haversine formula
- Redis caching enabled

Cache details:

cache:deals:{lat}:{lng}:{radius}
TTL: 30 seconds

---

### Claim Deal

POST /deals/{dealId}/claim?userId=u-1

---

## 🔐 Concurrency Control

Redis distributed locking is used.

Lock key:

lock:deal:{dealId}

Claim flow:

1. Acquire Redis lock (SET NX EX)
2. Validate deal existence
3. Check expiration
4. Check inventory
5. Prevent duplicate claims
6. Decrement inventory
7. Persist claim
8. Release lock

---

## ✅ Guarantees

- Inventory never goes below zero
- One voucher per user
- No overselling
- Safe under heavy concurrency

---

## 📊 API Responses

| Scenario | HTTP Status | Sample Response |
|--------|-------------|----------------|
| Successful claim | 200 | { "status": "Success", "voucher_code": "SHOP-abc123" } |
| Already claimed | 400 | { "message": "User already claimed this deal" } |
| Deal sold out | 400 | { "message": "Deal sold out" } |
| Deal expired | 400 | { "message": "Deal expired" } |
| Deal locked | 400 | { "message": "Deal is currently being claimed" } |

---

## ⚠️ Failure Handling

| Failure | Behavior |
|------|------|
| Redis unavailable | Claims rejected (fail-safe) |
| Database error | Transaction rollback |
| Duplicate claim | Gracefully rejected |
| Invalid request | Proper HTTP error |

---

## 📦 Deployment Notes

- Stateless Spring Boot services
- Horizontally scalable
- Redis handles high-contention operations
- PostgreSQL remains source of truth

---

## 👨‍💻 Author

Bhavesh Patil  
GitHub: https://github.com/patilcodesx

---

Built for the ShopCardd Backend Engineering Assessment.
