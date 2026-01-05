package gajeman.jagalchi.jagalchiserver.infrastructure.persistence.users;

import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByName(String name);
}
