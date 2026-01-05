package gajeman.jagalchi.jagalchiserver.infrastructure.jwt.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tbl_refreshToken")
public class RefreshToken {

    @Id
    private Long id;

    private String refreshToken;

    RefreshToken(Long id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
    }

    public static RefreshToken from(Long id, String refreshToken) {
        return new RefreshToken(id, refreshToken);
    }

}
