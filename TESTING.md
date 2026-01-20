# 🧪 Testing Guide – ShopCardd Flash Sale Engine

This document provides **step-by-step instructions** to test all features of the Hyperlocal Flash Sale backend service.

It includes:

- API testing using `curl`
- Redis key verification
- PostgreSQL data validation
- Concurrency and race-condition testing

---

## 📦 Prerequisites

Make sure the following are installed:

- Docker
- Docker Compose
- curl (or Postman)

---

## 🚀 Start the Application

From project root:

```bash
docker compose up --build
```

## ✅ Verify Running Containers

```bash
docker ps
```

## Expected containers
```bash
flashsale-app
flashsale-postgres
flashsale-redis
```

## 🔗 API Base URL
```bash
http://localhost:8080
```

## 🧩 1️⃣ Create Deal

### Endpoint
``` bash
POST /deals
```

### Command
```bash

curl -X POST http://localhost:8080/deals \
  -H "Content-Type: application/json" \
  -d '{
    "merchant_id": "merchant-001",
    "title": "Flat 50% Off Burgers",
    "total_vouchers": 5,
    "valid_until": "2026-12-31T23:59:59Z",
    "location": {
      "lat": 19.0760,
      "long": 72.8777
    }
  }'
  ```

### Expected Response
```bash
{
  "deal_id": "uuid",
  "merchant_id": "merchant-001",
  "title": "Flat 50% Off Burgers",
  "total_vouchers": 5,
  "inventory_remaining": 5,
  "valid_until": "2026-12-31T23:59:59Z",
  "location": {
    "lat": 19.076,
    "long": 72.8777
  }
}
```

## 🔎 2️⃣ Discover Deals
### Endpoint
``` bash
GET /deals/discover
```

Command
``` bash

curl "http://localhost:8080/deals/discover?lat=19.0760&lng=72.8777&radius=5"
```

### Expected Response
``` json

{
  "deals": [
    {
      "deal_id": "deal-001",
      "title": "Flat 50% Off Burgers",
      "distance_km": 0.3,
      "inventory_remaining": 5
    }
  ]
}
```
## 🎫 3️⃣ Claim Deal (Success)
### Endpoint
```bash
POST /deals/{dealId}/claim
```
### Command
```bash

curl -X POST \
  "http://localhost:8080/deals/{dealId}/claim?userId=user-1"
  ```

### Expected Response
```json

{
  "status": "success",
  "voucher_code": "SHOP-abc123"
}
```
### HTTP Status
```bash
200 OK
```

## ❌ 4️⃣ Duplicate Claim Test

## Command
``` bash

curl -X POST \
  "http://localhost:8080/deals/{dealId}/claim?userId=user-1"
  ```

### Expected Response
```json

{
  "status": "fail",
  "reason": "User already claimed"
}
```
### HTTP Status
```bash
400 Bad Request
```

## ❌ 5️⃣ Sold Out Test
Claim using different users until inventory becomes zero:

```sql

user-2
user-3
user-4
user-5
```
Then try again:

```bash

curl -X POST \
  "http://localhost:8080/deals/{dealId}/claim?userId=user-6"
  ```
## Expected Response
```json

{
  "status": "fail",
  "reason": "Deal Sold Out"
}
```
## HTTP Status
```pgsql

409 Conflict
```
## ❌ 6️⃣ Expired Deal Test
Create a deal with a past timestamp:

```json

"valid_until": "2023-01-01T00:00:00Z"
```
### Claim Request
```bash

curl -X POST \
  "http://localhost:8080/deals/{dealId}/claim?userId=user-x"
  ```
### Expected Response
```json

{
  "status": "fail",
  "reason": "Deal expired"
}
```
### HTTP Status
```bash
400 Bad Request
```
# 🔴 Redis Verification
## Enter Redis CLI
```bash

docker exec -it flashsale-redis redis-cli
```
## View inventory key
``` bash
GET deal:inventory:{dealId}
```
## View claimed users
```bash
SMEMBERS deal:users:{dealId}
```
## View active lock keys
```bash

KEYS lock:deal:*
``` 

# 🐘 PostgreSQL Verification
## Enter database
```bash

docker exec -it flashsale-postgres psql -U postgres -d flashsale
```

## Check deals table
```sql

SELECT deal_id, inventory_remaining FROM deals;
```
## Check claims table
```sql

SELECT * FROM claims;
```
## ⚡ High-Concurrency Test (Optional)
Run multiple parallel requests:

```bash

for i in {1..20}; do
  curl -X POST \
  "http://localhost:8080/deals/{dealId}/claim?userId=user-$i" &
done
```
## Expected Results
Inventory never drops below zero
Only allowed number of users succeed
Remaining users receive:

```pgsql

409 Conflict
```
✅ Validation Checklist
- Redis atomic decrement works
- No overselling
- One voucher per user
- Correct HTTP status codes
- Correct JSON response format
- Redis TTL respected
- Database consistency maintained


