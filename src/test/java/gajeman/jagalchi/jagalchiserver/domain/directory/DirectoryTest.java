package gajeman.jagalchi.jagalchiserver.domain.directory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DirectoryTest {

    @Test
    void when_디렉토리_이름을_업데이트한다() {
        Directory directory = Directory.builder().name("Old").build();
        directory.updateName("New");
        assertThat(directory.getName()).isEqualTo("New");
    }

    @Test
    void when_디렉토리를_이동한다() {
        Directory directory = Directory.builder().parentId(1L).build();
        directory.moveTo(2L);
        assertThat(directory.getParentId()).isEqualTo(2L);
    }
}
