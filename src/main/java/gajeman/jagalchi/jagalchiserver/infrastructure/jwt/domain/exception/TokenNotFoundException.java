package gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.exception;

import gajeman.jagalchi.jagalchiserver.global.error.GlobalException;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.exception.error.TokenErrorProperty;

public class TokenNotFoundException extends GlobalException {
    public TokenNotFoundException() {
        super(TokenErrorProperty.TOKEN_NOT_FOUND);
    }
}
