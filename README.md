# Converter — Backend

A REST API service for converting structured data files between **JSON↔XML↔CSV** formats,
with support for pattern-based field transformations applied during conversion.

---

## Table of Contents

- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Inner Logic](#inner-logic)
- [API Endpoints](#api-endpoints)
- [Error Responses](#error-responses)
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
Services (services/frontend/, services/api/)
    │  Entity mapping via Mappers
    ▼
Repositories (Spring Data JPA)
    │
    ▼
Database (PostgreSQL)
```

### Layer responsibilities

- **Controllers** — parse HTTP requests, delegate to services, serialize responses. Split into two groups:
  - `frontend/` — multipart file upload endpoints + auth + user/pattern/modification CRUD. 
CORS configured for `https://cson.site`.
  - `api/` — direct body-based JSON↔XML conversion.
- **Services** — business logic: file parsing, format conversion, pattern application, user auth with cookie management.
- **Repositories** — Spring Data JPA repositories for `User`, `Pattern`, `Modification` entities.
- **Mappers** — bidirectional conversion between entities and DTOs.
- **DTOs** — separate controller DTOs (exchanged with clients) and service DTOs (passed between layers).
  Creation and update operations use dedicated `ToCreate` / `ToUpdate` variants.

---

## Project Structure

```
src/main/java/team/anonyms/converter/
│
├── Main.java                          # Application entry point
│
├── configs/
│   ├── CorsConfiguration.java         # Global CORS setup
│   └── JacksonConfiguration.java      # Jackson mapper beans
│
├── controllers/
│   ├── GlobalExceptionHandler.java    # @RestControllerAdvice for all exceptions
│   ├── api/
│   │   └── ConversionApiController.java   # POST /direct/conversion (JSON↔XML, no auth)
│   └── frontend/
│       ├── AuthenticationController.java  # POST /auth/login, POST /auth/registration, DELETE /auth
│       ├── ConversionFrontendController.java # POST /conversion/* (multipart file upload)
│       ├── ModificationController.java    # GET /modifications/{patternId}/{limit}/{offset}
│       ├── PatternController.java         # CRUD /patterns
│       ├── UserController.java            # PUT /users, DELETE /users/{userId}
│       └── pagination/
│           └── PaginationHandler.java     # Generic slice helper for paginated responses
│
├── dto/
│   ├── controller/
│   │   ├── credentials/
│   │   ├── modification/
│   │   ├── pattern/
│   │   ├── responses/errors/
│   │   └── user/
│   │     
│   └── service/    # Mirrors some of controller dtos to be used in service layer
│       ├── credentials/
│       ├── modification/
│       ├── pattern/
│       └── user/
│
├── entities/
│   ├── Modification.java
│   ├── Pattern.java
│   └── User.java
│
├── mappers/
│   ├── CredentialsMapper.java
│   ├── ModificationMapper.java
│   ├── PatternMapper.java
│   └── UserMapper.java
│
├── repositories/
│   ├── ModificationRepository.java
│   ├── PatternRepository.java
│   └── UserRepository.java
│
├── services/
│   ├── api/
│   │   └── ConversionApiService.java       # JSON↔XML conversion (no file I/O)
│   └── frontend/
│       ├── AuthenticationService.java      # Login / register / logout + cookie management
│       ├── ConversionFrontendService.java  # File-based conversions (all 6 pairs)
│       ├── ModificationService.java
│       ├── PatternService.java
│       └── UserService.java
│
└── utility/
    ├── annotations/
    │   └── LastSupportedProjectVersion.java
    ├── enums/
    │   └── ProjectVersion.java
    └── exceptions/
        ├── IllegalPatternException.java
        └── UnsupportedExtensionException.java
```

---

### DTOs

DTOs are split into two layers: **controller DTOs** (used in HTTP requests/responses) and **service DTOs** 
(used for service layer). The naming convention is:

| Suffix         | Purpose                                    |
|----------------|--------------------------------------------|
| `Dto`          | Read / response DTO                        |
| `ToCreateDto`  | Request body for creation (no `id` field)  |
| `ToUpdateDto`  | Request body for updates                   |

---

## Inner Logic

### Authentication

Authentication uses a `user_id` cookie (max age 4 hours).
All frontend endpoints read this cookie to identify the current user.
Registration and login set the cookie; logout clears it.

### Conversion pipeline

Every file conversion follows these steps:

1. Validate the uploaded file's extension against the declared source format.
2. Deserialize the file into `List<Map<String, Object>>` (one map per row/object).
3. Apply pattern modifications to each row (if a pattern was provided).
4. Serialize the modified list to the target format.
5. Stream the result back to the client and delete the temporary file.

Supported conversion pairs: `json↔csv`, `json↔xml`, `xml↔csv`.

### Pattern & modification system

A **Pattern** belongs to a user and targets all conversion types.
It contains an ordered list of **Modifications**, each capable of:

| `oldName` | `newName`     | `newType`   | `newValue` | Effect                           |
|-----------|---------------|-------------|------------|----------------------------------|
| `"field"` | `null`        | `null`      | `null`     | Delete the field                 |
| `"field"` | `"renamed"`   | `null`      | `null`     | Rename the field                 |
| `"field"` | `null`        | `"Integer"` | `null`     | Cast a value to a different type |
| `"field"` | `null`        | `null`      | `"value"`  | Reassign a field's value         |
| `null`    | `"new field"` | `null`      | `"value"`  | Add a new field                  |

Supported `newType` values: `Integer`, `Float`, `Boolean`, `String`.

It is important to know that ***it is possible to combine*** in one modification
renaming field with reassigning a new value and with changing field's data type.

Please, note that when serializing to CSV, nested objects and arrays are stringified as JSON/XML.

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

All endpoints are served under the base path `/api/v1`

### Authentication — `/auth`

#### `POST /auth/login`
Log in with credentials.

**Request body** (`application/json`):
```json
{
  "email": "user@example.com",
  "password": "secret"
}
```

**Response `200 OK`** — sets `user_id` cookie:
```json
{
  "success": true,
  "username": "john",
  "email": "john@example.com",
  "userId": "uuid"
}
```

**Response `400 Bad Request`** — invalid credentials.

---

#### `POST /auth/registration`
Create a new account.

**Request body** (`application/json`):
```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret"
}
```

**Response `201 Created`** — sets `user_id` cookie:
```json
{
  "id": "uuid",
  "username": "john",
  "email": "john@example.com",
  "patterns": []
}
```

---

#### `DELETE /auth`
Log out the current user.

**Cookie required:** `user_id`

**Response `204 No Content`** — clears `user_id` cookie.

---

### Users — `/users`

#### `PUT /users`
Update the current user's profile.

**Request body** (`application/json`):
```json
{
  "id": "uuid",
  "username": "new_name",
  "email": "new@example.com",
  "password": "newPassword",
  "patterns": [
    { 
      "id": "uuid",
      "name": "My Pattern"
    }
  ]
}
```

**Response `200 OK`**:
```json
{
  "id": "uuid",
  "username": "new_name",
  "email": "new@example.com",
  "patterns": [
    { 
      "id": "uuid", 
      "name": "My Pattern"
    }
  ]
}
```

**Response `404 Not Found`** — user not found.

---

#### `DELETE /users/{userId}`
Delete a user account.

**Path variable:** `userId` (UUID)

**Response `204 No Content`**

**Response `404 Not Found`** — user not found.

---

### Patterns — `/patterns`

#### `GET /patterns/{userId}/{limit}/{offset}`
Get a paginated slice of patterns belonging to a user.

**Path variables:**
- `userId` — UUID of the user
- `limit` — maximum number of items to return
- `offset` — number of items to skip

**Response `200 OK`**:
```json
[
  { 
    "id": "uuid",
    "name": "My Pattern"
  },
  {
    "id": "uuid",
    "name": "Another Pattern"
  }
]
```

**Response `404 Not Found`** — user not found.

---

#### `POST /patterns`
Create a new pattern.

**Request body** (`application/json`):
```json
{
  "userId": "uuid",
  "name": "My Pattern",
  "modifications": [
    {
      "oldName": "field",
      "newName": "renamed field",
      "newType": null,
      "newValue": null
    }
  ]
}
```

**Response `201 Created`**:
```json
{
  "id": "uuid",
  "name": "My Pattern"
}
```

**Response `404 Not Found`** — user not found.

---

#### `PUT /patterns`
Update an existing pattern. The `id` must be present in the body. 
Note that this API endpoint is the only way to update modifications. Modification are updated only through updating a pattern they belong to.

**Request body** (`application/json`):
```json
{
  "id": "uuid",
  "name": "Updated Pattern",
  "modifications": [
    {
      "id": "uuid",
      "oldName": "field",
      "newName": "renamed field",
      "newType": null,
      "newValue": null
    }
  ]
}
```
> If a modification in the list has `id: null`, it is treated as a new modification to create.

**Response `200 OK`**:
```json
{ 
  "id": "uuid",
  "name": "Updated Pattern"
}
```

**Response `404 Not Found`** — pattern not found.

---

#### `DELETE /patterns/{patternId}`
Delete a pattern and all its modifications.

**Path variable:** `patternId` (UUID)

**Response `204 No Content`**

**Response `404 Not Found`** — pattern not found.

---

### Modifications — `/modifications`

#### `GET /modifications/{patternId}/{limit}/{offset}`
Get a paginated slice of modifications belonging to a pattern.

**Path variables:**
- `patternId` — UUID of the pattern
- `limit` — maximum number of items to return
- `offset` — number of items to skip

**Response `200 OK`**:
```json
[
  {
    "id": "uuid",
    "oldName": "field",
    "newName": "renamed field",
    "newType": null,
    "newValue": null
  }
]
```

**Response `404 Not Found`** — pattern not found.

---

### File Conversion — `/conversion`

All endpoints consume `multipart/form-data` and stream the converted file back as an attachment.

**Form parts:**
- `file` (required) — the source file to convert.
- `pattern` (optional, query param) — UUID of a saved pattern to apply during conversion.

**Common error responses:**
- `400 Bad Request` — file extension does not match the declared source format.
- `500 Internal Server Error` — I/O error during conversion.

| Endpoint                    | Source format | Target format | Response `Content-Type`    |
|-----------------------------|---------------|---------------|----------------------------|
| `POST /conversion/json/csv` | `.json`       | `.csv`        | `text/csv`                 |
| `POST /conversion/csv/json` | `.csv`        | `.json`       | `application/octet-stream` |
| `POST /conversion/json/xml` | `.json`       | `.xml`        | `application/octet-stream` |
| `POST /conversion/xml/json` | `.xml`        | `.json`       | `application/octet-stream` |
| `POST /conversion/xml/csv`  | `.xml`        | `.csv`        | `text/csv`                 |
| `POST /conversion/csv/xml`  | `.csv`        | `.xml`        | `application/octet-stream` |

All conversion responses include a `Content-Disposition: attachment; filename="<original_name>.<target_format>"` header.

---

### Direct Conversion API — `/direct/conversion`

No authentication required. Accepts and returns raw content bodies.

#### `POST /direct/conversion/json/xml`
Convert a JSON body to XML.

**Request:** `Content-Type: application/json` — any JSON object.

**Response `200 OK`:** `Content-Type: application/xml` — XML string.

---

#### `POST /direct/conversion/xml/json`
Convert an XML body to JSON.

**Request:** `Content-Type: application/xml` — any XML document.

**Response `200 OK`:** `Content-Type: application/json` — JSON string.

---

## Error Responses

All unhandled exceptions are caught by `GlobalExceptionHandler`. The mapping is:

| Exception                       | HTTP Status                 | Response body                    |
|---------------------------------|-----------------------------|----------------------------------|
| `EntityNotFoundException`       | `404 Not Found`             | empty                            |
| `UnsupportedExtensionException` | `400 Bad Request`           | empty                            |
| `IllegalArgumentException`      | `400 Bad Request`           | empty                            |
| `NullPointerException`          | `500 Internal Server Error` | empty                            |
| `CredentialException`           | `400 Bad Request`           | JSON — `message: "CREDENTIAL"`   |
| `IllegalPatternException`       | `400 Bad Request`           | JSON — `message: "PATTERN"`      |
| `EmailExistsException`          | `400 Bad Request`           | JSON — `message: "EMAIL EXISTS"` |

Exceptions that return a JSON body use the following envelope (defined in `ErrorResponse`):

```json
{
  "statusCode": 400,
  "message": "CREDENTIAL",
  "time": "2025-01-01T00:00:00Z"
}
```

---

## Configuration & Environment

### Required environment variables

| Variable            | Description                            |
|---------------------|----------------------------------------|
| `POSTGRES_USERNAME` | PostgreSQL superuser (Docker Compose)  |
| `POSTGRES_PASSWORD` | PostgreSQL password (Docker Compose)   |
| `POSTGRES_DB`       | Database name (used by Docker Compose) |
| `DATABASE_URL`      | PostgreSQL JDBC URL                    |

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
# Create a .env file with the required variables, then run:
docker compose up -d
```

The backend will be available at `http://localhost:8080/api/v1`.
PostgreSQL is exposed on port `1234`.