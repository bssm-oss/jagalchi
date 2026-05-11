package gajeman.jagalchi.jagalchiserver.domain.user.exception.error;

import gajeman.jagalchi.jagalchiserver.global.error.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorProperty implements ErrorProperty {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 유저를 찾지 못했습니다"),
    WRONG_LOGIN(HttpStatus.UNAUTHORIZED, "사용자가 없거나 비밀번호가 틀렸습니다."),
    EXTERNAL_LINKS_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "외부 연결 링크는 최대 5개입니다.");


    private final HttpStatus status;
    private final String message;
}
