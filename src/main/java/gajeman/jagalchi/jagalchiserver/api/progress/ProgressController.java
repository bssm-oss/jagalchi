package gajeman.jagalchi.jagalchiserver.api.progress;

import gajeman.jagalchi.jagalchiserver.api.progress.dto.CompleteNodeRequest;
import gajeman.jagalchi.jagalchiserver.api.progress.dto.NodeCompleteResponse;
import gajeman.jagalchi.jagalchiserver.api.progress.dto.ProgressResponse;
import gajeman.jagalchi.jagalchiserver.application.progress.ProgressService;
import gajeman.jagalchi.jagalchiserver.common.auth.AuthPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal) {
        ProgressResponse response = progressService.getMyProgress(roadmapId, principal.userId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roadmapId}/users/{userId}/progress")
    public ResponseEntity<ProgressResponse> getUserProgress(
            @PathVariable Long roadmapId,
            @PathVariable Long userId,
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal) {
        ProgressResponse response = progressService.getUserProgress(roadmapId, userId, principal.userId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roadmapId}/nodes/{nodeId}/complete")
    public ResponseEntity<NodeCompleteResponse> completeNode(
            @PathVariable Long roadmapId,
            @PathVariable Long nodeId,
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal,
            @RequestBody CompleteNodeRequest request) {
        NodeCompleteResponse response = progressService.completeNode(
                roadmapId, nodeId, principal.userId(), request.getIsCompleted());
        return ResponseEntity.ok(response);
    }
}
