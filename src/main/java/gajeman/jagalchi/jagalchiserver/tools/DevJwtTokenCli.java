package gajeman.jagalchi.jagalchiserver.tools;

import gajeman.jagalchi.jagalchiserver.common.auth.AuthPermission;
import gajeman.jagalchi.jagalchiserver.common.auth.JwtTokenService;

import java.time.Instant;
import java.util.Set;

public class DevJwtTokenCli {

    private static final Instant DEFAULT_EXPIRES_AT = Instant.parse("2030-01-01T00:00:00Z");

    public static void main(String[] args) {
        String secret = System.getenv("JAGALCHI_AUTH_JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            System.err.println("JAGALCHI_AUTH_JWT_SECRET is required");
            System.exit(1);
            return;
        }

        JwtTokenService tokenService = new JwtTokenService(secret);

        String user1Edit = tokenService.issue("user-1", "roadmap-1", Set.of(AuthPermission.EDIT), DEFAULT_EXPIRES_AT);
        String user1Read = tokenService.issue("user-1", "roadmap-1", Set.of(AuthPermission.READ), DEFAULT_EXPIRES_AT);

        System.out.println("JAGALCHI_AUTH_TOKEN_USER_1_EDIT=" + user1Edit);
        System.out.println("JAGALCHI_AUTH_TOKEN_USER_1_READ=" + user1Read);
    }
}

