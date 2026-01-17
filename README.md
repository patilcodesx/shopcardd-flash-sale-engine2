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

## ▶️ How to Run the Application

### ✅ Prerequisites

- Docker
- Docker Compose

---

### ▶️ Start All Services

```bash
docker compose up --build
Docker Compose automatically starts:

Spring Boot application

PostgreSQL database

Redis server

🌐 Running Services
Service	Address
API	http://localhost:8080

PostgreSQL	localhost:5432
Redis	localhost:6379

🔴 Redis Setup & Verification

Redis runs automatically inside Docker.

Verify Redis Container
docker ps


Expected output:

flashsale-redis   redis:7-alpine   Up

Connect to Redis CLI
docker exec -it flashsale-redis redis-cli


Test connection:

PING


Expected response:

PONG

Monitor Redis Locks & Cache
MONITOR


Common keys observed:

lock:deal:{dealId}
cache:deals:{lat}:{lng}:{radius}

🔗 API Endpoints
1️⃣ Create Deal

POST /deals

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

2️⃣ Discover Deals

GET

/deals/discover?lat=19.0760&lng=72.8777&radius=5

Behavior

Returns active (non-expired) deals only

Geo-distance filtering using Haversine formula

Redis caching enabled

Cache Details
Item	Value
Cache Key	cache:deals:{lat}:{lng}:{radius}
TTL	30 seconds
3️⃣ Claim Deal

POST

/deals/{dealId}/claim?userId=u-1

🔐 Concurrency Control

Voucher claiming uses Redis Distributed Locking.

Lock Key
lock:deal:{dealId}

Claim Execution Flow

Acquire Redis lock (SET NX EX)

Validate deal existence

Verify deal expiry

Check remaining inventory

Ensure one voucher per user

Atomically decrement inventory

Persist claim transaction

Release Redis lock

✅ Guarantees

Inventory never drops below zero

Exactly one voucher per user

No overselling

Thread-safe under heavy concurrency

📊 API Response Scenarios
Scenario	HTTP Status	Example Response
Successful claim	200 OK	{ "status": "Success", "voucher_code": "SHOP-abc123" }
Already claimed	400 Bad Request	{ "message": "User already claimed this deal" }
Deal sold out	400 Bad Request	{ "message": "Deal sold out" }
Deal expired	400 Bad Request	{ "message": "Deal expired" }
Deal locked	400 Bad Request	{ "message": "Deal is currently being claimed" }
⚠️ Failure Handling
Failure Case	System Behavior
Redis unavailable	Claim rejected (fail-safe)
Database failure	Transaction rollback
Duplicate request	Gracefully rejected
Invalid input	Proper HTTP validation error
📦 Deployment Notes

Stateless Spring Boot services

Horizontal scaling supported

Redis handles high-contention operations

PostgreSQL remains system of record

👨‍💻 Author

Bhavesh Patil
Backend Developer – Java & Spring Boot

🔗 GitHub: https://github.com/patilcodesx

