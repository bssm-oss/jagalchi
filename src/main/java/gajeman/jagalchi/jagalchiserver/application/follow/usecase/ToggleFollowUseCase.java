package gajeman.jagalchi.jagalchiserver.application.follow.usecase;

public interface ToggleFollowUseCase {
    /**
     * 팔로우 메서드
     * @param userId 팔로우 하는 유저 아이디
     * @param name 팔로우 대상 닉네임
     * @param toggle 팔로우/취소
     */
    void toggleFollowing(Long userId, String name, boolean toggle);
}
