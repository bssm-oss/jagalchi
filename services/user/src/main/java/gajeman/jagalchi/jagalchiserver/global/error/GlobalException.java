package gajeman.jagalchi.jagalchiserver.global.error;

import lombok.Getter;

@Getter
public abstract class GlobalException extends RuntimeException {

    private final ErrorProperty errorProperty;

    public GlobalException(ErrorProperty errorProperty, Object... args) {
        super(String.format(errorProperty.getMessage(), args));
        this.errorProperty = errorProperty;
    }

    public GlobalException(ErrorProperty errorProperty) {
        super(errorProperty.getMessage());
        this.errorProperty = errorProperty;
    }
}
