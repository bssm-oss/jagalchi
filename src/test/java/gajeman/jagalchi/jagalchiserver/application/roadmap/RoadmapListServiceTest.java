package gajeman.jagalchi.jagalchiserver.application.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapListResponse;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoadmapListServiceTest {

    @Mock
    private RoadmapRepository roadmapRepository;

    @InjectMocks
    private RoadmapService roadmapService;

    @Test
    void when_비로그인이면_공개_로드맵만_조회한다() {
        Roadmap roadmap = Roadmap.builder()
                .title("공개")
                .description("desc")
                .directoryId(null)
                .ownerId(2L)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", 1L);

        Page<Roadmap> page = new PageImpl<>(List.of(roadmap));
        when(roadmapRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        RoadmapListResponse result = roadmapService.getList(
                null, 0, 10, "latest", null, null, null, null, null);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void when_정렬값이_잘못되면_예외가_발생한다() {
        assertThatThrownBy(() -> roadmapService.getList(null, 0, 10, "wrong",
                null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
