package gajeman.jagalchi.jagalchiserver.application.directory;

import gajeman.jagalchi.jagalchiserver.domain.directory.DirectoryRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectoryService {

    private final DirectoryRepository directoryRepository;
    private final RoadmapRepository roadmapRepository;
}
