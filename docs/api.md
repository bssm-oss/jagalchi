# API Reference - Jagalchi Services

Updated: 2026-04-07

## Base URL

- API Gateway: `http://<gateway-host>:8080`
- WebSocket: `ws://<gateway-host>:8080/ws`

## Authentication

- Access tokens: Bearer JWT in `Authorization` header
- Refresh tokens: HttpOnly cookie (set by server on login)
- WebSocket/STOMP: `Authorization: Bearer <token>` OR `X-User-ID` + `X-User-Role` headers on CONNECT

## Global Conventions

- Timestamps: milliseconds epoch unless noted
- JSON keys: camelCase
- Error response:

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

- Error codes: `INVALID_INPUT`, `AUTH_REQUIRED`, `UNAUTHORIZED`, `FORBIDDEN`, `NOT_FOUND`, `VALIDATION_FAILED`, `INTERNAL_ERROR`

---

## 1. API Gateway

### Health

| Method | Path              | Response                                                     |
| ------ | ----------------- | ------------------------------------------------------------ |
| GET    | `/health`         | `{"user":"UP\|DOWN","node":"UP\|DOWN","roadmap":"UP\|DOWN"}` |
| GET    | `/user/health`    | Proxy to user service                                        |
| GET    | `/node/health`    | Proxy to node service                                        |
| GET    | `/roadmap/health` | Proxy to roadmap service                                     |

### WebSocket

- `ws://<gateway-host>:8080/ws` — STOMP pass-through to node service
- Forwards `Authorization`, `X-User-ID`, `X-User-Role` headers

---

## 2. User Service (base: `/users`)

### Auth

| Method | Path                                | Request                     | Response                                           |
| ------ | ----------------------------------- | --------------------------- | -------------------------------------------------- |
| POST   | `/users/auth/login`                 | `{ email, password }`       | `{ accessToken }` + Set-Cookie (refresh, HttpOnly) |
| PATCH  | `/users/auth/refresh`               | (HttpOnly cookie auto-sent) | `{ accessToken }`                                  |
| PATCH  | `/users/auth/password-reset`        | `{ email }`                 | 200 OK                                             |
| PATCH  | `/users/auth/password-reset/verify` | `{ email, code }`           | 200 OK                                             |

### Account

| Method | Path                  | Request                     | Response                    |
| ------ | --------------------- | --------------------------- | --------------------------- |
| POST   | `/users`              | `{ email, name, password }` | `{ id, email, name }` (201) |
| POST   | `/users/verification` | `{ email }`                 | 200 OK                      |
| PATCH  | `/users/verification` | `{ email, code }`           | 200 OK                      |
| DELETE | `/users`              | (Auth required)             | 204 No Content              |

### Profile

| Method | Path                 | Request                                             | Response                                            |
| ------ | -------------------- | --------------------------------------------------- | --------------------------------------------------- |
| GET    | `/users?name={name}` | —                                                   | `{ user: QueryUserDto, streak: StreakResponseDto }` |
| PATCH  | `/users/profile`     | `{ user: { profileImage?, bio?, externalLinks? } }` | `{ message }`                                       |

### Follow

| Method | Path                       | Request               | Response             |
| ------ | -------------------------- | --------------------- | -------------------- |
| PATCH  | `/users/{name}/follow`     | `{ toggle: boolean }` | `{ message }`        |
| GET    | `/users/{name}/followers`  | —                     | `FollowListResponse` |
| GET    | `/users/{name}/followings` | —                     | `FollowListResponse` |

### DTOs

- **QueryUserDto**: `{ name, email, profileImageUrl, bio, isFollowed, stats: { followersCount, followingCount }, externalLinks }`
- **StreakResponseDto**: `{ currentStreak, activities: [{ date, count }] }`
- **FollowListResponse**: `{ userId, type, totalCount, users: [{ id, name, profileImage, isFollowing }] }`

---

## 3. Roadmap Service (base: `/roadmaps`, `/directories`)

### Roadmap CRUD

| Method | Path                    | Request                                                               | Response                |
| ------ | ----------------------- | --------------------------------------------------------------------- | ----------------------- |
| POST   | `/roadmaps`             | `CreateRoadmapRequest`                                                | `RoadmapResponse` (201) |
| GET    | `/roadmaps/{roadmapId}` | —                                                                     | `RoadmapDetailResponse` |
| GET    | `/roadmaps`             | Query: `page, size, sort, query, userId, directoryId, isPublic, tags` | `RoadmapListResponse`   |
| PATCH  | `/roadmaps/{roadmapId}` | `UpdateRoadmapRequest`                                                | `{ id, updatedAt }`     |
| DELETE | `/roadmaps/{roadmapId}` | —                                                                     | `{ message }`           |

### Directory

| Method | Path                         | Request              | Response                |
| ------ | ---------------------------- | -------------------- | ----------------------- |
| GET    | `/directories/tree`          | —                    | `DirectoryTreeResponse` |
| POST   | `/directories`               | `{ name, parentId }` | `DirectoryResponse`     |
| PATCH  | `/directories/{directoryId}` | `{ name }`           | `DirectoryResponse`     |
| DELETE | `/directories/{directoryId}` | —                    | 204                     |

### Progress

| Method | Path                                            | Request                 | Response               |
| ------ | ----------------------------------------------- | ----------------------- | ---------------------- |
| GET    | `/roadmaps/{roadmapId}/my-progress`             | —                       | `ProgressResponse`     |
| GET    | `/roadmaps/{roadmapId}/users/{userId}/progress` | —                       | `ProgressResponse`     |
| POST   | `/roadmaps/{roadmapId}/nodes/{nodeId}/complete` | `{ isCompleted, link }` | `NodeCompleteResponse` |

### DTOs

- **CreateRoadmapRequest**: `{ title (required), description?, directoryId?, isPublic?, thumbnailUrl?, tags? }`
- **UpdateRoadmapRequest**: `{ title?, description?, isPublic?, thumbnailUrl?, tags? }`
- **RoadmapResponse**: `{ id, title, description, directoryId, ownerId, isPublic, viewCount, createdAt, updatedAt }`
- **RoadmapDetailResponse**: extends RoadmapResponse + `{ owner, stats, tags }`
- **RoadmapListResponse**: paginated list of RoadmapResponse
- **ProgressResponse**: `{ roadmapId, totalNodes, completedNodes, progressPercentage, completedNodeIds, updatedAt }`
- **NodeCompleteResponse**: `{ nodeId, isCompleted, roadmapProgress, completedAt }`
- **DirectoryTreeResponse**: nested directory tree
- **DirectoryResponse**: `{ id, name, parentId }`

---

## 4. Node Service (REST + WebSocket/STOMP)

### REST

| Method | Path                                               | Response  |
| ------ | -------------------------------------------------- | --------- |
| GET    | `/api/roadmap/{roadmapId}/events?since={sequence}` | `Event[]` |

### WebSocket / STOMP

- Connect: `ws://<gateway-host>:8080/ws` with `Authorization: Bearer <token>`

**Subscriptions:**

| Destination                          | Description              |
| ------------------------------------ | ------------------------ |
| `/user/queue/ack`                    | Per-user ACK/NACK        |
| `/topic/roadmap/{roadmapId}/state`   | Roadmap events broadcast |
| `/topic/roadmap/{roadmapId}/cursors` | Cursor positions         |

**Sends:**

| Destination                       | Payload                                                                    |
| --------------------------------- | -------------------------------------------------------------------------- |
| `/app/roadmap/{roadmapId}/action` | `{ actionId, roadmap, action: CREATE\|EDIT\|DELETE\|UNDO\|REDO, payload }` |
| `/app/roadmap/{roadmapId}/cursor` | `{ userId, userName, x, y, timestamp, state, targetId }`                   |

### Action Payload

```json
{
  "type": "INFO|MOVE|SCALE|LOCK|COPY",
  "target": { "type": "NODE|SECTION|EDGE", "object": "id" },
  "prev": {},
  "next": {},
  "data": {}
}
```

### ACK/NACK

- ACK: `{ "type": "ACK", "actionId": "...", "status": "ACCEPTED" }`
- NACK: `{ "actionId": "...", "actionType": "...", "errorCode": "...", "errorMessage": "..." }`

---

## Notes

- Node service is authoritative for real-time state and event sequencing
- Roadmap service manages persisted metadata and permissions
- REST events endpoint is for reconciliation / missed messages
- Gateway probes multiple health paths per service (`/health`, `/api/health`, `/actuator/health`)
