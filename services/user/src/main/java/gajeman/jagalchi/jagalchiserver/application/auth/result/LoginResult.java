package gajeman.jagalchi.jagalchiserver.application.auth.result;

public record LoginResult(
        String accessToken,
        String refreshToken
) {
    public static LoginResult from(String accessToken, String refreshToken) {
        return new LoginResult(accessToken, refreshToken);
    }
}
