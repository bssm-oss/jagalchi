package gajeman.jagalchi.jagalchiserver.infrastructure.persistence.refreshToken;

import gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findById(Long id);
}
