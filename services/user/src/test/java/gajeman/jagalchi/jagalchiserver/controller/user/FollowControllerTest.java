package gajeman.jagalchi.jagalchiserver.controller.user;

import gajeman.jagalchi.jagalchiserver.application.follow.service.GetFollowerListQuery;
import gajeman.jagalchi.jagalchiserver.application.follow.service.GetFollowingListQuery;
import gajeman.jagalchi.jagalchiserver.application.follow.service.ToggleFollowCommand;
import gajeman.jagalchi.jagalchiserver.presentation.user.FollowController;
import gajeman.jagalchi.jagalchiserver.presentation.user.request.FollowToggleRequest;
import gajeman.jagalchi.jagalchiserver.presentation.user.response.FollowListResponse;
import gajeman.jagalchi.jagalchiserver.presentation.user.response.FollowUserResponse;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = FollowController.class,
        excludeAutoConfiguration = {
                OAuth2ClientAutoConfiguration.class,
                OAuth2ClientWebSecurityAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ToggleFollowCommand toggleFollowCommand;

    @MockitoBean
    private GetFollowerListQuery getFollowerListQuery;

    @MockitoBean
    private GetFollowingListQuery getFollowingListQuery;

    @Test
    @WithMockUser(username = "testUser")
    void 팔로우를_토글한다() throws Exception {
        // given
        String targetName = "targetUser";
        FollowToggleRequest request = new FollowToggleRequest(true);

        doNothing().when(toggleFollowCommand)
                .toggleFollowing(anyLong(), eq(targetName), eq(true));

        // when & then
        mockMvc.perform(patch("/users/{name}/follow", targetName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testUser")
    void 언팔로우를_한다() throws Exception {
        // given
        String targetName = "targetUser";
        FollowToggleRequest request = new FollowToggleRequest(false);

        doNothing().when(toggleFollowCommand)
                .toggleFollowing(anyLong(), eq(targetName), eq(false));

        // when & then
        mockMvc.perform(patch("/users/{name}/follow", targetName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void 팔로워_목록을_조회한다() throws Exception {
        // given
        String userName = "testUser";
        FollowUserResponse follower1 = new FollowUserResponse(
                1L, "follower1", "profile1.jpg", true
        );
        FollowUserResponse follower2 = new FollowUserResponse(
                2L, "follower2", null, false
        );
        List<FollowUserResponse> followers = List.of(follower1, follower2);
        FollowListResponse mockResponse = FollowListResponse.of(1L, "FOLLOWER", followers);

        when(getFollowerListQuery.getFollowerList(eq(userName))).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/users/{name}/followers", userName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.type").value("FOLLOWER"))
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.users[0].id").value(1L))
                .andExpect(jsonPath("$.users[0].name").value("follower1"))
                .andExpect(jsonPath("$.users[0].profileImage").value("profile1.jpg"))
                .andExpect(jsonPath("$.users[0].isFollowing").value(true))
                .andExpect(jsonPath("$.users[1].id").value(2L))
                .andExpect(jsonPath("$.users[1].name").value("follower2"))
                .andExpect(jsonPath("$.users[1].profileImage").isEmpty())
                .andExpect(jsonPath("$.users[1].isFollowing").value(false));
    }

    @Test
    void 팔로잉_목록을_조회한다() throws Exception {
        // given
        String userName = "testUser";
        FollowUserResponse following1 = new FollowUserResponse(
                3L, "following1", "profile3.jpg", true
        );
        FollowUserResponse following2 = new FollowUserResponse(
                4L, "following2", "profile4.jpg", false
        );
        List<FollowUserResponse> followings = List.of(following1, following2);
        FollowListResponse mockResponse = FollowListResponse.of(1L, "FOLLOWING", followings);

        when(getFollowingListQuery.getFollowingList(eq(userName))).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/users/{name}/followings", userName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.type").value("FOLLOWING"))
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.users[0].id").value(3L))
                .andExpect(jsonPath("$.users[0].name").value("following1"))
                .andExpect(jsonPath("$.users[0].profileImage").value("profile3.jpg"))
                .andExpect(jsonPath("$.users[0].isFollowing").value(true))
                .andExpect(jsonPath("$.users[1].id").value(4L))
                .andExpect(jsonPath("$.users[1].name").value("following2"))
                .andExpect(jsonPath("$.users[1].profileImage").value("profile4.jpg"))
                .andExpect(jsonPath("$.users[1].isFollowing").value(false));
    }

    @Test
    void 팔로워가_없는_경우_빈_목록을_반환한다() throws Exception {
        // given
        String userName = "testUser";
        FollowListResponse mockResponse = FollowListResponse.of(1L, "FOLLOWER", List.of());

        when(getFollowerListQuery.getFollowerList(eq(userName))).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/users/{name}/followers", userName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.type").value("FOLLOWER"))
                .andExpect(jsonPath("$.totalCount").value(0))
                .andExpect(jsonPath("$.users").isEmpty());
    }

    @Test
    void 팔로잉이_없는_경우_빈_목록을_반환한다() throws Exception {
        // given
        String userName = "testUser";
        FollowListResponse mockResponse = FollowListResponse.of(1L, "FOLLOWING", List.of());

        when(getFollowingListQuery.getFollowingList(eq(userName))).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/users/{name}/followings", userName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.type").value("FOLLOWING"))
                .andExpect(jsonPath("$.totalCount").value(0))
                .andExpect(jsonPath("$.users").isEmpty());
    }
}