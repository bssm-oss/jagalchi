package gajeman.jagalchi.jagalchiserver.domain.directory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DirectoryRepository extends JpaRepository<Directory, Long> {

    List<Directory> findByOwnerId(Long ownerId);

    List<Directory> findByOwnerIdAndParentId(Long ownerId, Long parentId);

    List<Directory> findByOwnerIdAndParentIdIsNull(Long ownerId);
}
