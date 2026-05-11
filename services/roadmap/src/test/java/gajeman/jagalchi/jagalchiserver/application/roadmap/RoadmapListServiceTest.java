package gajeman.jagalchi.jagalchiserver.application.roadmap;

import gajeman.jagalchi.jagalchiserver.api.roadmap.dto.RoadmapListResponse;
import gajeman.jagalchi.jagalchiserver.application.user.UserService;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import gajeman.jagalchi.jagalchiserver.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoadmapListServiceTest {

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RoadmapService roadmapService;

    @Test
    void when_비로그인이면_공개_로드맵만_조회한다() {
        // given
        Long ownerId = 2L;
        Roadmap roadmap = Roadmap.builder()
                .title("공개")
                .description("desc")
                .directoryId(null)
                .ownerId(ownerId)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", 1L);

        User owner = User.builder()
                .nickname("owner")
                .email("owner@example.com")
                .build();
        ReflectionTestUtils.setField(owner, "id", ownerId);

        Page<Roadmap> page = new PageImpl<>(List.of(roadmap));
        when(roadmapRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(userService.findByIds(anyList())).thenReturn(Map.of(ownerId, owner));

        // when
        RoadmapListResponse result = roadmapService.getList(
                null, 0, 10, "latest", null, null, null, null, null, null);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getOwner().getNickname()).isEqualTo("owner");
    }

    @Test
    void when_검색어로_필터링한다() {
        // given
        Long ownerId = 1L;
        Roadmap roadmap1 = Roadmap.builder()
                .title("Spring Boot 로드맵")
                .description("백엔드 개발")
                .ownerId(ownerId)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap1, "id", 1L);

        User owner = User.builder()
                .nickname("developer")
                .email("dev@example.com")
                .build();
        ReflectionTestUtils.setField(owner, "id", ownerId);

        Page<Roadmap> page = new PageImpl<>(List.of(roadmap1));
        when(roadmapRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(userService.findByIds(anyList())).thenReturn(Map.of(ownerId, owner));

        // when
        RoadmapListResponse result = roadmapService.getList(
                null, 0, 10, "latest", "Spring", null, null, null, null, null);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).contains("Spring");
    }

    @Test
    void when_태그로_필터링한다() {
        // given
        Long ownerId = 1L;
        Roadmap roadmap = Roadmap.builder()
                .title("백엔드 로드맵")
                .description("desc")
                .ownerId(ownerId)
                .isPublic(true)
                .tags("spring,backend,java")
                .build();
        ReflectionTestUtils.setField(roadmap, "id", 1L);

        User owner = User.builder()
                .nickname("backend-dev")
                .email("backend@example.com")
                .build();
        ReflectionTestUtils.setField(owner, "id", ownerId);

        Page<Roadmap> page = new PageImpl<>(List.of(roadmap));
        when(roadmapRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(userService.findByIds(anyList())).thenReturn(Map.of(ownerId, owner));

        // when
        RoadmapListResponse result = roadmapService.getList(
                null, 0, 10, "latest", null, null, null, null, null, List.of("spring", "backend"));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTags()).contains("spring", "backend", "java");
    }

    @Test
    void when_정렬값이_잘못되면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> roadmapService.getList(null, 0, 10, "wrong",
                null, null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("sort must be latest|popular|forks|views|completion");
    }

    @Test
    void when_사이즈가_50을_초과하면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> roadmapService.getList(null, 0, 51, "latest",
                null, null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("size must be <= 50");
    }

    @Test
    void when_페이지가_음수면_0으로_보정한다() {
        // given
        Page<Roadmap> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(roadmapRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);
        when(userService.findByIds(anyList())).thenReturn(Map.of());

        // when
        RoadmapListResponse result = roadmapService.getList(
                null, -1, 10, "latest", null, null, null, null, null, null);

        // then
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
    }

    @Test
    void when_사이즈가_1미만이면_10으로_보정한다() {
        // given
        Page<Roadmap> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(roadmapRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);
        when(userService.findByIds(anyList())).thenReturn(Map.of());

        // when
        RoadmapListResponse result = roadmapService.getList(
                null, 0, 0, "latest", null, null, null, null, null, null);

        // then
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
}
