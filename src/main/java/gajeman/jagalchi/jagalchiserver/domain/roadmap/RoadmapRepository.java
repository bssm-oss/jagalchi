package gajeman.jagalchi.jagalchiserver.domain.roadmap;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

    List<Roadmap> findByOwnerId(Long ownerId);

    List<Roadmap> findByOwnerIdAndDirectoryId(Long ownerId, Long directoryId);

    List<Roadmap> findByOwnerIdAndDirectoryIdIsNull(Long ownerId);
}
