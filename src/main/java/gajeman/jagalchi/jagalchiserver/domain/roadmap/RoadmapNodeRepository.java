package gajeman.jagalchi.jagalchiserver.domain.roadmap;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapNodeRepository extends JpaRepository<RoadmapNode, Long> {

    long countByRoadmapId(Long roadmapId);
}
