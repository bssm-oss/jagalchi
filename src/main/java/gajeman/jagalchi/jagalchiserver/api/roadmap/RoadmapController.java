package gajeman.jagalchi.jagalchiserver.api.roadmap;

import gajeman.jagalchi.jagalchiserver.common.auth.AuthPrincipal;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.CreateRoadmapRequest;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapDeleteResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapDetailResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapForkStatusResponse;
import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapForkTreeResponse;
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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal,
            @Valid @RequestBody CreateRoadmapRequest request) {
        RoadmapResponse response = roadmapService.create(request, principal.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{roadmapId}/fork")
    public ResponseEntity<RoadmapResponse> fork(
            @PathVariable Long roadmapId,
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal) {
        RoadmapResponse response = roadmapService.fork(roadmapId, principal.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/popular")
    public ResponseEntity<RoadmapListResponse> getPopularRoadmaps(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "forks") String sortBy) {
        RoadmapListResponse response = roadmapService.getPopularRoadmaps(page, size, sortBy);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roadmapId}/fork-tree")
    public ResponseEntity<RoadmapForkTreeResponse> getForkTree(
            @PathVariable Long roadmapId) {
        RoadmapForkTreeResponse response = roadmapService.getForkTree(roadmapId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roadmapId}/fork-status")
    public ResponseEntity<RoadmapForkStatusResponse> getForkStatus(
            @PathVariable Long roadmapId,
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal) {
        RoadmapForkStatusResponse response = roadmapService.getForkStatus(roadmapId, principal.userId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roadmapId}")
    public ResponseEntity<RoadmapDetailResponse> getDetail(
            @PathVariable Long roadmapId,
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal) {
        RoadmapDetailResponse response = roadmapService.getDetail(roadmapId, principal.userId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<RoadmapListResponse> getList(
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String query,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(required = false) Long directoryId,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) List<String> tags) {
        RoadmapListResponse response = roadmapService.getList(
                principal.userId(), page, size, sort, period, query, userId, directoryId, isPublic, tags);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{roadmapId}")
    public ResponseEntity<RoadmapUpdateResponse> update(
            @PathVariable Long roadmapId,
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal,
            @Valid @RequestBody UpdateRoadmapRequest request) {
        RoadmapUpdateResponse response = roadmapService.update(roadmapId, request, principal.userId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{roadmapId}")
    public ResponseEntity<RoadmapDeleteResponse> delete(
            @PathVariable Long roadmapId,
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal) {
        roadmapService.delete(roadmapId, principal.userId());
        RoadmapDeleteResponse response = RoadmapDeleteResponse.builder()
                .message("Roadmap " + roadmapId + " deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
