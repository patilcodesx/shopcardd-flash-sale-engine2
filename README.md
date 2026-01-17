<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>ShopCardd – Hyperlocal Flash Sale Engine</title>
    <style>
        body {
            font-family: Arial, Helvetica, sans-serif;
            line-height: 1.6;
            margin: 40px;
            color: #222;
        }
        h1, h2, h3 {
            color: #0b5ed7;
        }
        code {
            background: #f4f4f4;
            padding: 3px 6px;
            border-radius: 4px;
        }
        pre {
            background: #f4f4f4;
            padding: 15px;
            overflow-x: auto;
            border-radius: 6px;
        }
        table {
            border-collapse: collapse;
            margin: 15px 0;
            width: 100%;
        }
        table, th, td {
            border: 1px solid #ccc;
            padding: 10px;
        }
        th {
            background: #f0f0f0;
        }
        .box {
            background: #f9fafb;
            padding: 15px;
            border-left: 5px solid #0b5ed7;
            margin: 20px 0;
        }
    </style>
</head>
<body>

<h1>🛒 ShopCardd – Hyperlocal Flash Sale Engine</h1>

<p><strong>Author:</strong> Bhavesh Patil<br>
<strong>GitHub:</strong> <a href="https://github.com/patilcodesx">https://github.com/patilcodesx</a></p>

<hr>

<h2>📖 Project Overview</h2>

<p>
ShopCardd Flash Sale Engine is a backend system designed to support
<strong>high-concurrency flash deal campaigns</strong>.
</p>

<p>
The system enables merchants to create limited-inventory deals while allowing
thousands of users to discover and claim vouchers safely at the same time —
without overselling or duplicate claims.
</p>

<ul>
    <li>Strict inventory correctness</li>
    <li>Concurrency control</li>
    <li>Distributed system safety</li>
    <li>Horizontal scalability</li>
</ul>

<hr>

<h2>🎯 Core Problem Solved</h2>

<p>Flash sales suffer from race conditions when many users claim simultaneously.</p>

<ul>
    <li>Inventory must never go below zero</li>
    <li>One voucher per user</li>
    <li>No duplicate claims</li>
</ul>

<div class="box">
<strong>Result:</strong> Safe voucher claiming even under extreme concurrency.
</div>

<hr>

<h2>🛠️ Tech Stack</h2>

<table>
<tr><th>Layer</th><th>Technology</th></tr>
<tr><td>Language</td><td>Java 17</td></tr>
<tr><td>Framework</td><td>Spring Boot</td></tr>
<tr><td>Database</td><td>PostgreSQL</td></tr>
<tr><td>Cache / Lock</td><td>Redis</td></tr>
<tr><td>Containerization</td><td>Docker</td></tr>
<tr><td>Orchestration</td><td>Docker Compose</td></tr>
</table>

<hr>

<h2>🧩 High-Level Architecture</h2>

<pre>
Client
   ↓
Load Balancer
   ↓
Spring Boot REST API
   ↓
Redis (Distributed Lock + Cache)
   ↓
PostgreSQL
</pre>

<hr>

<h2>▶️ How to Run</h2>

<h3>Prerequisites</h3>
<ul>
    <li>Docker</li>
    <li>Docker Compose</li>
</ul>

<h3>Start Application</h3>

<pre><code>docker compose up --build</code></pre>

<h3>Services</h3>

<table>
<tr><th>Service</th><th>URL / Port</th></tr>
<tr><td>API</td><td>http://localhost:8080</td></tr>
<tr><td>PostgreSQL</td><td>5432</td></tr>
<tr><td>Redis</td><td>6379</td></tr>
</table>

<hr>

<h2>🛠 API Endpoints</h2>

<h3>1️⃣ Create Deal</h3>

<pre><code>{
  "merchant_id": "merchant-123",
  "title": "Flat 50% Off",
  "total_vouchers": 100,
  "valid_until": "2026-12-31T23:59:59Z",
  "location": {
    "lat": 19.0760,
    "long": 72.8777
  }
}</code></pre>

<h3>2️⃣ Discover Deals</h3>

<pre><code>/deals/discover?lat=19.0760&lng=72.8777&radius=5</code></pre>

<ul>
    <li>Active deals only</li>
    <li>Haversine geo-distance filtering</li>
    <li>Redis caching (TTL 30 seconds)</li>
</ul>

<pre><code>cache:deals:{lat}:{lng}:{radius}</code></pre>

<hr>

<h3>3️⃣ Claim Deal</h3>

<pre><code>/deals/{dealId}/claim?userId=u-1</code></pre>

<ul>
    <li>Success</li>
    <li>Already claimed</li>
    <li>Sold out</li>
    <li>Expired</li>
</ul>

<hr>

<h2>🔐 Concurrency Handling</h2>

<p>Redis distributed locking protects critical inventory operations.</p>

<pre><code>lock:deal:{dealId}</code></pre>

<ol>
    <li>Acquire Redis lock</li>
    <li>Validate deal</li>
    <li>Check inventory</li>
    <li>Persist claim</li>
    <li>Release lock</li>
</ol>

<hr>

<h2>🧠 Offline Voucher Verification</h2>

<ul>
    <li>Signed voucher token (JWT / HMAC)</li>
    <li>Embedded inside QR code</li>
    <li>Offline signature verification</li>
    <li>Logs synced when online</li>
</ul>

<hr>

<h2>🚀 Scaling to 1 Million Requests / Minute</h2>

<ul>
    <li>Stateless Spring Boot services</li>
    <li>Horizontal scaling</li>
    <li>Redis-first locking strategy</li>
    <li>PostgreSQL read replicas</li>
</ul>

<hr>

<h2>⚠️ Failure Handling</h2>

<table>
<tr><th>Failure</th><th>Behavior</th></tr>
<tr><td>Redis down</td><td>Claims rejected (fail-safe)</td></tr>
<tr><td>DB error</td><td>Transaction rollback</td></tr>
<tr><td>Duplicate claim</td><td>Graceful rejection</td></tr>
</table>

<hr>

<h2>✅ Summary</h2>

<ul>
    <li>High-concurrency safe backend</li>
    <li>Redis distributed locking</li>
    <li>Geo-based deal discovery</li>
    <li>Production-ready architecture</li>
</ul>

<hr>

<h2>👨‍💻 Author</h2>

<p>
<strong>Bhavesh Patil</strong><br>
GitHub: <a href="https://github.com/patilcodesx">https://github.com/patilcodesx</a>
</p>

</body>
</html>
