package gajeman.jagalchi.jagalchiserver.common.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtTokenService {

    private static final String USER_PREFIX = "user-";
    private static final String ROADMAP_PREFIX = "roadmap-";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtTokenService(@Value("${jagalchi.auth.jwt.secret}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jagalchi.auth.jwt.secret is required");
        }
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).build();
    }

    public AuthPrincipal verify(String token) throws JWTVerificationException {
        DecodedJWT jwt = verifier.verify(token);
        String subject = jwt.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new JWTVerificationException("sub is required");
        }

        Long userId = parseId(subject, USER_PREFIX, "sub");
        Long roadmapId = null;
        String roadmapClaim = jwt.getClaim("roadmapId").asString();
        if (roadmapClaim != null && !roadmapClaim.isBlank()) {
            roadmapId = parseId(roadmapClaim, ROADMAP_PREFIX, "roadmapId");
        }

        List<String> permissionsRaw = jwt.getClaim("permissions").asList(String.class);
        if (permissionsRaw == null || permissionsRaw.isEmpty()) {
            throw new JWTVerificationException("permissions is required");
        }
        Set<AuthPermission> permissions = permissionsRaw.stream()
                .filter(v -> v != null && !v.isBlank())
                .map(String::trim)
                .map(String::toUpperCase)
                .map(AuthPermission::valueOf)
                .collect(Collectors.toUnmodifiableSet());

        return new AuthPrincipal(userId, permissions, roadmapId);
    }

    public String issue(String subject, String roadmapId, Set<AuthPermission> permissions, Instant expiresAt) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("subject is required");
        }
        if (permissions == null || permissions.isEmpty()) {
            throw new IllegalArgumentException("permissions is required");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("expiresAt is required");
        }

        var builder = JWT.create()
                .withSubject(subject)
                .withArrayClaim("permissions", permissions.stream().map(Enum::name).toArray(String[]::new))
                .withExpiresAt(expiresAt);
        if (roadmapId != null && !roadmapId.isBlank()) {
            builder.withClaim("roadmapId", roadmapId);
        }
        return builder.sign(algorithm);
    }

    private Long parseId(String raw, String prefix, String fieldName) {
        String normalized = raw.trim();
        if (normalized.startsWith(prefix)) {
            normalized = normalized.substring(prefix.length());
        }
        try {
            return Long.parseLong(normalized);
        } catch (NumberFormatException e) {
            throw new JWTVerificationException(fieldName + " must be a number (raw=" + raw + ")");
        }
    }
}

