package gajeman.jagalchi.jagalchiserver.api.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapDeleteResponse;
import gajeman.jagalchi.jagalchiserver.application.roadmap.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roadmaps")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

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
