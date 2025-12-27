package gajeman.jagalchi.jagalchiserver.api.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapDetailResponse;
import gajeman.jagalchi.jagalchiserver.application.roadmap.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
