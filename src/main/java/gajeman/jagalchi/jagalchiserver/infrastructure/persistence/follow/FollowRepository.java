package gajeman.jagalchi.jagalchiserver.infrastructure.persistence.follow;

import gajeman.jagalchi.jagalchiserver.domain.follow.Follow;
import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(Users follower, Users following);

    Optional<Follow> findByFollowerAndFollowing(Users follower, Users following);

    List<Follow> findByFollower(Users follower);

    List<Follow> findByFollowing(Users following);
}

