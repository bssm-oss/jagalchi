package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class RoadmapListResponse {

    private List<RoadmapListItemResponse> content;
    private PageableResponse pageable;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;

    public static RoadmapListResponse from(Page<RoadmapListItemResponse> page) {
        return RoadmapListResponse.builder()
                .content(page.getContent())
                .pageable(PageableResponse.of(page.getNumber(), page.getSize()))
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }
}
