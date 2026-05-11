package gajeman.jagalchi.jagalchiserver.infrastructure.persistence.verification;

import gajeman.jagalchi.jagalchiserver.domain.verification.Verification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerificationRepository extends CrudRepository<Verification, String> {
    Optional<Verification> findByEmail(String email);
}
