package gajeman.jagalchi.jagalchiserver.api.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.CreateRoadmapRequest;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapDeleteResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapDetailResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapListResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapUpdateResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.UpdateRoadmapRequest;
import gajeman.jagalchi.jagalchiserver.application.roadmap.RoadmapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    public ResponseEntity<RoadmapResponse> create(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody CreateRoadmapRequest request) {
        RoadmapResponse response = roadmapService.create(request, requireUserId(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

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

    @PatchMapping("/{roadmapId}")
    public ResponseEntity<RoadmapUpdateResponse> update(
            @PathVariable Long roadmapId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody UpdateRoadmapRequest request) {
        RoadmapUpdateResponse response = roadmapService.update(roadmapId, request, requireUserId(userId));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{roadmapId}")
    public ResponseEntity<RoadmapDeleteResponse> delete(
            @PathVariable Long roadmapId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        roadmapService.delete(roadmapId, requireUserId(userId));
        RoadmapDeleteResponse response = RoadmapDeleteResponse.builder()
                .message("Roadmap " + roadmapId + " deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    private Long requireUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("X-User-Id header is required");
        }
        return userId;
    }
}
