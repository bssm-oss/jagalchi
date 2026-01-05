package gajeman.jagalchi.jagalchiserver.application.auth.usecase;

import gajeman.jagalchi.jagalchiserver.domain.user.Users;

public interface DeleteAccountUseCase {
    /**
     * 유저 비활성화 메서드
     * @param users 유저 Id
     */
    void deleteAccount(Users users);
}
