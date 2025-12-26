package gajeman.jagalchi.jagalchiserver.api.directory.dto;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapSummaryResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DirectoryTreeResponse {

    private Long id;
    private String name;
    private Long parentId;
    private List<DirectoryTreeResponse> children;
    private List<RoadmapSummaryResponse> roadmaps;
}
