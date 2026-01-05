package gajeman.jagalchi.jagalchiserver.domain.roadmap;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapNodeRepository extends JpaRepository<RoadmapNode, Long> {

    List<RoadmapNode> findByRoadmapId(Long roadmapId);

    long countByRoadmapId(Long roadmapId);

    void deleteByRoadmapId(Long roadmapId);
}
