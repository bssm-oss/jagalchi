package gajeman.jagalchi.jagalchiserver.support;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import gajeman.jagalchi.jagalchiserver.common.auth.AuthPermission;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public final class TestJwtTokens {

    private static final String SECRET = "test-secret";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

    private TestJwtTokens() {
    }

    public static String bearerToken(long userId, AuthPermission... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("permissions is required");
        }

        String[] values = new String[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            values[i] = permissions[i].name();
        }

        String jwt = JWT.create()
                .withSubject("user-" + userId)
                .withArrayClaim("permissions", values)
                .withExpiresAt(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                .sign(ALGORITHM);
        return "Bearer " + jwt;
    }

    public static String bearerEditToken(long userId) {
        return bearerToken(userId, AuthPermission.EDIT);
    }

    public static String bearerReadToken(long userId) {
        return bearerToken(userId, AuthPermission.READ);
    }
}

