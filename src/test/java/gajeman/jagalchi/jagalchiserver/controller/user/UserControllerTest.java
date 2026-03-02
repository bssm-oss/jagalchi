package gajeman.jagalchi.jagalchiserver.controller.user;

import gajeman.jagalchi.jagalchiserver.application.user.service.QueryUserByNameCommand;
import gajeman.jagalchi.jagalchiserver.application.user.service.UpdateProfileCommand;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.presentation.user.UserController;
import gajeman.jagalchi.jagalchiserver.presentation.user.request.UpdateProfileDto;
import gajeman.jagalchi.jagalchiserver.presentation.user.request.UpdateProfileRequest;
import gajeman.jagalchi.jagalchiserver.presentation.user.response.QueryUserResponse;
import gajeman.jagalchi.jagalchiserver.presentation.user.response.StreakResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = {
                OAuth2ClientAutoConfiguration.class,
                OAuth2ClientWebSecurityAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UpdateProfileCommand updateProfileCommand;

    @MockitoBean
    private QueryUserByNameCommand queryUserByNameCommand;

    @MockitoBean
    private Users mockUser;

    @Test
    @WithMockUser(username = "testUser")
    void 프로필을_업데이트한다() throws Exception {
        // given
        HashMap<String, String> links = new HashMap<>();
        links.put("github", "https://github.com/username");
        links.put("linkedin", "https://linkedin.com/in/username");

        UpdateProfileDto dto = new UpdateProfileDto(
                "https://example.com/profiles/user_avatar.png",
                "안녕하세요, 저는 건우입니다.",
                links
        );

        UpdateProfileRequest request = new UpdateProfileRequest(dto);

        doNothing().when(updateProfileCommand)
                .updateProfile(any(Users.class), any(UpdateProfileRequest.class));

        // when & then
        mockMvc.perform(patch("/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("프로필이 성공적으로 수정되었습니다."));

        verify(updateProfileCommand, times(1))
                .updateProfile(any(Users.class), any(UpdateProfileRequest.class));
    }

    @Test
    void 유저를_조회한다() throws Exception {
        // given
        String targetName = "testUser";

        Map<String, String> links = new HashMap<>();
        links.put("github", "https://github.com/username");
        links.put("linkedin", "https://linkedin.com/in/username");

        StreakResponseDto streak = StreakResponseDto.from(3, List.of());

        QueryUserResponse response = QueryUserResponse.from(
                mockUser,
                false,
                10L,
                5L,
                links,
                streak
        );

        when(queryUserByNameCommand.getUserByName(eq(targetName), any()))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/users")
                        .param("name", targetName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").value(mockUser.getName()))
                .andExpect(jsonPath("$.user.email").value(mockUser.getEmail()))
                .andExpect(jsonPath("$.user.isFollowed").value(false))
                .andExpect(jsonPath("$.user.stats.followersCount").value(10))
                .andExpect(jsonPath("$.user.stats.followingCount").value(5))
                .andExpect(jsonPath("$.streak.currentStreak").value(3));

        verify(queryUserByNameCommand, times(1))
                .getUserByName(eq(targetName), any());
    }

}
