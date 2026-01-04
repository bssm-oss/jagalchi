package gajeman.jagalchi.jagalchiserver.api.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapDetailResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapListResponse;
import gajeman.jagalchi.jagalchiserver.application.roadmap.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roadmaps")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    @GetMapping("/{roadmapId}")
    public ResponseEntity<RoadmapDetailResponse> getDetail(
            @PathVariable Long roadmapId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        RoadmapDetailResponse response = roadmapService.getDetail(roadmapId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<RoadmapListResponse> getList(
            @RequestHeader(value = "X-User-Id", required = false) Long requesterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) String query,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(required = false) Long directoryId,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) List<String> tags) {
        RoadmapListResponse response = roadmapService.getList(
                requesterId, page, size, sort, query, userId, directoryId, isPublic, tags);
        return ResponseEntity.ok(response);
    }
}
