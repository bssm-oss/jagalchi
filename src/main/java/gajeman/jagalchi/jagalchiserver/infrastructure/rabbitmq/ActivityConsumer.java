package gajeman.jagalchi.jagalchiserver.infrastructure.rabbitmq;

import gajeman.jagalchi.jagalchiserver.infrastructure.rabbitmq.dto.ActivityEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityConsumer {

    private final ActivityService activityService;

    @RabbitListener(queues = "activity.queue")
    public void handle(ActivityEvent event) {
        activityService.recordActivity(
                event.userId(),
                event.date()
        );
    }

    @Bean
    public Queue activityQueue() {
        return new Queue("activity.queue", true);
    }
}
