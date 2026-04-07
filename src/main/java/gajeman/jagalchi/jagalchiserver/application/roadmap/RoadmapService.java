package gajeman.jagalchi.jagalchiserver.application.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapDetailResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapForkStatusResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapForkTreeResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapListItemResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapListResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapUpdateResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.UpdateRoadmapRequest;
import gajeman.jagalchi.jagalchiserver.application.user.UserService;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNode;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNodeRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import gajeman.jagalchi.jagalchiserver.domain.user.User;
import gajeman.jagalchi.jagalchiserver.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.CreateRoadmapRequest;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public RoadmapResponse create(CreateRoadmapRequest request, Long ownerId) {
        Roadmap roadmap = Roadmap.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .directoryId(request.getDirectoryId())
                .ownerId(ownerId)
                .isPublic(request.getIsPublic())
                .thumbnailUrl(request.getThumbnailUrl())
                .tags(joinTags(request.getTags()))
                .build();

        Roadmap saved = roadmapRepository.save(roadmap);
        return RoadmapResponse.from(saved);
    }

    @Transactional
    public RoadmapDetailResponse getDetail(Long roadmapId, Long userId) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap", roadmapId));

        if (!roadmap.isAccessibleBy(userId)) {
            throw new ResourceNotFoundException("Roadmap", roadmapId);
        }

        if (roadmap.getIsPublic() && (userId == null || !roadmap.isOwnedBy(userId))) {
            roadmap.incrementViewCount();
        }

        User owner = userService.findById(roadmap.getOwnerId());
        long totalNodes = roadmapNodeRepository.countByRoadmapId(roadmapId);
        long totalEdges = Math.max(totalNodes - 1, 0);
        return RoadmapDetailResponse.from(roadmap, owner, totalNodes, totalEdges);
    }

    public RoadmapListResponse getList(Long requesterId, int page, int size, String sort,
            String period, String query, Long userId, Long directoryId,
            Boolean isPublic, List<String> tags) {
        int resolvedPage = Math.max(page, 0);
        int resolvedSize = size < 1 ? 10 : size;
        if (resolvedSize > 50) {
            throw new IllegalArgumentException("size must be <= 50");
        }
        Sort sorting = resolveSort(sort);

        Pageable pageable = PageRequest.of(resolvedPage, resolvedSize, sorting);

        Specification<Roadmap> specification = buildSpecification(
                requesterId, userId, directoryId, isPublic, query, tags, period);

        Page<Roadmap> roadmaps = roadmapRepository.findAll(specification, pageable);

        // 모든 owner ID 수집
        List<Long> ownerIds = roadmaps.getContent().stream()
                .map(Roadmap::getOwnerId)
                .distinct()
                .collect(Collectors.toList());

        // 한 번에 모든 owner 정보 조회
        Map<Long, User> ownerMap = userService.findByIds(ownerIds);

        // RoadmapListItemResponse 생성 시 owner 정보 포함
        Page<RoadmapListItemResponse> responseItems = roadmaps.map(roadmap -> {
            User owner = ownerMap.get(roadmap.getOwnerId());
            if (owner == null) {
                owner = User.builder().nickname("Unknown").email("unknown@unknown.com").build();
            }
            return RoadmapListItemResponse.from(roadmap, owner);
        });

        return RoadmapListResponse.from(responseItems);
    }

    @Transactional
    public RoadmapUpdateResponse update(Long roadmapId, UpdateRoadmapRequest request, Long userId) {
        Roadmap roadmap = findByIdAndOwner(roadmapId, userId);
        if (roadmap == null) {
            throw new ResourceNotFoundException("Roadmap", roadmapId);
        }

        roadmap.update(
                request.getTitle(),
                request.getDescription(),
                request.getIsPublic(),
                request.getThumbnailUrl());
        if (request.getTags() != null) {
            roadmap.updateTags(joinTags(request.getTags()));
        }

        return RoadmapUpdateResponse.builder()
                .id(roadmap.getId())
                .updatedAt(roadmap.getUpdatedAt())
                .build();
    }

    @Transactional
    public void delete(Long roadmapId, Long userId) {
        Roadmap roadmap = findByIdAndOwner(roadmapId, userId);
        if (roadmap == null) {
            throw new ResourceNotFoundException("Roadmap", roadmapId);
        }
        roadmapRepository.delete(roadmap);
    }

    private Sort resolveSort(String sort) {
        if (sort == null || sort.isBlank() || sort.equals("latest")) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sort) {
            case "popular" -> Sort.by(Sort.Direction.DESC, "viewCount", "createdAt");
            case "views" -> Sort.by(Sort.Direction.DESC, "viewCount", "createdAt");
            case "forks" -> Sort.by(Sort.Direction.DESC, "forkCount", "createdAt");
            case "completion" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> throw new IllegalArgumentException("sort must be latest|popular|forks|views|completion");
        };
    }

    private Specification<Roadmap> buildSpecification(
            Long requesterId,
            Long userId,
            Long directoryId,
            Boolean isPublic,
            String query,
            List<String> tags,
            String period) {
        Specification<Roadmap> spec = Specification.where(accessibleSpec(requesterId, userId));

        if (directoryId != null) {
            spec = spec.and((root, criteriaQuery, cb) -> cb.equal(root.get("directoryId"), directoryId));
        }

        if (isPublic != null) {
            spec = spec.and((root, criteriaQuery, cb) -> cb.equal(root.get("isPublic"), isPublic));
        }

        if (period != null && !period.isBlank()) {
            LocalDateTime start = resolvePeriodStart(period);
            if (start != null) {
                spec = spec.and((root, criteriaQuery, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), start));
            }
        }

        if (query != null && !query.isBlank()) {
            String likeQuery = "%" + query.toLowerCase() + "%";
            spec = spec.and((root, criteriaQuery, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), likeQuery),
                    cb.like(cb.lower(root.get("description")), likeQuery),
                    cb.like(cb.lower(root.get("tags")), likeQuery)));
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

    private LocalDateTime resolvePeriodStart(String period) {
        return switch (period) {
            case "today" -> LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            case "week" -> LocalDateTime.now().minusWeeks(1);
            case "month" -> LocalDateTime.now().minusMonths(1);
            case "year" -> LocalDateTime.now().minusYears(1);
            default -> null;
        };
    }

    private Specification<Roadmap> accessibleSpec(Long requesterId, Long userId) {
        return (root, criteriaQuery, cb) -> {
            if (userId != null) {
                if (requesterId != null && requesterId.equals(userId)) {
                    return cb.equal(root.get("ownerId"), userId);
                }
                return cb.and(
                        cb.equal(root.get("ownerId"), userId),
                        cb.isTrue(root.get("isPublic")));
            }

            if (requesterId == null) {
                return cb.isTrue(root.get("isPublic"));
            }

            return cb.or(
                    cb.isTrue(root.get("isPublic")),
                    cb.equal(root.get("ownerId"), requesterId));
        };
    }

    private Roadmap findByIdAndOwner(Long roadmapId, Long ownerId) {
        return roadmapRepository.findById(roadmapId)
                .filter(roadmap -> roadmap.isOwnedBy(ownerId))
                .orElse(null);
    }

    private String joinTags(java.util.List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        return String.join(",", tags);
    }

    @Transactional
    public RoadmapResponse fork(Long roadmapId, Long userId) {
        Roadmap source = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap", roadmapId));

        if (!source.getIsPublic() && !source.isOwnedBy(userId)) {
            throw new ResourceNotFoundException("Roadmap", roadmapId);
        }

        Roadmap fork = Roadmap.builder()
                .title(source.getTitle() + " (forked)")
                .description(source.getDescription())
                .ownerId(userId)
                .isPublic(true)
                .thumbnailUrl(source.getThumbnailUrl())
                .tags(source.getTags())
                .originalRoadmapId(source.getId())
                .build();

        Roadmap savedFork = roadmapRepository.save(fork);

        List<RoadmapNode> nodes = roadmapNodeRepository.findByRoadmapId(roadmapId);
        List<RoadmapNode> forkedNodes = nodes.stream()
                .map(node -> RoadmapNode.builder()
                        .roadmapId(savedFork.getId())
                        .label(node.getLabel())
                        .build())
                .collect(Collectors.toList());
        roadmapNodeRepository.saveAll(forkedNodes);

        source.incrementForkCount();

        return RoadmapResponse.from(savedFork);
    }

    public RoadmapForkTreeResponse getForkTree(Long roadmapId) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap", roadmapId));

        Roadmap root = roadmap;
        while (root.getOriginalRoadmapId() != null) {
            Long originalId = root.getOriginalRoadmapId();
            Roadmap original = roadmapRepository.findById(originalId).orElse(null);
            if (original == null) break;
            root = original;
        }

        return buildForkTree(root);
    }

    private RoadmapForkTreeResponse buildForkTree(Roadmap roadmap) {
        String ownerName;
        User owner = userRepository.findById(roadmap.getOwnerId()).orElse(null);
        ownerName = owner != null ? owner.getNickname() : "Unknown";

        List<Roadmap> directChildren = roadmapRepository.findByOriginalRoadmapId(roadmap.getId());
        List<RoadmapForkTreeResponse> childrenResponses = new ArrayList<>();
        for (Roadmap child : directChildren) {
            childrenResponses.add(buildForkTree(child));
        }

        return RoadmapForkTreeResponse.from(roadmap, ownerName, childrenResponses);
    }

    public RoadmapForkStatusResponse getForkStatus(Long roadmapId, Long userId) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap", roadmapId));

        boolean isForkedByCurrentUser = false;
        if (userId != null) {
            isForkedByCurrentUser = roadmapRepository.existsForkByUser(roadmapId, userId);
        }

        String originalTitle = null;
        if (roadmap.getOriginalRoadmapId() != null) {
            Roadmap original = roadmapRepository.findById(roadmap.getOriginalRoadmapId()).orElse(null);
            if (original != null) {
                originalTitle = original.getTitle();
            }
        }

        return RoadmapForkStatusResponse.of(
                roadmap.getId(),
                roadmap.getForkCount(),
                isForkedByCurrentUser,
                roadmap.getOriginalRoadmapId(),
                originalTitle
        );
    }

    public RoadmapListResponse getPopularRoadmaps(int page, int size, String sortBy) {
        int resolvedPage = Math.max(page, 0);
        int resolvedSize = size < 1 ? 10 : size;
        if (resolvedSize > 50) {
            throw new IllegalArgumentException("size must be <= 50");
        }

        Sort sorting = switch (sortBy != null ? sortBy : "forks") {
            case "forks" -> Sort.by(Sort.Direction.DESC, "forkCount", "createdAt");
            case "views" -> Sort.by(Sort.Direction.DESC, "viewCount", "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "forkCount", "viewCount", "createdAt");
        };

        Pageable pageable = PageRequest.of(resolvedPage, resolvedSize, sorting);
        Page<Roadmap> roadmaps = roadmapRepository.findAllPublic(pageable);

        List<Long> ownerIds = roadmaps.getContent().stream()
                .map(Roadmap::getOwnerId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, User> ownerMap = userService.findByIds(ownerIds);

        Page<RoadmapListItemResponse> responseItems = roadmaps.map(roadmap -> {
            User owner = ownerMap.get(roadmap.getOwnerId());
            if (owner == null) {
                owner = User.builder().nickname("Unknown").email("unknown@unknown.com").build();
            }
            return RoadmapListItemResponse.from(roadmap, owner);
        });

        return RoadmapListResponse.from(responseItems);
    }
}
