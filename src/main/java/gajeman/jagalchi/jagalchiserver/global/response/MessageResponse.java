package gajeman.jagalchi.jagalchiserver.global.response;

import lombok.Getter;

@Getter
public class MessageResponse {

    private String message;

    private MessageResponse(String message) {
        this.message = message;
    }

    public static MessageResponse from(String message) {
        return new MessageResponse(message);
    }
}
