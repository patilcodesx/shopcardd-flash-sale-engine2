# 🛒 ShopCardd – Hyperlocal Flash Sale Engine

Backend service for managing **high-concurrency flash sales**, enabling merchants to create limited-inventory deals and users to safely discover and claim vouchers **without overselling**.

---

## 🚀 Tech Stack

- **Java 17**
- **Spring Boot**
- **PostgreSQL**
- **Redis**
- **Docker & Docker Compose**

---

## ✨ Features

- Create time-bound flash deals  
- Geo-based deal discovery  
- Redis-cached discovery results  
- Concurrency-safe voucher claiming  
- Distributed locking using Redis  
- Prevention of overselling and duplicate claims  

---

## 🧩 Architecture

Client
↓
Load Balancer
↓
Spring Boot API
↓
Redis (Distributed Lock + Cache)
↓
PostgreSQL

yaml
Copy code

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
bash
Copy code
docker ps
Expected output:

mathematica
Copy code
flashsale-redis   redis:7-alpine   Up
Connect to Redis CLI
bash
Copy code
docker exec -it flashsale-redis redis-cli
Test connection:

bash
Copy code
PING
Expected:

nginx
Copy code
PONG
Monitor Redis Locks & Cache
bash
Copy code
MONITOR
You will observe keys like:

css
Copy code
lock:deal:{dealId}
cache:deals:{lat}:{lng}:{radius}
🔗 API Endpoints
1️⃣ Create Deal
POST /deals

json
Copy code
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

bash
Copy code
/deals/discover?lat=19.0760&lng=72.8777&radius=5
Behavior
Active deals only

Geo-distance filtering (Haversine formula)

Redis cache enabled (TTL = 30 seconds)

Cache Key
css
Copy code
cache:deals:{lat}:{lng}:{radius}
3️⃣ Claim Deal
POST

bash
Copy code
/deals/{dealId}/claim?userId=u-1
🔐 Concurrency Control
Voucher claiming uses Redis Distributed Locking.

Lock Key
csharp
Copy code
lock:deal:{dealId}
Claim Flow
Acquire Redis lock (SET NX EX)

Validate deal existence

Check expiration

Check inventory

Prevent duplicate claims

Decrement inventory

Persist claim

Release lock safely

✅ Guarantees
Inventory never goes below zero

One voucher per user

No overselling

Safe under high concurrency

📊 Expected Outputs
Scenario	HTTP Status	Sample Response
Successful claim	200 OK	{ "status": "Success", "voucher_code": "SHOP-abc123" }
Already claimed	400 Bad Request	{ "message": "User already claimed this deal" }
Deal sold out	400 Bad Request	{ "message": "Deal sold out" }
Deal expired	400 Bad Request	{ "message": "Deal expired" }
Deal locked	400 Bad Request	{ "message": "Deal is currently being claimed" }

⚠️ Failure Handling
Failure	Behavior
Redis unavailable	Claims rejected (fail-safe)
Database error	Transaction rollback
Duplicate claim	Gracefully rejected
Invalid request	Proper HTTP error response

📦 Deployment Notes
Stateless Spring Boot services

Horizontally scalable

Redis handles high-contention operations

PostgreSQL remains source of truth

👨‍💻 Author
Bhavesh Patil

GitHub: https://github.com/patilcodesx

