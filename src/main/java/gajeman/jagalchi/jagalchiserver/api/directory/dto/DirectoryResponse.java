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
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DirectoryResponse from(Directory directory) {
        return DirectoryResponse.builder()
                .id(directory.getId())
                .name(directory.getName())
                .parentId(directory.getParentId())
                .ownerId(directory.getOwnerId())
                .createdAt(directory.getCreatedAt())
                .updatedAt(directory.getUpdatedAt())
                .build();
    }
}
