package gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.exception;

import gajeman.jagalchi.jagalchiserver.global.error.GlobalException;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.exception.error.TokenErrorProperty;

public class TokenNotEqualsException extends GlobalException {
    public TokenNotEqualsException( ) {
        super(TokenErrorProperty.NOT_EQUALS_TOKEN);
    }
}
