package gajeman.jagalchi.jagalchiserver.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final Error error;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Error {
        private final String code;
        private final String message;
        private final Map<String, Object> details;
        private final LocalDateTime timestamp;
    }

    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .error(Error.builder()
                        .code(code)
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build())
                .build();
    }

    public static ErrorResponse of(String code, String message, Map<String, Object> details) {
        return ErrorResponse.builder()
                .error(Error.builder()
                        .code(code)
                        .message(message)
                        .details(details)
                        .timestamp(LocalDateTime.now())
                        .build())
                .build();
    }
}
