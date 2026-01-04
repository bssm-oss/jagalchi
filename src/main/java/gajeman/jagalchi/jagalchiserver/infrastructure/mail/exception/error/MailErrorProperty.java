package gajeman.jagalchi.jagalchiserver.infrastructure.mail.exception.error;

import gajeman.jagalchi.jagalchiserver.global.error.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MailErrorProperty implements ErrorProperty {
    CANT_SEND_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "서버 측 오류로 메일 전송에 실패했습니다");

    private final HttpStatus status;
    private final String message;
}
