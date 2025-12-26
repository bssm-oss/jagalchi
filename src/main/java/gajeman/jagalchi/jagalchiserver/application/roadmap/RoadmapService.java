package gajeman.jagalchi.jagalchiserver.application.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapResponse;
import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final DirectoryRepository directoryRepository;

    public Page<RoadmapResponse> getList(Long userId, int page, int size, String sort) {
        int resolvedPage = Math.max(page, 0);
        int resolvedSize = size < 1 ? 10 : Math.min(size, 50);
        Sort sorting = resolveSort(sort);

        Pageable pageable = PageRequest.of(resolvedPage, resolvedSize, sorting);

        Page<Roadmap> roadmaps;
        if (userId == null) {
            roadmaps = roadmapRepository.findByIsPublicTrue(pageable);
        } else {
            roadmaps = roadmapRepository.findAllAccessibleBy(userId, pageable);
        }

        return roadmaps.map(RoadmapResponse::from);
    }

    private Sort resolveSort(String sort) {
        if (sort == null || sort.isBlank() || sort.equals("latest")) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sort) {
            case "popular", "views", "forks" -> Sort.by(Sort.Direction.DESC, "viewCount");
            default -> throw new IllegalArgumentException("sort must be latest|popular|forks|views");
        };
    }
}
