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
- **Atomic Redis inventory management**  
- **Distributed locking with safe Lua unlock**  
- **Duplicate claim prevention using Redis SET**  
- High-concurrency safe architecture

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

```
shopcardd-flash-sale-engine
│
├── docker-compose.yml
├── Dockerfile
├── README.md
├── ARCHITECTURE.md
├── TESTING.md
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
│ │ ├── model
│ │ ├── dto
│ │ └── config
│ │
│ └── resources
│ ├── application.properties
│ └── application-docker.yml
│
└── pom.xml

```

---

## ▶️ How to Run the Application

### Prerequisites

- Docker
- Docker Compose

---

### Start All Services

```bash
docker compose up --build
```
This will start:

- Spring Boot API
- PostgreSQL
- Redis


## 🌐 Running Services
| Service | Address |
|--------|---------|
| API | http://localhost:8080 |
| PostgreSQL | localhost:5432 |
| Redis | localhost:6379 |


## 🔴 Redis Setup & Verification


Verify Redis container:

```bash
docker ps
```


Expected output:
```bash
flashsale-redis   redis:7-alpine   Up
```

Connect to Redis CLI:
```bash
docker exec -it flashsale-redis redis-cli
```

Test connection:
```bash
PING
```


Expected:
```bash
PONG
```

🔑 Redis Keys Used:
```bash
MONITOR
```

Common keys:
```bash
lock:deal:{dealId}
deal:inventory:{dealId}
deal:users:{dealId}
cache:deals:{lat}:{lng}:{radius}
```

## 🔗 API Endpoints
### ➕ Create Deal
```bash
POST /deals
```
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
### Discover Deals
```bash
GET /deals/discover?lat=19.0760&lng=72.8777&radius=5
```

**Behavior**

- Active deals only
- Geo-distance filtering (Haversine formula)
- Redis caching enabled


Cache key:
```bash
cache:deals:{lat}:{lng}:{radius}
```

TTL: 30 seconds

### Claim Deal
```bash
POST /deals/{dealId}/claim?userId=u-1
```

🔐 Concurrency Control

The system uses Redis-first concurrency handling:

Distributed lock (SET NX EX)
-  Atomic inventory decrement (DECR)
-  Safe lock release using Lua script
-  Redis SET for one-voucher-per-user enforcement

Lock key:
```bash
lock:deal:{dealId}
```
### Claim Flow

1. Acquire Redis lock (`SET NX EX`)
2. Validate deal existence
3. Check expiration
4. Check inventory
5. Prevent duplicate claims
6. Decrement inventory
7. Persist claim
8. Release lock safely


### ✅ Guarantees
- Inventory never goes below zero
- One voucher per user
- No overselling
- Safe under heavy concurrency


Safe under heavy concurrency

## 📊 API Responses

| Scenario | HTTP Status | Response Body |
|----------|--------------|----------------|
| Successful claim | 200 OK | `{ "status": "success", "voucher_code": "SHOP-abc123" }` |
| Already claimed | 400 Bad Request | `{ "status": "fail", "reason": "User already claimed" }` |
| Deal sold out | 409 Conflict | `{ "status": "fail", "reason": "Deal Sold Out" }` |
| Deal expired | 400 Bad Request | `{ "status": "fail", "reason": "Deal expired" }` |
| Deal locked | 400 Bad Request | `{ "status": "fail", "reason": "Deal is currently being claimed. Try again." }` |
| Deal not found | 404 Not Found | `{ "status": "fail", "reason": "Deal not found" }` |

---

## ⚠️ Failure Handling

| Failure | Behavior |
|--------|----------|
| Redis unavailable | Requests fail safely |
| Database error | Transaction rollback |
| Duplicate claim | Redis + DB protection |
| Invalid request | Proper HTTP response |

---

## 🧪 Testing

All step-by-step testing commands are available in:

TESTING.md


### Includes

- cURL API tests  
- Redis key verification  
- PostgreSQL verification  
- High-concurrency simulations  


## 📦 Deployment Notes

- Stateless Spring Boot services  
- Horizontally scalable  
- Redis handles high-contention operations  
- PostgreSQL remains source of truth  




## 👨‍💻 Author

**Bhavesh Patil**
GitHub: https://github.com/patilcodesx



