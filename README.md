<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>ShopCardd – Flash Sale Engine</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial;
            background-color: #0d1117;
            color: #c9d1d9;
            padding: 40px;
            line-height: 1.6;
        }

        h1, h2, h3 {
            color: #ffffff;
            border-bottom: 1px solid #30363d;
            padding-bottom: 6px;
        }

        a {
            color: #58a6ff;
            text-decoration: none;
        }

        table {
            border-collapse: collapse;
            margin: 20px 0;
            width: 100%;
        }

        th, td {
            border: 1px solid #30363d;
            padding: 10px;
            text-align: left;
        }

        th {
            background: #161b22;
        }

        pre {
            background: #161b22;
            padding: 15px;
            overflow-x: auto;
            border-radius: 6px;
            border: 1px solid #30363d;
        }

        code {
            color: #79c0ff;
        }

        ul {
            margin-left: 20px;
        }

        .section {
            margin-top: 40px;
        }

        footer {
            margin-top: 60px;
            border-top: 1px solid #30363d;
            padding-top: 20px;
            color: #8b949e;
        }
    </style>
</head>

<body>

<h1>🛒 ShopCardd – Hyperlocal Flash Sale Engine</h1>

<p>
Backend service for managing <strong>high-concurrency flash sales</strong>,
enabling merchants to create limited-inventory deals and users to safely
discover and claim vouchers without overselling.
</p>

<hr>

<div class="section">
<h2>🚀 Tech Stack</h2>

<table>
<tr><th>Layer</th><th>Technology</th></tr>
<tr><td>Language</td><td>Java 17</td></tr>
<tr><td>Framework</td><td>Spring Boot</td></tr>
<tr><td>Database</td><td>PostgreSQL</td></tr>
<tr><td>Cache & Locking</td><td>Redis</td></tr>
<tr><td>Containerization</td><td>Docker & Docker Compose</td></tr>
</table>
</div>

<div class="section">
<h2>✨ Features</h2>

<ul>
    <li>Create time-bound flash deals</li>
    <li>Geo-based deal discovery</li>
    <li>Redis-cached discovery results</li>
    <li>Concurrency-safe voucher claiming</li>
    <li>Distributed locking using Redis</li>
    <li>Prevention of overselling and duplicate claims</li>
</ul>
</div>

<div class="section">
<h2>🧩 System Architecture</h2>

<pre>
Client
  ↓
Load Balancer
  ↓
Spring Boot API
  ↓
Redis (Distributed Lock + Cache)
  ↓
PostgreSQL
</pre>
</div>

<div class="section">
<h2>📁 Project Folder Structure</h2>

<pre>
shopcardd-flash-sale-engine
│
├── docker-compose.yml
├── Dockerfile
├── README.md
│
├── src
│   └── main
│       ├── java
│       │   └── com/shopcardd/flashsale
│       │       ├── controller
│       │       ├── service
│       │       ├── repository
│       │       ├── entity
│       │       ├── dto
│       │       └── config
│       └── resources
│           ├── application.yml
│           └── schema.sql
│
└── pom.xml
</pre>
</div>

<div class="section">
<h2>▶️ Run Application</h2>

<pre><code>docker compose up --build</code></pre>

<p>Starts:</p>
<ul>
    <li>Spring Boot API</li>
    <li>PostgreSQL</li>
    <li>Redis</li>
</ul>
</div>

<div class="section">
<h2>🌐 Services</h2>

<table>
<tr><th>Service</th><th>Address</th></tr>
<tr><td>API</td><td>http://localhost:8080</td></tr>
<tr><td>PostgreSQL</td><td>localhost:5432</td></tr>
<tr><td>Redis</td><td>localhost:6379</td></tr>
</table>
</div>

<div class="section">
<h2>🔗 API Endpoints</h2>

<h3>Create Deal</h3>

<pre>
POST /deals
</pre>

<pre>
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
</pre>

<h3>Discover Deals</h3>

<pre>
GET /deals/discover?lat=19.0760&lng=72.8777&radius=5
</pre>

<h3>Claim Deal</h3>

<pre>
POST /deals/{dealId}/claim?userId=u-1
</pre>

</div>

<div class="section">
<h2>🔐 Concurrency Control</h2>

<pre>
lock:deal:{dealId}
</pre>

<ul>
    <li>Redis SET NX EX locking</li>
    <li>Atomic inventory decrement</li>
    <li>No overselling guarantee</li>
</ul>
</div>

<div class="section">
<h2>📊 API Responses</h2>

<table>
<tr><th>Scenario</th><th>Status</th><th>Response</th></tr>
<tr><td>Success</td><td>200</td><td>{"status":"Success"}</td></tr>
<tr><td>Already claimed</td><td>400</td><td>User already claimed</td></tr>
<tr><td>Sold out</td><td>400</td><td>Deal sold out</td></tr>
<tr><td>Expired</td><td>400</td><td>Deal expired</td></tr>
</table>
</div>

<footer>
    <p><strong>Author:</strong> Bhavesh Patil</p>
    <p>GitHub: <a href="https://github.com/patilcodesx">https://github.com/patilcodesx</a></p>
    <p>Built for ShopCardd Backend Engineering Assessment.</p>
</footer>

</body>
</html>
