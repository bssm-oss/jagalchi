package gajeman.jagalchi.jagalchiserver.api.directory.dto;

import gajeman.jagalchi.jagalchiserver.domain.directory.Directory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DirectoryResponse {

    private Long id;
    private String name;
    private Long parentId;
    private String path;
    private LocalDateTime createdAt;

    public static DirectoryResponse from(Directory directory, String path) {
        return DirectoryResponse.builder()
                .id(directory.getId())
                .name(directory.getName())
                .parentId(directory.getParentId())
                .path(path)
                .createdAt(directory.getCreatedAt())
                .build();
    }
}
