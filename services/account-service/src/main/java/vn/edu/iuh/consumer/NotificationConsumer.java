package vn.edu.iuh.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import vn.edu.iuh.model.NotificationMessage;
import vn.edu.iuh.service.FirebaseMessagingService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
    private final FirebaseMessagingService firebaseMessagingService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "work_queue")
    public void receiveMessage(String message) {
        try {
            // Chuyển đổi JSON thành đối tượng NotificationMessage
            NotificationMessage notificationMessage = objectMapper.readValue(message, NotificationMessage.class);

            //NotificationMessage(title=You have a new bill request, body=You have a new bill request from Hoai An 1, image=null, accountId=4, data=null, type=BILL_REQUEST)
            if (notificationMessage.getData() == null) {
                Map<String, String> data = Map.of("", "");
                notificationMessage.setData(data);
            }
            
            // Gửi thông báo qua Firebase
            firebaseMessagingService.sendNotificationByToken(notificationMessage);
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }
}