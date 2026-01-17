<?xml version="1.0" encoding="UTF-8"?>
<readme>

  <title>🛒 ShopCardd – Hyperlocal Flash Sale Engine</title>

  <description>
    Backend service for managing high-concurrency flash sales, enabling merchants
    to create limited-inventory deals and users to safely discover and claim
    vouchers without overselling.
  </description>

  <techStack>
    <technology layer="Language">Java 17</technology>
    <technology layer="Framework">Spring Boot</technology>
    <technology layer="Database">PostgreSQL</technology>
    <technology layer="CacheAndLocking">Redis</technology>
    <technology layer="Containerization">Docker &amp; Docker Compose</technology>
  </techStack>

  <features>
    <feature>Create time-bound flash deals</feature>
    <feature>Geo-based deal discovery</feature>
    <feature>Redis-cached discovery results</feature>
    <feature>Concurrency-safe voucher claiming</feature>
    <feature>Distributed locking using Redis</feature>
    <feature>Prevention of overselling and duplicate claims</feature>
  </features>

  <architecture>
    <flow>Client</flow>
    <flow>Load Balancer</flow>
    <flow>Spring Boot API</flow>
    <flow>Redis (Distributed Lock + Cache)</flow>
    <flow>PostgreSQL</flow>
  </architecture>

  <projectStructure>
    <file>docker-compose.yml</file>
    <file>Dockerfile</file>
    <file>README.md</file>
    <directory name="src">
      <directory name="main">
        <directory name="java">
          <directory name="com.shopcardd.flashsale">
            <directory name="controller"/>
            <directory name="service"/>
            <directory name="repository"/>
            <directory name="entity"/>
            <directory name="dto"/>
            <directory name="config"/>
          </directory>
        </directory>
        <directory name="resources">
          <file>application.yml</file>
          <file>schema.sql</file>
        </directory>
      </directory>
    </directory>
    <file>pom.xml</file>
  </projectStructure>

  <run>

    <prerequisites>
      <tool>Docker</tool>
      <tool>Docker Compose</tool>
    </prerequisites>

    <command>docker compose up --build</command>

  </run>

  <services>
    <service name="API">http://localhost:8080</service>
    <service name="PostgreSQL">localhost:5432</service>
    <service name="Redis">localhost:6379</service>
  </services>

  <redis>
    <verify>docker ps</verify>
    <cli>docker exec -it flashsale-redis redis-cli</cli>
    <ping>PING</ping>
    <expected>PONG</expected>

    <keys>
      <key>lock:deal:{dealId}</key>
      <key>cache:deals:{lat}:{lng}:{radius}</key>
    </keys>
  </redis>

  <api>

    <endpoint method="POST" path="/deals">
      <request>
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
      </request>
    </endpoint>

    <endpoint method="GET" path="/deals/discover">
      <description>
        Geo-based deal discovery with Redis caching (TTL 30 seconds)
      </description>
    </endpoint>

    <endpoint method="POST" path="/deals/{dealId}/claim">
      <description>Concurrency-safe voucher claim</description>
    </endpoint>

  </api>

  <guarantees>
    <guarantee>No overselling</guarantee>
    <guarantee>One voucher per user</guarantee>
    <guarantee>Inventory never below zero</guarantee>
    <guarantee>Safe under heavy concurrency</guarantee>
  </guarantees>

  <failureHandling>
    <failure type="Redis">Fail-safe rejection</failure>
    <failure type="Database">Transaction rollback</failure>
    <failure type="Duplicate">Graceful rejection</failure>
    <failure type="InvalidRequest">Proper HTTP error</failure>
  </failureHandling>

  <deployment>
    <note>Stateless Spring Boot services</note>
    <note>Horizontally scalable</note>
    <note>Redis handles contention</note>
    <note>PostgreSQL is source of truth</note>
  </deployment>

  <author>
    <name>Bhavesh Patil</name>
    <github>https://github.com/patilcodesx</github>
  </author>

</readme>
