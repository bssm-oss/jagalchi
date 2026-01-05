package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RoadmapUpdateResponse {

    private Long id;
    private LocalDateTime updatedAt;
}
