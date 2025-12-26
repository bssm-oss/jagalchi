package gajeman.jagalchi.jagalchiserver.api.progress;

import gajeman.jagalchi.jagalchiserver.application.progress.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roadmaps")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;
}
