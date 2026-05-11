package gajeman.jagalchi.jagalchiserver.domain.user.exception;

import gajeman.jagalchi.jagalchiserver.domain.user.exception.error.UserErrorProperty;
import gajeman.jagalchi.jagalchiserver.global.error.GlobalException;

public class WrongLoginException extends GlobalException {
    public WrongLoginException() {
        super(UserErrorProperty.USER_NOT_FOUND);
    }
}
