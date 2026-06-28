# NexaRide

## Overview

NexaRide Backend is a microservices-based ride booking platform designed to simulate real-world distributed system architectures. The system is built using Spring Boot and follows event-driven principles with Kafka, ensuring scalability, fault tolerance, and reliability.

---

## Architecture

The system follows a microservices architecture with clear separation of concerns.

### Services

- API Gateway: Entry point for all client requests
- Booking Service: Core business logic for ride creation and lifecycle management
- OTP Service: Handles OTP generation and validation
- Audit Service: Logs ride events asynchronously
- Notification Service: Sends notifications based on ride events
- User Service: User management and authentication

---

## Tech Stack

- Java 17
- Spring Boot
- Spring Security (JWT Authentication)
- Apache Kafka
- Redis
- MySQL
- Feign Client
- Resilience4j (Circuit Breaker)
- Maven

---

## Key Features

### Event-Driven Architecture
- Kafka-based communication between services
- Single topic (`ride-events`) for ride lifecycle events
- Asynchronous processing for audit and notification services

### Idempotency Handling
- Redis-based duplicate detection
- Unique key strategy: `event:<rideId>-<eventType>`
- Database-level constraint fallback in Audit Service
- Ensures safe handling of Kafka's at-least-once delivery

### Circuit Breaker (Resilience4j)

- Implemented in Booking Service for OTP Service calls
- Prevents cascading failures during external service downtime
- Supports CLOSED, OPEN, and HALF-OPEN states
- Graceful fallback with controlled failure response

### Fault Tolerance

- Retry and fallback handling for external service failures
- Circuit breaker to avoid repeated failed calls
- Distributed system resilience patterns applied

### Security

- JWT-based authentication
- Secure API Gateway routing
- Role-based access control

---

## Ride Lifecycle

1. Create Ride
   - Ride is created and stored
   - OTP is generated via OTP Service
   - BOOKED event published to Kafka

2. Start Ride
   - OTP is validated
   - Ride status updated to STARTED
   - Event published

3. Complete Ride
   - Ride marked as COMPLETED
   - Event published

4. Cancel Ride
   - Ride marked as CANCELLED
   - Event published

---

## Kafka Flow

- Producer: Booking Service
- Topic: `ride-events`
- Consumers:
  - Audit Service
  - Notification Service

### Guarantees

- At-least-once delivery
- Idempotent consumers ensure no duplicate processing

---

## Idempotency Strategy

### Primary Layer (Redis)
- Fast in-memory duplicate detection
- Prevents repeated event processing

### Secondary Layer (Database)
- Unique constraint on `(rideId, eventType)` in Audit Service
- Guarantees no duplicate data persistence

---

## Circuit Breaker Flow

1. Normal operation (CLOSED)
2. Failures exceed threshold
3. Circuit opens (OPEN)
4. Requests blocked and fallback triggered
5. After timeout, HALF-OPEN state
6. System recovers back to CLOSED if successful

---

## Error Handling Strategy

- Fail-fast for critical operations (OTP generation)
- Graceful fallback for external service failures
- Logging of all failure scenarios

---

## How to Run

### Prerequisites

- Java 17
- Maven
- MySQL
- Redis
- Kafka and Zookeeper

---

### Steps

1. Start Zookeeper
2. Start Kafka
3. Start Redis
4. Start MySQL
5. Run services in the following order:
   - API Gateway
   - User Service
   - OTP Service
   - Booking Service
   - Audit Service
   - Notification Service

---

## Testing

- Stop OTP Service to test Circuit Breaker behavior
- Replay Kafka events to test idempotency
- Verify no duplicate entries in Audit logs
- Observe fallback execution during failures

---

## Future Enhancements/(Ongoing)

- Rate limiting for API protection
- Distributed tracing
- Monitoring and metrics (Prometheus/Grafana)
- Centralized logging
- Load balancing with multiple service instances

---

## Design Principles Applied

- Microservices architecture
- Event-driven communication
- Fault tolerance and resilience
- Idempotent processing
- Separation of concerns
- Scalability-first design

---

## Conclusion

This project demonstrates real-world backend system design using modern distributed system principles. It covers practical implementation of Kafka-based event processing, idempotency handling, and circuit breaker patterns, making it suitable for production-grade system design scenarios.
