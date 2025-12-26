package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoadmapRequest {

    @Size(min = 1, message = "title must not be blank")
    private String title;

    private String description;

    private Long directoryId;

    private Boolean isPublic;
}
