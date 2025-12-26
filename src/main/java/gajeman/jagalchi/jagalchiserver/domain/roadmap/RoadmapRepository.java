package gajeman.jagalchi.jagalchiserver.domain.roadmap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

    List<Roadmap> findByOwnerId(Long ownerId);

    List<Roadmap> findByOwnerIdAndDirectoryId(Long ownerId, Long directoryId);

    List<Roadmap> findByOwnerIdAndDirectoryIdIsNull(Long ownerId);

    @Query("SELECT r FROM Roadmap r WHERE r.isPublic = true")
    Page<Roadmap> findAllPublic(Pageable pageable);

    @Query("SELECT r FROM Roadmap r WHERE r.isPublic = true OR r.ownerId = :userId")
    Page<Roadmap> findAllAccessibleBy(@Param("userId") Long userId, Pageable pageable);

    Page<Roadmap> findByIsPublicTrue(Pageable pageable);
}
