package gajeman.jagalchi.jagalchiserver.api.roadmap.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageableResponse {

    private int pageNumber;
    private int pageSize;

    public static PageableResponse of(int pageNumber, int pageSize) {
        return PageableResponse.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();
    }
}
