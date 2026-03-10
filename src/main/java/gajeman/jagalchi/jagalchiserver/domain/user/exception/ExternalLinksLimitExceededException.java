package gajeman.jagalchi.jagalchiserver.domain.user.exception;

import gajeman.jagalchi.jagalchiserver.domain.user.exception.error.UserErrorProperty;
import gajeman.jagalchi.jagalchiserver.global.error.GlobalException;

public class ExternalLinksLimitExceededException extends GlobalException {
    public ExternalLinksLimitExceededException() {
        super(UserErrorProperty.EXTERNAL_LINKS_LIMIT_EXCEEDED);
    }
}
