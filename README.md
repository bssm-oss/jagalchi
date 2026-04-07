# Jagalchi Server Roadmap

Jagalchi Server Roadmap is a Spring Boot application for managing and sharing learning roadmaps. It allows users to create, explore, and fork roadmaps to track their learning progress.

## Features

- **Roadmap Management**: Create, update, delete, and view detailed learning roadmaps.
- **Forking (FRK-01)**: Fork existing public roadmaps to create your own copy. Original roadmap connection is maintained via `originalRoadmapId`.
- **Fork Status (FRK-01_02)**: Check how many users have forked a roadmap ("N명이 학습 중") and whether the current user has forked it.
- **Fork Tree (FRK-02)**: Visualize the full lineage of forked roadmaps — root → forks → nested forks.
- **Search & Filtering (SRC-01)**: Search roadmaps by title, description, or tags. Sort by latest, popular, views, forks, or completion. Filter by time period (today, week, month, year).
- **Popular Roadmaps (SRC-02)**: Query popular roadmaps ranked by fork count or view count.
- **Directory System**: Organize roadmaps into hierarchical directories.
- **Progress Tracking**: Track completion of individual nodes within a roadmap.

## API Endpoints

### Roadmap CRUD

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/roadmaps` | Create a new roadmap |
| `GET` | `/roadmaps` | List roadmaps (with pagination, sorting, filtering) |
| `GET` | `/roadmaps/{id}` | Get roadmap detail |
| `PATCH` | `/roadmaps/{id}` | Update a roadmap |
| `DELETE` | `/roadmaps/{id}` | Delete a roadmap |

### Fork Features

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/roadmaps/{id}/fork` | Fork a roadmap (FRK-01_01) |
| `GET` | `/roadmaps/{id}/fork-status` | Get fork count and user fork status (FRK-01_02) |
| `GET` | `/roadmaps/{id}/fork-tree` | Get full fork tree visualization (FRK-02_01) |

### Search & Discovery

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/roadmaps/popular` | Get popular roadmaps by forks/views (SRC-02_01) |

### Query Parameters for `GET /roadmaps`

| Param | Values | Description |
|-------|--------|-------------|
| `sort` | `latest`, `popular`, `views`, `forks`, `completion` | Sort order (SRC-01_03) |
| `period` | `today`, `week`, `month`, `year` | Time filter (SRC-01_03) |
| `query` | string | Full-text search |
| `tags` | string[] | Filter by tags |
| `userId` | number | Filter by owner |
| `directoryId` | number | Filter by directory |
| `isPublic` | boolean | Filter by visibility |

### Query Parameters for `GET /roadmaps/popular`

| Param | Default | Description |
|-------|---------|-------------|
| `sortBy` | `forks` | Sort by `forks` or `views` |
| `page` | `0` | Page number |
| `size` | `10` | Page size (max 50) |

## Tech Stack

- **Java 17+**
- **Spring Boot 3**
- **Spring Data JPA** (MySQL)
- **Liquibase** (Database Migration)
- **Docker & Docker Compose**
- **JWT Authentication**

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Java 17+ (for local development)

### Running with Docker

1. Build the application:
   ```bash
   ./gradlew bootJar -x test
   ```

2. Start the services:
   ```bash
   docker-compose up -d --build
   ```

The application will be available at `http://localhost:8080`.

### API Documentation

Swagger UI is available at `http://localhost:8080/swagger-ui.html` (when running locally).

## Project Structure

- `src/main/java/gajeman/jagalchi/jagalchiserver/api`: REST Controllers and DTOs.
- `src/main/java/gajeman/jagalchi/jagalchiserver/application`: Service layer implementation.
- `src/main/java/gajeman/jagalchi/jagalchiserver/domain`: Domain entities and repositories.
- `src/main/java/gajeman/jagalchi/jagalchiserver/common`: Shared utilities, configurations, and security.

## License

This project is private and for internal use only.

## Database Migrations

This project uses Liquibase for database schema management. Changsets are located in `src/main/resources/db/changelog/changes`. Liquibase will automatically run when starting the application via Docker or locally.
