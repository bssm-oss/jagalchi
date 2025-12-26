package gajeman.jagalchi.jagalchiserver.domain.progress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoadmapNodeProgressRepository extends JpaRepository<RoadmapNodeProgress, Long> {

    List<RoadmapNodeProgress> findByRoadmapIdAndUserId(Long roadmapId, Long userId);

    Optional<RoadmapNodeProgress> findByRoadmapIdAndNodeIdAndUserId(Long roadmapId, Long nodeId, Long userId);

    @Query("SELECT COUNT(p) FROM RoadmapNodeProgress p WHERE p.roadmapId = :roadmapId AND p.userId = :userId AND p.isCompleted = true")
    long countCompletedByRoadmapIdAndUserId(@Param("roadmapId") Long roadmapId, @Param("userId") Long userId);

    void deleteByRoadmapId(Long roadmapId);

    void deleteByNodeId(Long nodeId);
}
