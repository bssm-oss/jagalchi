package gajeman.jagalchi.jagalchiserver.domain.verification;

import gajeman.jagalchi.jagalchiserver.domain.verification.exception.NotVerificationException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Random;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RedisHash(value = "verification", timeToLive = 60 * 10)
public class Verification {

    @Id
    private String email;

    private String code;

    private boolean verified;

    private VerificationType type;

    @Builder
    private Verification(String email, VerificationType type) {
        this.email = email;
        this.type = type;
        this.code = generateCode();
        this.verified = false;
    }

    public static Verification from(String email, VerificationType type){
        return Verification.builder()
                .email(email)
                .type(type)
                .build();
    }

    private String generateCode() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    public void verify(String requestCode) {
        validateCode(requestCode);
        this.verified = true;
    }

    private void validateCode(String requestCode) {
        if (!code.equals(requestCode)) {
            throw new NotVerificationException();
        }
    }

    public void validateVerified() {
        if (!verified) {
            throw new NotVerificationException();
        }
    }
}
