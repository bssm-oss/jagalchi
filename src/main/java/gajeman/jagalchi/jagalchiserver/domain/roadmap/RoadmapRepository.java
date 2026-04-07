package gajeman.jagalchi.jagalchiserver.domain.roadmap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long>, JpaSpecificationExecutor<Roadmap> {

    List<Roadmap> findByOwnerId(Long ownerId);

    List<Roadmap> findByOwnerIdAndDirectoryId(Long ownerId, Long directoryId);

    List<Roadmap> findByOwnerIdAndDirectoryIdIsNull(Long ownerId);

    @Query("SELECT r FROM Roadmap r WHERE r.isPublic = true")
    Page<Roadmap> findAllPublic(Pageable pageable);

    @Query("SELECT r FROM Roadmap r WHERE r.isPublic = true OR r.ownerId = :userId")
    Page<Roadmap> findAllAccessibleBy(@Param("userId") Long userId, Pageable pageable);

    Page<Roadmap> findByIsPublicTrue(Pageable pageable);

    List<Roadmap> findByOriginalRoadmapId(Long originalRoadmapId);

    void deleteByOwnerIdAndDirectoryId(Long ownerId, Long directoryId);

    /**
     * Find all direct forks of a roadmap (children in fork tree)
     */
    @Query("SELECT r FROM Roadmap r WHERE r.originalRoadmapId = :roadmapId ORDER BY r.createdAt DESC")
    List<Roadmap> findDirectForks(@Param("roadmapId") Long roadmapId);

    /**
     * Count total forks (including nested) for a roadmap
     */
    @Query("SELECT COUNT(r) FROM Roadmap r WHERE r.originalRoadmapId = :roadmapId OR r.originalRoadmapId IN (SELECT r2.id FROM Roadmap r2 WHERE r2.originalRoadmapId = :roadmapId)")
    long countTotalForks(@Param("roadmapId") Long roadmapId);

    /**
     * Find popular roadmaps ordered by fork count
     */
    @Query("SELECT r FROM Roadmap r WHERE r.isPublic = true ORDER BY r.forkCount DESC, r.viewCount DESC")
    Page<Roadmap> findPopularByForks(Pageable pageable);

    /**
     * Find popular roadmaps ordered by view count
     */
    @Query("SELECT r FROM Roadmap r WHERE r.isPublic = true ORDER BY r.viewCount DESC, r.forkCount DESC")
    Page<Roadmap> findPopularByViews(Pageable pageable);

    /**
     * Check if a user has forked a specific roadmap
     */
    @Query("SELECT COUNT(r) > 0 FROM Roadmap r WHERE r.originalRoadmapId = :roadmapId AND r.ownerId = :userId")
    boolean existsForkByUser(@Param("roadmapId") Long roadmapId, @Param("userId") Long userId);

    /**
     * Find the fork of a roadmap owned by a specific user
     */
    @Query("SELECT r FROM Roadmap r WHERE r.originalRoadmapId = :roadmapId AND r.ownerId = :userId")
    Roadmap findForkByUser(@Param("roadmapId") Long roadmapId, @Param("userId") Long userId);
}
