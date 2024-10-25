package vn.edu.iuh.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.iuh.model.NotificationMessage;

@Service
public class FirebaseMessagingService {
    @Autowired
    private FirebaseMessaging firebaseMessaging;
    
    //sendNotificationByToken
    public String sendNotificationByToken(NotificationMessage notificationMessage) {
        Notification notification = Notification.builder()
                .setTitle(notificationMessage.getTitle())
                .setBody(notificationMessage.getBody())
                .setImage(notificationMessage.getImage())
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .putAllData(notificationMessage.getData())
                .setToken(notificationMessage.getRecipientToken())
                .build();
        
        try {
            firebaseMessaging.send(message);

            return "Notification sent successfully";
        } catch (FirebaseMessagingException e) {
            System.out.println("Error sending message: " + e.getMessage());
            return null;
        }
    }
}
