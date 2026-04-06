# API Reference - Jagalchi Services

Generated: 2026-04-06T08:20:00Z

Purpose
This document is a complete integration specification for frontend teams. It lists all REST endpoints, DTOs, authentication details, error formats, and WebSocket (STOMP) flows so a frontend developer can implement and test clients without reading source code.

Base URL (recommended)

- Use the API Gateway as primary entry: http://<gateway-host>:8080
- Direct service hosts can be used for debugging: user, node, roadmap hosts listed in environment or deployment docs.

Authentication

- Access tokens: Bearer JWT in Authorization header
  - Header: Authorization: Bearer <accessToken>
- Refresh tokens: issued as HttpOnly cookies by the server on login
- WebSocket/STOMP: include headers on CONNECT
  - X-User-ID: <userId>
  - X-User-Role: USER | ADMIN | GUEST

Global conventions

- All timestamps are in milliseconds epoch unless noted
- All JSON keys use camelCase
- Error response structure (all services):

```json
{
  "timestamp": "2026-04-06T08:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "INVALID_INPUT",
  "message": "email is required",
  "path": "/users"
}
```

- Common error codes: INVALID_INPUT, AUTH_REQUIRED, UNAUTHORIZED, FORBIDDEN, NOT_FOUND, VALIDATION_FAILED, INTERNAL_ERROR

## 1. API Gateway (health proxy & aggregator)

### Endpoints

- GET /user/health, /user/healthz
  - Proxy to USER service (/health,/api/health,/actuator/health)
  - Success: returns backend body (e.g., {"status":"UP","module":"user"})
  - Fail: 503 {"status":"DOWN"}

- GET /node/health, /node/healthz
  - Proxy to NODE service

- GET /roadmap/health, /roadmap/healthz
  - Proxy to ROADMAP service

- GET /health
  - Aggregates the three services and returns:
    {"user":"UP|DOWN","node":"UP|DOWN","roadmap":"UP|DOWN"}

Env vars used by gateway

- USER_SERVICE_URL
- NODE_SERVICE_URL
- ROADMAP_SERVICE_URL
  (Defaults are in HealthProxyController source)

## 2. User service (base path: /users)

### POST /users

- Purpose: create account
- Request (201 Created on success):

```json
{
  "email": "alice@example.com",
  "name": "alice",
  "password": "P@ssw0rd"
}
```

- Response (201):

```json
{
  "id": 123,
  "email": "alice@example.com",
  "name": "alice"
}
```

- Errors: 400 INVALID_INPUT

### POST /users/verification

- Purpose: send signup verification code
- Request:

```json
{ "email": "alice@example.com" }
```

- Response: 200 OK

### PATCH /users/verification

- Purpose: verify signup code
- Request:

```json
{ "email": "alice@example.com", "code": "123456" }
```

- Response: 200 OK or 400 VALIDATION_FAILED

### PATCH /users/auth/password-reset

- Purpose: send password reset code
- Request: `{ "email":"alice@example.com" }`

### PATCH /users/auth/password-reset/verify

- Purpose: verify password-reset code
- Request: `{ "email":"alice@example.com", "code":"123456" }`

### POST /users/auth/login

- Purpose: login
- Request:

```json
{ "email": "alice@example.com", "password": "P@ssw0rd" }
```

- Response (200): `{ "accessToken":"<jwt>" }`
- Side-effect: Refresh token cookie is set (HttpOnly)
- Errors: 401 UNAUTHORIZED

### PATCH /users/auth/refresh

- Purpose: issue new access token using refresh token
- Request body (if cookie-less): `{ "refreshToken":"<token>" }`
- Response: `{ "accessToken":"<new-jwt>" }`

### DELETE /users

- Purpose: delete authenticated user's account
- Auth: Authorization header required
- Response: 204 No Content

### PATCH /users/profile

- Purpose: update profile
- Request:

```json
{
  "user": {
    "profileImage": "https://.../img.jpg",
    "bio": "Hello",
    "externalLinks": { "github": "https://github.com/alice" }
  }
}
```

- Response: 200 MessageResponse { message }

### GET /users?name={name}

- Purpose: search user by name
- Response example:

```json
{
  "user": {
    "name": "alice",
    "email": "alice@example.com",
    "profileImageUrl": null,
    "bio": null,
    "isFollowed": false,
    "stats": { "followersCount": 10, "followingCount": 5 },
    "externalLinks": {}
  },
  "streak": { "currentStreak": 5, "activities": [{ "date": "2026-04-01", "count": 1 }] }
}
```

### PATCH /users/{name}/follow

- Purpose: toggle follow (auth required)
- Request: `{ "toggle": true }`
- Response: 200 OK

### GET /users/{name}/followers

### GET /users/{name}/followings

- Purpose: list followers/followings
- Response: FollowListResponse

```json
{
  "userId": 123,
  "type": "FOLLOWERS",
  "totalCount": 10,
  "users": [{ "id": 1, "name": "bob", "profileImage": null, "isFollowing": true }]
}
```

### GET /health, /api/health

- Purpose: health check for user service
- Response: `{ "status":"UP","module":"user" }`

## 3. Node service (REST + WebSocket/STOMP)

### REST

#### GET /api/roadmap/{roadmapId}/events?since={sequence}

- Purpose: fetch server-confirmed events after 'since'
- Response: 200 [ Event ]
- Event example:

```json
{
  "type": "EVENT",
  "eventId": "evt-301",
  "sequence": 43,
  "payload": {
    "type": "MOVE",
    "target": { "type": "NODE", "object": "node-1" },
    "state": { "x": 300, "y": 200 }
  }
}
```

#### GET /health, /api/health

- Response: `{ "status":"UP","module":"node" }`

### WebSocket / STOMP

- WS URL (via gateway): ws://<gateway-host>:8080/ws
- STOMP connect headers: X-User-ID, X-User-Role

#### Subscription & send patterns

- Subscribe to per-user ACKs: `SUBSCRIBE /user/queue/ack`
- Subscribe to roadmap events: `SUBSCRIBE /topic/roadmap/{roadmapId}/state`
- Send actions: `SEND /app/roadmap/{roadmapId}/action` with Action JSON

#### Action domain (client -> server)

Action JSON fields:

- actionId: string (client-generated)
- roadmap: string
- action: string (CREATE | EDIT | DELETE | UNDO | REDO)
- payload: object | null (see below)

ActionPayload:

- type: string (INFO | MOVE | SCALE | LOCK | COPY | ...)
- target: { type: string, object: string }
- prev: object | null
- next: object | null
- data: object | null

#### Ack/Nack

- ACK (to user): `{ "type":"ACK","actionId":"act-123","status":"ACCEPTED" }`
- NACK (to user): `{ "actionId":"act-123","actionType":"CREATE","errorCode":"VALIDATION_FAILED","errorMessage":"label required" }`

#### Cursor tracking

- SEND `/app/roadmap/{roadmapId}/cursor`
- Payload CursorPosition:

```json
{
  "userId": 123,
  "userName": "alice",
  "x": 100.5,
  "y": 200.25,
  "timestamp": 1680000000000,
  "state": "NORMAL",
  "targetId": null
}
```

- Broadcast to `/topic/roadmap/{roadmapId}/cursors`

## 4. Roadmap service

- Current source exposes only health endpoints
- GET /health, /api/health -> `{ "status":"UP","module":"roadmap" }`
- Roadmap business logic (actions/events) implemented in node service

## DTOs (full shapes & validations)

- **SignUpRequest**: { email: string (email, not blank), name: string (not blank), password: string (not blank) }
- **SignUpResponse**: { id: long, email: string, name: string }
- **LoginRequest**: { email: string, password: string }
- **LoginResponse**: { accessToken: string }
- **RefreshTokenRequest**: { refreshToken: string }
- **SendVerificationCodeRequest**: { email: string }
- **VerifyRequest**: { email: string, code: string }
- **ChangePasswordRequest**: { email: string, newPassword: string }
- **UpdateProfileRequest**: { user: { profileImage?: string, bio?: string, externalLinks?: map<string,string> } }
- **FollowToggleRequest**: { toggle: boolean }
- **FollowUserResponse**: { id: long, name: string, profileImage?: string, isFollowing: boolean }
- **FollowListResponse**: { userId, type, totalCount, users: [FollowUserResponse] }
- **QueryUserResponse**: { user: QueryUserDto, streak: StreakResponseDto }
- **QueryUserDto**: { name, email, profileImageUrl, bio, isFollowed, stats: { followersCount, followingCount }, externalLinks }
- **Event**: { type, eventId, sequence, payload: map }
- **CursorPosition**: { userId?, userName, x, y, timestamp, state, targetId? }

## Examples (summary)

- Create action (client->server):

```json
{
  "actionId": "act-123",
  "roadmap": "roadmap-1",
  "action": "CREATE",
  "payload": {
    "type": "INFO",
    "target": { "type": "NODE", "object": "tmp-1" },
    "data": { "label": "New node", "x": 120, "y": 300 }
  }
}
```

- ACK:

```json
{ "type": "ACK", "actionId": "act-123", "status": "ACCEPTED" }
```

- NACK:

```json
{
  "actionId": "act-123",
  "actionType": "CREATE",
  "errorCode": "INVALID_PAYLOAD",
  "errorMessage": "missing label"
}
```

## Integration examples (quick)

- Login:
  ```bash
  curl -X POST http://<gateway-host>:8080/users/auth/login -H "Content-Type: application/json" -d '{"email":"alice@example.com","password":"P@ssw0rd"}'
  ```
- Health:
  ```bash
  curl http://<gateway-host>:8080/health
  ```

## Next steps (recommended)

- Generate OpenAPI 3.0 YAML from controllers and DTOs
- Provide example 4xx/5xx responses for each endpoint
- Create STOMP automated tests (Node.js) to validate ACK/NACK/event broadcast
