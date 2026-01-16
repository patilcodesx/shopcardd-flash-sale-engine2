# Architecture & Design – Hyperlocal Flash Sale Engine

## 1. System Overview

This service is a hyperlocal flash-sale backend engine designed to allow merchants to create time-bound deals and enable users to discover and claim vouchers safely under high concurrency.

The system exposes REST APIs for:
- Deal creation
- Location-based deal discovery
- Secure voucher claiming

The design prioritizes:
- Data consistency
- Concurrency safety
- Horizontal scalability
- Low-latency responses

---

## 2. High-Level Architecture

### Core Components
- **Spring Boot REST API** – Core business logic and API layer
- **PostgreSQL** – Persistent storage for deals and claims
- **Redis** – Distributed locking (and optional caching)
- **Docker** – Containerized deployment
- **Load Balancer** – Horizontal scaling in production environments

### Request Flow

Client → Load Balancer → Spring Boot Application
↓
Redis (Distributed Lock)
↓
PostgreSQL


---

## 3. Deal Discovery Design

### Discovery Flow
1. Fetch active deals (not expired and inventory > 0)
2. Apply geo-distance filtering using the Haversine formula
3. Return only deals within the requested radius

### Performance Optimization (Proposed)
To reduce database load during high read traffic, discovery results can be cached in Redis with a short TTL (e.g., 30 seconds).

**Cache Key Format**
deals:{lat}:{lng}:{radius}


> Note: This caching strategy is a proposed optimization and not mandatory for correctness.

---

## 4. Voucher Claim Concurrency Handling (Core Challenge)

### Problem
Flash sales involve limited inventory and a large number of users attempting to claim deals simultaneously.  
Without proper synchronization, this can result in:
- Overselling inventory
- Duplicate claims by the same user

### Solution: Redis Distributed Lock

A Redis-based distributed lock is used to protect the critical claim section.

**Lock Key Format**
lock:deal:{dealId}


### Claim Flow
1. Acquire Redis lock using `SET NX EX`
2. Validate deal existence and expiration
3. Check remaining inventory
4. Ensure the user has not already claimed the deal
5. Decrement inventory
6. Persist the claim
7. Release the lock safely

### Guarantees
- Inventory never goes below zero
- Strictly one voucher per user
- Safe behavior under high concurrency

---

## 5. Scenario 1: Offline “Jugaad” Voucher Verification

### Problem
Merchants may need to verify voucher authenticity when their device has no internet connectivity.

### Solution
- Upon successful claim, the system generates a signed voucher token (JWT or HMAC-based)
- The token is embedded in a QR code
- The token contains:
  - dealId
  - userId
  - expiry timestamp
  - cryptographic signature

### Offline Verification
- Merchant application stores the public key (or shared secret)
- Voucher signature is verified locally without contacting the backend
- Verification logs are synced to the server once connectivity is restored

### Benefits
- Offline-first verification
- Tamper-proof vouchers
- No backend dependency during redemption

---

## 6. Scenario 2: Scaling to 1 Million Requests per Minute

### Stateless Application
- Spring Boot services remain stateless
- Enables horizontal scaling

### Load Balancing
- API placed behind a load balancer (ALB / NGINX)
- Requests distributed across multiple application instances

### Redis-First Strategy
- Redis handles distributed locking
- Minimizes database contention during flash sales

### Database Scaling
- Primary database for writes
- Read replicas for discovery queries
- Indexes on `deal_id` and `valid_until`
- Optional sharding by `deal_id` at extreme scale

### Asynchronous Processing
- Non-critical tasks (analytics, notifications) handled via message queues (Kafka / RabbitMQ)

### Result
- Horizontal scalability
- High throughput
- Inventory correctness preserved

---

## 7. Failure Handling

- If Redis is unavailable → claim requests are rejected (fail-safe)
- If database transaction fails → changes are rolled back
- Clear error responses returned to clients

---

## 8. Trade-offs & Assumptions

- Authentication is intentionally omitted as per assignment scope
- Redis is assumed to be highly available
- Inventory correctness is prioritized over raw throughput
