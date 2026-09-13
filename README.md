# 🚦 Smart Traffic Management & Incident Detection Platform

A reactive, real-time traffic management platform built with **Spring Boot WebFlux**, **Reactive MongoDB**, and **Project Reactor**.

The goal of this project is to process traffic sensor events, analyze congestion, detect incidents, provide real-time traffic updates, and eventually optimize traffic signals.

---

## 📌 Project Overview

Modern cities generate large amounts of traffic data from sensors installed at intersections and roads.

This project simulates a smart traffic management system that receives information such as:

* Vehicle count
* Average vehicle speed
* Waiting vehicles
* Traffic density
* Traffic-light status
* Accidents/incidents
* Emergency vehicles

The system processes these events reactively and calculates the current traffic condition.

The project is also designed as a **learning project for Spring WebFlux and Reactive Programming**, gradually evolving from a single WebFlux application into an event-driven architecture using Kafka and microservices.

---

# 🎯 Project Goals

The main goals are:

1. Learn Spring WebFlux practically.
2. Understand `Mono` and `Flux`.
3. Work with reactive MongoDB.
4. Learn Project Reactor operators.
5. Implement real-time streaming using SSE.
6. Understand backpressure.
7. Implement functional WebFlux.
8. Introduce Kafka for event streaming.
9. Detect traffic incidents.
10. Prioritize emergency vehicles.
11. Optimize traffic signal timings.
12. Eventually evolve the application into microservices.

---

# 🏗️ Current Architecture

At the current stage, the application is intentionally kept as a **single Spring Boot WebFlux application**.

```text
                  Traffic Sensor
                       │
                       │ Traffic Event
                       ▼
              ┌──────────────────┐
              │ Traffic Controller│
              │     WebFlux       │
              └────────┬─────────┘
                       │
                       ▼
              ┌──────────────────┐
              │ Traffic Service  │
              └────────┬─────────┘
                       │
                       ▼
             Reactive MongoDB
                       │
                       ▼
              Traffic Analyzer
                       │
                       ▼
              Traffic Status
                       │
                       ▼
                  SSE Stream
                       │
                       ▼
                Live Dashboard
```

Kafka and microservices will be introduced in later stages.

---

# 🛠️ Technology Stack

## Backend

* Java
* Spring Boot
* Spring WebFlux
* Project Reactor
* Spring Data Reactive MongoDB

## Database

* MongoDB

## Event Streaming

Planned:

* Apache Kafka

## Real-Time Communication

* Server-Sent Events (SSE)

## Build Tool

* Maven

## Future Infrastructure

* Docker
* Docker Compose

---

# 📂 Project Structure

Current project structure:

```text
smart-traffic-management
│
├── src
│   └── main
│       ├── java
│       │   └── com.example.traffic
│       │       │
│       │       ├── analyzer
│       │       │   └── TrafficAnalyzer.java
│       │       │
│       │       ├── controller
│       │       │   └── TrafficController.java
│       │       │
│       │       ├── exception
│       │       │   ├── GlobalExceptionHandler.java
│       │       │   └── TrafficDataNotFoundException.java
│       │       │
│       │       ├── model
│       │       │   ├── TrafficEvent.java
│       │       │   └── TrafficStatus.java
│       │       │
│       │       ├── repository
│       │       │   └── TrafficEventRepository.java
│       │       │
│       │       └── service
│       │           ├── TrafficService.java
│       │           └── BackpressureService.java
│       │
│       └── resources
│           └── application.yml
│
├── pom.xml
└── README.md
```

---

# ⚙️ Configuration

The application uses MongoDB.

`application.yml`:

```yaml
spring:
  application:
    name: smart-traffic-management

  data:
    mongodb:
      uri: mongodb://localhost:27017/smart_traffic_management

server:
  port: 8080
```

MongoDB must be running before starting the application.

---

# 📦 Maven Dependencies

The main dependencies are:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
```

---

# 🚦 Traffic Event

A traffic sensor sends an event containing information about an intersection.

Example:

```json
{
  "intersectionId": "I001",
  "roadId": "ROAD_A",
  "vehicleCount": 85,
  "averageSpeed": 12.5,
  "waitingVehicles": 40,
  "timestamp": "2026-09-13T20:00:00"
}
```

---

# 📊 Traffic Status

The system analyzes traffic events and generates a traffic status.

Possible statuses:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

Example response:

```json
{
  "intersectionId": "I001",
  "status": "HIGH",
  "congestionScore": 68.5,
  "vehicleCount": 85,
  "waitingVehicles": 40,
  "averageSpeed": 12.5
}
```

---

# 🔌 REST APIs

## 1. Submit Traffic Event

```http
POST /api/traffic/events
```

### Request

```json
{
  "intersectionId": "I001",
  "roadId": "ROAD_A",
  "vehicleCount": 85,
  "averageSpeed": 12.5,
  "waitingVehicles": 40
}
```

### Response

```json
{
  "id": "68c...",
  "intersectionId": "I001",
  "roadId": "ROAD_A",
  "vehicleCount": 85,
  "averageSpeed": 12.5,
  "waitingVehicles": 40,
  "timestamp": "2026-09-13T20:00:00"
}
```

---

## 2. Get Traffic Events

```http
GET /api/traffic/events/{intersectionId}
```

Example:

```http
GET /api/traffic/events/I001
```

Returns:

```text
Flux<TrafficEvent>
```

---

## 3. Get Current Traffic Status

```http
GET /api/traffic/status/{intersectionId}
```

Example:

```http
GET /api/traffic/status/I001
```

Returns:

```text
Mono<TrafficStatus>
```

---

## 4. Live Traffic Stream

```http
GET /api/traffic/live/{intersectionId}
```

The endpoint uses:

```text
text/event-stream
```

and continuously sends traffic updates.

Example:

```text
data: {"intersectionId":"I001","status":"MEDIUM",...}

data: {"intersectionId":"I001","status":"HIGH",...}

data: {"intersectionId":"I001","status":"CRITICAL",...}
```

---

# 🧠 Reactive Programming Concepts

This project is designed to demonstrate the following Project Reactor concepts.

## Mono

Used when there is zero or one result.

Example:

```java
Mono<TrafficStatus>
```

---

## Flux

Used when multiple values are emitted.

Example:

```java
Flux<TrafficEvent>
```

---

## map()

Used to transform one object into another.

```java
.map(trafficAnalyzer::analyze)
```

Conceptually:

```text
TrafficEvent
     ↓
    map()
     ↓
TrafficStatus
```

---

## flatMap()

Used when the transformation itself returns a reactive type.

```java
.flatMap(trafficAnalyzer::analyze)
```

Conceptually:

```text
Mono<TrafficEvent>
       ↓
    flatMap()
       ↓
Mono<TrafficStatus>
```

---

## filter()

Used to remove invalid sensor data.

```java
.filter(event ->
        event.getVehicleCount() >= 0
        && event.getWaitingVehicles() >= 0
        && event.getAverageSpeed() >= 0
)
```

---

## next()

Used to retrieve the latest event from a `Flux`.

```java
.next()
```

Converts:

```text
Flux<TrafficEvent>
```

into:

```text
Mono<TrafficEvent>
```

---

## switchIfEmpty()

Used when no valid traffic data exists.

```java
.switchIfEmpty(
    Mono.error(
        new TrafficDataNotFoundException(...)
    )
)
```

---

# 🔄 Reactive Traffic Pipeline

The current traffic-status pipeline is:

```text
MongoDB
   │
   ▼
Flux<TrafficEvent>
   │
   ▼
filter()
   │
   ▼
sort()
   │
   ▼
next()
   │
   ▼
Mono<TrafficEvent>
   │
   ▼
flatMap()
   │
   ▼
Mono<TrafficStatus>
```

---

# 🌐 Real-Time SSE

The live endpoint currently uses a periodic reactive stream.

```java
Flux.interval(Duration.ofSeconds(5))
```

Conceptually:

```text
Every 5 seconds
      │
      ▼
Find latest event
      │
      ▼
Analyze traffic
      │
      ▼
TrafficStatus
      │
      ▼
SSE
```

This provides a foundation for a future real-time traffic dashboard.

---

# 🔙 Backpressure

The project also contains a backpressure experiment.

The producer can generate events faster than the consumer can process them.

```text
Producer
   │
   │ 100 events/sec
   ▼
Reactive Pipeline
   │
   │ 10 events/sec
   ▼
Consumer
```

The project demonstrates:

```java
onBackpressureBuffer()
onBackpressureDrop()
onBackpressureLatest()
```

### Buffer

Keeps pending events.

```java
.onBackpressureBuffer()
```

### Drop

Drops events when the downstream cannot keep up.

```java
.onBackpressureDrop()
```

### Latest

Keeps the latest available value.

```java
.onBackpressureLatest()
```

For a live traffic dashboard, keeping the latest state can be more useful than processing every intermediate sensor reading.

---

# 🚨 Planned Incident Detection

The next stages will introduce incident detection.

Potential incident types:

```text
ACCIDENT
EMERGENCY_VEHICLE
ROAD_BLOCK
ABNORMAL_CONGESTION
```

Example accident detection concept:

```text
Sudden speed decrease
        +
Large number of waiting vehicles
        +
Abnormal traffic pattern
        ↓
ACCIDENT SUSPECTED
```

---

# 🚑 Emergency Vehicle Priority

The system will eventually support emergency vehicles such as:

```text
AMBULANCE
FIRE_TRUCK
POLICE
```

Concept:

```text
Emergency Vehicle Detected
          │
          ▼
Identify Route
          │
          ▼
Identify Intersections
          │
          ▼
Prioritize Traffic Signals
          │
          ▼
       GREEN
          │
          ▼
Emergency Vehicle Passes
          │
          ▼
Restore Normal Signal Plan
```

---

# 🚥 Traffic Signal Optimization

The system will eventually calculate signal durations dynamically.

Example:

```text
Intersection I001

ROAD_A → 90 vehicles
ROAD_B → 20 vehicles
ROAD_C → 15 vehicles
ROAD_D → 10 vehicles
```

Instead of:

```text
Every road → GREEN 30 sec
```

the optimizer may calculate:

```text
ROAD_A → GREEN 90 sec
ROAD_B → GREEN 30 sec
ROAD_C → GREEN 20 sec
ROAD_D → GREEN 20 sec
```

The actual optimization algorithm will be developed in a later phase.

---

# 📨 Future Kafka Architecture

The current application uses MongoDB polling for the live-stream learning exercise.

The target architecture will eventually become event-driven:

```text
Traffic Sensors
      │
      ▼
Traffic Gateway
      │
      ▼
    Kafka
      │
      ▼
Traffic Processing Service
      │
      ├──────────────┐
      ▼              ▼
Traffic Analyzer   Incident Detector
      │              │
      └───────┬──────┘
              ▼
       Signal Optimizer
              │
              ▼
           MongoDB
              │
              ▼
        SSE / WebSocket
              │
              ▼
       Traffic Dashboard
```

---

# 🧩 Future Microservices

After the reactive concepts are completed, the application will be split into services such as:

```text
traffic-gateway
traffic-processing
traffic-analyzer
incident-detector
signal-optimizer
dashboard-api
```

The application will **not** start as microservices. The monolithic reactive application is intentional so that the WebFlux concepts can be learned first.

---

# 🗺️ Development Roadmap

```text
[✓] Step 1 — Spring WebFlux project setup

[✓] Step 2 — Reactive MongoDB

[✓] Step 3 — Traffic congestion analysis

[✓] Step 4 — Reactive operators & error handling

[✓] Step 5 — Flux + Server-Sent Events

[✓] Step 6 — Backpressure

[ ] Step 7 — Functional WebFlux

[ ] Step 8 — Advanced traffic event processing

[ ] Step 9 — Windowing & aggregation

[ ] Step 10 — Kafka integration

[ ] Step 11 — Incident detection

[ ] Step 12 — Emergency vehicle priority

[ ] Step 13 — Traffic signal optimization

[ ] Step 14 — Dashboard

[ ] Step 15 — Microservices

[ ] Step 16 — Docker & deployment
```

---

# 🧪 Testing Strategy

The project should eventually contain tests for:

### Controller

```text
POST /api/traffic/events
GET  /api/traffic/events/{id}
GET  /api/traffic/status/{id}
GET  /api/traffic/live/{id}
```

### Service

Test:

```text
Valid traffic event
Invalid traffic event
No traffic data
Traffic status calculation
```

### Reactive Streams

Test:

```text
Mono
Flux
filter
map
flatMap
switchIfEmpty
Backpressure
SSE
```

---

# ▶️ Running the Application

## 1. Start MongoDB

Make sure MongoDB is running on:

```text
localhost:27017
```

Database:

```text
smart_traffic_management
```

## 2. Start the application

Using Maven:

```bash
./mvnw spring-boot:run
```

or on Windows:

```bash
mvnw.cmd spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

---

# 🔍 Example Development Flow

A typical development/test flow is:

```text
1. Start MongoDB
       ↓
2. Start Spring Boot
       ↓
3. POST traffic event
       ↓
4. Event stored in MongoDB
       ↓
5. Request traffic status
       ↓
6. TrafficAnalyzer calculates score
       ↓
7. Request live endpoint
       ↓
8. Receive traffic updates through SSE
```

---

# 📚 Learning Objectives

By completing this project, you should be comfortable with:

* Reactive programming
* Spring WebFlux
* `Mono`
* `Flux`
* Reactive MongoDB
* Reactive repositories
* Project Reactor operators
* Error handling in reactive pipelines
* Server-Sent Events
* Backpressure
* Functional WebFlux
* Reactive event processing
* Kafka
* Event-driven architecture
* Microservices

---

# 🚀 Final Vision

The final platform will process traffic events in real time and provide intelligent traffic-management decisions.

```text
                    ┌─────────────────┐
                    │ Traffic Sensors │
                    └────────┬────────┘
                             │
                             ▼
                         Kafka
                             │
                             ▼
                 ┌─────────────────────┐
                 │ Traffic Processing  │
                 └──────────┬──────────┘
                            │
             ┌──────────────┼──────────────┐
             ▼              ▼              ▼
        Traffic         Incident        Emergency
        Analyzer        Detector         Handler
             │              │              │
             └──────────────┼──────────────┘
                            ▼
                    Signal Optimizer
                            │
                            ▼
                         MongoDB
                            │
                            ▼
                    Real-Time Dashboard
```

The ultimate objective is a **reactive, event-driven smart traffic management platform** capable of processing continuous traffic data, identifying incidents, prioritizing emergency vehicles, and dynamically optimizing traffic signals.
