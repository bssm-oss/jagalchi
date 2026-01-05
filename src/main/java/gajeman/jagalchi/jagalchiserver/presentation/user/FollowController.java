package gajeman.jagalchi.jagalchiserver.presentation.user;

import gajeman.jagalchi.jagalchiserver.application.follow.service.GetFollowerListQuery;
import gajeman.jagalchi.jagalchiserver.application.follow.service.GetFollowingListQuery;
import gajeman.jagalchi.jagalchiserver.application.follow.service.ToggleFollowCommand;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.request.FollowToggleRequest;
import gajeman.jagalchi.jagalchiserver.presentation.user.dto.response.FollowListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class FollowController {

    private final ToggleFollowCommand toggleFollowCommand;
    private final GetFollowerListQuery getFollowerListQuery;
    private final GetFollowingListQuery getFollowingListQuery;

    /**
     * 팔로우 거는 메서드
     *
     * @param name 유저 이름
     * @param request boolean
     */
    @PatchMapping("/{name}/follow")
    public void toggleFollow(
            @PathVariable String name,
            @RequestBody FollowToggleRequest request,
            @AuthenticationPrincipal Users user
    ) {
        toggleFollowCommand.toggleFollowing(
                user.getId(),
                name,
                request.getToggle()
        );
    }

    /**
     * 비밀번호 재설정용 이메일 인증 코드를 발송합니다.
     *
     * @param name 이메일 정보를 담은 요청 DTO
     */
    @GetMapping("/{name}/followers")
    public FollowListResponse getFollowers(
            @PathVariable String name
    ){
        return getFollowerListQuery.getFollowerList(name);
    }

    @GetMapping("/{name}/followings")
    public FollowListResponse getFollowings(
            @PathVariable String name
    ){
        return getFollowingListQuery.getFollowingList(name);
    }

}
