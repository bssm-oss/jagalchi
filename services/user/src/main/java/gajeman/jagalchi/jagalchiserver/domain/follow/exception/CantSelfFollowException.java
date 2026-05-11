package gajeman.jagalchi.jagalchiserver.domain.follow.exception;

import gajeman.jagalchi.jagalchiserver.domain.follow.exception.error.FollowErrorProperty;
import gajeman.jagalchi.jagalchiserver.global.error.GlobalException;

public class CantSelfFollowException extends GlobalException {
    public CantSelfFollowException() {
        super(FollowErrorProperty.CANT_SELF_FOLLOW_EXCEPTION);
    }
}

