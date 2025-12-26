package gajeman.jagalchi.jagalchiserver.domain.progress;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "roadmap_node_progress", uniqueConstraints = @UniqueConstraint(name = "uk_progress_roadmap_node_user", columnNames = {
        "roadmap_id", "node_id", "user_id" }))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RoadmapNodeProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "roadmap_id", nullable = false)
    private Long roadmapId;

    @Column(name = "node_id", nullable = false)
    private Long nodeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public RoadmapNodeProgress(Long roadmapId, Long nodeId, Long userId) {
        this.roadmapId = roadmapId;
        this.nodeId = nodeId;
        this.userId = userId;
        this.isCompleted = false;
    }

    public void markCompleted() {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
    }

    public void markIncomplete() {
        this.isCompleted = false;
        this.completedAt = null;
    }

    public void toggleComplete(boolean completed) {
        if (completed) {
            markCompleted();
        } else {
            markIncomplete();
        }
    }
}
