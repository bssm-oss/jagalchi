package gajeman.jagalchi.jagalchiserver.domain.user.exception;

import gajeman.jagalchi.jagalchiserver.domain.user.exception.error.UserErrorProperty;
import gajeman.jagalchi.jagalchiserver.global.error.GlobalException;

public class UserNotFoundException extends GlobalException {
    public UserNotFoundException() {
        super(UserErrorProperty.USER_NOT_FOUND);
    }
}

