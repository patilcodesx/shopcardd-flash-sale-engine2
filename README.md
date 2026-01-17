# ShopCardd – Hyperlocal Flash Sale Engine

This project is a backend service built as part of the **ShopCardd Backend Engineering Assignment**.

It enables merchants to create time-bound flash deals and allows users to discover and safely claim vouchers under **high-concurrency conditions**, ensuring that inventory is never oversold.

---

## 🚀 Tech Stack

- Java 17
- Spring Boot
- PostgreSQL
- Redis
- Docker & Docker Compose

---

## 📌 System Capabilities

- Create flash deals with limited inventory
- Discover nearby deals using geo-location
- Claim vouchers safely under heavy concurrency
- Prevent overselling and duplicate claims

### Design Priorities

- Data consistency
- Concurrency safety
- Horizontal scalability
- Low-latency responses

---

## 🧩 Architecture Overview

Client
↓
Load Balancer
↓
Spring Boot Application
↓
Redis (Distributed Lock + Cache)
↓
PostgreSQL

yaml
Copy code

### Components

- **Spring Boot REST API** – business logic
- **PostgreSQL** – persistent data storage
- **Redis**
  - Distributed locking for flash claims
  - Caching for deal discovery
- **Docker** – containerized deployment

---

## ▶️ How to Run the Application

### Prerequisites

- Docker
- Docker Compose

---

### Start the Application

```bash
docker compose up --build
Services
Service	URL / Port
API	http://localhost:8080
PostgreSQL	5432
Redis	6379

🛠 API Endpoints
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
Returns only active deals

Geo-filtered using Haversine formula

Filters by radius in kilometers

Uses Redis caching

Redis Cache
TTL: 30 seconds

Key format:

css
Copy code
cache:deals:{lat}:{lng}:{radius}
3️⃣ Claim Deal (Concurrency-Safe)
POST

bash
Copy code
/deals/{dealId}/claim?userId=u-1
Possible Outcomes
Scenario	Result
Successful claim	Voucher issued
Duplicate claim	Rejected
Sold out	Rejected
Deal expired	Rejected

🔐 Concurrency Handling (Core Challenge)
To prevent race conditions when multiple users claim simultaneously, a Redis distributed lock is used.

Lock Key
csharp
Copy code
lock:deal:{dealId}
Claim Flow
Acquire Redis lock

pgsql
Copy code
SET lock:deal:{dealId} NX EX 10
Validate deal existence

Check expiration time

Check remaining inventory

Ensure user has not already claimed

Decrement inventory

Persist claim

Release lock safely

Guarantees
✅ Inventory never goes below zero
✅ One voucher per user
✅ Safe under high concurrency
✅ No race conditions

🧠 Scenario 1: Offline Voucher Verification (Offline Jugaad)
Problem
Merchant device has no internet connectivity.

Solution
Voucher token generated during claim

Token is embedded into a QR code

Token signed using JWT / HMAC

Token Contains
dealId

userId

expiry timestamp

cryptographic signature

Offline Verification
Merchant validates signature locally

No backend call required

Logs synced once internet is restored

Benefits
Offline-first

Tamper-proof

Secure verification

No server dependency

🚀 Scenario 2: Scaling to 1 Million Requests per Minute
Architecture Strategy
Stateless Spring Boot services

Horizontal scaling behind load balancer

Redis used as first-level system

PostgreSQL optimized for writes

Database Strategy
Primary database for writes

Read replicas for discovery traffic

Indexed queries

Connection pooling

Performance Enhancements
Redis caching

Distributed locking

Async processing for non-critical operations

Result
High throughput

Stable performance

Inventory integrity preserved

⚠️ Failure Handling
Failure	Behavior
Redis unavailable	Claims rejected (fail-safe)
Database error	Transaction rollback
Invalid input	Proper HTTP error
Duplicate claim	Rejected gracefully

✅ Summary
Fully containerized backend

Safe flash-sale concurrency handling

Redis-based distributed locking

Geo-based deal discovery

Horizontally scalable architecture

Production-ready design

Author: Bhavesh Patil
GitHub: https://github.com/patilcodesx
