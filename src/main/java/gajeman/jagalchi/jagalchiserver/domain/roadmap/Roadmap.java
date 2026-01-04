package gajeman.jagalchi.jagalchiserver.domain.roadmap;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "roadmaps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Roadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "directory_id")
    private Long directoryId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "fork_count")
    private Long forkCount = 0L;

    @Lob
    @Column(name = "tags")
    private String tags;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Roadmap(String title, String description, Long directoryId, Long ownerId, String thumbnailUrl,
            String tags, Boolean isPublic, Long forkCount) {
        this.title = title;
        this.description = description;
        this.directoryId = directoryId;
        this.ownerId = ownerId;
        this.thumbnailUrl = thumbnailUrl;
        this.tags = tags;
        this.isPublic = isPublic != null ? isPublic : true;
        this.viewCount = 0L;
        this.forkCount = forkCount != null ? forkCount : 0L;
    }

    public void update(String title, String description, Boolean isPublic, String thumbnailUrl) {
        if (title != null) {
            this.title = title;
        }
        if (description != null) {
            this.description = description;
        }
        if (isPublic != null) {
            this.isPublic = isPublic;
        }
        if (thumbnailUrl != null) {
            this.thumbnailUrl = thumbnailUrl;
        }
    }

    public void updateTags(String tags) {
        this.tags = tags;
    }

    public void moveToDirectory(Long directoryId) {
        this.directoryId = directoryId;
    }

    public boolean isOwnedBy(Long userId) {
        return this.ownerId.equals(userId);
    }

    public boolean isAccessibleBy(Long userId) {
        return this.isPublic || this.isOwnedBy(userId);
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}
