package gajeman.jagalchi.jagalchiserver.presentation.user.dto.response;

import gajeman.jagalchi.jagalchiserver.domain.user.Users;

public record SignUpResponse(
        Long id,
        String email,
        String name
) {
    public static SignUpResponse from(Users users) {
        return new SignUpResponse(
                users.getId(),
                users.getEmail(),
                users.getName()
        );
    }
}
