🛒 ShopCardd – Hyperlocal Flash Sale Backend

This project implements a hyperlocal flash-sale backend service where merchants can create limited-time deals and users can discover and claim vouchers safely under high concurrency.

The system is designed with correctness, scalability, and simplicity in mind and follows real-world backend engineering practices.

✨ Features

Create flash-sale deals with limited inventory

Discover nearby active deals using geo-location

Claim a deal with:

One claim per user

No overselling of inventory

Concurrency-safe design (Redis-based in production)

Clean REST APIs

Clear system architecture documentation

🧱 Tech Stack

Backend: Java 17, Spring Boot 3.x

Database (Local Dev): H2 (in-memory)

Database (Production): PostgreSQL

Caching / Locking: Redis

Build Tool: Maven

Deployment: Docker (production setup)

📐 High-Level Design

REST-based stateless service

Inventory correctness prioritized

Transactional claim logic

Distributed locking using Redis (explained in ARCHITECTURE.md)

For detailed design and scaling strategies, see 👉 ARCHITECTURE.md

🚀 Getting Started (Local Development)
Prerequisites

Java 17+

Maven

Run the application
mvn spring-boot:run


The server will start at:

http://localhost:8080

🧪 API Endpoints
1️⃣ Create Deal

POST /deals

{
"merchantId": "m1",
"title": "Flat 50% Off on Pizza",
"totalVouchers": 10,
"validUntil": "2026-12-31T23:59:59Z",
"location": {
"lat": 19.07,
"lng": 72.87
}
}

2️⃣ Discover Deals

GET /deals/discover?lat=19.07&lng=72.87&radius=5

Returns all nearby, active, non-expired deals.

3️⃣ Claim Deal

POST /deals/{dealId}/claim

{
"userId": "user123"
}

Business Rules

A user can claim only once

Inventory is decremented safely

Claim fails if:

Deal is expired

Inventory is sold out

User already claimed

🔒 Concurrency Handling

Claim operation is transactional

In production, Redis distributed locking is used:

Prevents overselling

Guarantees one-claim-per-user

Locking strategy is explained in detail in ARCHITECTURE.md

🧠 Design Decisions

Authentication intentionally omitted (out of scope)

Focused on core business logic and correctness

Redis chosen for low-latency locking

PostgreSQL used for strong consistency

📦 Database Notes

H2 is used for fast local development

PostgreSQL + Redis are used in production (Docker-based)

In-memory DB resets on restart (expected behavior)

🛠️ Future Enhancements

JWT-based authentication

Redis caching for deal discovery

Async event processing (Kafka/RabbitMQ)

Monitoring & metrics (Micrometer + Prometheus)

📄 Project Structure
src/main/java/com/shopcardd/flashsale
├── controller
├── service
├── repository
├── model
├── dto
└── config

👨‍💻 Author

Bhavesh Patil
Backend Developer (Java, Spring Boot)

✅ Assignment Notes

Built according to the provided requirements

Docker & Redis integration finalized in production setup

Architecture decisions documented separately