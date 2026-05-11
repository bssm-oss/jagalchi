package gajeman.jagalchi.jagalchiserver.application.user.usecase;

import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.presentation.user.request.UpdateProfileRequest;

public interface UpdateProfileUseCase {
    /**
     * 프로필 업데이트 메서드
     * @param user 유저 식별자
     * @param request 프로필 이미지 url, 자기소개, 링크를 담은 dto
     */
    void updateProfile(Users user, UpdateProfileRequest request);
}
