package gajeman.jagalchi.jagalchiserver.api.progress;

import gajeman.jagalchi.jagalchiserver.api.progress.dto.CompleteNodeRequest;
import gajeman.jagalchi.jagalchiserver.api.progress.dto.NodeCompleteResponse;
import gajeman.jagalchi.jagalchiserver.api.progress.dto.ProgressResponse;
import gajeman.jagalchi.jagalchiserver.application.progress.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roadmaps")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/{roadmapId}/my-progress")
    public ResponseEntity<ProgressResponse> getMyProgress(
            @PathVariable Long roadmapId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        ProgressResponse response = progressService.getMyProgress(roadmapId, requireUserId(userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roadmapId}/users/{userId}/progress")
    public ResponseEntity<ProgressResponse> getUserProgress(
            @PathVariable Long roadmapId,
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long requesterId) {
        ProgressResponse response = progressService.getUserProgress(roadmapId, userId, requireUserId(requesterId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roadmapId}/nodes/{nodeId}/complete")
    public ResponseEntity<NodeCompleteResponse> completeNode(
            @PathVariable Long roadmapId,
            @PathVariable Long nodeId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody CompleteNodeRequest request) {
        NodeCompleteResponse response = progressService.completeNode(
                roadmapId, nodeId, requireUserId(userId), request.getIsCompleted());
        return ResponseEntity.ok(response);
    }

    private Long requireUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("X-User-Id header is required");
        }
        return userId;
    }
}