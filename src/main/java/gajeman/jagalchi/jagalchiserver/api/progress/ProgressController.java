package gajeman.jagalchi.jagalchiserver.api.progress;

import gajeman.jagalchi.jagalchiserver.api.progress.dto.CompleteNodeRequest;
import gajeman.jagalchi.jagalchiserver.application.progress.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{roadmapId}/nodes/{nodeId}/complete")
    public ResponseEntity<Void> completeNode(
            @PathVariable Long roadmapId,
            @PathVariable Long nodeId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody CompleteNodeRequest request) {
        progressService.completeNode(roadmapId, nodeId, requireUserId(userId), request.getIsCompleted());
        return ResponseEntity.ok().build();
    }

    private Long requireUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("X-User-Id header is required");
        }
        return userId;
    }
}
