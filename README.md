# Smart Traffic Management & Incident Detection Platform

A reactive Spring Boot application for monitoring traffic conditions, calculating congestion levels, detecting emergency vehicles, and assigning traffic-management priority.

The project is designed as a foundation for a smart traffic-management system that can later support real-time traffic streaming, incident detection, emergency-vehicle route prioritization, and intelligent traffic-signal optimization.

---

## 🚦 Project Overview

The **Smart Traffic Management & Incident Detection Platform** collects traffic information for road intersections and analyzes:

* Number of vehicles
* Number of waiting vehicles
* Average vehicle speed
* Traffic congestion level
* Emergency vehicle presence
* Emergency vehicle type
* Traffic priority

The system calculates a congestion score from `0–100` and classifies the traffic condition as:

|       Score | Traffic Status |
| ----------: | -------------- |
|      `< 25` | LOW            |
| `25 – < 50` | MEDIUM         |
| `50 – < 75` | HIGH           |
|     `>= 75` | CRITICAL       |

In addition to congestion, emergency vehicles receive a separate priority level.

### Emergency Priority

| Condition              | Priority    |
| ---------------------- | ----------- |
| Normal traffic         | `NORMAL`    |
| Heavy/critical traffic | `HIGH`      |
| Emergency vehicle      | `EMERGENCY` |
| Ambulance              | `AMBULANCE` |

An ambulance does **not artificially increase the congestion score**. Instead, congestion and emergency priority are treated as separate concepts.

---

# 🏗️ Architecture

```text
                    ┌──────────────────────┐
                    │       Client         │
                    │ Postman / Frontend   │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │ Traffic Controller   │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │   Traffic Service    │
                    │    / ServiceImpl     │
                    └──────────┬───────────┘
                               │
                 ┌─────────────┴─────────────┐
                 ▼                           ▼
        ┌─────────────────┐         ┌─────────────────┐
        │ Mongo Repository│         │ Traffic Analyzer│
        └────────┬────────┘         └────────┬────────┘
                 │                           │
                 ▼                           ▼
        ┌─────────────────┐         ┌─────────────────┐
        │    MongoDB      │         │ Congestion      │
        │ traffic_events  │         │ + Priority      │
        └─────────────────┘         └────────┬────────┘
                                             │
                                             ▼
                                    ┌──────────────────┐
                                    │ Traffic Status   │
                                    └──────────────────┘
```

---

# 🛠️ Technology Stack

* **Java**
* **Spring Boot**
* **Spring WebFlux**
* **Spring Data MongoDB Reactive**
* **MongoDB**
* **Project Reactor**
* **Lombok**
* **Maven**
* **Postman**
* **Git / GitHub**

---

# 📁 Project Structure

```text
src
└── main
    └── java
        └── com.mdswaley.traffic.smart_traffic_management
            │
            ├── Analyzer
            │   └── TrafficAnalyzer.java
            │
            ├── Controller
            │   └── TrafficController.java
            │
            ├── Model
            │   ├── TrafficEvent.java
            │   ├── TrafficStatus.java
            │   └── EmergencyVehicleType.java
            │
            ├── Repository
            │   └── TrafficEventRepository.java
            │
            ├── Service
            │   ├── TrafficService.java
            │   └── TrafficServiceImp.java
            │
            └── error
                └── TrafficDataNotFoundException.java
```

---

# 🚗 Traffic Event

A traffic event represents the current traffic condition at an intersection.

Example:

```json
{
    "intersectionId": "I002",
    "roadId": "ROAD_A",
    "vehicleCount": 50,
    "averageSpeed": 15,
    "waitingVehicles": 20,
    "emergencyVehicle": false,
    "emergencyVehicleType": "NONE"
}
```

### Fields

| Field                  | Description                             |
| ---------------------- | --------------------------------------- |
| `intersectionId`       | Unique intersection identifier          |
| `roadId`               | Road associated with the event          |
| `vehicleCount`         | Number of vehicles                      |
| `averageSpeed`         | Average vehicle speed                   |
| `waitingVehicles`      | Number of waiting vehicles              |
| `timestamp`            | Event creation time                     |
| `emergencyVehicle`     | Whether an emergency vehicle is present |
| `emergencyVehicleType` | Type of emergency vehicle               |

Supported emergency vehicle types:

```text
NONE
AMBULANCE
FIRE_TRUCK
POLICE
```

---

# 📊 Congestion Score

The congestion score is calculated using three traffic factors.

```text
Vehicle Count     → 40%
Waiting Vehicles  → 40%
Average Speed     → 20%
```

The formula is:

```java
vehicleScore = Math.min(vehicleCount, 100);

waitingScore = Math.min(waitingVehicles, 100);

speedScore = Math.max(0, 100 - (averageSpeed * 5));

score =
    (vehicleScore * 0.4)
    + (waitingScore * 0.4)
    + (speedScore * 0.2);
```

## Why 40%, 40%, 20%?

The current implementation gives:

* Vehicle count: **40%**
* Waiting vehicles: **40%**
* Average speed: **20%**

These are configurable design assumptions for the current version and can later be calibrated using real traffic data.

## Why `averageSpeed * 5`?

The current model converts speed into an inverse congestion contribution.

For example:

```text
0 km/h  → 100
5 km/h  → 75
10 km/h → 50
15 km/h → 25
20 km/h → 0
```

The multiplier `5` is a scaling factor used by the current model. It can later be adjusted based on real-world traffic data.

---

# 🚑 Emergency Vehicle Priority

Emergency vehicles are handled separately from congestion scoring.

For example:

```text
Congestion Score = 33
Traffic Status   = MEDIUM
Emergency       = AMBULANCE
Priority        = AMBULANCE
```

The ambulance does not change the congestion score from `33` to another value.

Instead, it creates a separate priority signal for future traffic-signal optimization.

### Priority Logic

```text
                    Traffic Event
                         │
                         ▼
                Emergency Vehicle?
                    /          \
                  No            Yes
                  │              │
                  ▼              ▼
               NORMAL      Emergency Type
                              │
                    ┌─────────┼─────────┐
                    ▼         ▼         ▼
                AMBULANCE  FIRE_TRUCK  POLICE
                    │         │         │
                    ▼         └────┬────┘
               AMBULANCE          │
               PRIORITY        EMERGENCY
```

Ambulances receive the highest priority because the system needs to support future emergency-route and traffic-signal prioritization.

---

# 🔌 API Endpoints

## Create Traffic Event

```http
POST /api/traffic/events
```

Example request:

```json
{
    "intersectionId": "I002",
    "roadId": "ROAD_A",
    "vehicleCount": 50,
    "averageSpeed": 15,
    "waitingVehicles": 20,
    "emergencyVehicle": false,
    "emergencyVehicleType": "NONE"
}
```

---

## Create Ambulance Traffic Event

```http
POST /api/traffic/events
```

Example:

```json
{
    "intersectionId": "I002",
    "roadId": "ROAD_A",
    "vehicleCount": 50,
    "averageSpeed": 15,
    "waitingVehicles": 20,
    "emergencyVehicle": true,
    "emergencyVehicleType": "AMBULANCE"
}
```

---

## Get Traffic Events

```http
GET /api/traffic/events/{intersectionId}
```

Example:

```http
GET /api/traffic/events/I002
```

Returns traffic events associated with the intersection.

---

## Get Current Traffic Status

```http
GET /api/traffic/status/{intersectionId}
```

Example:

```http
GET /api/traffic/status/I002
```

Example response:

```json
{
    "intersectionId": "I002",
    "status": "MEDIUM",
    "congestionScore": 33.0,
    "vehicleCount": 50,
    "waitingVehicles": 20,
    "averageSpeed": 15.0,
    "vehicleType": "AMBULANCE",
    "priority": "AMBULANCE"
}
```

---

## Live Traffic

```http
GET /api/traffic/live/{intersectionId}
```

The application periodically retrieves the latest traffic information and analyzes it reactively.

---

# 🔄 Reactive Processing

The application uses **Spring WebFlux** and **Project Reactor**.

The service layer uses:

```java
Mono<TrafficStatus>
```

for a single traffic status and:

```java
Flux<TrafficEvent>
```

for multiple traffic events.

Example:

```java
return repository
        .findByIntersectionId(intersectionId)
        .sort((event1, event2) ->
                event2.getTimestamp()
                        .compareTo(event1.getTimestamp()))
        .next()
        .flatMap(trafficAnalyzer::analyze);
```

This allows traffic data to be processed using a non-blocking reactive approach.

---

# 🗄️ MongoDB

Traffic events are stored in:

```text
traffic_events
```

MongoDB is used because traffic events are naturally represented as flexible documents and the schema can evolve as new traffic information is introduced.

Example document:

```json
{
    "_id": "...",
    "intersectionId": "I002",
    "roadId": "ROAD_A",
    "vehicleCount": 50,
    "averageSpeed": 15.0,
    "waitingVehicles": 20,
    "timestamp": "...",
    "emergencyVehicle": true,
    "emergencyVehicleType": "AMBULANCE"
}
```

---

# ⚙️ Configuration

Example `application.yml`:

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

Adjust the MongoDB URI according to your local environment.

---

# ▶️ Running the Application

## 1. Clone the repository

```bash
git clone https://github.com/mdswaley/Smart-Traffic-Management-Incident-Detection-Platform.git
```

## 2. Open the project

Open the project in:

* IntelliJ IDEA
* Eclipse
* VS Code

## 3. Start MongoDB

Make sure MongoDB is running locally.

Default MongoDB port:

```text
27017
```

## 4. Run Spring Boot

Using Maven:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

Or run the main Spring Boot application from IntelliJ IDEA.

The application will start on:

```text
http://localhost:8080
```

---

# 🧪 Testing With Postman

Recommended testing sequence:

### 1. Normal traffic

```json
{
    "intersectionId": "I002",
    "roadId": "ROAD_A",
    "vehicleCount": 50,
    "averageSpeed": 15,
    "waitingVehicles": 20,
    "emergencyVehicle": false,
    "emergencyVehicleType": "NONE"
}
```

### 2. Ambulance

```json
{
    "intersectionId": "I002",
    "roadId": "ROAD_A",
    "vehicleCount": 50,
    "averageSpeed": 15,
    "waitingVehicles": 20,
    "emergencyVehicle": true,
    "emergencyVehicleType": "AMBULANCE"
}
```

### 3. Check status

```http
GET /api/traffic/status/I002
```

Verify:

```text
status = MEDIUM
congestionScore = 33.0
priority = AMBULANCE
```

---

# 🧭 Current Development Progress

```text
[x] Spring Boot project setup
[x] MongoDB configuration
[x] TrafficEvent model
[x] TrafficStatus model
[x] Reactive repository
[x] Traffic service
[x] Traffic analyzer
[x] Congestion score calculation
[x] Traffic status classification
[x] Emergency vehicle detection
[x] Ambulance priority
[x] Fire truck / police emergency priority
[ ] Traffic signal optimizer
[ ] Emergency vehicle route prioritization
[ ] Green corridor for ambulances
[ ] Real-time event streaming
[ ] Incident detection
[ ] Kafka integration
[ ] WebSocket/SSE frontend
[ ] Traffic dashboard
```

---

# 🚀 Future Architecture

The planned system will evolve toward:

```text
Traffic Sensors
      │
      ▼
Traffic Events
      │
      ▼
Kafka / Event Stream
      │
      ▼
Traffic Analyzer
      │
      ├───────────────┐
      ▼               ▼
Congestion       Emergency
Analysis         Detection
      │               │
      └───────┬───────┘
              ▼
       Signal Optimizer
              │
       ┌──────┴───────┐
       ▼              ▼
Normal Traffic   Emergency Route
Optimization     Prioritization
                      │
                      ▼
                 Ambulance
                 Green Corridor
```

---

# 🎯 Project Goals

The long-term goal is to build a smart traffic-management platform capable of:

1. Monitoring traffic in real time.
2. Detecting traffic congestion.
3. Detecting traffic incidents.
4. Identifying emergency vehicles.
5. Giving ambulances high-priority treatment.
6. Optimizing traffic signals.
7. Creating emergency green corridors.
8. Processing traffic events reactively.
9. Supporting scalable event streaming.
10. Providing a real-time traffic dashboard.

---

# 👨‍💻 Development Approach

The project is being developed incrementally.

Current focus:

```text
Traffic Data
     ↓
Congestion Analysis
     ↓
Emergency Vehicle Detection
     ↓
Priority Assignment
     ↓
Traffic Signal Optimization
```

The next major feature is the **Traffic Signal Optimizer**, which will use the `AMBULANCE` priority to determine how an intersection should respond when an ambulance is approaching.
