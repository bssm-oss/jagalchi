package gajeman.jagalchi.jagalchiserver.infrastructure.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import gajeman.jagalchi.jagalchiserver.infrastructure.rabbitmq.dto.ActivityEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityConsumer {

    private final ActivityService activityService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfig.ACTIVITY_QUEUE)
    public void handle(Message message) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            ActivityEvent event = objectMapper.readValue(payload, ActivityEvent.class);
            log.info("Received ActivityEvent (raw): userId={}, date={}", event.userId(), event.date());
            activityService.recordActivity(
                    event.userId(),
                    event.date()
            );
            log.info("Successfully processed ActivityEvent for userId={}", event.userId());
        } catch (Exception e) {
            log.error("Failed to process ActivityEvent payload={} error={}", payload, e.getMessage(), e);
            // Throw to allow retry interceptor to perform retries, and final recoverer will reject so DLX routes to DLQ
            throw new RuntimeException(e);
        }
    }
}
