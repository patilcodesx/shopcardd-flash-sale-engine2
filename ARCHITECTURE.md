# Flash Sale System Design Documentation

---

# Scenario 1: Offline Voucher Verification (“Offline JuGaad”)

---

## Situation

A customer arrives at a merchant store with a successfully claimed voucher.

However, the merchant’s device temporarily has **no internet connectivity**, making it impossible to call backend APIs for voucher verification.

---

## Challenge

The merchant must still be able to verify:

- The voucher is genuine  
- The voucher has not been modified  
- The voucher has not expired  

All **without contacting the backend server**.

---

## Design Goal

Enable **secure offline voucher validation** without backend dependency while maintaining strong protection against fraud, replay attacks, and data tampering.

---

## Design Approach

Instead of attempting to replicate backend validation logic offline, the **voucher itself is designed to be self-verifiable**.

The backend becomes the **issuer of trust**, while the merchant application performs **local cryptographic verification**.

 

---

## Solution: Signed Offline Voucher Tokens

When a deal is successfully claimed:

1. Backend creates a voucher payload  
2. Payload is cryptographically signed  
3. Signed token is returned to the client  
4. Token is embedded inside a **QR code**

---

## Voucher Payload Fields

The backend generates a payload containing:

- `voucherId` – unique identifier  
- `dealId` – associated deal  
- `userId` – claiming user  
- `expiry` – expiration timestamp  

---

## Cryptographic Signing Methods

The payload is signed using one of the following:

### Option 1: JWT with RSA (RS256)

- Backend signs using **private key**
- Merchant verifies using **public key**
- Public key can be distributed safely

### Option 2: HMAC SHA-256

- Backend and merchant share a secret key
- Faster but less flexible for public distribution

---

## Voucher Token Structure

```json
{
  "voucherId": "VCH_8937423",
  "dealId": "DEAL_1029",
  "userId": "USR_55221",
  "expiry": "2026-01-20T23:59:59Z",
  "signature": "BASE64_ENCODED_SIGNATURE"
}
```
## Digital Signature Guarantees

The digital signature guarantees:

- **Payload authenticity**
- **Tamper detection**
- **Data integrity**

Any modification invalidates the signature.

---

## QR Code Contents

The QR code stores:

- Complete signed voucher token (JWT or custom format)
- No backend call required during scan

---

## Offline Verification Flow

Before going offline, the merchant application stores:

- **Public key** (JWT / RSA)
- **Shared secret** (HMAC)

---

### Step-by-Step Verification

1. Merchant scans QR code  
2. Voucher token is extracted  
3. Signature is verified locally  
4. Voucher payload is decoded  
5. Expiry timestamp is checked  
6. Voucher schema is validated  

If all checks pass → **Voucher Accepted**

---

## Offline Validation Logic

```bash
if signature_valid \
   and current_time < expiry \
   and voucher_format_valid
then
   accept_voucher
else
   reject_voucher
fi
```
## Preventing Abuse in Offline Mode

Offline systems are vulnerable to repeated usage.

To mitigate this:

---

### Local Redemption Tracking

- Merchant device stores a local list:
  - `redeemed_voucher_ids`

- If the same voucher is scanned again:
  - **Rejected immediately**

---

### Sync After Connectivity Returns

Once internet connectivity is restored:

- All offline redemptions are synced
- Backend permanently marks vouchers as redeemed
- Conflicts are resolved server-side

---

## Security Guarantees

| Threat | Mitigation |
|------|-----------|
| QR tampering | Cryptographic signature |
| Fake vouchers | Private-key signing |
| Expired vouchers | Timestamp validation |
| Duplicate scans | Local redemption cache |
| Replay attack | Server sync validation |

---







---

# Scenario 2: Handling Massive Scale  
## 1 Million Requests per Minute

---

## Situation

During a flash sale or limited-offer event:

- Up to **1,000,000 requests per minute**
- ~**16,000–20,000 requests per second**
- Majority of traffic targets only **few popular deals**

---

## Core Challenges

- Prevent overselling inventory  
- Prevent duplicate claims  
- Maintain sub-100ms latency  
- Avoid database meltdown  
- Handle burst traffic safely  

---

## High-Level Scaling Philosophy

The system scales by:

- Isolating contention  
- Centralizing concurrency control  
- Keeping application layer stateless  
- Using Redis as a traffic gate  
- Using database as final authority  

---

## 1️⃣ Stateless Application Layer

Spring Boot services are completely stateless:

- No HTTP sessions  
- No in-memory locks  
- No local caches for correctness  

All state is stored in:

- Redis  
- PostgreSQL  

---

### Advantages

- Horizontal scaling  
- Container-friendly  
- No inter-service coordination  
- Auto-scaling ready  

---

## 2️⃣ Load Balancing Architecture

```bash
Client Requests
      ↓
Cloud Load Balancer
      ↓
Spring Boot App Instances
(App-1 | App-2 | App-3 | App-N)
```
Requests are evenly distributed across all instances.

---

## 3️⃣ Redis-First Concurrency Control

Redis acts as the **first line of defense**.

---

### Deal-Based Distributed Lock

```bash
lock:deal:{dealId}
```
- Requests for the **same deal** compete  
- Requests for **different deals** proceed in parallel  

---

## Redis Lock Behavior

- TTL-based locks prevent deadlocks  
- Atomic operations (`SETNX` / Lua scripts)  
- Extremely low latency (~1 ms)  

---

## Redis Cluster Mode

At scale:

- Redis runs in **cluster mode**  
- Locks distributed across shards  
- Horizontal throughput scaling  

---

# 4️⃣ Database as Source of Truth

PostgreSQL remains the **final authority**.

---

## Responsibilities

### Primary DB

- Inventory decrement  
- Voucher claim insert  
- Transaction handling  

### Read Replicas

- Deal listing  
- Deal metadata  
- User history queries  

---

## Database Safety Mechanisms

- ACID transactions  
- Row-level locking  

Unique constraint:

```sql
UNIQUE (deal_id, user_id)
```
### Indexed Fields

- `deal_id`  
- `valid_until`  

Even if Redis fails, the database guarantees correctness.

---

# 5️⃣ Asynchronous Processing (Optional)

To reduce latency under extreme load:

Move non-critical work out of the request path.

---

## Offloaded Operations

- Notifications  
- Email / SMS  
- Analytics  
- Audit logging  

---

## Messaging Tools

- Apache Kafka  
- RabbitMQ  
- AWS SQS  

---

## Claim Request Execution Flow

```bash
Client Request
   ↓
Load Balancer
   ↓
Spring Boot App
   ↓
Redis Lock Check
   ↓
PostgreSQL Transaction
   ↓
Commit / Rollback
   ↓
Async Events Published
```
## Final Scaled Architecture

```bash
Clients
   ↓
Load Balancer
   ↓
Spring Boot Containers (Auto-Scaled)
   ↓
Redis Cluster (Locks + Cache)
   ↓
PostgreSQL Primary
   ↓
PostgreSQL Read Replicas
```
## Key Design Outcomes

- No overselling  
- No duplicate claims  
- Linear horizontal scalability  
- Predictable latency  
- Strong consistency  
- Fault tolerance  

---

## Technologies Used

- **Backend:** Java Spring Boot  
- **Cache / Locks:** Redis Cluster  
- **Database:** PostgreSQL  
- **Messaging:** Kafka / RabbitMQ  
- **Deployment:** Docker + Kubernetes  
- **Security:** JWT, RSA, HMAC  

---
