package gajeman.jagalchi.jagalchiserver.application.progress;

import gajeman.jagalchi.jagalchiserver.api.progress.dto.NodeCompleteResponse;
import gajeman.jagalchi.jagalchiserver.api.progress.dto.ProgressResponse;
import gajeman.jagalchi.jagalchiserver.common.exception.ResourceNotFoundException;
import gajeman.jagalchi.jagalchiserver.domain.progress.RoadmapNodeProgress;
import gajeman.jagalchi.jagalchiserver.domain.progress.RoadmapNodeProgressRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.Roadmap;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNode;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapNodeRepository;
import gajeman.jagalchi.jagalchiserver.domain.roadmap.RoadmapRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapNodeRepository roadmapNodeRepository;

    @Mock
    private RoadmapNodeProgressRepository progressRepository;

    @InjectMocks
    private ProgressService progressService;

    @Test
    @DisplayName("내 학습 진도를 조회할 수 있다")
    void getMyProgress() {
        // given
        Long roadmapId = 1L;
        Long userId = 1L;
        
        Roadmap roadmap = Roadmap.builder()
                .title("테스트 로드맵")
                .ownerId(2L)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", roadmapId);

        LocalDateTime updatedAt = LocalDateTime.now();
        
        given(roadmapRepository.findById(roadmapId)).willReturn(Optional.of(roadmap));
        given(roadmapNodeRepository.countByRoadmapId(roadmapId)).willReturn(10L);
        given(progressRepository.countCompletedByRoadmapIdAndUserId(roadmapId, userId)).willReturn(3L);
        given(progressRepository.findCompletedNodeIdsByRoadmapIdAndUserId(roadmapId, userId))
                .willReturn(List.of(1L, 2L, 3L));
        given(progressRepository.findLatestUpdatedAtByRoadmapIdAndUserId(roadmapId, userId))
                .willReturn(Optional.of(updatedAt));

        // when
        ProgressResponse response = progressService.getMyProgress(roadmapId, userId);

        // then
        assertThat(response.getRoadmapId()).isEqualTo(roadmapId);
        assertThat(response.getTotalNodes()).isEqualTo(10L);
        assertThat(response.getCompletedNodes()).isEqualTo(3L);
        assertThat(response.getProgressPercentage()).isEqualTo(new BigDecimal("30.0"));
        assertThat(response.getCompletedNodeIds()).containsExactly(1L, 2L, 3L);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("비공개 로드맵의 진도 조회 시 접근 권한이 없으면 예외가 발생한다")
    void getMyProgressAccessDenied() {
        // given
        Long roadmapId = 1L;
        Long userId = 1L;
        
        Roadmap roadmap = Roadmap.builder()
                .title("비공개 로드맵")
                .ownerId(2L)
                .isPublic(false)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", roadmapId);

        given(roadmapRepository.findById(roadmapId)).willReturn(Optional.of(roadmap));

        // when & then
        assertThatThrownBy(() -> progressService.getMyProgress(roadmapId, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("특정 사용자의 학습 진도를 조회할 수 있다")
    void getUserProgress() {
        // given
        Long roadmapId = 1L;
        Long targetUserId = 2L;
        Long requesterId = 1L;
        
        Roadmap roadmap = Roadmap.builder()
                .title("테스트 로드맵")
                .ownerId(requesterId) // 요청자가 소유자
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", roadmapId);

        given(roadmapRepository.findById(roadmapId)).willReturn(Optional.of(roadmap));
        given(roadmapNodeRepository.countByRoadmapId(roadmapId)).willReturn(5L);
        given(progressRepository.countCompletedByRoadmapIdAndUserId(roadmapId, targetUserId)).willReturn(2L);
        given(progressRepository.findCompletedNodeIdsByRoadmapIdAndUserId(roadmapId, targetUserId))
                .willReturn(List.of(1L, 3L));
        given(progressRepository.findLatestUpdatedAtByRoadmapIdAndUserId(roadmapId, targetUserId))
                .willReturn(Optional.empty());

        // when
        ProgressResponse response = progressService.getUserProgress(roadmapId, targetUserId, requesterId);

        // then
        assertThat(response.getRoadmapId()).isEqualTo(roadmapId);
        assertThat(response.getTotalNodes()).isEqualTo(5L);
        assertThat(response.getCompletedNodes()).isEqualTo(2L);
        assertThat(response.getProgressPercentage()).isEqualTo(new BigDecimal("40.0"));
        assertThat(response.getCompletedNodeIds()).containsExactly(1L, 3L);
        assertThat(response.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("로드맵 소유자가 아닌 경우 다른 사용자 진도 조회 시 예외가 발생한다")
    void getUserProgressNotOwner() {
        // given
        Long roadmapId = 1L;
        Long targetUserId = 2L;
        Long requesterId = 3L;
        
        Roadmap roadmap = Roadmap.builder()
                .title("테스트 로드맵")
                .ownerId(1L) // 다른 사용자가 소유자
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", roadmapId);

        given(roadmapRepository.findById(roadmapId)).willReturn(Optional.of(roadmap));

        // when & then
        assertThatThrownBy(() -> progressService.getUserProgress(roadmapId, targetUserId, requesterId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("노드를 완료 상태로 변경할 수 있다")
    void completeNode() {
        // given
        Long roadmapId = 1L;
        Long nodeId = 1L;
        Long userId = 1L;
        
        Roadmap roadmap = Roadmap.builder()
                .title("테스트 로드맵")
                .ownerId(2L)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", roadmapId);

        RoadmapNode node = RoadmapNode.builder()
                .roadmapId(roadmapId)
                .label("테스트 노드")
                .build();
        ReflectionTestUtils.setField(node, "id", nodeId);

        RoadmapNodeProgress progress = RoadmapNodeProgress.builder()
                .roadmapId(roadmapId)
                .nodeId(nodeId)
                .userId(userId)
                .build();
        ReflectionTestUtils.setField(progress, "id", 1L);

        given(roadmapRepository.findById(roadmapId)).willReturn(Optional.of(roadmap));
        given(roadmapNodeRepository.findById(nodeId)).willReturn(Optional.of(node));
        given(progressRepository.findByRoadmapIdAndNodeIdAndUserId(roadmapId, nodeId, userId))
                .willReturn(Optional.of(progress));
        given(progressRepository.save(any(RoadmapNodeProgress.class))).willReturn(progress);
        given(roadmapNodeRepository.countByRoadmapId(roadmapId)).willReturn(10L);
        given(progressRepository.countCompletedByRoadmapIdAndUserId(roadmapId, userId)).willReturn(5L);

        // when
        NodeCompleteResponse response = progressService.completeNode(roadmapId, nodeId, userId, true);

        // then
        assertThat(response.getNodeId()).isEqualTo(nodeId);
        assertThat(response.getIsCompleted()).isTrue();
        assertThat(response.getRoadmapProgress()).isEqualTo(new BigDecimal("50.0"));
        assertThat(response.getCompletedAt()).isNotNull();
        verify(progressRepository).save(any(RoadmapNodeProgress.class));
    }

    @Test
    @DisplayName("새로운 노드 진행 상황을 생성하여 완료할 수 있다")
    void completeNewNode() {
        // given
        Long roadmapId = 1L;
        Long nodeId = 2L;
        Long userId = 1L;
        
        Roadmap roadmap = Roadmap.builder()
                .title("테스트 로드맵")
                .ownerId(2L)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", roadmapId);

        RoadmapNode node = RoadmapNode.builder()
                .roadmapId(roadmapId)
                .label("새 노드")
                .build();
        ReflectionTestUtils.setField(node, "id", nodeId);

        RoadmapNodeProgress newProgress = RoadmapNodeProgress.builder()
                .roadmapId(roadmapId)
                .nodeId(nodeId)
                .userId(userId)
                .build();
        newProgress.markCompleted();

        given(roadmapRepository.findById(roadmapId)).willReturn(Optional.of(roadmap));
        given(roadmapNodeRepository.findById(nodeId)).willReturn(Optional.of(node));
        given(progressRepository.findByRoadmapIdAndNodeIdAndUserId(roadmapId, nodeId, userId))
                .willReturn(Optional.empty());
        given(progressRepository.save(any(RoadmapNodeProgress.class))).willReturn(newProgress);
        given(roadmapNodeRepository.countByRoadmapId(roadmapId)).willReturn(4L);
        given(progressRepository.countCompletedByRoadmapIdAndUserId(roadmapId, userId)).willReturn(1L);

        // when
        NodeCompleteResponse response = progressService.completeNode(roadmapId, nodeId, userId, true);

        // then
        assertThat(response.getNodeId()).isEqualTo(nodeId);
        assertThat(response.getIsCompleted()).isTrue();
        assertThat(response.getRoadmapProgress()).isEqualTo(new BigDecimal("25.0"));
        verify(progressRepository).save(any(RoadmapNodeProgress.class));
    }

    @Test
    @DisplayName("노드를 미완료 상태로 변경할 수 있다")
    void uncompleteNode() {
        // given
        Long roadmapId = 1L;
        Long nodeId = 1L;
        Long userId = 1L;
        
        Roadmap roadmap = Roadmap.builder()
                .title("테스트 로드맵")
                .ownerId(2L)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", roadmapId);

        RoadmapNode node = RoadmapNode.builder()
                .roadmapId(roadmapId)
                .label("테스트 노드")
                .build();
        ReflectionTestUtils.setField(node, "id", nodeId);

        RoadmapNodeProgress progress = RoadmapNodeProgress.builder()
                .roadmapId(roadmapId)
                .nodeId(nodeId)
                .userId(userId)
                .build();
        progress.markCompleted(); // 먼저 완료 상태로 만들고
        progress.markIncomplete(); // 미완료로 변경
        ReflectionTestUtils.setField(progress, "id", 1L);

        given(roadmapRepository.findById(roadmapId)).willReturn(Optional.of(roadmap));
        given(roadmapNodeRepository.findById(nodeId)).willReturn(Optional.of(node));
        given(progressRepository.findByRoadmapIdAndNodeIdAndUserId(roadmapId, nodeId, userId))
                .willReturn(Optional.of(progress));
        given(progressRepository.save(any(RoadmapNodeProgress.class))).willReturn(progress);
        given(roadmapNodeRepository.countByRoadmapId(roadmapId)).willReturn(10L);
        given(progressRepository.countCompletedByRoadmapIdAndUserId(roadmapId, userId)).willReturn(4L);

        // when
        NodeCompleteResponse response = progressService.completeNode(roadmapId, nodeId, userId, false);

        // then
        assertThat(response.getNodeId()).isEqualTo(nodeId);
        assertThat(response.getIsCompleted()).isFalse();
        assertThat(response.getRoadmapProgress()).isEqualTo(new BigDecimal("40.0"));
        assertThat(response.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 노드 완료 시 예외가 발생한다")
    void completeNonExistentNode() {
        // given
        Long roadmapId = 1L;
        Long nodeId = 999L;
        Long userId = 1L;
        
        Roadmap roadmap = Roadmap.builder()
                .title("테스트 로드맵")
                .ownerId(2L)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", roadmapId);

        given(roadmapRepository.findById(roadmapId)).willReturn(Optional.of(roadmap));
        given(roadmapNodeRepository.findById(nodeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> progressService.completeNode(roadmapId, nodeId, userId, true))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("RoadmapNode not found with id: 999");
    }

    @Test
    @DisplayName("다른 로드맵의 노드 완료 시 예외가 발생한다")
    void completeNodeFromDifferentRoadmap() {
        // given
        Long roadmapId = 1L;
        Long nodeId = 1L;
        Long userId = 1L;
        
        Roadmap roadmap = Roadmap.builder()
                .title("테스트 로드맵")
                .ownerId(2L)
                .isPublic(true)
                .build();
        ReflectionTestUtils.setField(roadmap, "id", roadmapId);

        RoadmapNode node = RoadmapNode.builder()
                .roadmapId(2L) // 다른 로드맵의 노드
                .label("다른 로드맵 노드")
                .build();
        ReflectionTestUtils.setField(node, "id", nodeId);

        given(roadmapRepository.findById(roadmapId)).willReturn(Optional.of(roadmap));
        given(roadmapNodeRepository.findById(nodeId)).willReturn(Optional.of(node));

        // when & then
        assertThatThrownBy(() -> progressService.completeNode(roadmapId, nodeId, userId, true))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("RoadmapNode not found with id: 1");
    }
}