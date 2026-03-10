package gajeman.jagalchi.jagalchiserver.presentation.user.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDto {

    private String profileImage;

    private String bio;

    private HashMap<String, String> externalLinks;

}
