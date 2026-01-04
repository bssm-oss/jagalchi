package gajeman.jagalchi.jagalchiserver.domain.verification.exception;

import gajeman.jagalchi.jagalchiserver.domain.verification.exception.error.VerificationErrorProperty;
import gajeman.jagalchi.jagalchiserver.global.error.GlobalException;
import org.springframework.http.HttpStatus;

public class NotVerificationException extends GlobalException {
    public NotVerificationException() {
        super(VerificationErrorProperty.NOT_VERIFICATION);
    }
}
