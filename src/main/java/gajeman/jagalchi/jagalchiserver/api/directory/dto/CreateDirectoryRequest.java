package gajeman.jagalchi.jagalchiserver.api.directory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDirectoryRequest {

    @NotBlank(message = "name is required")
    private String name;

    private Long parentId;
}
