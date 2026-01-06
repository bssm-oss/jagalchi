package gajeman.jagalchi.jagalchiserver.presentation.user;

import gajeman.jagalchi.jagalchiserver.application.user.service.UpdateProfileCommand;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.global.response.MessageResponse;
import gajeman.jagalchi.jagalchiserver.presentation.user.request.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UpdateProfileCommand updateProfileCommand;

    /**
     * 프로필 업데이트 메서드
     * @param user 유저 식별자
     * @param request 프로필 이미지 url, 자기소개, 링크를 담은 dto
     */
    @PatchMapping("/profile")
    public MessageResponse updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Users user
    ) {
        updateProfileCommand.updateProfile(user, request);

        return  MessageResponse.from("프로필이 성공적으로 수정되었습니다.");
    }

}
