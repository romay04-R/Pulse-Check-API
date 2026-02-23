# Pulse-Check-API

A robust Dead Man's Switch API for monitoring critical infrastructure devices. Built with Spring Boot and PostgreSQL.

## 🏗️ Architecture Diagram

```mermaid
graph TD
    A[Device] -->|POST /monitors| B[Monitor Controller]
    A -->|POST /monitors/{id}/heartbeat| B
    A -->|POST /monitors/{id}/pause| B
    A -->|POST /monitors/{id}/resume| B
    
    B --> C[Monitor Service]
    C --> D[Monitor Repository]
    C --> E[Cache Layer]
    
    F[Monitor Alert Service] -->|Scheduled Check| C
    F --> G[Email Service]
    G --> H[SMTP Server]
    
    I[Dashboard Controller] --> C
    
    D --> J[(PostgreSQL)]
    E --> K[Caffeine Cache]
    
    style A fill:#e1f5fe
    style J fill:#f3e5f5
    style H fill:#fff3e0
```

## 🚀 Features

- **Device Registration**: Create monitors with custom timeout settings
- **Heartbeat Monitoring**: Real-time device status tracking
- **Automatic Alerts**: Email notifications when devices go offline
- **Pause/Resume**: Maintenance mode to prevent false alarms
- **Batch Operations**: Bulk device registration
- **Dashboard**: Real-time operational overview
- **Caching**: High-performance response times
- **Professional Email Templates**: HTML-based alert notifications

## 📋 Prerequisites

- Java 11+
- PostgreSQL 12+
- Maven 3.6+

## 🛠️ Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/Pulse-Check-API.git
   cd Pulse-Check-API
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE pulse_check_db;
   ```

3. **Configure Environment**
   ```bash
   # Copy and edit the application.yml
   cp src/main/resources/application.yml.example src/main/resources/application.yml
   
   # Update database credentials and email settings
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080/api`

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api/monitors
```

### 1. Create Monitor
**POST** `/monitors`

Create a new monitor for a device.

**Request Body:**
```json
{
  "deviceId": "device-001",
  "timeout": 60,
  "alertEmail": "admin@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Monitor created successfully",
  "data": {
    "id": "monitor-uuid",
    "deviceId": "device-001",
    "timeout": 60,
    "alertEmail": "admin@example.com",
    "createdAt": "2026-02-23T15:30:00Z",
    "lastHeartbeat": "2026-02-23T15:30:00Z",
    "isActive": true,
    "isPaused": false,
    "expiresAt": "2026-02-23T15:31:00Z"
  },
  "timestamp": "2026-02-23T15:30:00Z"
}
```

### 2. Send Heartbeat
**POST** `/monitors/{id}/heartbeat`

Reset the countdown timer for a device.

**Path Parameters:**
- `id` (string): Monitor ID

**Response:**
```json
{
  "success": true,
  "message": "Heartbeat received for device: device-001",
  "timestamp": "2026-02-23T15:31:00Z"
}
```

### 3. Pause Monitor
**POST** `/monitors/{id}/pause`

Pause monitoring for maintenance.

**Response:**
```json
{
  "success": true,
  "message": "Monitor paused for device: device-001",
  "timestamp": "2026-02-23T15:32:00Z"
}
```

### 4. Resume Monitor
**POST** `/monitors/{id}/resume`

Resume monitoring and restart timer.

**Response:**
```json
{
  "success": true,
  "message": "Monitor resumed for device: device-001",
  "timestamp": "2026-02-23T15:33:00Z"
}
```

### 5. Get Monitor
**GET** `/monitors/{id}`

Retrieve monitor details.

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "monitor-uuid",
    "deviceId": "device-001",
    "timeout": 60,
    "alertEmail": "admin@example.com",
    "createdAt": "2026-02-23T15:30:00Z",
    "lastHeartbeat": "2026-02-23T15:31:00Z",
    "isActive": true,
    "isPaused": false,
    "expiresAt": "2026-02-23T15:32:00Z"
  },
  "timestamp": "2026-02-23T15:33:00Z"
}
```

### 6. Get All Monitors
**GET** `/monitors`

Retrieve all monitors.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "monitor-uuid-1",
      "deviceId": "device-001",
      "timeout": 60,
      "alertEmail": "admin@example.com",
      "createdAt": "2026-02-23T15:30:00Z",
      "lastHeartbeat": "2026-02-23T15:31:00Z",
      "isActive": true,
      "isPaused": false,
      "expiresAt": "2026-02-23T15:32:00Z"
    }
  ],
  "timestamp": "2026-02-23T15:33:00Z"
}
```

### 7. Batch Create Monitors
**POST** `/monitors/batch`

Create multiple monitors at once.

**Request Body:**
```json
{
  "devices": [
    {
      "id": "device-001",
      "timeout": 60,
      "alert_email": "admin@example.com"
    },
    {
      "id": "device-002",
      "timeout": 120,
      "alert_email": "alerts@example.com"
    },
    {
      "id": "device-003",
      "timeout": 30,
      "alert_email": "monitor@example.com"
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Batch creation completed: 2 successful, 1 failed",
  "data": {
    "totalRequested": 3,
    "successful": 2,
    "failed": 1,
    "createdMonitors": [
      {
        "id": "monitor-uuid-1",
        "deviceId": "device-001",
        "timeout": 60,
        "alertEmail": "admin@example.com"
      },
      {
        "id": "monitor-uuid-2",
        "deviceId": "device-002",
        "timeout": 120,
        "alertEmail": "alerts@example.com"
      }
    ],
    "errors": [
      {
        "deviceId": "device-003",
        "error": "Monitor already exists for device: device-003"
      }
    ]
  },
  "timestamp": "2026-02-23T15:34:00Z"
}
```

### 8. Device Status Dashboard
**GET** `/monitors/dashboard`

Get operational overview statistics.

**Response:**
```json
{
  "success": true,
  "data": {
    "activeDevices": 1247,
    "downDevices": 3,
    "alertsToday": 12,
    "averageUptime": 99.7
  },
  "timestamp": "2026-02-23T15:35:00Z"
}
```

### 9. Delete Monitor
**DELETE** `/monitors/{id}`

Delete a monitor permanently.

**Response:**
```json
{
  "success": true,
  "message": "Monitor deleted successfully",
  "timestamp": "2026-02-23T15:36:00Z"
}
```

## 🎯 Developer's Choice Features

### 1. **Batch Device Registration**
**Why Added**: For large-scale deployments, registering devices individually is inefficient. The batch endpoint allows bulk registration with detailed error reporting.

### 2. **Device Status Dashboard**
**Why Added**: Provides quick operational overview without digging through logs. Essential for monitoring teams to assess system health at a glance.

### 3. **Resume Functionality**
**Why Added**: Complements the pause feature. After maintenance, technicians can easily resume monitoring without waiting for the next heartbeat.

## 🔄 Error Handling

All endpoints return consistent error responses:

```json
{
  "success": false,
  "message": "Error description",
  "timestamp": "2026-02-23T15:30:00Z"
}
```

Common HTTP Status Codes:
- `200 OK` - Successful operation
- `201 Created` - Resource created successfully
- `206 Partial Content` - Batch operation partially successful
- `400 Bad Request` - Invalid input
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## 📧 Email Alerts

When a device goes offline, the system sends professional HTML email alerts with:
- Device information
- Last heartbeat timestamp
- Expected expiration time
- Timeout duration
- Alert generation time

## 🏃‍♂️ Running Tests

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Generate test coverage report
mvn jacoco:report
```

## 📊 Performance Features

- **Caching**: Caffeine cache for frequently accessed monitors
- **Connection Pooling**: Optimized database connections
- **Batch Operations**: Efficient bulk device registration
- **Scheduled Tasks**: Optimized alert checking

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Ensure PostgreSQL is running
   - Check database credentials in `application.yml`

2. **Email Not Sending**
   - Verify SMTP settings
   - Check email credentials and security settings

3. **Cache Issues**
   - Cache auto-refreshes every 30 minutes
   - Manual cache eviction on monitor updates

## 📝 License

MIT License - see LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

---

**Built with ❤️ for critical infrastructure monitoring**

### The Solution
You need to build a **Dead Man’s Switch API**. Devices will register a "monitor" with a countdown timer (e.g., 60 seconds). If the device fails to "ping" (send a heartbeat) to the API before the timer runs out, the system automatically triggers an alert.

---

## 2. Technical Objective
Build a backend service that manages stateful timers.

* **Registration:** Allow a client to create a monitor with a specific timeout duration.
* **Heartbeat:** Reset the countdown when a ping is received.
* **Trigger:** Fire a webhook (or log a critical error) if the countdown reaches zero.


---

## 3. Getting Started

1.  **Fork this Repository:** Do not clone it directly. Create a fork to your own GitHub account.
2.  **Environment:** You may use **Node.js, Python, Java or Go, etc.**.
3.  **Submission:** Your final submission will be a link to your forked repository containing:
    * The source code.
    * The **Architecture Diagram**
    * The `README.md` with documentation.

---

## 4. The Architecture Diagram 
**Task:** Before you write any code, you must design the logic flow.
**Deliverable:** A **Sequence Diagram** or **State Flowchart** embedded in your `README.md`.

---

## 5. User Stories & Acceptance Criteria

### User Story 1: Registering a Monitor
**As a** device administrator,  
**I want to** create a new monitor for my device,  
**So that** the system knows to track its status.

**Acceptance Criteria:**
- [ ] The API accepts a `POST /monitors` request.
- [ ] Input: `{"id": "device-123", "timeout": 60, "alert_email": "admin@critmon.com"}`.
- [ ] The system starts a countdown timer for 60 seconds associated with `device-123`.
- [ ] Response: `201 Created` with a confirmation message.

### User Story 2: The Heartbeat (Reset)
**As a** remote device,  
**I want to** send a signal to the server,  
**So that** my timer is reset and no alert is sent.

**Acceptance Criteria:**
- [ ] The API accepts a `POST /monitors/{id}/heartbeat` request.
- [ ] If the ID exists and the timer has NOT expired:
    - [ ] Restart the countdown from the beginning (e.g., reset to 60 seconds).
    - [ ] Return `200 OK`.
- [ ] If the ID does not exist:
    - [ ] Return `404 Not Found`.

### User Story 3: The Alert (Failure State)
**As a** support engineer,  
**I want to** be notified immediately if a device stops sending heartbeats,  
**So that** I can deploy a repair team.

**Acceptance Criteria:**
- [ ] If the timer for `device-123` reaches 0 seconds (no heartbeat received):
    - [ ] The system must internally "fire" an alert.
    - [ ] **Implementation:** For this project, simply `console.log` a JSON object: `{"ALERT": "Device device-123 is down!", "time": <timestamp>}`. (Or simulate sending an email).
    - [ ] The monitor status changes to `down`.

---

## 6. Bonus User Story (The "Snooze" Button)
**As a** maintenance technician,  
**I want to** pause monitoring while I am repairing a device,  
**So that** I don't trigger false alarms.

**Acceptance Criteria:**
- [ ] Create a `POST /monitors/{id}/pause` endpoint.
- [ ] When called, the timer stops completely. No alerts will fire.
- [ ] Calling the heartbeat endpoint again automatically "un-pauses" the monitor and restarts the timer.

---

## 7. The "Developer's Choice" Challenge
We value engineers who look for "what's missing."

**Task:** Identify **one** additional feature that makes this system more robust or user-friendly.
1.  **Implement it.**
2.  **Document it:** Explain *why* you added it in your README.

---

## 8. Documentation Requirements
Your final `README.md` must replace these instructions. It must cover:

1.  **Architecture Diagram** 
2.  **Setup Instructions** 
3.  **API Documentation** 
4.  **The Developer's Choice:** Explanation of your added feature.

---
Submit your repo link via the [online](https://forms.office.com/e/rGKtfeZCsH) form.

## 🛑 Pre-Submission Checklist
**WARNING:** Before you submit your solution, you **MUST** pass every item on this list.
If you miss any of these critical steps, your submission will be **automatically rejected** and you will **NOT** be invited to an interview.

### 1. 📂 Repository & Code
- [x] **Public Access:** Is your GitHub repository set to **Public**? (We cannot review private repos).
- [x] **Clean Code:** Did you remove unnecessary files (like `node_modules`, `.env` with real keys, or `.DS_Store`)?
- [x] **Run Check:** if we clone your repo and run `npm start` (or equivalent), does the server start immediately without crashing?

### 2. 📄 Documentation (Crucial)
- [x] **Architecture Diagram:** Did you include a visual Diagram (Flowchart or Sequence Diagram) in the README?
- [x] **README Swap:** Did you **DELETE** the original instructions (the problem brief) from this file and replace it with your own documentation?
- [x] **API Docs:** Is there a clear list of Endpoints and Example Requests in the README?


### 3. 🧹 Git Hygiene
- [x] **Commit History:** Does your repo have multiple commits with meaningful messages? (A single "Initial Commit" is a red flag).

---
**Ready?**
If you checked all the boxes above, submit your repository link in the application form. Good luck! 🚀
