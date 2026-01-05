package gajeman.jagalchi.jagalchiserver.presentation.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FollowToggleRequest {

    @NotNull(message = "필수값입니다.")
    private Boolean toggle;

}
