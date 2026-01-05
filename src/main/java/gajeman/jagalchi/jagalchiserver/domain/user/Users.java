package gajeman.jagalchi.jagalchiserver.domain.user;

import gajeman.jagalchi.jagalchiserver.domain.user.exception.ExternalLinksLimitExceededException;
import jakarta.persistence.*;
import lombok.AccessLevel;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String profileImageUrl;

    private String bio;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean isActive = false;

    @Column(columnDefinition = "json")
    private String externalLinks;

    @Builder
    private Users(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = UserRole.STUDENT;
    }

    public static Users from(String email, String password, String name) {
        return Users.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void changeActive(){
        this.isActive = !this.isActive;
    }

    public void updateProfile(String profileImage, String bio, Map<String, String> externalLinks) {
        this.profileImageUrl = profileImage;
        this.bio = bio;
        if (externalLinks.size() > 5) {
            throw new ExternalLinksLimitExceededException();
        }
        ObjectMapper mapper = new ObjectMapper();
        this.externalLinks = mapper.writeValueAsString(externalLinks);
    }

}
