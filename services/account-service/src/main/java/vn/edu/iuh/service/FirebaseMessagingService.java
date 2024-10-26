package vn.edu.iuh.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.edu.iuh.model.FcmToken;
import vn.edu.iuh.model.NotificationMessage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FirebaseMessagingService {
    private static final Logger log = LoggerFactory.getLogger(FirebaseMessagingService.class);
    private final FirebaseMessaging firebaseMessaging;
    private final AccountService accountService;
    private final NotificationHistoryService notificationHistoryService;
    
    //sendNotificationByToken
    public void sendNotificationByToken(NotificationMessage notificationMessage) {
        Notification notification = Notification.builder()
                .setTitle(notificationMessage.getTitle())
                .setBody(notificationMessage.getBody())
                .setImage(notificationMessage.getImage())
                .build();

        List<String> registrationTokens = accountService.getFcmTokens(notificationMessage.getAccountId())
                .stream()
                .map(FcmToken::getFcmToken)
                .toList();
        
        registrationTokens.forEach(token -> {
            Message message = Message.builder()
                    .setNotification(notification)
                    .putAllData(notificationMessage.getData())
                    .setToken(token)
                    .build();

            try {
                firebaseMessaging.send(message);

                notificationHistoryService.saveNotificationHistory(notificationMessage);
            } catch (FirebaseMessagingException e) {
                log.error("Error sending message to token: {}", token);
            }
        });
    }
}
