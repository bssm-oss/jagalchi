package gajeman.jagalchi.jagalchiserver.application.user;

import gajeman.jagalchi.jagalchiserver.application.user.service.UpdateProfileCommand;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.domain.user.exception.ExternalLinksLimitExceededException;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import gajeman.jagalchi.jagalchiserver.presentation.user.request.UpdateProfileDto;
import gajeman.jagalchi.jagalchiserver.presentation.user.request.UpdateProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class UpdateProfileCommandTest {

    @InjectMocks
    private UpdateProfileCommand updateProfileCommand;

    @Mock
    private UsersRepository userRepository;

    @Test
    void 프로필을_성공적으로_수정한다() {
        //given
        HashMap<String, String> map = new HashMap<>();

        map.put("깃허브", "건우 깃허브");

        UpdateProfileDto updateProfileDto = new UpdateProfileDto(
                "imageurl.com",
                "난 이건우다, 최강이지.",
                map
        );

        Users me = Users.from("me@test.com", "pw", "me");

        UpdateProfileRequest request = new UpdateProfileRequest(updateProfileDto);

        //when
        updateProfileCommand.updateProfile(me, request);

        //then
        then(userRepository).should().save(any(Users.class));
    }

    @Test
    void 링크가_5개를_넘을_경우_예외가_발생한다(){
        HashMap<String, String> map = new HashMap<>();

        map.put("깃허브1", "건우 깃허브");
        map.put("깃허브2", "건우 깃허브");
        map.put("깃허브3", "건우 깃허브");
        map.put("깃허브4", "건우 깃허브");
        map.put("깃허브5", "건우 깃허브");
        map.put("깃허브6", "건우 깃허브");

        UpdateProfileDto updateProfileDto = new UpdateProfileDto(
                "imageurl.com",
                "난 이건우다, 최강이지.",
                map
        );

        Users me = Users.from("me@test.com", "pw", "me");

        UpdateProfileRequest request = new UpdateProfileRequest(updateProfileDto);

        assertThrows(ExternalLinksLimitExceededException.class,
                () -> updateProfileCommand.updateProfile(me, request));
    }
}
