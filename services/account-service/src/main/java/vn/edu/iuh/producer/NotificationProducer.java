package vn.edu.iuh.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import vn.edu.iuh.model.NotificationMessage;

@Service
@RequiredArgsConstructor
public class NotificationProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void sendToQueue(NotificationMessage notificationMessage) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(notificationMessage);
        rabbitTemplate.convertAndSend("work_queue", message);
    }
}
