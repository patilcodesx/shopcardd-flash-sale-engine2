<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>ShopCardd – Flash Sale Engine</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<style>
body {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, monospace;
    background-color: #0d1117;
    color: #c9d1d9;
    padding: 40px;
    line-height: 1.6;
}

main {
    max-width: 1100px;
    margin: auto;
}

h1, h2, h3 {
    color: #ffffff;
    border-bottom: 1px solid #30363d;
    padding-bottom: 6px;
}

a { color: #58a6ff; }

table {
    border-collapse: collapse;
    width: 100%;
    margin: 20px 0;
}

th, td {
    border: 1px solid #30363d;
    padding: 10px;
}

th {
    background: #161b22;
}

pre {
    background: #161b22;
    padding: 15px;
    border-radius: 6px;
    border: 1px solid #30363d;
    overflow-x: auto;
    font-family: Consolas, monospace;
}

code {
    color: #79c0ff;
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
<main>

<h1>🛒 ShopCardd – Hyperlocal Flash Sale Engine</h1>

<p>
Backend service for managing <strong>high-concurrency flash sales</strong>,
enabling merchants to create limited-inventory deals and users to safely
discover and claim vouchers without overselling.
</p>

<hr>

<h2>🚀 Tech Stack</h2>

<table>
<tr><th>Layer</th><th>Technology</th></tr>
<tr><td>Language</td><td>Java 17</td></tr>
<tr><td>Framework</td><td>Spring Boot</td></tr>
<tr><td>Database</td><td>PostgreSQL</td></tr>
<tr><td>Cache & Locking</td><td>Redis</td></tr>
<tr><td>Containerization</td><td>Docker & Docker Compose</td></tr>
</table>

<h2>✨ Features</h2>
<ul>
<li>Create time-bound flash deals</li>
<li>Geo-based deal discovery</li>
<li>Redis-cached discovery results</li>
<li>Concurrency-safe voucher claiming</li>
<li>Distributed locking using Redis</li>
<li>No overselling or duplicate claims</li>
</ul>

<h2>🧩 System Architecture</h2>

<pre><code>
Client
  ↓
Load Balancer
  ↓
Spring Boot API
  ↓
Redis (Distributed Lock + Cache)
  ↓
PostgreSQL
</code></pre>

<h2>📁 Project Folder Structure</h2>

<pre><code>
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
</code></pre>

<h2>▶️ Run Application</h2>

<pre><code>docker compose up --build</code></pre>

<h2>🔗 API Endpoints</h2>

<h3>Create Deal</h3>
<pre><code>POST /deals</code></pre>

<pre><code>
{
  &quot;merchant_id&quot;: &quot;merchant-123&quot;,
  &quot;title&quot;: &quot;Flat 50% Off&quot;,
  &quot;total_vouchers&quot;: 100,
  &quot;valid_until&quot;: &quot;2026-12-31T23:59:59Z&quot;,
  &quot;location&quot;: {
    &quot;lat&quot;: 19.0760,
    &quot;long&quot;: 72.8777
  }
}
</code></pre>

<h3>Discover Deals</h3>
<pre><code>GET /deals/discover?lat=19.0760&lng=72.8777&radius=5</code></pre>

<h3>Claim Deal</h3>
<pre><code>POST /deals/{dealId}/claim?userId=u-1</code></pre>

<h2>🔐 Concurrency Control</h2>

<pre><code>lock:deal:{dealId}</code></pre>

<h2>📊 API Responses</h2>

<table>
<tr><th>Scenario</th><th>Status</th><th>Response</th></tr>
<tr><td>Success</td><td>200</td><td>{ "status": "Success" }</td></tr>
<tr><td>Already claimed</td><td>400</td><td>User already claimed</td></tr>
<tr><td>Sold out</td><td>400</td><td>Deal sold out</td></tr>
<tr><td>Expired</td><td>400</td><td>Deal expired</td></tr>
</table>

<footer>
<p><strong>Author:</strong> Bhavesh Patil</p>
<p>GitHub: <a href="https://github.com/patilcodesx">https://github.com/patilcodesx</a></p>
<p>Built for ShopCardd Backend Engineering Assessment.</p>
</footer>

</main>
</body>
</html>
