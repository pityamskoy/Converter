# Converter — Backend

A REST API service for converting structured data files between **JSON↔XML↔CSV** formats,
with support for pattern-based field transformations applied during conversion.

---

## Table of Contents

- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Inner Logic](#inner-logic)
- [API Endpoints](#api-endpoints)
- [Configuration & Environment](#configuration--environment)
- [Running Locally](#running-locally)

---

## Technology Stack

| Category          | Technology              |
|-------------------|-------------------------|
| Language          | Java 25                 |
| Framework         | Spring Boot 4.0.1       |
| Web layer         | Spring MVC              |
| Persistence layer | Spring Data JPA         |
| Database          | PostgreSQL 15           |
| Conversion        | Jackson 2.17.2          |
| Code generation   | Lombok 1.18.42          |
| Build tool        | Maven 4.0.0             |
| Containerization  | Docker + Docker Compose |
| CI/CD             | Gitlab CI/CD            |

---

## Architecture

The project follows a three-layered architecture:

```
HTTP Request
    │
    ▼
Controllers (controllers/frontend/, controllers/api/)
    │  DTO mapping via Mappers
    ▼
Services (services/frontend/, services/api)
    │  Entity mapping via Mappers
    ▼
Repositories (Spring Data JPA)
    │
    ▼
Database (PostgreSQL)
```

### Layer responsibilities

- **Controllers** — parse HTTP requests, delegate to services, serialize responses. Split into two groups:
  - `frontend/` — multipart file upload endpoints + auth + user/pattern CRUD. CORS configured for `https://cson.site`.
  - `api/` — direct body-based JSON↔XML conversion.
- **Services** — business logic: file parsing, format conversion, pattern application, user auth with cookie management.
- **Repositories** — Spring Data JPA repositories for User, Pattern, Modification entities.
- **Mappers** — bidirectional conversion between entities and DTOs.
- **DTOs** — separate controller DTOs (exchanged with clients) and service DTOs (passed between layers).
Creation and update operations use dedicated `ToCreate` / `ToUpdate` variants.

---

## Inner Logic

### Authentication

Authentication uses a `user_id` cookie (max age 4 hours).
All frontend endpoints read this cookie to identify the current user.
Registration and login set the cookie; logout clears it.

### Conversion pipeline

Every file conversion follows these steps:

1. Validate the uploaded file's extension against the declared source format.
2. If a pattern ID is provided, validate that the pattern's `conversionType` matches the requested conversion (e.g., `.json .csv`).
3. Deserialize the file into `List<Map<String, Object>>` (one map per row/object).
4. Apply pattern modifications to each row (if a pattern was provided).
5. Serialize the modified list to the target format.
6. Stream the result back to the client and delete the temporary file.

Supported conversion pairs: `json↔csv`, `json↔xml`, `xml↔csv`.

### Pattern & modification system

A **Pattern** belongs to a user and targets a specific conversion type (e.g., `.json .csv`).
It contains an ordered list of **Modifications**, each capable of:

| `oldName` | `newName`    | `newValue` | `newType`   | Effect                             |
|-----------|--------------|------------|-------------|------------------------------------|
| `"field"` | `null`       | `null`     | `null`      | Delete the field                   |
| `"field"` | `"renamed"`  | `null`     | `null`      | Rename the field                   |
| `"field"` | `"field"`    | `"value"`  | `null`      | Replace the field's value          |
| `"field"` | `"field"`    | `null`     | `"Integer"` | Cast the value to a different type |
| `null`    | `"newField"` | `"value"`  | `null`      | Add a new field                    |

Supported `newType` values: `Integer`, `Float`, `Boolean`, `String`.

When serializing to CSV, nested objects and arrays are stringified as JSON.

### CSV auto-type detection

When reading a CSV file, each cell value is inspected and cast:

| Value pattern          | Java type                              |
|------------------------|----------------------------------------|
| Starts with `{` or `[` | Nested `Map` / `List` (parsed as JSON) |
| Matches `-?\d+`        | `Long`                                 |
| Matches `-?\d*\.\d+`   | `Double`                               |
| Anything else          | `String`                               |

---

## API Endpoints

### Authentication — `/auth`

#### `POST /auth/login`
Log in with credentials.

**Request body:**
```json
{
  "email": "user@example.com",
  "password": "secret"
}
```
**Response:** `200 OK` — sets `user_id` cookie.
```json
{
  "success": true,
  "username": "john",
  "userId": "uuid"
}
```

#### `POST /auth/registration`
Create a new account.

**Request body:**
```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret"
}
```
**Response:** `201 Created` — sets `user_id` cookie, returns created user.
```json
{
  "id": "uuid",
  "username": "john",
  "email": "john@example.com",
  "patterns": []
}
```

#### `DELETE /auth`
Log out the current user (requires `user_id` cookie).

**Response:** `204 No Content` — clears `user_id` cookie.

---

### Users — `/users`

#### `PUT /users`
Update the current user's profile.

**Request body:**
```json
{
  "id": "uuid",
  "username": "new_name",
  "email": "new@example.com",
  "password": "newpass",
  "patterns": []
}
```
**Response:** `200 OK` — returns updated user object.

#### `DELETE /users/{userId}`
Delete a user account.

**Response:** `204 No Content`

---

### Patterns — `/patterns`

#### `GET /patterns/{userId}`
Get all patterns belonging to a user.

**Response:** `200 OK`
```json
[
  {
    "id": "uuid",
    "name": "My Pattern",
    "conversionType": ".json .csv",
    "modifications": [
      {
        "oldName": "field1",
        "newName": "renamed",
        "newType": null,
        "newValue": null
      }
    ]
  }
]
```

#### `POST /patterns`
Create a new pattern.

**Request body:**
```json
{
  "userId": "uuid",
  "name": "My Pattern",
  "conversionType": ".json .csv",
  "modifications": [
    {
      "oldName": "old",
      "newName": "new",
      "newType": null,
      "newValue": null
    }
  ]
}
```
**Response:** `201 Created` — returns created pattern.

#### `PUT /patterns`
Update an existing pattern (id must be present in the body).

**Request body:**
```json
{
  "id": "uuid",
  "name": "My Pattern",
  "conversionType": ".json .csv",
  "modifications": [
    {
      "oldName": "old",
      "newName": "new",
      "newType": null,
      "newValue": null
    }
  ]
}
```

**Response:** `200 OK` — returns updated pattern.

#### `DELETE /patterns/{patternId}`
Delete a pattern.

**Response:** `204 No Content`

---

### File Conversion — `/conversion`

All endpoints accept `multipart/form-data` with:
- `file` (required) — the file to convert.
- `pattern` (optional) — UUID of a pattern to apply.

#### `POST /conversion/json/csv`
Convert JSON → CSV. Returns `text/csv`.

#### `POST /conversion/csv/json`
Convert CSV → JSON. Returns `application/octet-stream`.

#### `POST /conversion/json/xml`
Convert JSON → XML. Returns `application/octet-stream`.

#### `POST /conversion/xml/json`
Convert XML → JSON. Returns `application/octet-stream`.

#### `POST /conversion/xml/csv`
Convert XML → CSV. Returns `text/csv`.

#### `POST /conversion/csv/xml`
Convert CSV → XML. Returns `application/octet-stream`.

---

### Direct Conversion API — `/direct/conversion`

No authentication required. Accepts and returns raw content bodies.

#### `POST /direct/conversion/json/xml`
Convert a JSON body to XML.

- **Request:** `Content-Type: application/json` — any JSON object.
- **Response:** `Content-Type: application/xml` — XML string.

#### `POST /direct/conversion/xml/json`
Convert an XML body to JSON.

- **Request:** `Content-Type: application/xml` — any XML document.
- **Response:** `Content-Type: application/json` — JSON string.

---

## Configuration & Environment

### Required environment variables

| Variable            | Description                            |
|---------------------|----------------------------------------|
| `DB_URL`            | PostgreSQL JDBC URL                    |
| `DB_USERNAME`       | Database username                      |
| `DB_PASSWORD`       | Database password                      |
| `POSTGRES_DB`       | Database name (used by Docker Compose) |
| `POSTGRES_USERNAME` | PostgreSQL superuser (Docker Compose)  |
| `POSTGRES_PASSWORD` | PostgreSQL password (Docker Compose)   |

### Key application settings (`application.yaml`)

```yaml
spring:
  mvc.servlet.path: /api/v1
  servlet.multipart:
    max-file-size: 10MB
    max-request-size: 10MB
  jpa.hibernate.ddl-auto: update
logging:
  file.name: /logs/converter.log
```

---

## Running Locally

### With Docker Compose

```bash
# Create a .env file with the required variables, then:
docker compose up -d
```

The backend will be available at `http://localhost:8080/api/v1`.
PostgreSQL is exposed on port `1234`.
