package gajeman.jagalchi.jagalchiserver.domain.verification.exception;

import gajeman.jagalchi.jagalchiserver.domain.verification.exception.error.VerificationErrorProperty;
import gajeman.jagalchi.jagalchiserver.global.error.GlobalException;

public class VerificationNotFoundException extends GlobalException {
    public VerificationNotFoundException() {
        super(VerificationErrorProperty.VERIFICATION_NOT_FOUND);
    }
}

