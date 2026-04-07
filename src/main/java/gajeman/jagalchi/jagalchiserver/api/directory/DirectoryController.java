package gajeman.jagalchi.jagalchiserver.api.directory;

import gajeman.jagalchi.jagalchiserver.api.directory.dto.UpdateDirectoryRequest;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.CreateDirectoryRequest;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.DirectoryResponse;
import gajeman.jagalchi.jagalchiserver.api.directory.dto.DirectoryTreeResponse;
import gajeman.jagalchi.jagalchiserver.application.directory.DirectoryService;
import gajeman.jagalchi.jagalchiserver.common.auth.AuthPrincipal;
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
import java.util.Map;

@RestController
@RequestMapping("/directories")
@RequiredArgsConstructor
public class DirectoryController {

    private final DirectoryService directoryService;

    @GetMapping("/tree")
    public ResponseEntity<List<DirectoryTreeResponse>> getTree(
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal) {
        List<DirectoryTreeResponse> response = directoryService.getTree(principal.userId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{directoryId}")
    public ResponseEntity<DirectoryResponse> update(
            @PathVariable Long directoryId,
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal,
            @Valid @RequestBody UpdateDirectoryRequest request) {
        DirectoryResponse response = directoryService.update(directoryId, request, principal.userId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{directoryId}")
    public ResponseEntity<Map<String, String>> delete(
            @PathVariable Long directoryId,
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) Long targetDirectoryId) {
        directoryService.delete(directoryId, principal.userId(), mode, targetDirectoryId);
        return ResponseEntity.ok(Map.of("message", "Directory deleted successfully"));
    }

    @PostMapping
    public ResponseEntity<DirectoryResponse> create(
            @RequestAttribute(AuthPrincipal.ATTRIBUTE_NAME) AuthPrincipal principal,
            @Valid @RequestBody CreateDirectoryRequest request) {
        DirectoryResponse response = directoryService.create(request, principal.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
