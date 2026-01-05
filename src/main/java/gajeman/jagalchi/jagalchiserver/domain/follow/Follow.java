package gajeman.jagalchi.jagalchiserver.domain.follow;

import gajeman.jagalchi.jagalchiserver.domain.user.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "tbl_follow",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private Users follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private Users following;

    @Builder
    private Follow(Users follower, Users following) {
        this.follower = follower;
        this.following = following;
    }

    public static Follow of(Users follower, Users following) {
        return Follow.builder()
                .follower(follower)
                .following(following)
                .build();
    }

}
