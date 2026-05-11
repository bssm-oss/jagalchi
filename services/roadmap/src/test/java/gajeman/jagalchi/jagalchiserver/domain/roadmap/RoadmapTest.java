package gajeman.jagalchi.jagalchiserver.domain.roadmap;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoadmapTest {

    @Test
    void when_로드맵_정보를_업데이트하면_변경된_값이_반영된다() {
        Roadmap roadmap = Roadmap.builder()
                .title("Original")
                .description("Desc")
                .isPublic(true)
                .build();

        roadmap.update("Updated", "New Desc", false, "http://new.img");

        assertThat(roadmap.getTitle()).isEqualTo("Updated");
        assertThat(roadmap.getDescription()).isEqualTo("New Desc");
        assertThat(roadmap.getIsPublic()).isFalse();
        assertThat(roadmap.getThumbnailUrl()).isEqualTo("http://new.img");
    }

    @Test
    void when_일부_필드만_업데이트하면_null인_필드는_변경되지_않는다() {
        Roadmap roadmap = Roadmap.builder()
                .title("Original")
                .description("Desc")
                .isPublic(true)
                .thumbnailUrl("http://old.img")
                .build();

        roadmap.update(null, null, null, null);

        assertThat(roadmap.getTitle()).isEqualTo("Original");
        assertThat(roadmap.getDescription()).isEqualTo("Desc");
        assertThat(roadmap.getIsPublic()).isTrue();
        assertThat(roadmap.getThumbnailUrl()).isEqualTo("http://old.img");
    }

    @Test
    void when_소유자인지_확인한다() {
        Roadmap roadmap = Roadmap.builder().ownerId(1L).build();

        assertThat(roadmap.isOwnedBy(1L)).isTrue();
        assertThat(roadmap.isOwnedBy(2L)).isFalse();
    }

    @Test
    void when_접근권한을_확인한다() {
        Roadmap publicRoadmap = Roadmap.builder().isPublic(true).ownerId(1L).build();
        Roadmap privateRoadmap = Roadmap.builder().isPublic(false).ownerId(1L).build();

        // Public: Everyone can access
        assertThat(publicRoadmap.isAccessibleBy(1L)).isTrue();
        assertThat(publicRoadmap.isAccessibleBy(2L)).isTrue();

        // Private: Only owner can access
        assertThat(privateRoadmap.isAccessibleBy(1L)).isTrue();
        assertThat(privateRoadmap.isAccessibleBy(2L)).isFalse();
    }
}
