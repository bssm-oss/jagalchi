package gajeman.jagalchi.jagalchiserver.domain.verification.exception.error;

import gajeman.jagalchi.jagalchiserver.global.error.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum VerificationErrorProperty implements ErrorProperty {
    VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 인증코드를 찾지 못했습니다"),
    NOT_VERIFICATION(HttpStatus.UNAUTHORIZED, "해당 코드가 인증되지 않았거나 올바른 코드가 아닙니다.");

    private final HttpStatus status;
    private final String message;
}
