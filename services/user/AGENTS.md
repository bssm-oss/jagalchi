# Jagalchi User Service - Agent Guidelines

이 문서는 AI 에이전트가 이 프로젝트에서 코드를 작성할 때 따라야 하는 코드 스타일 및 컨벤션을 정의합니다.

---

## 📦 프로젝트 개요

| 항목 | 값 |
|------|-----|
| **프로젝트명** | jagalchi-server |
| **언어** | Java 25 |
| **프레임워크** | Spring Boot 4.0.0 |
| **빌드 도구** | Gradle |
| **패키지 구조** | Clean Architecture (Layered) |

---

## 🏗️ 아키텍처 패턴

### 레이어 구조

```
src/main/java/gajeman/jagalchi/jagalchiserver/
├── application/          # 유스케이스 및 서비스 레이어
│   ├── {도메인}/
│   │   ├── result/       # 서비스 결과 DTO (record)
│   │   ├── service/      # 유스케이스 구현체 (Command)
│   │   └── usecase/      # 유스케이스 인터페이스
├── domain/               # 도메인 엔티티 및 비즈니스 로직
│   └── {도메인}/
├── global/               # 전역 설정
│   └── config/
├── infrastructure/       # 외부 시스템 연동
│   ├── cookie/
│   ├── jwt/
│   ├── mail/
│   ├── oauth2/
│   └── persistence/      # Repository
└── presentation/         # 컨트롤러 및 DTO
    └── {도메인}/
        └── dto/
            ├── request/
            └── response/
```

### 네이밍 패턴

| 계층 | 네이밍 규칙 | 예시 |
|-----|-----------|------|
| UseCase 인터페이스 | `{동사}{명사}UseCase` | `LoginUseCase`, `SignUpUseCase` |
| UseCase 구현체 | `{동사}{명사}Command` | `LoginCommand`, `SignUpCommand` |
| Controller | `{도메인}Controller` | `AuthController`, `VerificationController` |
| Request DTO | `{동작}Request` | `LoginRequest`, `SignUpRequest` |
| Response DTO | `{동작}Response` | `LoginResponse`, `SignUpResponse` |
| Result DTO | `{동작}Result` | `LoginResult` |
| Entity | 복수형 명사 | `Users`, `RefreshToken` |
| Repository | `{엔티티}Repository` | `UsersRepository`, `VerificationRepository` |
| Util/Helper | `{기능}Util` | `CookieUtil`, `MailUtil` |

---

## 📝 코드 스타일

### 1. 클래스 어노테이션 순서

```java
// Entity
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_{테이블명}")
public class EntityName { }

// Service
@Service
@RequiredArgsConstructor
public class ServiceName implements UseCaseInterface { }

// Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/{base-path}")
public class ControllerName { }

// Configuration
@EnableWebSecurity  // 또는 다른 Enable 어노테이션
@Configuration
@RequiredArgsConstructor
public class ConfigName { }

// Redis Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RedisHash(value = "{hash-name}", timeToLive = 60 * 10)
public class RedisEntityName { }
```

### 2. 생성자 패턴

**Entity/Domain 객체:**
- `@Builder`는 private 생성자에 적용
- 정적 팩토리 메서드 `from()` 사용

```java
@Builder
private Users(String email, String name, String password) {
    this.email = email;
    this.name = name;
    this.password = password;
    this.role = UserRole.STUDENT;
}

public static Users from(String email, String password, String name) {
    return Users.builder()
            .email(email)
            .password(password)
            .name(name)
            .build();
}
```

**DTO (Response/Result):**
- Java Record 사용
- 정적 팩토리 메서드 `from()` 사용

```java
public record LoginResponse(
        String accessToken
) {
    public static LoginResponse from(String accessToken) {
        return new LoginResponse(accessToken);
    }
}
```

**Request DTO:**
- 일반 클래스 사용 (Validation 때문)
- `@Getter`, `@NoArgsConstructor`, `@AllArgsConstructor` 사용

```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 비어있으면 안됩니다.")
    private String email;

    @NotBlank(message = "비밀번호는 비어있으면 안됩니다.")
    private String password;
}
```

### 3. Enum 스타일

```java
public enum UserRole {
    STUDENT,
    TEACHER,
    ADMIN
}
```

---

## 📚 JavaDoc 컨벤션

### UseCase 인터페이스 (권장)

```java
public interface LoginUseCase {
    /**
     * 로그인 메서드
     * @param request 이메일, 비밀번호를 담은 DTO
     */
    LoginResult login(LoginRequest request);
}
```

### Controller 메서드

```java
/**
 * 회원가입 메서드
 * @param request 이메일, 비밀번호, 이름을 담은 DTO
 */
@PostMapping
public ResponseEntity<SignUpResponse> signUp(
        @RequestBody @Valid SignUpRequest request
) { }
```

### Service 메서드 (private 메서드 포함)

```java
/**
 * 인증코드 검증 메서드
 * 검증이 될 시 삭제
 * @param email 인증코드를 찾기 위한 이메일
 */
private void validate(String email) { }
```

### Infrastructure 메서드

```java
/**
 * 액세스토큰 생성 메서드
 *
 * @param user 유저의 정보를 활용하기 위해 받습니다.
 */
public String generateAccessToken(Users user) { }
```

---

## 🔧 의존성 주입

- **항상** `@RequiredArgsConstructor` + `private final` 필드 사용
- 생성자 주입 방식만 사용

```java
@Service
@RequiredArgsConstructor
public class LoginCommand implements LoginUseCase {

    private final TokenService tokenService;
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
}
```

---

## 🗄️ Repository 패턴

### JPA Repository

```java
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
}
```

### Redis Repository

```java
public interface VerificationRepository extends CrudRepository<Verification, String> {
    Optional<Verification> findByEmail(String email);
}
```

---

## 🏷️ 테이블 네이밍

- 접두사: `tbl_`
- 소문자 스네이크 케이스

```java
@Table(name = "tbl_users")
@Table(name = "tbl_refreshToken")
```

---

## ✅ Validation

- Jakarta Validation 사용
- 한글 메시지 사용
- Request DTO에 어노테이션 적용

```java
@Email(message = "올바른 이메일 형식이 아닙니다.")
@NotBlank(message = "이메일은 비어있으면 안됩니다.")
private String email;
```

---

## 🚨 예외 처리

- `IllegalArgumentException` 사용
- 한글 메시지 사용

```java
throw new IllegalArgumentException("유저를 찾을 수 없습니다.");
throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
throw new IllegalArgumentException("인증코드를 찾을 수 없습니다.");
```

---

## 🎯 Controller 응답 패턴

### 생성 (201 Created)

```java
return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
```

### 조회/성공 (200 OK)

```java
return ResponseEntity.ok(response);
```

### 반환값 없음 (void)

```java
@PatchMapping("/auth/password-reset")
public void changePassword(@RequestBody @Valid ChangePasswordRequest request) {
    changePasswordCommand.changePassword(request);
}
```

---

## 📁 Import 순서

1. 같은 프로젝트 패키지 (`gajeman.jagalchi.jagalchiserver.*`)
2. 외부 라이브러리 (`io.jsonwebtoken.*`, `lombok.*`)
3. Spring Framework (`org.springframework.*`)
4. Jakarta (`jakarta.*`)
5. Java 표준 라이브러리 (`java.*`)

---

## 🔐 보안 관련

### JWT 토큰

- Access Token: 1시간
- Refresh Token: 7일 (1주일)
- Bearer 토큰 형식 사용

### Cookie

- HttpOnly: true
- Secure: 환경에 따라 설정
- Path: "/"

---

## 📦 사용 기술 스택

| 분류 | 기술 |
|-----|-----|
| Web | Spring Boot Starter WebMVC |
| Security | Spring Security, OAuth2 Client |
| Database | MySQL, Spring Data JPA |
| Cache | Redis (Spring Data Redis) |
| JWT | jjwt (0.11.5) |
| Mail | Spring Boot Starter Mail |
| Validation | Spring Boot Starter Validation |
| Utility | Lombok |

---

## 💡 코드 작성 시 주의사항

1. **모든 Entity의 기본 생성자는 `AccessLevel.PROTECTED` 또는 `PRIVATE`**
2. **Response DTO는 Java Record 사용**
3. **Request DTO는 일반 클래스 사용 (Validation 호환성)**
4. **정적 팩토리 메서드명은 `from()` 사용**
5. **UseCase 인터페이스에 JavaDoc 작성 권장**
6. **한글로 예외 메시지 및 Validation 메시지 작성**
7. **상수는 `private static final`로 클래스 상단에 선언**

```java
private static final String REFRESH_TOKEN = "refreshToken";
private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60;
```
