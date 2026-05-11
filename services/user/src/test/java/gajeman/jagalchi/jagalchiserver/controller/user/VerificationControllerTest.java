package gajeman.jagalchi.jagalchiserver.controller.user;

import gajeman.jagalchi.jagalchiserver.application.verification.service.SendVerificationCodeCommand;
import gajeman.jagalchi.jagalchiserver.application.verification.service.ValidVerificationCodeCommand;
import gajeman.jagalchi.jagalchiserver.domain.verification.VerificationType;
import gajeman.jagalchi.jagalchiserver.presentation.auth.VerificationController;
import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.request.SendVerificationCodeRequest;
import gajeman.jagalchi.jagalchiserver.presentation.auth.dto.request.VerifyRequest;
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
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = VerificationController.class,
        excludeAutoConfiguration = {
                OAuth2ClientAutoConfiguration.class,
                OAuth2ClientWebSecurityAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SendVerificationCodeCommand sendVerificationCodeCommand;

    @MockitoBean
    private ValidVerificationCodeCommand validVerificationCodeCommand;

    @Test
    void 회원가입용_인증코드를_발송한다() throws Exception {
        // given
        SendVerificationCodeRequest request = new SendVerificationCodeRequest("test@test.com");

        doNothing().when(sendVerificationCodeCommand)
                .sendVerificationCode(any(SendVerificationCodeRequest.class), eq(VerificationType.SIGN_UP));

        // when & then
        mockMvc.perform(post("/users/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(sendVerificationCodeCommand).sendVerificationCode(any(SendVerificationCodeRequest.class), eq(VerificationType.SIGN_UP));
    }

    @Test
    void 비밀번호_재설정용_인증코드를_발송한다() throws Exception {
        // given
        SendVerificationCodeRequest request = new SendVerificationCodeRequest("test@test.com");

        doNothing().when(sendVerificationCodeCommand)
                .sendVerificationCode(any(SendVerificationCodeRequest.class), eq(VerificationType.UPDATE_PASSWORD));

        // when & then
        mockMvc.perform(post("/users/auth/password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(sendVerificationCodeCommand).sendVerificationCode(any(SendVerificationCodeRequest.class), eq(VerificationType.UPDATE_PASSWORD));
    }

    @Test
    void 회원가입용_인증코드를_검증한다() throws Exception {
        // given
        VerifyRequest request = new VerifyRequest("test@test.com", "123456");

        doNothing().when(validVerificationCodeCommand)
                .validVerificationCode(any(VerifyRequest.class));

        // when & then
        mockMvc.perform(patch("/users/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(validVerificationCodeCommand).validVerificationCode(any(VerifyRequest.class));
    }

    @Test
    void 비밀번호_재설정용_인증코드를_검증한다() throws Exception {
        // given
        VerifyRequest request = new VerifyRequest("test@test.com", "123456");

        doNothing().when(validVerificationCodeCommand)
                .validVerificationCode(any(VerifyRequest.class));

        // when & then
        mockMvc.perform(patch("/users/auth/password-reset/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(validVerificationCodeCommand).validVerificationCode(any(VerifyRequest.class));
    }

    @Test
    void 잘못된_이메일_형식으로_인증코드_발송시_400_에러() throws Exception {
        // given
        SendVerificationCodeRequest request = new SendVerificationCodeRequest("invalid-email");

        // when & then
        mockMvc.perform(post("/users/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 빈_이메일로_인증코드_발송시_400_에러() throws Exception {
        // given
        SendVerificationCodeRequest request = new SendVerificationCodeRequest("");

        // when & then
        mockMvc.perform(post("/users/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}