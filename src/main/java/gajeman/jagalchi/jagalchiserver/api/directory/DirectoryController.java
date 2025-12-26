package gajeman.jagalchi.jagalchiserver.api.directory;

import gajeman.jagalchi.jagalchiserver.application.directory.DirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roadmaps/directories")
@RequiredArgsConstructor
public class DirectoryController {

    private final DirectoryService directoryService;
}
