package vn.edu.iuh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.model.NotificationHistory;
import vn.edu.iuh.model.NotificationMessage;
import vn.edu.iuh.repository.AccountRepository;
import vn.edu.iuh.repository.FcmTokenRepository;
import vn.edu.iuh.repository.NotificationHistoryRepository;

@RequiredArgsConstructor
@Service
public class NotificationHistoryService {
    private final NotificationHistoryRepository notificationHistoryRepository;
    private final AccountRepository accountRepository;
    private final FcmTokenRepository fcmTokenRepository;

    public void saveNotificationHistory(NotificationMessage notificationMessage) {
        NotificationHistory notificationHistory = new NotificationHistory();
        notificationHistory.setTitle(notificationMessage.getTitle());
        notificationHistory.setBody(notificationMessage.getBody());
        notificationHistory.setImage(notificationMessage.getImage());
        notificationHistory.setAccountId(notificationMessage.getAccountId());
        notificationHistory.setData(notificationMessage.getData().toString());
        notificationHistory.setType(notificationMessage.getType());
        notificationHistoryRepository.save(notificationHistory);
    }
}
