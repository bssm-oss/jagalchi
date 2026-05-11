package gajeman.jagalchi.jagalchiserver.infrastructure.jwt.service;

import gajeman.jagalchi.jagalchiserver.application.auth.result.LoginResult;
import gajeman.jagalchi.jagalchiserver.domain.user.UserRole;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import gajeman.jagalchi.jagalchiserver.domain.user.exception.UserNotFoundException;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.RefreshToken;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.TokenType;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.exception.InvalidTokenTypeException;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.exception.TokenNotEqualsException;
import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.exception.TokenNotFoundException;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.refreshToken.RefreshTokenRepository;
import gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsersRepository usersRepository;

    @Value("${jwt.secret}")
    private String secret;

    /**
     * 액세스토큰 생성 메서드
     *
     * @param user 유저의 정보를 활용하기 위해 받습니다.
     */
    public String generateAccessToken(Users user) {
        Long accessTokenExpiration = 60 * 60 * 1000L;

        return generateToken(user.getId(), user.getRole(), TokenType.ACCESS_TOKEN, accessTokenExpiration);
    }

    /**
     * 리프레쉬토큰 생성 메서드
     *
     * @param user 유저의 정보를 활용하기 위해 받습니다.
     */
    public String generateRefreshToken(Users user) {
        Long refreshTokenExpiration = 7 * 24 * 60 * 60 * 1000L; //1시간

        if (refreshTokenRepository.existsById(user.getId())) {
            refreshTokenRepository.deleteById(user.getId());
        }

        String token = generateToken(user.getId(), user.getRole(), TokenType.REFRESH_TOKEN, refreshTokenExpiration);

        refreshTokenRepository.save(RefreshToken.from(user.getId(), token));

        return token;
    }
    /**
     * 액세스토큰 생성 메서드
     *
     * @param id 유저의 id
     * @param role 유저의 권한
     * @param type 토큰타입
     * @param time 만료시간
     */

    public String generateToken(Long id, UserRole role, TokenType type, Long time) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("role", role.toString());
        claims.put("type", type.name());
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + time))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 액세스 토큰 재발급 메서드
     *
     * @param refreshToken 리프레쉬토큰
     */
    public LoginResult refreshAccessToken(String refreshToken) {
        Claims claims = parseToken(refreshToken);

        String type = claims.get("type", String.class);
        if (!TokenType.REFRESH_TOKEN.name().equals(type)) {
            throw new InvalidTokenTypeException();
        }

        Long id = claims.get("id", Long.class);

        RefreshToken token = refreshTokenRepository.findById(id)
                .orElseThrow(TokenNotFoundException::new);

        if (!token.getRefreshToken().equals(refreshToken)) {
            throw new TokenNotEqualsException();
        }

        Users user = getUserById(id);

        return LoginResult.from(generateAccessToken(user), generateRefreshToken(user));
    }

    /**
     * 토큰 해석 메서드
     *
     * @param token 토큰
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰 타입 확인 메서드
     *
     * @param token 토큰
     */
    public String getType(String token) {
        return parseToken(token).get("type", String.class);
    }

    /**
     * 유저 찾는 메서드
     *
     * @param id 유저 id
     */
    public Users getUserById(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

}
