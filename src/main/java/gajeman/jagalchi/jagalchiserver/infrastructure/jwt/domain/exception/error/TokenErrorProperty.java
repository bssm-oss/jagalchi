package gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.exception.error;

import gajeman.jagalchi.jagalchiserver.global.error.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TokenErrorProperty implements ErrorProperty {
    INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "올바른 토큰 타입이 아닙니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "리프레시 토큰을 찾지 못했습니다."),
    NOT_EQUALS_TOKEN(HttpStatus.CONFLICT, "토큰이 일치하지 않습니다.");

    private final HttpStatus status;
    private final String message;
}
