📄 ARCHITECTURE.md (FINAL CONTENT)
1. System Overview

This service is a hyperlocal flash-sale backend engine designed to allow merchants to create time-bound deals and users to discover and claim vouchers safely under high concurrency.

The system exposes REST APIs for:

Deal creation

Deal discovery based on location

Secure voucher claiming

The design prioritizes:

Data consistency

Concurrency safety

Horizontal scalability

Low-latency responses

2. High-Level Architecture

Components:

Spring Boot REST API – core business logic

PostgreSQL – persistent storage (used in production)

Redis – distributed locking + caching

Docker – containerized deployment

Load Balancer – horizontal scaling (cloud deployment)

Client → Load Balancer → Spring Boot App
↓
Redis (Lock / Cache)
↓
PostgreSQL

3. Deal Discovery Design

Deal discovery follows these steps:

Fetch active deals (not expired, inventory > 0)

Apply geo-distance filtering using the Haversine formula

Return only deals within the requested radius

Optimization (Production):

Discovery results are cached in Redis with short TTL (30 seconds)

Key format:

deals:{lat}:{lng}:{radius}


This reduces database load during high traffic.

4. Voucher Claim Concurrency Handling (MOST IMPORTANT)

Claiming a deal is a critical section due to:

Limited inventory

Multiple users claiming simultaneously

Problem

Without protection, concurrent requests can:

Oversell inventory

Allow duplicate claims

Solution: Redis Distributed Lock

Lock Key:

deal:{dealId}:lock


Claim Flow:

Acquire Redis lock

Check if deal exists and is valid

Check if inventory > 0

Ensure user has not already claimed

Decrement inventory

Persist claim

Release lock

This guarantees:

Inventory never goes below zero

One voucher per user

Safe behavior under heavy concurrency

5. Offline Voucher Verification (Scenario 1)
   Problem

Merchants may need to verify vouchers without internet connectivity.

Solution

Each claimed voucher generates a signed JWT / QR code

QR contains:

voucherId

userId

dealId

expiry

Merchant app verifies the signature offline

When connectivity is restored, verification logs are synced to the server

This ensures:

Offline-first verification

Tamper-proof vouchers

6. Scaling to 1 Million Requests per Minute (Scenario 2)

To handle massive scale:

Stateless Application

Multiple Spring Boot instances behind load balancer

Redis First Strategy

Locks + caching handled in Redis

DB accessed only when necessary

Database Optimization

Index on deal_id, valid_until

Read replicas

Sharding by deal_id if required

Async Processing

Claim confirmations/events pushed to message queue (Kafka/RabbitMQ)

Auto Scaling

CPU & request-based scaling in cloud environment

7. Failure Handling

If Redis is unavailable → reject claims (fail-safe)

If DB transaction fails → rollback

Clear error responses returned to client

8. Trade-offs & Assumptions

Authentication is intentionally omitted as per assignment scope

Redis is assumed to be highly available

Inventory correctness is prioritized over throughput