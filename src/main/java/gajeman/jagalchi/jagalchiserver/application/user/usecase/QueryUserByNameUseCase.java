package gajeman.jagalchi.jagalchiserver.application.user.usecase;

import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.presentation.user.response.QueryUserResponse;

public interface QueryUserByNameUseCase {
    /**
     * 이름으로 사용자 조회 메서드
     * @param name 조회할 사용자 이름
     * @param user 현재 로그인한 사용자 (팔로우 여부 확인용, null 가능)
     * @return 사용자 정보 및 팔로우 통계
     */
    QueryUserResponse getUserByName(String name, Users user);
}
