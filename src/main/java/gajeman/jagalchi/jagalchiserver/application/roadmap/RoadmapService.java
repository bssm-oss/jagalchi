package gajeman.jagalchi.jagalchiserver.application.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapListItemResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapListResponse;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;

    public RoadmapListResponse getList(Long requesterId, int page, int size, String sort,
                                       String query, Long userId, Long directoryId,
                                       Boolean isPublic, List<String> tags) {
        int resolvedPage = Math.max(page, 0);
        int resolvedSize = size < 1 ? 10 : size;
        if (resolvedSize > 50) {
            throw new IllegalArgumentException("size must be <= 50");
        }
        Sort sorting = resolveSort(sort);

        Pageable pageable = PageRequest.of(resolvedPage, resolvedSize, sorting);

        Specification<Roadmap> specification = buildSpecification(
                requesterId, userId, directoryId, isPublic, query, tags);

        Page<Roadmap> roadmaps = roadmapRepository.findAll(specification, pageable);

        return RoadmapListResponse.from(roadmaps.map(RoadmapListItemResponse::from));
    }

    private Sort resolveSort(String sort) {
        if (sort == null || sort.isBlank() || sort.equals("latest")) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sort) {
            case "popular" -> Sort.by(Sort.Direction.DESC, "viewCount", "createdAt");
            case "views" -> Sort.by(Sort.Direction.DESC, "viewCount", "createdAt");
            case "forks" -> Sort.by(Sort.Direction.DESC, "forkCount", "createdAt");
            default -> throw new IllegalArgumentException("sort must be latest|popular|forks|views");
        };
    }

    private Specification<Roadmap> buildSpecification(
            Long requesterId,
            Long userId,
            Long directoryId,
            Boolean isPublic,
            String query,
            List<String> tags) {
        Specification<Roadmap> spec = Specification.where(accessibleSpec(requesterId, userId));

        if (directoryId != null) {
            spec = spec.and((root, criteriaQuery, cb) -> cb.equal(root.get("directoryId"), directoryId));
        }

        if (isPublic != null) {
            spec = spec.and((root, criteriaQuery, cb) -> cb.equal(root.get("isPublic"), isPublic));
        }

        if (query != null && !query.isBlank()) {
            String likeQuery = "%" + query.toLowerCase() + "%";
            spec = spec.and((root, criteriaQuery, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), likeQuery),
                    cb.like(cb.lower(root.get("description")), likeQuery),
                    cb.like(cb.lower(root.get("tags")), likeQuery)
            ));
        }

        if (tags != null) {
            List<String> tagFilters = tags.stream()
                    .filter(tag -> tag != null && !tag.isBlank())
                    .toList();
            for (String tag : tagFilters) {
                String likeTag = "%" + tag.toLowerCase() + "%";
                spec = spec.and((root, criteriaQuery, cb) -> cb.like(cb.lower(root.get("tags")), likeTag));
            }
        }

        return spec;
    }

    private Specification<Roadmap> accessibleSpec(Long requesterId, Long userId) {
        return (root, criteriaQuery, cb) -> {
            if (userId != null) {
                if (requesterId != null && requesterId.equals(userId)) {
                    return cb.equal(root.get("ownerId"), userId);
                }
                return cb.and(
                        cb.equal(root.get("ownerId"), userId),
                        cb.isTrue(root.get("isPublic"))
                );
            }

            if (requesterId == null) {
                return cb.isTrue(root.get("isPublic"));
            }

            return cb.or(
                    cb.isTrue(root.get("isPublic")),
                    cb.equal(root.get("ownerId"), requesterId)
            );
        };
    }
}
