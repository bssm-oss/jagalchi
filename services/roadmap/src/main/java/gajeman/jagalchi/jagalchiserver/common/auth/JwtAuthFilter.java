package gajeman.jagalchi.jagalchiserver.common.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gajeman.jagalchi.jagalchiserver.common.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService tokenService;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(JwtTokenService tokenService, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) {
            return true;
        }

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        // Allow API docs & dev consoles without auth
        return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/h2-console")
                || path.equals("/error");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Authorization header is required");
            return;
        }
        if (!authHeader.startsWith("Bearer ")) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Authorization must be Bearer token");
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Bearer token is empty");
            return;
        }

        AuthPrincipal principal;
        try {
            principal = tokenService.verify(token);
        } catch (JWTVerificationException e) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Invalid or expired token");
            return;
        }

        if (requiresEditPermission(request) && !principal.canEdit()) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "EDIT permission is required");
            return;
        }
        if (requiresReadPermission(request) && !principal.canRead()) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "READ permission is required");
            return;
        }

        request.setAttribute(AuthPrincipal.ATTRIBUTE_NAME, principal);
        filterChain.doFilter(request, response);
    }

    private boolean requiresEditPermission(HttpServletRequest request) {
        String method = request.getMethod();
        return HttpMethod.POST.matches(method)
                || HttpMethod.PUT.matches(method)
                || HttpMethod.PATCH.matches(method)
                || HttpMethod.DELETE.matches(method);
    }

    private boolean requiresReadPermission(HttpServletRequest request) {
        String method = request.getMethod();
        return HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method);
    }

    private void writeError(HttpServletResponse response, int status, String code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ErrorResponse.of(code, message));
    }
}

