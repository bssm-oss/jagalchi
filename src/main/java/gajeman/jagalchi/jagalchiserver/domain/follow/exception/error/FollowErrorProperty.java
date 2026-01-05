package gajeman.jagalchi.jagalchiserver.domain.follow.exception.error;

import gajeman.jagalchi.jagalchiserver.global.error.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FollowErrorProperty implements ErrorProperty {
    CANT_SELF_FOLLOW_EXCEPTION(HttpStatus.UNPROCESSABLE_CONTENT, "자기 자신을 팔로우 할 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
