    package gajeman.jagalchi.jagalchiserver.controller.user;

    import gajeman.jagalchi.jagalchiserver.application.auth.result.LoginResult;
    import gajeman.jagalchi.jagalchiserver.application.auth.service.*;
    import gajeman.jagalchi.jagalchiserver.application.verification.service.SendVerificationCodeCommand;
    import gajeman.jagalchi.jagalchiserver.infrastructure.cookie.CookieUtil;
    import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.verification.VerificationRepository;
    import gajeman.jagalchi.jagalchiserver.presentation.auth.AuthController;
    import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.request.ChangePasswordRequest;
    import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.request.LoginRequest;
    import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.request.SignUpRequest;
    import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.response.SignUpResponse;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
    import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
    import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
    import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
    import org.springframework.http.MediaType;
    import org.springframework.test.context.ActiveProfiles;
    import org.springframework.test.context.bean.override.mockito.MockitoBean;
    import org.springframework.test.web.servlet.MockMvc;
    import jakarta.servlet.http.Cookie;
    import tools.jackson.databind.ObjectMapper;

    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.ArgumentMatchers.anyString;
    import static org.mockito.Mockito.doNothing;
    import static org.mockito.Mockito.when;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    @WebMvcTest(
            controllers = AuthController.class,
            excludeAutoConfiguration = {
                    OAuth2ClientAutoConfiguration.class,
                    OAuth2ClientWebSecurityAutoConfiguration.class
            }
    )
    @AutoConfigureMockMvc(addFilters = false)
    @ActiveProfiles("test")
    class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private ChangePasswordCommand changePasswordCommand;

        @MockitoBean
        private LoginCommand loginCommand;

        @MockitoBean
        private RefreshAccessTokenCommand refreshAccessTokenCommand;

        @MockitoBean
        private SignUpCommand signUpCommand;

        @MockitoBean
        private SendVerificationCodeCommand sendVerificationCodeCommand;

        @MockitoBean
        private VerificationRepository verificationRepository;

        @MockitoBean
        private DeleteAccountCommand deleteAccountCommand;

        @MockitoBean
        private CookieUtil cookieUtil;

        @Test
        void 회원가입에_성공한다() throws Exception {
            // given
            SignUpRequest request = new SignUpRequest("test@test.com", "이건우", "1234");
            SignUpResponse mockResponse = new SignUpResponse(1L, "test@test.com", "이건우");

            when(signUpCommand.signUp(any())).thenReturn(mockResponse);

            // when & then
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value("test@test.com"))
                    .andExpect(jsonPath("$.name").value("이건우"));
        }

        @Test
        void 비밀번호를_변경한다() throws Exception {
            // given
            ChangePasswordRequest request = new ChangePasswordRequest("test@test.com", "newPassword");

            // when & then
            mockMvc.perform(patch("/users/auth/password-reset")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Test
        void 로그인을_성공한다() throws Exception {
            // given
            LoginRequest request = new LoginRequest("test@test.com", "password");
            LoginResult mockResult = LoginResult.from("mock-access-token", "mock-refresh-token");

            when(loginCommand.login(any(LoginRequest.class))).thenReturn(mockResult);
            doNothing().when(cookieUtil).addRefreshToken(any(), anyString(), any(Boolean.class));

            // when & then
            mockMvc.perform(post("/users/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("mock-access-token"));
        }

        @Test
        void 리프레시_토큰으로_재발급한다() throws Exception {
            // given
            LoginResult mockResult = LoginResult.from("new-access-token", "new-refresh-token");

            when(refreshAccessTokenCommand.refreshAccessToken(anyString())).thenReturn(mockResult);
            doNothing().when(cookieUtil).addRefreshToken(any(), anyString(), any(Boolean.class));

            // when & then
            mockMvc.perform(patch("/users/auth/refresh")
                            .cookie(new Cookie("refreshToken", "refresh-token"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("new-access-token"));
        }
    }