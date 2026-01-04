package gajeman.jagalchi.jagalchiserver.infrastructure.mail.exception;

import gajeman.jagalchi.jagalchiserver.global.error.GlobalException;
import gajeman.jagalchi.jagalchiserver.infrastructure.mail.exception.error.MailErrorProperty;

public class FailedSendMailException extends GlobalException {
    public FailedSendMailException() {
        super(MailErrorProperty.CANT_SEND_EMAIL);
    }
}
