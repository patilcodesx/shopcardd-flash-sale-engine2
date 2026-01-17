<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>ShopCardd – Hyperlocal Flash Sale Engine</title>

    <style>
        body {
            font-family: Arial, Helvetica, sans-serif;
            margin: 40px;
            line-height: 1.6;
            color: #1f2937;
        }

        h1, h2, h3 {
            color: #1d4ed8;
        }

        hr {
            margin: 30px 0;
        }

        pre {
            background: #f3f4f6;
            padding: 15px;
            border-radius: 6px;
            overflow-x: auto;
        }

        code {
            background: #e5e7eb;
            padding: 2px 6px;
            border-radius: 4px;
        }

        table {
            border-collapse: collapse;
            width: 100%;
            margin: 15px 0;
        }

        th, td {
            border: 1px solid #d1d5db;
            padding: 10px;
            text-align: left;
        }

        th {
            background: #f9fafb;
        }

        .highlight {
            background: #f8fafc;
            padding: 15px;
            border-left: 5px solid #1d4ed8;
            margin: 20px 0;
        }
    </style>
</head>

<body>

<h1>🛒 ShopCardd – Hyperlocal Flash Sale Engine</h1>

<p>
<strong>Author:</strong> Bhavesh Patil<br>
<strong>GitHub:</strong>
<a href="https://github.com/patilcodesx">https://github.com/patilcodesx</a>
</p>

<hr>

<h2>📖 Project Overview</h2>

<p>
ShopCardd Flash Sale Engine is a backend service developed as part of the
<strong>ShopCardd Backend Engineering Assignment</strong>.
</p>

<p>
The system enables merchants to publish limited-time deals with restricted inventory
and allows users to discover and claim vouchers safely under
<strong>high-concurrency traffic</strong>.
</p>

<p>
The core objective of this project is to ensure:
</p>

<ul>
    <li>Inventory is never oversold</li>
    <li>Each user can claim a deal only once</li>
    <li>Concurrent requests are handled safely</li>
    <li>The system remains horizontally scalable</li>
</ul>

<hr>

<h2>🎯 Problem Statement</h2>

<p>
During flash sales, hundreds or thousands of users may attempt to claim the same deal
simultaneously.
</p>

<p>
Without proper concurrency control, this can lead to:
</p>

<ul>
    <li>Negative inventory</li>
    <li>Duplicate voucher claims</li>
    <li>Race conditions</li>
    <li>Inconsistent database state</li>
</ul>

<div class="highlight">
<strong>Goal:</strong>
Guarantee correctness first — even under extreme parallel traffic.
</div>

<hr>

<h2>🛠️ Technology Stack</h2>

<table>
<tr><th>Layer</th><th>Technology</th></tr>
<tr><td>Programming Language</td><td>Java 17</td></tr>
<tr><td>Framework</td><td>Spring Boot</td></tr>
<tr><td>Database</td><td>PostgreSQL</td></tr>
<tr><td>Distributed Cache</td><td>Redis</td></tr>
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

<h2>🏗 Architecture Components</h2>

<ul>
    <li><strong>Spring Boot API</strong> – Stateless application layer</li>
    <li><strong>PostgreSQL</strong> – Source of truth with ACID transactions</li>
    <li><strong>Redis</strong> – Distributed locking and discovery caching</li>
    <li><strong>Docker</strong> – Portable deployment environment</li>
</ul>

<hr>

<h2>▶️ How to Run the Application</h2>

<h3>Prerequisites</h3>

<ul>
    <li>Docker</li>
    <li>Docker Compose</li>
</ul>

<h3>Start the System</h3>

<pre><code>docker compose up --build</code></pre>

<h3>Running Services</h3>

<table>
<tr><th>Service</th><th>Address</th></tr>
<tr><td>Backend API</td><td>http://localhost:8080</td></tr>
<tr><td>PostgreSQL</td><td>localhost:5432</td></tr>
<tr><td>Redis</td><td>localhost:6379</td></tr>
</table>

<hr>

<h2>🛠 REST API Endpoints</h2>

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

<hr>

<h3>2️⃣ Discover Deals</h3>

<pre><code>/deals/discover?lat=19.0760&lng=72.8777&radius=5</code></pre>

<ul>
    <li>Returns only active deals</li>
    <li>Filters expired and sold-out deals</li>
    <li>Geo-distance calculated using Haversine formula</li>
</ul>

<h4>Redis Discovery Cache</h4>

<pre><code>cache:deals:{lat}:{lng}:{radius}</code></pre>

<p>Cache TTL: <strong>30 seconds</strong></p>

<hr>

<h3>3️⃣ Claim Deal</h3>

<pre><code>/deals/{dealId}/claim?userId=u-1</code></pre>

<table>
<tr><th>Scenario</th><th>Result</th></tr>
<tr><td>Successful claim</td><td>Voucher issued</td></tr>
<tr><td>Already claimed</td><td>Rejected</td></tr>
<tr><td>Sold out</td><td>Rejected</td></tr>
<tr><td>Expired deal</td><td>Rejected</td></tr>
</table>

<hr>

<h2>🔐 Concurrency Control Strategy</h2>

<p>
Voucher claiming is a critical section protected using
<strong>Redis distributed locking</strong>.
</p>

<pre><code>lock:deal:{dealId}</code></pre>

<h3>Claim Execution Flow</h3>

<ol>
    <li>Acquire Redis lock using <code>SET NX EX</code></li>
    <li>Validate deal existence</li>
    <li>Check expiration timestamp</li>
    <li>Check remaining inventory</li>
    <li>Verify user has not claimed before</li>
    <li>Decrement inventory</li>
    <li>Persist claim transaction</li>
    <li>Release lock safely</li>
</ol>

<div class="highlight">
✅ Inventory never drops below zero<br>
✅ One voucher per user<br>
✅ Safe under heavy concurrency
</div>

<hr>

<h2>🧠 Scenario 1 — Offline Voucher Verification</h2>

<p>
To support merchants operating without internet connectivity:
</p>

<ul>
    <li>Voucher token generated at claim time</li>
    <li>Token signed using JWT or HMAC</li>
    <li>Embedded into QR code</li>
</ul>

<p>
Merchant applications can verify the voucher signature offline and sync logs
once connectivity is restored.
</p>

<hr>

<h2>🚀 Scenario 2 — Scaling to 1 Million Requests / Minute</h2>

<ul>
    <li>Stateless Spring Boot services</li>
    <li>Horizontal scaling behind load balancer</li>
    <li>Redis-first concurrency strategy</li>
    <li>PostgreSQL read replicas</li>
    <li>Indexed queries and connection pooling</li>
</ul>

<hr>

<h2>⚠️ Failure Handling</h2>

<table>
<tr><th>Failure</th><th>Behavior</th></tr>
<tr><td>Redis unavailable</td><td>Claims rejected (fail-safe)</td></tr>
<tr><td>Database failure</td><td>Transaction rollback</td></tr>
<tr><td>Duplicate claim</td><td>Graceful rejection</td></tr>
<tr><td>Invalid request</td><td>Proper HTTP error</td></tr>
</table>

<hr>

<h2>✅ Summary</h2>

<ul>
    <li>High-concurrency safe flash sale backend</li>
    <li>Redis distributed locking</li>
    <li>Geo-based deal discovery</li>
    <li>Fully containerized deployment</li>
    <li>Production-grade system design</li>
</ul>

<hr>

<h2>👨‍💻 Author</h2>

<p>
<strong>Bhavesh Patil</strong><br>
GitHub:
<a href="https://github.com/patilcodesx">https://github.com/patilcodesx</a>
</p>

</body>
</html>
