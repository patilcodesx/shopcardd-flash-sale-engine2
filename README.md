# ShopCardd – Hyperlocal Flash Sale Engine

This project is a backend service built as part of the ShopCardd backend engineering assignment.  
It enables merchants to create time-bound flash deals and allows users to discover and safely claim vouchers under high concurrency.

---

## 🚀 Tech Stack

- Java 17
- Spring Boot
- PostgreSQL
- Redis
- Docker & Docker Compose

---

## 📌 System Capabilities

- Deal creation
- Geo-based deal discovery
- Secure voucher claiming under high concurrency

Design priorities:
- Data consistency
- Concurrency safety
- Horizontal scalability
- Low-latency responses

---

## 🧩 Architecture Overview

Client  
→ Load Balancer  
→ Spring Boot Application  
→ Redis (Locks / Cache)  
→ PostgreSQL  

Components:
- Spring Boot REST API
- PostgreSQL (persistent storage)
- Redis (distributed locking + caching)
- Docker (containerized deployment)

---

## ▶️ Run the Application

### Prerequisites
- Docker
- Docker Compose

### Start Services
```bash
docker compose up --build

Services:

API: http://localhost:8080

PostgreSQL: 5432

Redis: 6379

🛠 API Endpoints
1️⃣ Create Deal

POST /deals

{
  "merchant_id": "merchant-123",
  "title": "Flat 50% Off",# ShopCardd – Hyperlocal Flash Sale Engine

This project is a backend service built as part of the ShopCardd backend engineering assignment.  
It enables merchants to create time-bound flash deals and allows users to discover and safely claim vouchers under high concurrency.

---

## 🚀 Tech Stack

- Java 17
- Spring Boot
- PostgreSQL
- Redis
- Docker & Docker Compose

---

## 📌 System Capabilities

- Deal creation
- Geo-based deal discovery
- Secure voucher claiming under high concurrency

Design priorities:
- Data consistency
- Concurrency safety
- Horizontal scalability
- Low-latency responses

---

## 🧩 Architecture Overview

Client  
→ Load Balancer  
→ Spring Boot Application  
→ Redis (Locks / Cache)  
→ PostgreSQL  

Components:
- Spring Boot REST API
- PostgreSQL (persistent storage)
- Redis (distributed locking + caching)
- Docker (containerized deployment)

---

## ▶️ Run the Application

### Prerequisites
- Docker
- Docker Compose

### Start Services
```bash
docker compose up --build

Services:

API: http://localhost:8080

PostgreSQL: 5432

Redis: 6379

🛠 API Endpoints
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

GET /deals/discover?lat=19.0760&lng=72.8777&radius=5

Behavior:

Only active deals

Geo-filter using Haversine formula

Redis cache (TTL = 30 seconds)

Cache key format:

cache:deals:{lat}:{lng}:{radius}

3️⃣ Claim Deal (Concurrency-Safe)

POST /deals/{dealId}/claim?userId=u-1

Outcomes:

Success → voucher claimed

Duplicate claim → rejected

Sold out → rejected

Expired → rejected

🔐 Concurrency Handling (Core Logic)

Redis distributed lock is used to protect inventory.

Lock key:

lock:deal:{dealId}


Flow:

Acquire Redis lock (SET NX EX)

Validate deal

Check inventory

Check duplicate claim

Decrement inventory

Persist claim

Release lock safely

Guarantees:

No overselling

One voucher per user

Safe under heavy concurrency

🧠 Scenario 1: Offline Voucher Verification

Voucher token generated on claim (JWT / HMAC)

Embedded in QR code

Contains:

dealId

userId

expiry

signature

Merchant verifies signature offline

Logs synced when internet is restored

Benefits:

Offline-first

Tamper-proof

No backend dependency

🚀 Scenario 2: Scale to 1M Requests/Minute

Stateless Spring Boot services

Horizontal scaling via load balancer

Redis-first strategy

PostgreSQL:

Primary for writes

Read replicas for discovery

Indexed queries

Async processing for non-critical tasks

Result:

High throughput

Inventory integrity preserved

⚠️ Failure Handling

Redis down → claims rejected (fail-safe)

DB failure → transaction rollback

Clear error responses
  "total_vouchers": 100,
  "valid_until": "2026-12-31T23:59:59Z",
  "location": {
    "lat": 19.0760,
    "long": 72.8777
  }
}

2️⃣ Discover Deals

GET /deals/discover?lat=19.0760&lng=72.8777&radius=5

Behavior:

Only active deals

Geo-filter using Haversine formula

Redis cache (TTL = 30 seconds)

Cache key format:

cache:deals:{lat}:{lng}:{radius}

3️⃣ Claim Deal (Concurrency-Safe)

POST /deals/{dealId}/claim?userId=u-1

Outcomes:

Success → voucher claimed

Duplicate claim → rejected

Sold out → rejected

Expired → rejected

🔐 Concurrency Handling (Core Logic)

Redis distributed lock is used to protect inventory.

Lock key:

lock:deal:{dealId}


Flow:

Acquire Redis lock (SET NX EX)

Validate deal

Check inventory

Check duplicate claim

Decrement inventory

Persist claim

Release lock safely

Guarantees:

No overselling

One voucher per user

Safe under heavy concurrency

🧠 Scenario 1: Offline Voucher Verification

Voucher token generated on claim (JWT / HMAC)

Embedded in QR code

Contains:

dealId

userId

expiry

signature

Merchant verifies signature offline

Logs synced when internet is restored

Benefits:

Offline-first

Tamper-proof

No backend dependency

🚀 Scenario 2: Scale to 1M Requests/Minute

Stateless Spring Boot services

Horizontal scaling via load balancer

Redis-first strategy

PostgreSQL:

Primary for writes

Read replicas for discovery

Indexed queries

Async processing for non-critical tasks

Result:

High throughput

Inventory integrity preserved

⚠️ Failure Handling

Redis down → claims rejected (fail-safe)

DB failure → transaction rollback

Clear error responses