package gajeman.jagalchi.jagalchiserver.infrastructure.rabbitmq.dto;

import java.time.LocalDate;

public record ActivityEvent(
        Long userId,
        LocalDate date
) {}

