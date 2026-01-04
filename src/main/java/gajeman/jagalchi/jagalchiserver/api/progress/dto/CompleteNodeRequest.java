package gajeman.jagalchi.jagalchiserver.api.progress.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompleteNodeRequest {

    private Boolean isCompleted;
    private String link;
}
