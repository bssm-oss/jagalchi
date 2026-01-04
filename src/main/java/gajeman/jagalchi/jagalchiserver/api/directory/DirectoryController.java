package gajeman.jagalchi.jagalchiserver.api.directory;

import gajeman.jagalchi.jagalchiserver.api.directory.dto.CreateDirectoryRequest;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.DirectoryResponse;
import gajeman.jagalchi.jagalchiserver.application.directory.DirectoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/roadmaps/directories")
@RequiredArgsConstructor
public class DirectoryController {

    private final DirectoryService directoryService;

    @DeleteMapping("/{directoryId}")
    public ResponseEntity<Map<String, String>> delete(
            @PathVariable Long directoryId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) Long targetDirectoryId) {
        directoryService.delete(directoryId, requireUserId(userId), mode, targetDirectoryId);
        return ResponseEntity.ok(Map.of("message", "Directory deleted successfully"));
    }

    @PostMapping
    public ResponseEntity<DirectoryResponse> create(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody CreateDirectoryRequest request) {
        DirectoryResponse response = directoryService.create(request, requireUserId(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private Long requireUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("X-User-Id header is required");
        }
        return userId;
    }
}
