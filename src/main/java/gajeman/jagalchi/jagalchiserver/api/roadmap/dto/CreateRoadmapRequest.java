package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoadmapRequest {
    @NotBlank(message = "title is required")
    private String title;

    private String description;
    private Long directoryId;
    private Boolean isPublic;
    private String thumbnailUrl;
    private List<String> tags;
}
