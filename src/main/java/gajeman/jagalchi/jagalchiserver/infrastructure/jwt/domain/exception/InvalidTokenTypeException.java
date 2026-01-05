package gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.exception;

import gajeman.jagalchi.jagalchiserver.global.error.GlobalException;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.exception.error.TokenErrorProperty;

public class InvalidTokenTypeException extends GlobalException {
    public InvalidTokenTypeException() {
        super(TokenErrorProperty.INVALID_TOKEN_TYPE);
    }
}

